package dev.henriquepelanda.api_pedidos.client.services;

import dev.henriquepelanda.api_pedidos.client.dto.ClientRequestDTO;
import dev.henriquepelanda.api_pedidos.client.dto.ClientResponseDTO;
import dev.henriquepelanda.api_pedidos.client.entity.Client;
import dev.henriquepelanda.api_pedidos.client.repository.ClientRepository;
import dev.henriquepelanda.api_pedidos.common.exception.BusinessException;
import org.springframework.stereotype.Service;

@Service
public class ClientService {
  private final ClientRepository _clientRepository;

  public ClientService(ClientRepository clientRepository)
  {
    this._clientRepository = clientRepository;
  }

  public ClientResponseDTO create(ClientRequestDTO request)
  {
    if(!request.password().equals(request.confirmPassword()))
    {
      throw new BusinessException("Password and Confirm Password do not match");
    }

    if(_clientRepository.existsByEmail(request.email()))
    {
      throw new BusinessException("Email already exist!");
    }

    if(_clientRepository.existsByDocument(request.document()))
    {
      throw new BusinessException("Document already exists!");
    }

    Client client = new Client(
      request.name(),
      request.email(),
      request.document(),
      request.password()
    );

    Client savedClient = _clientRepository.save(client);

    return new ClientResponseDTO(
      savedClient.getId(),
      savedClient.getName(),
      savedClient.getEmail(),
      savedClient.getDocument()
    );
  }
}
