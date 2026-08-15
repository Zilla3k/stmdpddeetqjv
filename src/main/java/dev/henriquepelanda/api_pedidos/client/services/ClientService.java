package dev.henriquepelanda.api_pedidos.client.services;

import dev.henriquepelanda.api_pedidos.client.dto.ClientRequestDTO;
import dev.henriquepelanda.api_pedidos.client.dto.ClientResponseDTO;
import dev.henriquepelanda.api_pedidos.client.dto.ClientUpdateDTO;
import dev.henriquepelanda.api_pedidos.client.entity.Client;
import dev.henriquepelanda.api_pedidos.client.repository.ClientRepository;
import dev.henriquepelanda.api_pedidos.client.specifications.ClientSpecification;
import dev.henriquepelanda.api_pedidos.common.exception.BusinessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ClientService {
  private final ClientRepository _clientRepository;

  public ClientService(ClientRepository clientRepository)
  {
    this._clientRepository = clientRepository;
  }

  public ClientResponseDTO create(ClientRequestDTO request)
  {
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

  public Page<ClientResponseDTO> findAll(String name, String email, Pageable pageable) {
    return _clientRepository.findAll(
                    ClientSpecification.nameContains(name).and(ClientSpecification.emailContains(email)),
                    pageable)
            .map(client -> new ClientResponseDTO(
                    client.getId(),
                    client.getName(),
                    client.getEmail(),
                    client.getDocument()
            ));
  }

  public ClientResponseDTO findById(UUID id){
    Client client = _clientRepository.findById(id)
            .orElseThrow(() -> new BusinessException("Client not found!"));

    return new ClientResponseDTO(
            client.getId(),
            client.getName(),
            client.getEmail(),
            client.getDocument()
    );
  }

  public ClientResponseDTO update(UUID id, ClientUpdateDTO request){
    Client client = _clientRepository.findById(id)
            .orElseThrow(() -> new BusinessException("Client not found!"));

    String name = request.name() != null ? request.name() : client.getName();
    String email = request.email() != null ? request.email() : client.getEmail();
    String document = request.document() != null ? request.document() : client.getDocument();

    if (request.email() != null && !request.email().equals(client.getEmail()) && _clientRepository.existsByEmail(request.email())) {
      throw new BusinessException("Email already exists!");
    }

    client.update(
            name,
            email,
            document
    );

    Client updated = _clientRepository.save(client);

    return new ClientResponseDTO(
            updated.getId(),
            updated.getName(),
            updated.getEmail(),
            updated.getDocument()
    );
  }

  public void delete(UUID id){
    Client client = _clientRepository.findById(id)
            .orElseThrow(() -> new BusinessException("Client not found!"));

    _clientRepository.delete(client);
  }
}
