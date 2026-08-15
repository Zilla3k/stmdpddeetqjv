package dev.henriquepelanda.api_pedidos.order.services;

import dev.henriquepelanda.api_pedidos.client.entity.Client;
import dev.henriquepelanda.api_pedidos.client.repository.ClientRepository;
import dev.henriquepelanda.api_pedidos.common.exception.BusinessException;
import dev.henriquepelanda.api_pedidos.common.exception.ResourceNotFoundException;
import dev.henriquepelanda.api_pedidos.order.dto.OrderItemRequestDTO;
import dev.henriquepelanda.api_pedidos.order.dto.OrderRequestDTO;
import dev.henriquepelanda.api_pedidos.order.dto.OrderStatusRequestDTO;
import dev.henriquepelanda.api_pedidos.order.entity.Order;
import dev.henriquepelanda.api_pedidos.order.entity.OrderItem;
import dev.henriquepelanda.api_pedidos.order.entity.OrderStatus;
import dev.henriquepelanda.api_pedidos.order.repository.OrderRepository;
import dev.henriquepelanda.api_pedidos.product.entity.Product;
import dev.henriquepelanda.api_pedidos.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private ProductRepository productRepository;

    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderService(orderRepository, clientRepository, productRepository);
    }

    @Test
    void createShouldPersistOrderWithItemsAndDecreaseStock() {
        UUID clientId = UUID.fromString("6febd52f-0b06-4f73-b722-ea1db05ddc3b");
        UUID productId = UUID.fromString("116527c3-f6ea-4ae8-a6a5-2baef7bb3761");
        UUID orderId = UUID.fromString("5134daf0-d364-4555-becd-e154d2971ffc");

        Client client = new Client("Henrique Pelanda", "henrique@gmail.com", "333", "12345678");
        ReflectionTestUtils.setField(client, "id", clientId);

        Product product = new Product(
                "T-Shirt",
                "Basic black T-Shirt",
                new BigDecimal("79.90"),
                UUID.fromString("61661601-7f45-499c-bc9c-59918bd68934"),
                10
        );
        ReflectionTestUtils.setField(product, "id", productId);

        Order persistedOrder = new Order(client, OrderStatus.PENDING, new BigDecimal("159.80"));
        ReflectionTestUtils.setField(persistedOrder, "id", orderId);
        ReflectionTestUtils.setField(persistedOrder, "createdAt", Instant.parse("2026-08-14T11:22:51.108374Z"));
        ReflectionTestUtils.setField(persistedOrder, "updatedAt", Instant.parse("2026-08-14T11:22:51.108496Z"));

        OrderItem item = new OrderItem(product, 2, product.getPrice());
        persistedOrder.addItem(item);

        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(orderRepository.saveAndFlush(any(Order.class))).thenAnswer(invocation -> {
            Order saved = invocation.getArgument(0);
            ReflectionTestUtils.setField(saved, "id", orderId);
            return saved;
        });
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(persistedOrder));

        OrderRequestDTO request = new OrderRequestDTO(
                clientId,
                List.of(new OrderItemRequestDTO(productId, 2))
        );

        var response = orderService.create(request);

        assertEquals(orderId, response.id());
        assertEquals(clientId, response.clientId());
        assertEquals(OrderStatus.PENDING, response.status());
        assertEquals(new BigDecimal("159.80"), response.totalAmount());
        assertNotNull(response.createdAt());
        assertNotNull(response.updatedAt());
        assertEquals(1, response.items().size());
        assertEquals(productId, response.items().get(0).productId());
        assertEquals("T-Shirt", response.items().get(0).productName());
        assertEquals(2, response.items().get(0).quantity());
        assertEquals(new BigDecimal("79.90"), response.items().get(0).unitPrice());
        assertEquals(new BigDecimal("159.80"), response.items().get(0).subtotal());
        assertEquals(8, product.getStockQuantity());

        verify(orderRepository).saveAndFlush(any(Order.class));
        verify(orderRepository).findById(orderId);
    }

    @Test
    void createShouldThrowWhenClientDoesNotExist() {
        UUID clientId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        when(clientRepository.findById(clientId)).thenReturn(Optional.empty());

        OrderRequestDTO request = new OrderRequestDTO(
                clientId,
                List.of(new OrderItemRequestDTO(productId, 1))
        );

        assertThrows(ResourceNotFoundException.class, () -> orderService.create(request));

        verify(productRepository, never()).findById(any());
        verify(orderRepository, never()).saveAndFlush(any());
    }

    @Test
    void createShouldThrowWhenProductStockIsInsufficient() {
        UUID clientId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        Client client = new Client("Henrique", "henrique@gmail.com", "333", "12345678");
        ReflectionTestUtils.setField(client, "id", clientId);

        Product product = new Product(
                "T-Shirt",
                "Basic black T-Shirt",
                new BigDecimal("79.90"),
                UUID.randomUUID(),
                1
        );
        ReflectionTestUtils.setField(product, "id", productId);

        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        OrderRequestDTO request = new OrderRequestDTO(
                clientId,
                List.of(new OrderItemRequestDTO(productId, 2))
        );

        assertThrows(BusinessException.class, () -> orderService.create(request));

        verify(orderRepository, never()).saveAndFlush(any());
    }

    @Test
    void updateStatusShouldPersistNewStatus() {
        UUID orderId = UUID.randomUUID();
        UUID clientId = UUID.randomUUID();

        Client client = new Client("Henrique", "henrique@gmail.com", "333", "12345678");
        ReflectionTestUtils.setField(client, "id", clientId);

        Order order = new Order(client, OrderStatus.PENDING, new BigDecimal("159.80"));
        ReflectionTestUtils.setField(order, "id", orderId);
        ReflectionTestUtils.setField(order, "createdAt", Instant.parse("2026-08-14T11:22:51.108374Z"));
        ReflectionTestUtils.setField(order, "updatedAt", Instant.parse("2026-08-14T11:22:51.108496Z"));

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepository.saveAndFlush(order)).thenReturn(order);

        var response = orderService.updateStatus(orderId, new OrderStatusRequestDTO(OrderStatus.CONFIRMED));

        assertEquals(OrderStatus.CONFIRMED, response.status());
        assertEquals(orderId, response.id());
        verify(orderRepository).saveAndFlush(order);
    }

    @Test
    void findByIdShouldReturnMappedResponse() {
        UUID orderId = UUID.randomUUID();
        UUID clientId = UUID.randomUUID();

        Client client = new Client("Henrique", "henrique@gmail.com", "333", "12345678");
        ReflectionTestUtils.setField(client, "id", clientId);

        Order order = new Order(client, OrderStatus.PENDING, new BigDecimal("159.80"));
        ReflectionTestUtils.setField(order, "id", orderId);
        ReflectionTestUtils.setField(order, "createdAt", Instant.parse("2026-08-14T11:22:51.108374Z"));
        ReflectionTestUtils.setField(order, "updatedAt", Instant.parse("2026-08-14T11:22:51.108496Z"));

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        var response = orderService.findById(orderId);

        assertEquals(orderId, response.id());
        assertEquals(clientId, response.clientId());
        assertEquals(OrderStatus.PENDING, response.status());
        assertEquals(new BigDecimal("159.80"), response.totalAmount());
    }
}
