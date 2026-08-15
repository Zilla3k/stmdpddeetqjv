package dev.henriquepelanda.api_pedidos.client.controller;

import dev.henriquepelanda.api_pedidos.client.dto.ClientRequestDTO;
import dev.henriquepelanda.api_pedidos.client.dto.ClientResponseDTO;
import dev.henriquepelanda.api_pedidos.client.dto.ClientUpdateDTO;
import dev.henriquepelanda.api_pedidos.client.services.ClientService;
import dev.henriquepelanda.api_pedidos.common.dto.ErrorResponseDTO;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/clients")
@Tag(name = "Clients", description = "Operations for creating and querying clients.")
public class ClientController {
  private final ClientService clientService;

  public ClientController(ClientService clientService) {
    this.clientService = clientService;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "Create client", description = "Creates a new client with email, document, and password validation.")
  @ApiResponses({
          @ApiResponse(responseCode = "201", description = "Client created successfully"),
          @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
          @ApiResponse(responseCode = "422", description = "Business rule violated", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
  })
  public ClientResponseDTO create(@RequestBody @Valid ClientRequestDTO request) {
    return clientService.create(request);
  }

  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  @Operation(summary = "List clients", description = "Lists clients with optional name and email filters.")
  @ApiResponses({
          @ApiResponse(responseCode = "200", description = "Clients returned successfully"),
          @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
  })
  public Page<ClientResponseDTO> findAll(
          @RequestParam(required = false) String name,
          @RequestParam(required = false) String email,
          Pageable pageable
  ){
    return clientService.findAll(name, email, pageable);
  }

  @GetMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  @Operation(summary = "Get client by id", description = "Gets a client by its identifier.")
  @ApiResponses({
          @ApiResponse(responseCode = "200", description = "Client returned successfully"),
          @ApiResponse(responseCode = "404", description = "Client not found", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
  })
  public ClientResponseDTO findById(@PathVariable UUID id){
    return clientService.findById(id);
  }

  @PatchMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  @Operation(summary = "Update client", description = "Partially updates a client.")
  @ApiResponses({
          @ApiResponse(responseCode = "200", description = "Client updated successfully"),
          @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
          @ApiResponse(responseCode = "404", description = "Client not found", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
          @ApiResponse(responseCode = "422", description = "Business rule violated", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
  })
  public ClientResponseDTO update(@PathVariable UUID id, @RequestBody @Valid ClientUpdateDTO request){
    return clientService.update(id, request);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(summary = "Delete client", description = "Deletes a client by its identifier.")
  @ApiResponses({
          @ApiResponse(responseCode = "204", description = "Client deleted successfully"),
          @ApiResponse(responseCode = "404", description = "Client not found", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
  })
  public void delete(@PathVariable UUID id){
    clientService.delete(id);
  }

}
