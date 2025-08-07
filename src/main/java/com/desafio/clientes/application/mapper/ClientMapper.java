package com.desafio.clientes.application.mapper;

import org.springframework.stereotype.Component;

import com.desafio.clientes.domain.entity.Client;
import com.desafio.clientes.application.dto.ClientDTO;

import java.util.List;

/**
 * Mapper para conversiones entre entidades y DTOs.
 * 
 * Implementa el patrón Mapper para separar la lógica de conversión
 * y mantener limpia la separación entre capas. Proporciona métodos
 * para convertir entre diferentes representaciones de los datos.
 * 
 * @author Sistema de Desarrollo
 * @version 1.0.0
 */
@Component
public class ClientMapper {

    /**
     * Convierte un CreateClientRequest DTO a entidad Client.
     * 
     * @param request DTO de creación de cliente
     * @return entidad Client
     */
    public Client toEntity(ClientDTO.CreateClientRequest request) {
        if (request == null) {
            return null;
        }

        return new Client(
                request.getFirstName(),
                request.getLastName(),
                request.getAge(),
                request.getBirthDate());
    }

    /**
     * Convierte una entidad Client a ClientResponse DTO.
     * Incluye cálculos derivados como la esperanza de vida.
     * 
     * @param client entidad Client
     * @return DTO de respuesta de cliente
     */
    public ClientDTO.ClientResponse toDto(Client client) {
        if (client == null) {
            return null;
        }

        return new ClientDTO.ClientResponse(
                client.getId(),
                client.getFirstName(),
                client.getLastName(),
                client.getFullName(),
                client.getAge(),
                client.calculateCurrentAge(),
                client.getBirthDate(),
                client.calculateLifeExpectancyDate(),
                client.getCreatedAt(),
                client.getUpdatedAt());
    }

    /**
     * Convierte una lista de entidades Client a lista de DTOs.
     * 
     * @param clients lista de entidades
     * @return lista de DTOs
     */
    public List<ClientDTO.ClientResponse> toDtoList(List<Client> clients) {
        if (clients == null) {
            return List.of();
        }

        return clients.stream()
                .map(this::toDto)
                .toList();
    }

    /**
     * Actualiza una entidad Client existente con datos de un DTO.
     * Útil para operaciones de actualización parcial.
     * 
     * @param client  entidad existente
     * @param request DTO con datos actualizados
     * @return entidad actualizada
     */
    public Client updateEntity(Client client, ClientDTO.CreateClientRequest request) {
        if (client == null || request == null) {
            return client;
        }

        if (request.getFirstName() != null) {
            client.setFirstName(request.getFirstName());
        }

        if (request.getLastName() != null) {
            client.setLastName(request.getLastName());
        }

        if (request.getAge() != null) {
            client.setAge(request.getAge());
        }

        if (request.getBirthDate() != null) {
            client.setBirthDate(request.getBirthDate());
        }

        return client;
    }

    /**
     * Crea un DTO de respuesta de API exitosa con datos de cliente.
     * 
     * @param client  entidad Client
     * @param message mensaje de respuesta
     * @return ApiResponse con datos del cliente
     */
    public ClientDTO.ApiResponse<ClientDTO.ClientResponse> toApiResponse(Client client, String message) {
        return ClientDTO.ApiResponse.success(toDto(client), message);
    }

    /**
     * Crea un DTO de respuesta de API exitosa con ClientResponse DTO.
     * 
     * @param clientResponse DTO de respuesta de cliente
     * @param message        mensaje de respuesta
     * @return ApiResponse con datos del cliente
     */
    public ClientDTO.ApiResponse<ClientDTO.ClientResponse> toApiResponse(ClientDTO.ClientResponse clientResponse,
            String message) {
        return ClientDTO.ApiResponse.success(clientResponse, message);
    }

    /**
     * Crea un DTO de respuesta de API exitosa con lista de clientes.
     * 
     * @param clients lista de entidades Client
     * @param message mensaje de respuesta
     * @return ApiResponse con lista de clientes
     */
    public ClientDTO.ApiResponse<List<ClientDTO.ClientResponse>> toApiResponse(List<Client> clients, String message) {
        return ClientDTO.ApiResponse.success(toDtoList(clients), message);
    }

    /**
     * Crea un DTO de respuesta de API con métricas.
     * 
     * @param metrics métricas calculadas
     * @param message mensaje de respuesta
     * @return ApiResponse con métricas
     */
    public ClientDTO.ApiResponse<ClientDTO.ClientMetrics> toApiResponse(ClientDTO.ClientMetrics metrics,
            String message) {
        return ClientDTO.ApiResponse.success(metrics, message);
    }
}