package com.desafio.clientes.infraestructure.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Punto de entrada personalizado para manejar errores de autenticación JWT.
 * 
 * Implementa AuthenticationEntryPoint para proporcionar respuestas
 * personalizadas cuando falla la autenticación. Esto mejora la
 * experiencia del usuario proporcionando mensajes de error claros
 * y estructurados.
 * 
 * @author Sistema de Desarrollo
 * @version 1.0.0
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationEntryPoint.class);

    /**
     * Método invocado cuando falla la autenticación.
     * 
     * @param request       petición HTTP
     * @param response      respuesta HTTP
     * @param authException excepción de autenticación
     * @throws IOException si hay errores de E/O
     */
    @Override
    public void commence(HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException) throws IOException {

        logger.error("Error de autenticación no autorizada: {}", authException.getMessage());

        // Configurar tipo de contenido y código de estado
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        // Crear respuesta de error estructurada
        Map<String, Object> errorResponse = createErrorResponse(request, authException);

        // Escribir respuesta JSON
        final ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getOutputStream(), errorResponse);
    }

    /**
     * Crea una respuesta de error estructurada con información detallada.
     * 
     * @param request       petición HTTP
     * @param authException excepción de autenticación
     * @return mapa con datos del error
     */
    private Map<String, Object> createErrorResponse(HttpServletRequest request,
            AuthenticationException authException) {
        Map<String, Object> errorResponse = new HashMap<>();

        // Información básica del error
        errorResponse.put("success", false);
        errorResponse.put("timestamp", LocalDateTime.now().toString());
        errorResponse.put("status", HttpServletResponse.SC_UNAUTHORIZED);
        errorResponse.put("error", "Unauthorized");
        errorResponse.put("message", determineErrorMessage(request, authException));
        errorResponse.put("path", request.getRequestURI());

        // Información adicional para debugging (solo en desarrollo)
        if (isDevelopmentMode()) {
            errorResponse.put("method", request.getMethod());
            errorResponse.put("headers", getRequestHeaders(request));
            errorResponse.put("exceptionType", authException.getClass().getSimpleName());
        }

        return errorResponse;
    }

    /**
     * Determina el mensaje de error apropiado basado en la petición y excepción.
     * 
     * @param request       petición HTTP
     * @param authException excepción de autenticación
     * @return mensaje de error apropiado
     */
    private String determineErrorMessage(HttpServletRequest request,
            AuthenticationException authException) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null) {
            return "Token de acceso requerido. Incluya el header 'Authorization: Bearer <token>'";
        }

        if (!authHeader.startsWith("Bearer ")) {
            return "Formato de token inválido. Use 'Authorization: Bearer <token>'";
        }

        // Token presente pero inválido
        return "Token de acceso inválido o expirado. Por favor, autentíquese nuevamente";
    }

    /**
     * Obtiene los headers de la petición para debugging.
     * 
     * @param request petición HTTP
     * @return mapa con headers relevantes
     */
    private Map<String, String> getRequestHeaders(HttpServletRequest request) {
        Map<String, String> headers = new HashMap<>();

        // Solo incluir headers relevantes para seguridad
        String[] relevantHeaders = { "Authorization", "Content-Type", "User-Agent", "X-Forwarded-For" };

        for (String headerName : relevantHeaders) {
            String headerValue = request.getHeader(headerName);
            if (headerValue != null) {
                // Ofuscar token para seguridad
                if ("Authorization".equals(headerName) && headerValue.startsWith("Bearer ")) {
                    headerValue = "Bearer " + headerValue.substring(7, Math.min(15, headerValue.length())) + "...";
                }
                headers.put(headerName, headerValue);
            }
        }

        return headers;
    }

    /**
     * Determina si la aplicación está en modo desarrollo.
     * 
     * @return true si está en desarrollo, false en caso contrario
     */
    private boolean isDevelopmentMode() {
        // En una implementación real, esto debería verificar el perfil activo
        String profile = System.getProperty("spring.profiles.active", "development");
        return "development".equals(profile) || "dev".equals(profile);
    }
}