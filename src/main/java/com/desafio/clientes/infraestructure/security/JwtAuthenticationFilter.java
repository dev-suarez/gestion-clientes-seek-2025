package com.desafio.clientes.infraestructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Filtro de autenticación JWT personalizado.
 * 
 * Implementa el patrón Filter para interceptar todas las peticiones HTTP
 * y validar tokens JWT. Extiende OncePerRequestFilter para garantizar
 * que se ejecute una sola vez por petición.
 * 
 * Responsabilidades:
 * - Extraer token JWT del header Authorization
 * - Validar el token usando JwtUtil
 * - Establecer la autenticación en el SecurityContext
 * - Manejar roles y autoridades del usuario
 * 
 * @author Sistema de Desarrollo
 * @version 1.0.0
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    /**
     * Método principal del filtro que procesa cada petición HTTP.
     * 
     * @param request     petición HTTP
     * @param response    respuesta HTTP
     * @param filterChain cadena de filtros
     * @throws ServletException si hay errores en el servlet
     * @throws IOException      si hay errores de E/O
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        try {
            // Extraer token JWT de la petición
            String jwt = parseJwt(request);

            if (jwt != null && jwtUtil.validateJwtToken(jwt)) {
                // Extraer información del usuario del token
                String username = jwtUtil.getUsernameFromJwtToken(jwt);
                String roles = jwtUtil.getRolesFromJwtToken(jwt);

                if (username != null) {
                    // Crear lista de autoridades basada en los roles
                    List<GrantedAuthority> authorities = parseAuthorities(roles);

                    // Crear token de autenticación
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            username, null, authorities);

                    // Establecer detalles de la petición web
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // Establecer autenticación en el contexto de seguridad
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    logger.debug("Usuario autenticado: {} con roles: {}", username, roles);
                } else {
                    logger.warn("No se pudo extraer username del token JWT");
                }
            } else if (jwt != null) {
                logger.warn("Token JWT inválido o expirado");
            }

        } catch (Exception e) {
            logger.error("Error al establecer autenticación de usuario: {}", e.getMessage(), e);
            // No lanzar excepción para permitir que la cadena continúe
            // Spring Security manejará la falta de autenticación
        }

        // Continuar con la cadena de filtros
        filterChain.doFilter(request, response);
    }

    /**
     * Extrae el token JWT del header Authorization de la petición.
     * 
     * @param request petición HTTP
     * @return token JWT sin el prefijo "Bearer ", null si no existe
     */
    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            String token = headerAuth.substring(7);
            logger.debug("Token JWT extraído del header Authorization");
            return token;
        }

        return null;
    }

    /**
     * Convierte una cadena de roles en una lista de autoridades de Spring Security.
     * 
     * @param roles cadena de roles separados por comas
     * @return lista de autoridades otorgadas
     */
    private List<GrantedAuthority> parseAuthorities(String roles) {
        if (roles == null || roles.trim().isEmpty()) {
            logger.debug("No se encontraron roles, asignando rol USER por defecto");
            return List.of(new SimpleGrantedAuthority("ROLE_USER"));
        }

        // Dividir roles por comas y crear autoridades
        List<GrantedAuthority> authorities = Arrays.stream(roles.split(","))
                .map(String::trim)
                .filter(role -> !role.isEmpty())
                .map(role -> {
                    // Asegurar que el rol tenga el prefijo ROLE_
                    if (!role.startsWith("ROLE_")) {
                        role = "ROLE_" + role;
                    }
                    return new SimpleGrantedAuthority(role);
                })
                .collect(Collectors.toList());

        logger.debug("Autoridades parseadas: {}", authorities);
        return authorities;
    }

    /**
     * Determina si este filtro debe aplicarse a la petición actual.
     * 
     * Permite omitir el filtro para ciertas rutas que no requieren autenticación.
     * 
     * @param request petición HTTP
     * @return false si el filtro debe omitirse, true en caso contrario
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();

        // Rutas que no requieren filtrado JWT
        return path.startsWith("/api/v1/auth/") ||
                path.equals("/api/v1/clients/health") ||
                path.startsWith("/v3/api-docs") ||
                path.startsWith("/swagger-ui") ||
                path.startsWith("/actuator");
    }
}