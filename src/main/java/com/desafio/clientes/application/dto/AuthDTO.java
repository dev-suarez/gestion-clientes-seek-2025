package com.desafio.clientes.application.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Objetos de Transferencia de Datos (DTOs) para Autenticación.
 * 
 * Contiene todos los DTOs relacionados con operaciones de autenticación,
 * login, tokens JWT y verificación de credenciales.
 * 
 * @author Sistema de Desarrollo
 * @version 1.0.0
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AuthDTO {

    /**
     * DTO para solicitud de login.
     * Contiene las credenciales del usuario para autenticación.
     */
    @Getter
    @Setter
    public static class LoginRequest {

        @NotBlank(message = "El nombre de usuario es obligatorio")
        private String username;

        @NotBlank(message = "La contraseña es obligatoria")
        private String password;

        // Constructor por defecto
        public LoginRequest() {
        }

        // Constructor con parámetros
        public LoginRequest(String username, String password) {
            this.username = username;
            this.password = password;
        }

        @Override
        public String toString() {
            return "LoginRequest{" +
                    "username='" + username + '\'' +
                    ", password='[PROTECTED]'" +
                    '}';
        }
    }

    /**
     * DTO para respuesta de login exitoso.
     * Contiene el token JWT y información del usuario autenticado.
     */
    @Getter
    @Setter
    public static class LoginResponse {

        private String token;
        private String tokenType;
        private String username;
        private String roles;
        private long expiresIn;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime issuedAt;

        // Constructor por defecto
        public LoginResponse() {
            this.tokenType = "Bearer";
            this.issuedAt = LocalDateTime.now();
        }

        // Constructor con parámetros
        public LoginResponse(String token, String username, String roles, long expiresIn) {
            this();
            this.token = token;
            this.username = username;
            this.roles = roles;
            this.expiresIn = expiresIn;
        }
    }

    /**
     * DTO para solicitud de verificación de token.
     * Contiene el token JWT a verificar.
     */
    @Getter
    @Setter
    public static class TokenVerificationRequest {

        @NotBlank(message = "El token es obligatorio")
        private String token;

        // Constructor por defecto
        public TokenVerificationRequest() {
        }

        // Constructor con parámetros
        public TokenVerificationRequest(String token) {
            this.token = token;
        }

        @Override
        public String toString() {
            return "TokenVerificationRequest{" +
                    "token='" + (token != null ? token.substring(0, Math.min(10, token.length())) + "..." : "null")
                    + '\'' +
                    '}';
        }
    }

    /**
     * DTO para respuesta de verificación de token.
     * Contiene el resultado de la validación del token.
     */
    @Getter
    @Setter
    public static class TokenVerificationResponse {

        private boolean valid;
        private String username;
        private String roles;
        private Long remainingTime;
        private String status;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime verifiedAt;

        // Constructor por defecto
        public TokenVerificationResponse() {
            this.verifiedAt = LocalDateTime.now();
        }

        // Constructor con parámetros básicos
        public TokenVerificationResponse(boolean valid, String username, String roles) {
            this();
            this.valid = valid;
            this.username = username;
            this.roles = roles;
            this.status = valid ? "VALID" : "INVALID";
        }

        // Constructor completo
        public TokenVerificationResponse(boolean valid, String username, String roles,
                Long remainingTime, String status) {
            this();
            this.valid = valid;
            this.username = username;
            this.roles = roles;
            this.remainingTime = remainingTime;
            this.status = status;
        }
    }

    /**
     * DTO para información del usuario autenticado.
     * Contiene detalles del usuario sin información sensible.
     */
    @Getter
    @Setter
    public static class UserInfo {

        private String username;
        private String roles;
        private boolean active;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime lastLogin;

        // Constructor por defecto
        public UserInfo() {
        }

        // Constructor con parámetros
        public UserInfo(String username, String roles, boolean active, LocalDateTime lastLogin) {
            this.username = username;
            this.roles = roles;
            this.active = active;
            this.lastLogin = lastLogin;
        }
    }
}