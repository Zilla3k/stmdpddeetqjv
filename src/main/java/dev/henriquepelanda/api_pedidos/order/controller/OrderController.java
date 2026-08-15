package dev.henriquepelanda.api_pedidos.order.controller;

import dev.henriquepelanda.api_pedidos.order.dto.OrderRequestDTO;
import dev.henriquepelanda.api_pedidos.order.dto.OrderResponseDTO;
import dev.henriquepelanda.api_pedidos.order.dto.OrderStatusRequestDTO;
import dev.henriquepelanda.api_pedidos.order.services.OrderService;
import dev.henriquepelanda.api_pedidos.common.dto.ErrorResponseDTO;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Orders", description = "Operations for creating, querying, and managing orders.")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create order", description = "Creates an order with items and reduces product stock.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Order created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Client or product not found", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "422", description = "Business rule violated", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public OrderResponseDTO create(@RequestBody @Valid OrderRequestDTO request) {
        return orderService.create(request);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "List orders", description = "Lists all registered orders.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Orders returned successfully")
    })
    public List<OrderResponseDTO> findAll() {
        return orderService.findAll();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get order by id", description = "Gets an order by its identifier.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order returned successfully"),
            @ApiResponse(responseCode = "404", description = "Order not found", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public OrderResponseDTO findById(@PathVariable UUID id) {
        return orderService.findById(id);
    }

    @PatchMapping("/{id}/status")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update order status", description = "Changes an order status while respecting transition rules.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Order not found", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "422", description = "Business rule violated", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public OrderResponseDTO updateStatus(
            @PathVariable UUID id,
            @RequestBody @Valid OrderStatusRequestDTO request
    ) {
        return orderService.updateStatus(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete order", description = "Deletes an order by its identifier.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Order deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Order not found", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public void delete(@PathVariable UUID id) {
        orderService.delete(id);
    }
}
