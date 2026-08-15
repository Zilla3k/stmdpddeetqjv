package dev.henriquepelanda.api_pedidos.client.services;

import dev.henriquepelanda.api_pedidos.client.dto.ClientRequestDTO;
import dev.henriquepelanda.api_pedidos.client.dto.ClientResponseDTO;
import dev.henriquepelanda.api_pedidos.client.dto.ClientUpdateDTO;
import dev.henriquepelanda.api_pedidos.client.entity.Client;
import dev.henriquepelanda.api_pedidos.client.repository.ClientRepository;
import dev.henriquepelanda.api_pedidos.client.specifications.ClientSpecification;
import dev.henriquepelanda.api_pedidos.common.exception.BusinessException;
import dev.henriquepelanda.api_pedidos.common.exception.InvalidRequestException;
import dev.henriquepelanda.api_pedidos.common.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.Locale;

@Service
public class ClientService {
  private final ClientRepository _clientRepository;

  public ClientService(ClientRepository clientRepository)
  {
    this._clientRepository = clientRepository;
  }

  public ClientResponseDTO create(ClientRequestDTO request)
  {
    String name = requireText(request.name(), "Name cannot be blank!");
    String email = normalizeEmail(request.email());
    String document = requireText(request.document(), "Document cannot be blank!");

    if(_clientRepository.existsByEmail(email))
    {
      throw new BusinessException("Email already exist!");
    }

    if(_clientRepository.existsByDocument(document))
    {
      throw new BusinessException("Document already exists!");
    }

    Client client = new Client(
      name,
      email,
      document,
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
            .orElseThrow(() -> new ResourceNotFoundException("Client not found!"));

    return new ClientResponseDTO(
            client.getId(),
            client.getName(),
            client.getEmail(),
            client.getDocument()
    );
  }

  public ClientResponseDTO update(UUID id, ClientUpdateDTO request){
    Client client = _clientRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Client not found!"));

    String name = request.name() != null ? requireText(request.name(), "Name cannot be blank!") : client.getName();
    String email = request.email() != null ? normalizeEmail(request.email()) : client.getEmail();
    String document = request.document() != null ? requireText(request.document(), "Document cannot be blank!") : client.getDocument();

    if (request.email() != null && !email.equals(client.getEmail()) && _clientRepository.existsByEmail(email)) {
      throw new BusinessException("Email already exists!");
    }

    if (request.document() != null && !document.equals(client.getDocument()) && _clientRepository.existsByDocument(document)) {
      throw new BusinessException("Document already exists!");
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
            .orElseThrow(() -> new ResourceNotFoundException("Client not found!"));

    _clientRepository.delete(client);
  }

  private String requireText(String value, String message) {
    if (value == null || value.trim().isEmpty()) {
      throw new InvalidRequestException(message);
    }

    return value.trim();
  }

  private String normalizeEmail(String email) {
    String normalized = requireText(email, "Email cannot be blank!");
    return normalized.toLowerCase(Locale.ROOT);
  }
}
