package com.desafio.clientes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Aplicación principal del microservicio de gestión de clientes.
 * 
 * Este microservicio implementa un sistema completo de gestión de clientes
 * con autenticación JWT, cálculos estadísticos y operaciones CRUD.
 * 
 * Características principales:
 * - RESTful API para gestión de clientes
 * - Autenticación y autorización con JWT
 * - Cálculos estadísticos (promedio, desviación estándar)
 * - Estimación de esperanza de vida
 * - Migración de base de datos con Flyway
 * 
 * @author Sergio Suarez
 * @version 1.0.0
 */
@SpringBootApplication
public class ClientesApplication {

	public static void main(String[] args) {
		SpringApplication.run(ClientesApplication.class, args);
	}

}
