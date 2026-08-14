package dev.henriquepelanda.api_pedidos.order.services;

import dev.henriquepelanda.api_pedidos.client.repository.ClientRepository;
import dev.henriquepelanda.api_pedidos.common.exception.BusinessException;
import dev.henriquepelanda.api_pedidos.order.dto.OrderRequestDTO;
import dev.henriquepelanda.api_pedidos.order.dto.OrderItemRequestDTO;
import dev.henriquepelanda.api_pedidos.order.dto.OrderItemResponseDTO;
import dev.henriquepelanda.api_pedidos.order.dto.OrderResponseDTO;
import dev.henriquepelanda.api_pedidos.order.dto.OrderStatusRequestDTO;
import dev.henriquepelanda.api_pedidos.order.entity.OrderItem;
import dev.henriquepelanda.api_pedidos.order.entity.Order;
import dev.henriquepelanda.api_pedidos.order.entity.OrderStatus;
import dev.henriquepelanda.api_pedidos.order.repository.OrderRepository;
import dev.henriquepelanda.api_pedidos.product.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final ClientRepository clientRepository;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository, ClientRepository clientRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.clientRepository = clientRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public OrderResponseDTO create(OrderRequestDTO request) {
        var client = clientRepository.findById(request.clientId())
                .orElseThrow(() -> new BusinessException("Client not found!"));

        if (request.items() == null || request.items().isEmpty()) {
            throw new BusinessException("Order must have at least one item!");
        }

        Order order = new Order(client, OrderStatus.PENDING, BigDecimal.ZERO);
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderItemRequestDTO itemRequest : request.items()) {
            var product = productRepository.findById(itemRequest.productId())
                    .orElseThrow(() -> new BusinessException("Product not found!"));

            if (itemRequest.quantity() > product.getStockQuantity()) {
                throw new BusinessException("Insufficient stock for product " + product.getName() + "!");
            }

            product.decreaseStock(itemRequest.quantity());

            OrderItem item = new OrderItem(
                    product,
                    itemRequest.quantity(),
                    product.getPrice()
            );

            order.addItem(item);
            totalAmount = totalAmount.add(item.getSubtotal());
        }

        order.updateTotalAmount(totalAmount);
        Order savedOrder = orderRepository.saveAndFlush(order);

        Order persistedOrder = orderRepository.findById(savedOrder.getId())
                .orElseThrow(() -> new BusinessException("Order not found after save!"));

        return toResponse(persistedOrder);
    }

    @Transactional(readOnly = true)
    public List<OrderResponseDTO> findAll() {
        return orderRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public OrderResponseDTO findById(UUID id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Order not found!"));

        return toResponse(order);
    }

    @Transactional
    public OrderResponseDTO updateStatus(UUID id, OrderStatusRequestDTO request) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Order not found!"));

        order.updateStatus(request.status());
        Order updated = orderRepository.saveAndFlush(order);

        Order persistedOrder = orderRepository.findById(updated.getId())
                .orElseThrow(() -> new BusinessException("Order not found after update!"));

        return toResponse(persistedOrder);
    }

    @Transactional
    public void delete(UUID id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Order not found!"));

        orderRepository.delete(order);
    }

    private OrderResponseDTO toResponse(Order order) {
        return new OrderResponseDTO(
                order.getId(),
                order.getClient().getId(),
                order.getStatus(),
                order.getTotalAmount(),
                order.getCreatedAt(),
                order.getUpdatedAt(),
                order.getItems().stream()
                        .map(item -> new OrderItemResponseDTO(
                                item.getProduct().getId(),
                                item.getProduct().getName(),
                                item.getQuantity(),
                                item.getUnitPrice(),
                                item.getSubtotal()
                        ))
                        .toList()
        );
    }
}
