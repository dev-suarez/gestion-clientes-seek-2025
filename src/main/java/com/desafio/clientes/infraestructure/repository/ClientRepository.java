package com.desafio.clientes.infraestructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.desafio.clientes.domain.entity.Client;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Client.
 * 
 * Implementa el patrón Repository proporcionando una abstracción
 * para las operaciones de acceso a datos. Extiende JpaRepository
 * para obtener operaciones CRUD básicas y define consultas personalizadas
 * para los cálculos estadísticos requeridos.
 * 
 * @author Sistema de Desarrollo
 * @version 1.0.0
 */
@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    /**
     * Busca clientes por nombre (first_name).
     * Útil para búsquedas y validaciones.
     * 
     * @param firstName nombre a buscar
     * @return lista de clientes con ese nombre
     */
    List<Client> findByFirstNameContainingIgnoreCase(String firstName);

    /**
     * Busca un cliente por nombre y apellido exactos.
     * Útil para evitar duplicados.
     * 
     * @param firstName nombre exacto
     * @param lastName  apellido exacto
     * @return cliente si existe
     */
    Optional<Client> findByFirstNameAndLastName(String firstName, String lastName);

    /**
     * Calcula el promedio de edad de todos los clientes.
     * Query personalizada para métricas estadísticas.
     * 
     * @return promedio de edad o null si no hay clientes
     */
    @Query("SELECT AVG(c.age) FROM Client c")
    Double calculateAverageAge();

    /**
     * Obtiene todas las edades para cálculos estadísticos.
     * Necesario para calcular la desviación estándar.
     * 
     * @return lista de todas las edades
     */
    @Query("SELECT c.age FROM Client c")
    List<Integer> getAllAges();

    /**
     * Cuenta el total de clientes registrados.
     * Útil para métricas generales.
     * 
     * @return número total de clientes
     */
    @Query("SELECT COUNT(c) FROM Client c")
    Long countTotalClients();

    /**
     * Busca clientes en un rango de edad específico.
     * Útil para análisis demográficos.
     * 
     * @param minAge edad mínima
     * @param maxAge edad máxima
     * @return lista de clientes en el rango
     */
    @Query("SELECT c FROM Client c WHERE c.age BETWEEN :minAge AND :maxAge ORDER BY c.age")
    List<Client> findByAgeRange(Integer minAge, Integer maxAge);
}