package com.desafio.clientes.application.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.desafio.clientes.domain.entity.Client;
import com.desafio.clientes.application.dto.ClientDTO;
import com.desafio.clientes.infraestructure.repository.ClientRepository;
import com.desafio.clientes.application.mapper.ClientMapper;

import java.util.List;
import java.util.Optional;

/**
 * Servicio principal para la gestión de clientes.
 * 
 * Implementa la lógica de negocio para las operaciones CRUD
 * y los cálculos estadísticos requeridos. Utiliza el patrón
 * Service Layer para coordinar entre controladores, repositorios
 * y otros servicios especializados.
 * 
 * @author Sistema de Desarrollo
 * @version 1.0.0
 */
@Service
@Transactional
public class ClientService {

    private static final Logger logger = LoggerFactory.getLogger(ClientService.class);

    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;
    private final StatisticsService statisticsService;

    public ClientService(ClientRepository clientRepository,
            ClientMapper clientMapper,
            StatisticsService statisticsService) {
        this.clientRepository = clientRepository;
        this.clientMapper = clientMapper;
        this.statisticsService = statisticsService;
    }

    /**
     * Crea un nuevo cliente en el sistema.
     * 
     * Implementa validaciones de negocio y utiliza el patrón Builder
     * implícitamente a través del mapper para construir la entidad.
     * 
     * @param request datos del cliente a crear
     * @return cliente creado como entidad
     * @throws IllegalArgumentException si los datos son inválidos
     */
    public Client createClient(ClientDTO.CreateClientRequest request) {
        logger.info("Creando nuevo cliente: {} {}", request.getFirstName(), request.getLastName());

        // Validar que no existe un cliente con el mismo nombre y apellido
        Optional<Client> existingClient = clientRepository
                .findByFirstNameAndLastName(request.getFirstName(), request.getLastName());

        if (existingClient.isPresent()) {
            logger.warn("Intento de crear cliente duplicado: {} {}",
                    request.getFirstName(), request.getLastName());
            throw new IllegalArgumentException(
                    "Ya existe un cliente con el nombre: " + request.getFirstName() + " " + request.getLastName());
        }

        // Validar coherencia entre edad y fecha de nacimiento
        Client tempClient = clientMapper.toEntity(request);
        int calculatedAge = tempClient.calculateCurrentAge();

        if (Math.abs(calculatedAge - request.getAge()) > 1) {
            logger.warn("Inconsistencia entre edad ({}) y fecha de nacimiento ({}) para cliente: {} {}",
                    request.getAge(), request.getBirthDate(), request.getFirstName(), request.getLastName());
            throw new IllegalArgumentException(
                    "La edad proporcionada no es coherente con la fecha de nacimiento");
        }

        try {
            Client client = clientMapper.toEntity(request);
            Client savedClient = clientRepository.save(client);

            logger.info("Cliente creado exitosamente con ID: {}", savedClient.getId());
            return savedClient;

        } catch (Exception e) {
            logger.error("Error al crear cliente: {} {}", request.getFirstName(), request.getLastName(), e);
            throw new RuntimeException("Error al crear el cliente: " + e.getMessage(), e);
        }
    }

