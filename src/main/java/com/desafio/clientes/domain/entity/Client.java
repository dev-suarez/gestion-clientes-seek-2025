package com.desafio.clientes.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;
import java.time.Period;

/**
 * Entidad que representa un cliente en el sistema.
 * 
 * Implementa el patrón Entity del Domain-Driven Design,
 * encapsulando tanto los datos como la lógica de negocio
 * relacionada con el cliente.
 */
@Entity
@Table(name = "clients")
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @NotBlank(message = "El apellido es obligatorio")
    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @NotNull(message = "La edad es obligatoria")
    @Positive(message = "La edad debe ser positiva")
    @Column(name = "age", nullable = false)
    private Integer age;

    @NotNull(message = "La fecha de nacimiento es obligatoria")
    @Past(message = "La fecha de nacimiento debe ser en el pasado")
    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDate createdAt;

    @Column(name = "updated_at")
    private LocalDate updatedAt;

    // Constructor por defecto requerido por JPA
    public Client() {
    }

    // Constructor para creación de cliente (patrón Builder se usará en el servicio)
    public Client(String firstName, String lastName, Integer age, LocalDate birthDate) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.birthDate = birthDate;
        this.createdAt = LocalDate.now();
        this.updatedAt = LocalDate.now();
    }

    /**
     * Calcula la edad actual basada en la fecha de nacimiento.
     * Implementa lógica de negocio dentro de la entidad.
     * 
     * @return edad calculada
     */
    public int calculateCurrentAge() {
        return Period.between(this.birthDate, LocalDate.now()).getYears();
    }

    /**
     * Calcula la fecha estimada de esperanza de vida basada en estadísticas
     * generales.
     * Implementa lógica de negocio para el cálculo derivado requerido.
     * 
     * Utiliza una esperanza de vida promedio de 78 años (puede ser configurable).
     * 
     * @return fecha estimada de esperanza de vida
     */
    public LocalDate calculateLifeExpectancyDate() {
        final int AVERAGE_LIFE_EXPECTANCY = 78;
        return this.birthDate.plusYears(AVERAGE_LIFE_EXPECTANCY);
    }

    /**
     * Obtiene el nombre completo del cliente.
     * 
     * @return nombre completo concatenado
     */
    public String getFullName() {
        return this.firstName + " " + this.lastName;
    }

    /**
     * Actualiza la fecha de modificación.
     * Método de utilidad para mantener la auditoría.
     */
    public void updateTimestamp() {
        this.updatedAt = LocalDate.now();
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
        updateTimestamp();
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
        updateTimestamp();
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
        updateTimestamp();
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
        updateTimestamp();
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

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Client client = (Client) o;
        return id != null && id.equals(client.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Client{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", age=" + age +
                ", birthDate=" + birthDate +
                '}';
    }
}