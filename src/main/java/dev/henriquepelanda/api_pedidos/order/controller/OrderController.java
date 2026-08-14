package dev.henriquepelanda.api_pedidos.order.controller;

import dev.henriquepelanda.api_pedidos.order.dto.OrderRequestDTO;
import dev.henriquepelanda.api_pedidos.order.dto.OrderResponseDTO;
import dev.henriquepelanda.api_pedidos.order.dto.OrderStatusRequestDTO;
import dev.henriquepelanda.api_pedidos.order.services.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponseDTO create(@RequestBody @Valid OrderRequestDTO request) {
        return orderService.create(request);
    }

    @GetMapping
    public List<OrderResponseDTO> findAll() {
        return orderService.findAll();
    }

    @GetMapping("/{id}")
    public OrderResponseDTO findById(@PathVariable UUID id) {
        return orderService.findById(id);
    }

    @PatchMapping("/{id}/status")
    public OrderResponseDTO updateStatus(
            @PathVariable UUID id,
            @RequestBody @Valid OrderStatusRequestDTO request
    ) {
        return orderService.updateStatus(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        orderService.delete(id);
    }
}
