package dev.henriquepelanda.api_pedidos.client.controller;

import dev.henriquepelanda.api_pedidos.client.dto.ClientRequestDTO;
import dev.henriquepelanda.api_pedidos.client.dto.ClientResponseDTO;
import dev.henriquepelanda.api_pedidos.client.services.ClientService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/clients")
public class ClientController {
  private final ClientService clientService;

  public ClientController(ClientService clientService) {
    this.clientService = clientService;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public ClientResponseDTO create(@RequestBody @Valid ClientRequestDTO request) {
    return clientService.create(request);
  }

  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  public List<ClientResponseDTO> findAll(){
    return clientService.findAll();
  }

  @GetMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public ClientResponseDTO findById(@PathVariable UUID id){
    return clientService.findById(id);
  }

  @PatchMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public ClientResponseDTO update(@PathVariable UUID id, @RequestBody ClientRequestDTO request){
    return clientService.update(id, request);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public void delete(@PathVariable UUID id){
    clientService.delete(id);
  }

}
