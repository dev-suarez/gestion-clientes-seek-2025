package com.desafio.clientes.application.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

/**
 * Objetos de Transferencia de Datos (DTOs) para el Cliente.
 * 
 * Implementa el patrón DTO para separar la representación interna
 * de los datos (entidades) de la representación externa (API).
 * Esto proporciona flexibilidad y seguridad en las interfaces.
 */
public class ClientDTO {

    /**
     * DTO para crear un nuevo cliente.
     * Contiene solo los campos necesarios para la creación.
     */
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

        // Getters y Setters
        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public Integer getAge() {
            return age;
        }

        public void setAge(Integer age) {
            this.age = age;
        }

        public LocalDate getBirthDate() {
            return birthDate;
        }

        public void setBirthDate(LocalDate birthDate) {
            this.birthDate = birthDate;
        }
    }

    /**
     * DTO para la respuesta de cliente completa.
     * Incluye todos los datos del cliente más campos calculados.
     */
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

        // Getters y Setters
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }

        public Integer getAge() {
            return age;
        }

        public void setAge(Integer age) {
            this.age = age;
        }

        public Integer getCurrentAge() {
            return currentAge;
        }

        public void setCurrentAge(Integer currentAge) {
            this.currentAge = currentAge;
        }

        public LocalDate getBirthDate() {
            return birthDate;
        }

        public void setBirthDate(LocalDate birthDate) {
            this.birthDate = birthDate;
        }

        public LocalDate getLifeExpectancyDate() {
            return lifeExpectancyDate;
        }

        public void setLifeExpectancyDate(LocalDate lifeExpectancyDate) {
            this.lifeExpectancyDate = lifeExpectancyDate;
        }

        public LocalDate getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(LocalDate createdAt) {
            this.createdAt = createdAt;
        }

        public LocalDate getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(LocalDate updatedAt) {
            this.updatedAt = updatedAt;
        }
    }

    /**
     * DTO para métricas estadísticas de clientes.
     * Proporciona información agregada sobre la base de clientes.
     */
    public static class ClientMetrics {
        
        private Long totalClients;
        private Double averageAge;
        private Double ageStandardDeviation;
        private Integer youngestAge;
        private Integer oldestAge;
        
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDate calculatedAt;

        // Constructor por defecto
        public ClientMetrics() {
            this.calculatedAt = LocalDate.now();
        }

        // Constructor con parámetros
        public ClientMetrics(Long totalClients, Double averageAge, Double ageStandardDeviation, 
                           Integer youngestAge, Integer oldestAge) {
            this.totalClients = totalClients;
            this.averageAge = averageAge;
            this.ageStandardDeviation = ageStandardDeviation;
            this.youngestAge = youngestAge;
            this.oldestAge = oldestAge;
            this.calculatedAt = LocalDate.now();
        }

        // Getters y Setters
        public Long getTotalClients() {
            return totalClients;
        }

        public void setTotalClients(Long totalClients) {
            this.totalClients = totalClients;
        }

        public Double getAverageAge() {
            return averageAge;
        }

        public void setAverageAge(Double averageAge) {
            this.averageAge = averageAge;
        }

        public Double getAgeStandardDeviation() {
            return ageStandardDeviation;
        }

        public void setAgeStandardDeviation(Double ageStandardDeviation) {
            this.ageStandardDeviation = ageStandardDeviation;
        }

        public Integer getYoungestAge() {
            return youngestAge;
        }

        public void setYoungestAge(Integer youngestAge) {
            this.youngestAge = youngestAge;
        }

        public Integer getOldestAge() {
            return oldestAge;
        }

        public void setOldestAge(Integer oldestAge) {
            this.oldestAge = oldestAge;
        }

        public LocalDate getCalculatedAt() {
            return calculatedAt;
        }

        public void setCalculatedAt(LocalDate calculatedAt) {
            this.calculatedAt = calculatedAt;
        }
    }

    /**
     * DTO genérico para respuestas de API.
     * Implementa un formato estándar para todas las respuestas.
     */
    public static class ApiResponse<T> {
        
        private boolean success;
        private String message;
        private T data;
        private LocalDate timestamp;

        // Constructor por defecto
        public ApiResponse() {
            this.timestamp = LocalDate.now();
        }

        // Constructor para respuesta exitosa
        public ApiResponse(T data, String message) {
            this.success = true;
            this.message = message;
            this.data = data;
            this.timestamp = LocalDate.now();
        }

        // Constructor para respuesta de error
        public ApiResponse(String message) {
            this.success = false;
            this.message = message;
            this.timestamp = LocalDate.now();
        }

        // Métodos estáticos para crear respuestas
        public static <T> ApiResponse<T> success(T data, String message) {
            return new ApiResponse<>(data, message);
        }

        public static <T> ApiResponse<T> error(String message) {
            return new ApiResponse<>(message);
        }

        // Getters y Setters
        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public T getData() {
            return data;
        }

        public void setData(T data) {
            this.data = data;
        }

        public LocalDate getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(LocalDate timestamp) {
            this.timestamp = timestamp;
        }
    }
}