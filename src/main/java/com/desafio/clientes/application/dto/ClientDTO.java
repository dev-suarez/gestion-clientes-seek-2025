package com.desafio.clientes.application.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Objetos de Transferencia de Datos (DTOs) para el Cliente.
 * 
 * Implementa el patrón DTO para separar la representación interna
 * de los datos (entidades) de la representación externa (API).
 * Esto proporciona flexibilidad y seguridad en las interfaces.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClientDTO {

    /**
     * DTO para crear un nuevo cliente.
     * Contiene solo los campos necesarios para la creación.
     */
    @Getter
    @Setter
    public static class CreateClientRequest {
        
        @NotBlank(message = "El nombre es obligatorio")
        private String firstName;

        @NotBlank(message = "El apellido es obligatorio")
        private String lastName;

        @NotNull(message = "La edad es obligatoria")
        @Positive(message = "La edad debe ser positiva")
        private Integer age;

        @NotNull(message = "La fecha de nacimiento es obligatoria")
        @Past(message = "La fecha de nacimiento debe ser en el pasado")
        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate birthDate;

        // Constructor por defecto
        public CreateClientRequest() {}

        // Constructor con parámetros
        public CreateClientRequest(String firstName, String lastName, Integer age, LocalDate birthDate) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.age = age;
            this.birthDate = birthDate;
        }
    }

    /**
     * DTO para la respuesta de cliente completa.
     * Incluye todos los datos del cliente más campos calculados.
     */
    @Getter
    @Setter
    public static class ClientResponse {
        
        private Long id;
        private String firstName;
        private String lastName;
        private String fullName;
        private Integer age;
        private Integer currentAge;
        
        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate birthDate;
        
        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate lifeExpectancyDate;
        
        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate createdAt;
        
        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate updatedAt;

        // Constructor por defecto
        public ClientResponse() {}

        // Constructor completo
        public ClientResponse(Long id, String firstName, String lastName, String fullName, 
                            Integer age, Integer currentAge, LocalDate birthDate, 
                            LocalDate lifeExpectancyDate, LocalDate createdAt, LocalDate updatedAt) {
            this.id = id;
            this.firstName = firstName;
            this.lastName = lastName;
            this.fullName = fullName;
            this.age = age;
            this.currentAge = currentAge;
            this.birthDate = birthDate;
            this.lifeExpectancyDate = lifeExpectancyDate;
            this.createdAt = createdAt;
            this.updatedAt = updatedAt;
        }
    }

    /**
     * DTO para métricas estadísticas de clientes.
     * Proporciona información agregada sobre la base de clientes.
     */
    @Getter
    @Setter
    public static class ClientMetrics {
        
        private Long totalClients;
        private Double averageAge;
        private Double ageStandardDeviation;
        private Integer youngestAge;
        private Integer oldestAge;
        
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime calculatedAt;

        // Constructor por defecto
        public ClientMetrics() {
            this.calculatedAt = LocalDateTime.now();
        }

        // Constructor con parámetros
        public ClientMetrics(Long totalClients, Double averageAge, Double ageStandardDeviation, 
                           Integer youngestAge, Integer oldestAge) {
            this.totalClients = totalClients;
            this.averageAge = averageAge;
            this.ageStandardDeviation = ageStandardDeviation;
            this.youngestAge = youngestAge;
            this.oldestAge = oldestAge;
            this.calculatedAt = LocalDateTime.now();
        }
    }

    /**
     * DTO genérico para respuestas de API.
     * Implementa un formato estándar para todas las respuestas.
     */
    @Getter
    @Setter
    public static class ApiResponse<T> {
        
        private boolean success;
        private String message;
        private T data;
        private LocalDateTime timestamp;

        // Constructor por defecto
        public ApiResponse() {
            this.timestamp = LocalDateTime.now();
        }

        // Constructor para respuesta exitosa
        public ApiResponse(T data, String message) {
            this.success = true;
            this.message = message;
            this.data = data;
            this.timestamp = LocalDateTime.now();
        }

        // Constructor para respuesta de error
        public ApiResponse(String message) {
            this.success = false;
            this.message = message;
            this.timestamp = LocalDateTime.now();
        }

        // Métodos estáticos para crear respuestas
        public static <T> ApiResponse<T> success(T data, String message) {
            return new ApiResponse<>(data, message);
        }

        public static <T> ApiResponse<T> error(String message) {
            return new ApiResponse<>(message);
        }
    }
}