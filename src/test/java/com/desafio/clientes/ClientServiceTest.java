package com.desafio.clientes;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import com.desafio.clientes.application.dto.ClientDTO;
import com.desafio.clientes.application.mapper.ClientMapper;
import com.desafio.clientes.application.service.ClientService;
import com.desafio.clientes.application.service.StatisticsService;
import com.desafio.clientes.domain.entity.Client;
import com.desafio.clientes.infraestructure.repository.ClientRepository;

 /**
 * Clase de pruebas unitarias para {@link ClientService}.
 * Verifica la lógica de negocio relacionada con la gestión de clientes,
 * incluyendo creación, búsqueda y validaciones.
 */
@ExtendWith(MockitoExtension.class)
class ClientServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private ClientMapper clientMapper;

    @Mock
    private StatisticsService statisticsService;

    @InjectMocks
    private ClientService clientService;

    /**
     * Verifica que se pueda crear un nuevo cliente cuando se proporciona una solicitud válida.
     * El test comprueba:
     * - La creación exitosa del cliente
     * - El mapeo correcto de datos
     * - La validación de cliente no duplicado
     * - El almacenamiento en repositorio
     */
    @Test
    void createClient_ShouldCreateClient_WhenValidRequest() {
        // Arrange
        ClientDTO.CreateClientRequest request = new ClientDTO.CreateClientRequest(
                "Juan", "Pérez", 30, LocalDate.of(1994, 3, 15));
        Client client = new Client("Juan", "Pérez", 30, LocalDate.of(1994, 3, 15));

        when(clientRepository.findByFirstNameAndLastName("Juan", "Pérez"))
                .thenReturn(Optional.empty());
        when(clientMapper.toEntity(request)).thenReturn(client);
        when(clientRepository.save(any(Client.class))).thenReturn(client);

        // Act
        Client result = clientService.createClient(request);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getFirstName()).isEqualTo("Juan");
        assertThat(result.getLastName()).isEqualTo("Pérez");
        verify(clientRepository).save(any(Client.class));
    }

    /**
     * Verifica que se lance una excepción cuando se intenta crear un cliente
     * que ya existe en el sistema. La existencia se determina por la combinación
     * de nombre y apellido.
     * 
     * @throws IllegalArgumentException cuando el cliente ya existe
     */
    @Test
    void createClient_ShouldThrowException_WhenClientExists() {
        // Arrange
        ClientDTO.CreateClientRequest request = new ClientDTO.CreateClientRequest(
                "Juan", "Pérez", 30, LocalDate.of(1994, 3, 15));
        Client existingClient = new Client("Juan", "Pérez", 30, LocalDate.of(1994, 3, 15));

        when(clientRepository.findByFirstNameAndLastName("Juan", "Pérez"))
                .thenReturn(Optional.of(existingClient));

        // Act & Assert
        assertThatThrownBy(() -> clientService.createClient(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Ya existe un cliente con el nombre: Juan Pérez");
    }

    /**
     * Verifica la recuperación exitosa de un cliente por su ID.
     * Comprueba que los datos retornados correspondan al cliente solicitado
     * cuando existe en la base de datos.
     */
    @Test
    void getClientById_ShouldReturnClient_WhenClientExists() {
        // Arrange
        Long clientId = 1L;
        Client client = new Client("Juan", "Pérez", 30, LocalDate.of(1994, 3, 15));
        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));

        // Act
        Client result = clientService.getClientById(clientId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getFirstName()).isEqualTo("Juan");
    }

    /**
     * Verifica que se lance una excepción cuando se intenta recuperar
     * un cliente con un ID que no existe en el sistema.
     * 
     * @throws IllegalArgumentException cuando el ID del cliente no existe
     */
    @Test
    void getClientById_ShouldThrowException_WhenClientNotFound() {
        // Arrange
        Long clientId = 999L;
        when(clientRepository.findById(clientId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> clientService.getClientById(clientId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Cliente no encontrado con ID: 999");
    }
}