    /**
     * Obtiene un cliente por su ID.
     * 
     * @param id identificador del cliente
     * @return cliente encontrado como entidad
     * @throws IllegalArgumentException si el cliente no existe
     */
    @Transactional(readOnly = true)
    public Client getClientById(Long id) {
        logger.info("Buscando cliente con ID: {}", id);

        Client client = clientRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Cliente no encontrado con ID: {}", id);
                    return new IllegalArgumentException("Cliente no encontrado con ID: " + id);
                });

        return client;
    }

    /**
     * Obtiene todos los clientes registrados con sus datos completos
     * y cálculos derivados (esperanza de vida).
     * 
     * @return lista de todos los clientes como entidades
     */
    @Transactional(readOnly = true)
    public List<Client> getAllClients() {
        logger.info("Obteniendo todos los clientes");

        List<Client> clients = clientRepository.findAll();
        logger.info("Se encontraron {} clientes", clients.size());

        return clients;
    }

    /**
     * Actualiza un cliente existente.
     * 
     * @param id      identificador del cliente a actualizar
     * @param request nuevos datos del cliente
     * @return cliente actualizado como entidad
     * @throws IllegalArgumentException si el cliente no existe
     */
    public Client updateClient(Long id, ClientDTO.CreateClientRequest request) {
        logger.info("Actualizando cliente con ID: {}", id);

        Client existingClient = clientRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Cliente no encontrado para actualización con ID: {}", id);
                    return new IllegalArgumentException("Cliente no encontrado con ID: " + id);
                });

        try {
            Client updatedClient = clientMapper.updateEntity(existingClient, request);
            Client savedClient = clientRepository.save(updatedClient);

            logger.info("Cliente actualizado exitosamente con ID: {}", savedClient.getId());
            return savedClient;

        } catch (Exception e) {
            logger.error("Error al actualizar cliente con ID: {}", id, e);
            throw new RuntimeException("Error al actualizar el cliente: " + e.getMessage(), e);
        }
    }

    /**
     * Elimina un cliente del sistema.
     * 
     * @param id identificador del cliente a eliminar
     * @throws IllegalArgumentException si el cliente no existe
     */
    public void deleteClient(Long id) {
        logger.info("Eliminando cliente con ID: {}", id);

        if (!clientRepository.existsById(id)) {
            logger.warn("Cliente no encontrado para eliminación con ID: {}", id);
            throw new IllegalArgumentException("Cliente no encontrado con ID: " + id);
        }

        try {
            clientRepository.deleteById(id);
            logger.info("Cliente eliminado exitosamente con ID: {}", id);

        } catch (Exception e) {
            logger.error("Error al eliminar cliente con ID: {}", id, e);
            throw new RuntimeException("Error al eliminar el cliente: " + e.getMessage(), e);
        }
    }

    /**
     * Calcula y obtiene métricas estadísticas sobre los clientes.
     * 
     * Incluye promedio de edad, desviación estándar, y otros cálculos
     * estadísticos relevantes para el análisis de la base de clientes.
     * 
     * @return métricas estadísticas como DTO
     */
    @Transactional(readOnly = true)
    public ClientDTO.ClientMetrics getClientMetrics() {
        logger.info("Calculando métricas de clientes");

        try {
            // Obtener datos básicos
            Long totalClients = clientRepository.countTotalClients();
            Double averageAge = clientRepository.calculateAverageAge();
            List<Integer> allAges = clientRepository.getAllAges();

            // Calcular estadísticas avanzadas
            Double standardDeviation = statisticsService.calculateStandardDeviation(allAges);
            Integer youngestAge = statisticsService.findMinimum(allAges);
            Integer oldestAge = statisticsService.findMaximum(allAges);

            // Manejar caso sin clientes
            if (totalClients == 0) {
                logger.info("No hay clientes registrados para calcular métricas");
                return new ClientDTO.ClientMetrics(0L, 0.0, 0.0, 0, 0);
            }

            ClientDTO.ClientMetrics metrics = new ClientDTO.ClientMetrics(
                    totalClients,
                    averageAge != null ? averageAge : 0.0,
                    standardDeviation,
                    youngestAge,
                    oldestAge);

            logger.info("Métricas calculadas - Total: {}, Promedio edad: {:.2f}, Desv. estándar: {:.2f}",
                    totalClients, averageAge, standardDeviation);

            return metrics;

        } catch (Exception e) {
            logger.error("Error al calcular métricas de clientes", e);
            throw new RuntimeException("Error al calcular las métricas: " + e.getMessage(), e);
        }
    }

    /**
     * Busca clientes por nombre (parcial, insensible a mayúsculas).
     * 
     * @param firstName nombre a buscar
     * @return lista de clientes que coinciden
     */
    @Transactional(readOnly = true)
    public List<Client> findClientsByFirstName(String firstName) {
        logger.info("Buscando clientes por nombre: {}", firstName);

        List<Client> clients = clientRepository.findByFirstNameContainingIgnoreCase(firstName);
        logger.info("Se encontraron {} clientes con el nombre: {}", clients.size(), firstName);

        return clients;
    }

    /**
     * Busca clientes en un rango de edad específico.
     * 
     * @param minAge edad mínima
     * @param maxAge edad máxima
     * @return lista de clientes en el rango
     */
    @Transactional(readOnly = true)
    public List<Client> findClientsByAgeRange(Integer minAge, Integer maxAge) {
        logger.info("Buscando clientes en rango de edad: {} - {}", minAge, maxAge);

        if (minAge > maxAge) {
            throw new IllegalArgumentException("La edad mínima no puede ser mayor que la edad máxima");
        }

        List<Client> clients = clientRepository.findByAgeRange(minAge, maxAge);
        logger.info("Se encontraron {} clientes en el rango de edad: {} - {}",
                clients.size(), minAge, maxAge);

        return clients;
    }
}