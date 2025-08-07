package com.desafio.clientes.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.desafio.clientes.application.dto.ClientDTO;
import com.desafio.clientes.application.mapper.ClientMapper;
import com.desafio.clientes.application.service.ClientService;
import com.desafio.clientes.domain.entity.Client;

import java.util.List;

/**
 * Controlador REST para la gestión de clientes.
 * 
 * Implementa una API RESTful siguiendo las mejores prácticas
 * de diseño de APIs. Proporciona endpoints para todas las
 * operaciones CRUD y consultas estadísticas.
 * 
 * Endpoints principales:
 * - POST /api/v1/clients - Crear cliente
 * - GET /api/v1/clients - Listar todos los clientes
 * - GET /api/v1/clients/{id} - Obtener cliente por ID
 * - PUT /api/v1/clients/{id} - Actualizar cliente
 * - DELETE /api/v1/clients/{id} - Eliminar cliente
 * - GET /api/v1/clients/metrics - Obtener métricas estadísticas
 * 
 * @author Sistema de Desarrollo
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/v1/clients")
@Tag(name = "Clientes", description = "API para gestión de clientes")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ClientController {

    private static final Logger logger = LoggerFactory.getLogger(ClientController.class);

    private final ClientService clientService;
    private final ClientMapper clientMapper;

    public ClientController(ClientService clientService, ClientMapper clientMapper) {
        this.clientService = clientService;
        this.clientMapper = clientMapper;
    }

    /**
     * Crear un nuevo cliente.
     * 
     * @param request datos del cliente a crear
     * @return respuesta con el cliente creado
     */
    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(summary = "Crear nuevo cliente", 
               description = "Registra un nuevo cliente en el sistema con validaciones de negocio")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Cliente creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
        @ApiResponse(responseCode = "409", description = "Cliente ya existe"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<ClientDTO.ApiResponse<ClientDTO.ClientResponse>> createClient(
            @Valid @RequestBody ClientDTO.CreateClientRequest request) {
        
        logger.info("Solicitud para crear cliente: {} {}", request.getFirstName(), request.getLastName());

        try {
            Client client = clientService.createClient(request);
            ClientDTO.ApiResponse<ClientDTO.ClientResponse> response = 
                clientMapper.toApiResponse(client, "Cliente creado exitosamente");

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (IllegalArgumentException e) {
            logger.warn("Error de validación al crear cliente: {}", e.getMessage());
            ClientDTO.ApiResponse<ClientDTO.ClientResponse> errorResponse = 
                ClientDTO.ApiResponse.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);

        } catch (Exception e) {
            logger.error("Error interno al crear cliente", e);
            ClientDTO.ApiResponse<ClientDTO.ClientResponse> errorResponse = 
                ClientDTO.ApiResponse.error("Error interno del servidor");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Obtener todos los clientes con sus datos completos y cálculos derivados.
     * 
     * @return lista de todos los clientes
     */
    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(summary = "Listar todos los clientes", 
               description = "Obtiene la lista completa de clientes con datos calculados como esperanza de vida")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<ClientDTO.ApiResponse<List<ClientDTO.ClientResponse>>> getAllClients() {
        
        logger.info("Solicitud para obtener todos los clientes");

        try {
            List<Client> clients = clientService.getAllClients();
            ClientDTO.ApiResponse<List<ClientDTO.ClientResponse>> response = 
                ClientDTO.ApiResponse.success(clientMapper.toDtoList(clients), 
                    String.format("Se encontraron %d clientes", clients.size()));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error al obtener lista de clientes", e);
            ClientDTO.ApiResponse<List<ClientDTO.ClientResponse>> errorResponse = 
                ClientDTO.ApiResponse.error("Error interno del servidor");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Obtener un cliente específico por su ID.
     * 
     * @param id identificador del cliente
     * @return datos del cliente
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(summary = "Obtener cliente por ID", 
               description = "Obtiene los datos completos de un cliente específico")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cliente encontrado"),
        @ApiResponse(responseCode = "404", description = "Cliente no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<ClientDTO.ApiResponse<ClientDTO.ClientResponse>> getClientById(
            @Parameter(description = "ID del cliente") @PathVariable Long id) {
        
        logger.info("Solicitud para obtener cliente con ID: {}", id);

        try {
            Client client = clientService.getClientById(id);
            ClientDTO.ApiResponse<ClientDTO.ClientResponse> response = 
                clientMapper.toApiResponse(client, "Cliente encontrado exitosamente");

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            logger.warn("Cliente no encontrado con ID: {}", id);
            ClientDTO.ApiResponse<ClientDTO.ClientResponse> errorResponse = 
                ClientDTO.ApiResponse.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);

        } catch (Exception e) {
            logger.error("Error al obtener cliente con ID: {}", id, e);
            ClientDTO.ApiResponse<ClientDTO.ClientResponse> errorResponse = 
                ClientDTO.ApiResponse.error("Error interno del servidor");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Actualizar un cliente existente.
     * 
     * @param id identificador del cliente
     * @param request nuevos datos del cliente
     * @return cliente actualizado
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Actualizar cliente", 
               description = "Actualiza los datos de un cliente existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cliente actualizado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
        @ApiResponse(responseCode = "404", description = "Cliente no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<ClientDTO.ApiResponse<ClientDTO.ClientResponse>> updateClient(
            @Parameter(description = "ID del cliente") @PathVariable Long id,
            @Valid @RequestBody ClientDTO.CreateClientRequest request) {
        
        logger.info("Solicitud para actualizar cliente con ID: {}", id);

        try {
            Client updatedClient = clientService.updateClient(id, request);
            ClientDTO.ApiResponse<ClientDTO.ClientResponse> response = 
                clientMapper.toApiResponse(updatedClient, "Cliente actualizado exitosamente");

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            logger.warn("Error al actualizar cliente con ID {}: {}", id, e.getMessage());
            ClientDTO.ApiResponse<ClientDTO.ClientResponse> errorResponse = 
                ClientDTO.ApiResponse.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);

        } catch (Exception e) {
            logger.error("Error interno al actualizar cliente con ID: {}", id, e);
            ClientDTO.ApiResponse<ClientDTO.ClientResponse> errorResponse = 
                ClientDTO.ApiResponse.error("Error interno del servidor");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Eliminar un cliente del sistema.
     * 
     * @param id identificador del cliente
     * @return confirmación de eliminación
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar cliente", 
               description = "Elimina un cliente del sistema (solo administradores)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cliente eliminado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Cliente no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<ClientDTO.ApiResponse<Void>> deleteClient(
            @Parameter(description = "ID del cliente") @PathVariable Long id) {
        
        logger.info("Solicitud para eliminar cliente con ID: {}", id);

        try {
            clientService.deleteClient(id);
            ClientDTO.ApiResponse<Void> response = 
                ClientDTO.ApiResponse.success(null, "Cliente eliminado exitosamente");

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            logger.warn("Cliente no encontrado para eliminación con ID: {}", id);
            ClientDTO.ApiResponse<Void> errorResponse = 
                ClientDTO.ApiResponse.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);

        } catch (Exception e) {
            logger.error("Error interno al eliminar cliente con ID: {}", id, e);
            ClientDTO.ApiResponse<Void> errorResponse = 
                ClientDTO.ApiResponse.error("Error interno del servidor");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Obtener métricas estadísticas de los clientes.
     * 
     * Calcula promedio de edad, desviación estándar y otras métricas
     * relevantes para el análisis de la base de clientes.
     * 
     * @return métricas estadísticas
     */
    @GetMapping("/metrics")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(summary = "Obtener métricas de clientes", 
               description = "Calcula estadísticas como promedio de edad y desviación estándar")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Métricas calculadas exitosamente"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<ClientDTO.ApiResponse<ClientDTO.ClientMetrics>> getClientMetrics() {
        
        logger.info("Solicitud para obtener métricas de clientes");

        try {
            ClientDTO.ClientMetrics metrics = clientService.getClientMetrics();
            ClientDTO.ApiResponse<ClientDTO.ClientMetrics> response = 
                clientMapper.toApiResponse(metrics, "Métricas calculadas exitosamente");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error al calcular métricas de clientes", e);
            ClientDTO.ApiResponse<ClientDTO.ClientMetrics> errorResponse = 
                ClientDTO.ApiResponse.error("Error interno del servidor");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Buscar clientes por nombre.
     * 
     * @param firstName nombre a buscar (búsqueda parcial)
     * @param minAge edad mínima
     * @param maxAge edad máxima
     * @return lista de clientes que coinciden
     */
    @GetMapping("/search")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(summary = "Buscar clientes por nombre", 
               description = "Busca clientes por nombre (búsqueda parcial e insensible a mayúsculas)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Búsqueda realizada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Parámetros de búsqueda inválidos"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<ClientDTO.ApiResponse<List<ClientDTO.ClientResponse>>> searchClients(
            @Parameter(description = "Nombre a buscar") @RequestParam(required = false) String firstName,
            @Parameter(description = "Edad mínima") @RequestParam(required = false) Integer minAge,
            @Parameter(description = "Edad máxima") @RequestParam(required = false) Integer maxAge) {
        
        logger.info("Solicitud de búsqueda - firstName: {}, minAge: {}, maxAge: {}", firstName, minAge, maxAge);

        try {
            String message;
            ClientDTO.ApiResponse<List<ClientDTO.ClientResponse>> response;

            if (firstName != null && !firstName.trim().isEmpty()) {
                List<Client> clientsFound = clientService.findClientsByFirstName(firstName.trim());
                message = String.format("Se encontraron %d clientes con el nombre '%s'", clientsFound.size(), firstName);
                response = ClientDTO.ApiResponse.success(clientMapper.toDtoList(clientsFound), message);
                
            } else if (minAge != null && maxAge != null) {
                if (minAge < 0 || maxAge < 0 || minAge > maxAge) {
                    ClientDTO.ApiResponse<List<ClientDTO.ClientResponse>> errorResponse = 
                        ClientDTO.ApiResponse.error("Rango de edad inválido");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
                }
                
                List<Client> clientsFound = clientService.findClientsByAgeRange(minAge, maxAge);
                message = String.format("Se encontraron %d clientes entre %d y %d años", 
                                      clientsFound.size(), minAge, maxAge);
                response = ClientDTO.ApiResponse.success(clientMapper.toDtoList(clientsFound), message);
                
            } else {
                ClientDTO.ApiResponse<List<ClientDTO.ClientResponse>> errorResponse = 
                    ClientDTO.ApiResponse.error("Debe proporcionar al menos un criterio de búsqueda");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error en búsqueda de clientes", e);
            ClientDTO.ApiResponse<List<ClientDTO.ClientResponse>> errorResponse = 
                ClientDTO.ApiResponse.error("Error interno del servidor");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Endpoint de salud para verificar el estado del servicio.
     * 
     * @return estado del servicio
     */
    @GetMapping("/health")
    @Operation(summary = "Verificar salud del servicio", 
               description = "Endpoint para verificar que el servicio está funcionando correctamente")
    @ApiResponse(responseCode = "200", description = "Servicio funcionando correctamente")
    public ResponseEntity<ClientDTO.ApiResponse<String>> healthCheck() {
        
        logger.debug("Verificación de salud del servicio");
        
        ClientDTO.ApiResponse<String> response = 
            ClientDTO.ApiResponse.success("OK", "Servicio de gestión de clientes funcionando correctamente");
        
        return ResponseEntity.ok(response);
    }
}