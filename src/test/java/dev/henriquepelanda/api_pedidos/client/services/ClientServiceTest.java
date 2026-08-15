package dev.henriquepelanda.api_pedidos.client.services;

import dev.henriquepelanda.api_pedidos.client.dto.ClientRequestDTO;
import dev.henriquepelanda.api_pedidos.client.dto.ClientResponseDTO;
import dev.henriquepelanda.api_pedidos.client.dto.ClientUpdateDTO;
import dev.henriquepelanda.api_pedidos.client.entity.Client;
import dev.henriquepelanda.api_pedidos.client.repository.ClientRepository;
import dev.henriquepelanda.api_pedidos.common.exception.BusinessException;
import dev.henriquepelanda.api_pedidos.common.exception.InvalidRequestException;
import dev.henriquepelanda.api_pedidos.common.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {

    @Mock
    private ClientRepository clientRepository;

    private ClientService clientService;

    @BeforeEach
    void setUp() {
        clientService = new ClientService(clientRepository);
    }

    @Test
    void createShouldPersistClientAndNormalizeEmail() {
        UUID clientId = UUID.fromString("6febd52f-0b06-4f73-b722-ea1db05ddc3b");

        ClientRequestDTO request = new ClientRequestDTO(
                "  Henrique Pelanda  ",
                "  Henrique@Example.com  ",
                " 12345678900 ",
                "12345678",
                "12345678"
        );

        ArgumentCaptor<Client> clientCaptor = ArgumentCaptor.forClass(Client.class);

        when(clientRepository.existsByEmail("henrique@example.com")).thenReturn(false);
        when(clientRepository.existsByDocument("12345678900")).thenReturn(false);
        when(clientRepository.save(clientCaptor.capture())).thenAnswer(invocation -> {
            Client saved = invocation.getArgument(0);
            ReflectionTestUtils.setField(saved, "id", clientId);
            return saved;
        });

        ClientResponseDTO response = clientService.create(request);

        assertEquals(clientId, response.id());
        assertEquals("Henrique Pelanda", response.name());
        assertEquals("henrique@example.com", response.email());
        assertEquals("12345678900", response.document());

        Client persisted = clientCaptor.getValue();
        assertEquals("Henrique Pelanda", persisted.getName());
        assertEquals("henrique@example.com", persisted.getEmail());
        assertEquals("12345678900", persisted.getDocument());
        verify(clientRepository).existsByEmail("henrique@example.com");
        verify(clientRepository).existsByDocument("12345678900");
        verify(clientRepository).save(any(Client.class));
    }

    @Test
    void createShouldThrowWhenNameIsBlank() {
        ClientRequestDTO request = new ClientRequestDTO(
                "   ",
                "henrique@example.com",
                "12345678900",
                "12345678",
                "12345678"
        );

        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> clientService.create(request));

        assertEquals("Name cannot be blank!", exception.getMessage());
        verifyNoInteractions(clientRepository);
    }

    @Test
    void createShouldThrowWhenEmailIsBlank() {
        ClientRequestDTO request = new ClientRequestDTO(
                "Henrique",
                "   ",
                "12345678900",
                "12345678",
                "12345678"
        );

        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> clientService.create(request));

        assertEquals("Email cannot be blank!", exception.getMessage());
        verifyNoInteractions(clientRepository);
    }

    @Test
    void createShouldThrowWhenDocumentIsBlank() {
        ClientRequestDTO request = new ClientRequestDTO(
                "Henrique",
                "henrique@example.com",
                "   ",
                "12345678",
                "12345678"
        );

        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> clientService.create(request));

        assertEquals("Document cannot be blank!", exception.getMessage());
        verifyNoInteractions(clientRepository);
    }

    @Test
    void createShouldThrowWhenEmailAlreadyExists() {
        ClientRequestDTO request = new ClientRequestDTO(
                "Henrique",
                "henrique@example.com",
                "12345678900",
                "12345678",
                "12345678"
        );

        when(clientRepository.existsByEmail("henrique@example.com")).thenReturn(true);

        BusinessException exception = assertThrows(BusinessException.class, () -> clientService.create(request));

        assertEquals("Email already exist!", exception.getMessage());
        verify(clientRepository).existsByEmail("henrique@example.com");
        verify(clientRepository, never()).existsByDocument(any());
        verify(clientRepository, never()).save(any());
    }

    @Test
    void createShouldThrowWhenDocumentAlreadyExists() {
        ClientRequestDTO request = new ClientRequestDTO(
                "Henrique",
                "henrique@example.com",
                "12345678900",
                "12345678",
                "12345678"
        );

        when(clientRepository.existsByEmail("henrique@example.com")).thenReturn(false);
        when(clientRepository.existsByDocument("12345678900")).thenReturn(true);

        BusinessException exception = assertThrows(BusinessException.class, () -> clientService.create(request));

        assertEquals("Document already exists!", exception.getMessage());
        verify(clientRepository).existsByEmail("henrique@example.com");
        verify(clientRepository).existsByDocument("12345678900");
        verify(clientRepository, never()).save(any());
    }

    @Test
    void findAllShouldReturnMappedPage() {
        Pageable pageable = PageRequest.of(0, 10);

        Client first = new Client("Henrique", "henrique@example.com", "12345678900", "12345678");
        ReflectionTestUtils.setField(first, "id", UUID.fromString("6febd52f-0b06-4f73-b722-ea1db05ddc3b"));

        Client second = new Client("Maria", "maria@example.com", "98765432100", "12345678");
        ReflectionTestUtils.setField(second, "id", UUID.fromString("2ef7c0a6-2b28-4d63-9c9f-9a1f2d8df111"));

        when(clientRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of(first, second), pageable, 2));

        Page<ClientResponseDTO> response = clientService.findAll("hen", "example.com", pageable);

        assertEquals(2, response.getTotalElements());
        assertEquals("Henrique", response.getContent().get(0).name());
        assertEquals("henrique@example.com", response.getContent().get(0).email());
        assertEquals("Maria", response.getContent().get(1).name());
        assertEquals("maria@example.com", response.getContent().get(1).email());
        verify(clientRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void findByIdShouldReturnMappedResponse() {
        UUID clientId = UUID.fromString("6febd52f-0b06-4f73-b722-ea1db05ddc3b");

        Client client = new Client("Henrique Pelanda", "henrique@example.com", "12345678900", "12345678");
        ReflectionTestUtils.setField(client, "id", clientId);

        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));

        ClientResponseDTO response = clientService.findById(clientId);

        assertEquals(clientId, response.id());
        assertEquals("Henrique Pelanda", response.name());
        assertEquals("henrique@example.com", response.email());
        assertEquals("12345678900", response.document());
        verify(clientRepository).findById(clientId);
    }

    @Test
    void findByIdShouldThrowWhenClientDoesNotExist() {
        UUID clientId = UUID.randomUUID();

        when(clientRepository.findById(clientId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> clientService.findById(clientId));

        assertEquals("Client not found!", exception.getMessage());
        verify(clientRepository).findById(clientId);
    }

    @Test
    void updateShouldPersistPartialChanges() {
        UUID clientId = UUID.fromString("6febd52f-0b06-4f73-b722-ea1db05ddc3b");

        Client client = new Client("Henrique Pelanda", "henrique@example.com", "12345678900", "12345678");
        ReflectionTestUtils.setField(client, "id", clientId);

        ClientUpdateDTO request = new ClientUpdateDTO(
                "  Henrique Updated  ",
                "  NewEmail@Example.com  ",
                " 99999999999 "
        );

        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));
        when(clientRepository.existsByEmail("newemail@example.com")).thenReturn(false);
        when(clientRepository.existsByDocument("99999999999")).thenReturn(false);
        when(clientRepository.save(client)).thenReturn(client);

        ClientResponseDTO response = clientService.update(clientId, request);

        assertEquals(clientId, response.id());
        assertEquals("Henrique Updated", response.name());
        assertEquals("newemail@example.com", response.email());
        assertEquals("99999999999", response.document());
        assertEquals("Henrique Updated", client.getName());
        assertEquals("newemail@example.com", client.getEmail());
        assertEquals("99999999999", client.getDocument());
        verify(clientRepository).save(client);
    }

    @Test
    void updateShouldKeepExistingValuesWhenRequestFieldsAreNull() {
        UUID clientId = UUID.fromString("6febd52f-0b06-4f73-b722-ea1db05ddc3b");

        Client client = new Client("Henrique Pelanda", "henrique@example.com", "12345678900", "12345678");
        ReflectionTestUtils.setField(client, "id", clientId);

        ClientUpdateDTO request = new ClientUpdateDTO(null, null, null);

        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));
        when(clientRepository.save(client)).thenReturn(client);

        ClientResponseDTO response = clientService.update(clientId, request);

        assertEquals("Henrique Pelanda", response.name());
        assertEquals("henrique@example.com", response.email());
        assertEquals("12345678900", response.document());
        verify(clientRepository, never()).existsByEmail(any());
        verify(clientRepository, never()).existsByDocument(any());
    }

    @Test
    void updateShouldThrowWhenClientDoesNotExist() {
        UUID clientId = UUID.randomUUID();

        when(clientRepository.findById(clientId)).thenReturn(Optional.empty());

        ClientUpdateDTO request = new ClientUpdateDTO("Henrique", "henrique@example.com", "12345678900");

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> clientService.update(clientId, request));

        assertEquals("Client not found!", exception.getMessage());
        verify(clientRepository).findById(clientId);
        verify(clientRepository, never()).save(any());
    }

    @Test
    void updateShouldThrowWhenUpdatedEmailAlreadyExists() {
        UUID clientId = UUID.fromString("6febd52f-0b06-4f73-b722-ea1db05ddc3b");

        Client client = new Client("Henrique Pelanda", "henrique@example.com", "12345678900", "12345678");
        ReflectionTestUtils.setField(client, "id", clientId);

        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));
        when(clientRepository.existsByEmail("newemail@example.com")).thenReturn(true);

        ClientUpdateDTO request = new ClientUpdateDTO(null, "newemail@example.com", null);

        BusinessException exception = assertThrows(BusinessException.class, () -> clientService.update(clientId, request));

        assertEquals("Email already exists!", exception.getMessage());
        verify(clientRepository, never()).save(any());
    }

    @Test
    void updateShouldThrowWhenUpdatedDocumentAlreadyExists() {
        UUID clientId = UUID.fromString("6febd52f-0b06-4f73-b722-ea1db05ddc3b");

        Client client = new Client("Henrique Pelanda", "henrique@example.com", "12345678900", "12345678");
        ReflectionTestUtils.setField(client, "id", clientId);

        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));
        when(clientRepository.existsByDocument("99999999999")).thenReturn(true);

        ClientUpdateDTO request = new ClientUpdateDTO(null, null, "99999999999");

        BusinessException exception = assertThrows(BusinessException.class, () -> clientService.update(clientId, request));

        assertEquals("Document already exists!", exception.getMessage());
        verify(clientRepository, never()).save(any());
    }

    @Test
    void deleteShouldRemoveClient() {
        UUID clientId = UUID.fromString("6febd52f-0b06-4f73-b722-ea1db05ddc3b");

        Client client = new Client("Henrique Pelanda", "henrique@example.com", "12345678900", "12345678");
        ReflectionTestUtils.setField(client, "id", clientId);

        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));

        clientService.delete(clientId);

        verify(clientRepository).delete((Client) client);
    }

    @Test
    void deleteShouldThrowWhenClientDoesNotExist() {
        UUID clientId = UUID.randomUUID();

        when(clientRepository.findById(clientId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> clientService.delete(clientId));

        assertEquals("Client not found!", exception.getMessage());
        verify(clientRepository, never()).delete((Client) any());
    }
}
