package com.desafio.clientes.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import com.desafio.clientes.application.dto.AuthDTO;
import com.desafio.clientes.application.dto.ClientDTO;
import com.desafio.clientes.infraestructure.security.JwtUtil;

/**
 * Controlador de autenticación para generar tokens JWT.
 * 
 * Proporciona endpoints para autenticación de usuarios y
 * generación de tokens de acceso. Utiliza DTOs separados
 * para mantener la separación de responsabilidades.
 * 
 * @author Sistema de Desarrollo
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Autenticación", description = "API para autenticación y generación de tokens")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Autentica un usuario y genera un token JWT.
     * 
     * @param loginRequest credenciales del usuario
     * @return token JWT y información del usuario
     */
    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión", description = "Autentica un usuario y devuelve un token JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Autenticación exitosa"),
            @ApiResponse(responseCode = "401", description = "Credenciales inválidas"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    })
    public ResponseEntity<ClientDTO.ApiResponse<AuthDTO.LoginResponse>> login(
            @Valid @RequestBody AuthDTO.LoginRequest loginRequest) {

        logger.info("Intento de login para usuario: {}", loginRequest.getUsername());

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()));

            String jwt = jwtUtil.generateJwtToken(authentication);

            // Determinar roles basado en el usuario
            String roles = determineUserRoles(loginRequest.getUsername());

            AuthDTO.LoginResponse response = new AuthDTO.LoginResponse(
                    jwt,
                    loginRequest.getUsername(),
                    roles,
                    jwtUtil.getJwtExpirationMs());

            logger.info("Login exitoso para usuario: {}", loginRequest.getUsername());

            ClientDTO.ApiResponse<AuthDTO.LoginResponse> apiResponse = ClientDTO.ApiResponse.success(response,
                    "Login exitoso");

            return ResponseEntity.ok(apiResponse);

        } catch (AuthenticationException e) {
            logger.warn("Fallo de autenticación para usuario: {}", loginRequest.getUsername());

            ClientDTO.ApiResponse<AuthDTO.LoginResponse> errorResponse = ClientDTO.ApiResponse
                    .error("Credenciales inválidas");

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }

    /**
     * Genera un token JWT para testing con credenciales predefinidas.
     * 
     * @return token JWT de prueba
     */
    @PostMapping("/token")
    @Operation(summary = "Obtener token de prueba", description = "Genera un token JWT para testing sin autenticación")
    @ApiResponse(responseCode = "200", description = "Token generado exitosamente")
    public ResponseEntity<ClientDTO.ApiResponse<AuthDTO.LoginResponse>> getTestToken() {

        logger.info("Generando token de prueba");

        // Generar token con usuario de prueba
        String jwt = jwtUtil.generateJwtToken("testuser", "USER,ADMIN");

        AuthDTO.LoginResponse response = new AuthDTO.LoginResponse(
                jwt,
                "testuser",
                "USER,ADMIN",
                jwtUtil.getJwtExpirationMs());

        ClientDTO.ApiResponse<AuthDTO.LoginResponse> apiResponse = ClientDTO.ApiResponse.success(response,
                "Token de prueba generado");

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Verifica si un token JWT es válido.
     * 
     * @param tokenRequest token a verificar
     * @return estado de validez del token
     */
    @PostMapping("/verify")
    @Operation(summary = "Verificar token", description = "Verifica si un token JWT es válido")
    public ResponseEntity<ClientDTO.ApiResponse<AuthDTO.TokenVerificationResponse>> verifyToken(
            @Valid @RequestBody AuthDTO.TokenVerificationRequest tokenRequest) {

        logger.info("Verificando token: {}", tokenRequest);

        boolean isValid = jwtUtil.validateJwtToken(tokenRequest.getToken());
        String username = null;
        String roles = null;
        Long remainingTime = null;

        if (isValid) {
            username = jwtUtil.getUsernameFromJwtToken(tokenRequest.getToken());
            roles = jwtUtil.getRolesFromJwtToken(tokenRequest.getToken());
            remainingTime = jwtUtil.getTokenRemainingTime(tokenRequest.getToken());
        }

        AuthDTO.TokenVerificationResponse response = new AuthDTO.TokenVerificationResponse(
                isValid,
                username,
                roles,
                remainingTime,
                isValid ? "VALID" : "INVALID");

        ClientDTO.ApiResponse<AuthDTO.TokenVerificationResponse> apiResponse = ClientDTO.ApiResponse.success(response,
                isValid ? "Token válido" : "Token inválido");

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Obtiene información del usuario actual basada en el token JWT.
     * 
     * @param authHeader header de autorización con token JWT
     * @return información del usuario
     */
    @GetMapping("/me")
    @Operation(summary = "Obtener información del usuario actual", description = "Extrae información del usuario desde el token JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Información obtenida exitosamente"),
            @ApiResponse(responseCode = "401", description = "Token inválido o expirado")
    })
    public ResponseEntity<ClientDTO.ApiResponse<AuthDTO.UserInfo>> getCurrentUser(
            @RequestHeader("Authorization") String authHeader) {

        try {
            String token = authHeader.replace("Bearer ", "");

            if (jwtUtil.validateJwtToken(token)) {
                String username = jwtUtil.getUsernameFromJwtToken(token);
                String roles = jwtUtil.getRolesFromJwtToken(token);

                AuthDTO.UserInfo userInfo = new AuthDTO.UserInfo(
                        username,
                        roles,
                        true,
                        java.time.LocalDateTime.now());

                ClientDTO.ApiResponse<AuthDTO.UserInfo> apiResponse = ClientDTO.ApiResponse.success(userInfo,
                        "Información de usuario obtenida");

                return ResponseEntity.ok(apiResponse);
            } else {
                ClientDTO.ApiResponse<AuthDTO.UserInfo> errorResponse = ClientDTO.ApiResponse.error("Token inválido");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
            }
        } catch (Exception e) {
            logger.error("Error al obtener información del usuario", e);
            ClientDTO.ApiResponse<AuthDTO.UserInfo> errorResponse = ClientDTO.ApiResponse
                    .error("Error al procesar el token");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }

    /**
     * Determina los roles de un usuario basado en su nombre.
     * En una implementación real, esto consultaría la base de datos.
     * 
     * @param username nombre de usuario
     * @return roles del usuario
     */
    private String determineUserRoles(String username) {
        // Mapeo de usuarios a roles (debe coincidir con SecurityConfig)
        return switch (username.toLowerCase()) {
            case "admin" -> "USER,ADMIN";
            case "user" -> "USER";
            case "testuser" -> "USER,ADMIN"; // Para el token de prueba
            default -> "USER"; // Rol por defecto
        };
    }

}