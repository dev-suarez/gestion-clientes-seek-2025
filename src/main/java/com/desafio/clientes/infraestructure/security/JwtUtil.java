package com.desafio.clientes.infraestructure.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * Utilidad para manejo de tokens JWT.
 * 
 * Implementa el patrón Utility para centralizar toda la lógica
 * relacionada con la generación, validación y extracción de
 * información de tokens JWT.
 * 
 * Características:
 * - Generación de tokens con información de usuario y roles
 * - Validación de tokens con manejo de excepciones
 * - Extracción segura de claims
 * - Configuración de expiración personalizable
 * 
 * @author Sistema de Desarrollo
 * @version 1.0.0
 */
@Component
public class JwtUtil {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    // Clave secreta para firmar tokens (en producción debe estar en variables de
    // entorno)
    @Value("${app.jwt.secret:defaultSecretKeyForDevelopmentOnlyDoNotUseInProduction}")
    private String jwtSecret;

    // Tiempo de expiración en milisegundos (24 horas por defecto)
    @Value("${app.jwt.expiration:86400000}")
    private int jwtExpirationMs;

    /**
     * Genera la clave secreta para firmar tokens JWT.
     * 
     * @return clave secreta
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    /**
     * Genera un token JWT basado en la autenticación del usuario.
     * 
     * @param authentication información de autenticación
     * @return token JWT generado
     */
    public String generateJwtToken(Authentication authentication) {
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();

        Date expirationDate = new Date((new Date()).getTime() + jwtExpirationMs);

        logger.debug("Generando token JWT para usuario: {}", userPrincipal.getUsername());

        return Jwts.builder()
                .setSubject(userPrincipal.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(expirationDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Genera un token JWT con información personalizada.
     * 
     * @param username nombre de usuario
     * @param roles    roles del usuario
     * @return token JWT generado
     */
    public String generateJwtToken(String username, String roles) {
        Date expirationDate = new Date((new Date()).getTime() + jwtExpirationMs);

        logger.debug("Generando token JWT personalizado para usuario: {}", username);

        return Jwts.builder()
                .setSubject(username)
                .claim("roles", roles)
                .setIssuedAt(new Date())
                .setExpiration(expirationDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Extrae el nombre de usuario del token JWT.
     * 
     * @param token token JWT
     * @return nombre de usuario
     */
    public String getUsernameFromJwtToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return claims.getSubject();

        } catch (JwtException e) {
            logger.error("Error al extraer username del token JWT: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Extrae los roles del usuario del token JWT.
     * 
     * @param token token JWT
     * @return roles del usuario
     */
    public String getRolesFromJwtToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return claims.get("roles", String.class);

        } catch (JwtException e) {
            logger.error("Error al extraer roles del token JWT: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Extrae la fecha de expiración del token JWT.
     * 
     * @param token token JWT
     * @return fecha de expiración
     */
    public Date getExpirationDateFromJwtToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return claims.getExpiration();

        } catch (JwtException e) {
            logger.error("Error al extraer fecha de expiración del token JWT: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Valida un token JWT verificando su firma y expiración.
     * 
     * @param authToken token JWT a validar
     * @return true si el token es válido, false en caso contrario
     */
    public boolean validateJwtToken(String authToken) {
        logger.debug("Validando token con secret: {}", jwtSecret);

        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(authToken);

            logger.debug("Token JWT validado exitosamente");
            return true;

        } catch (MalformedJwtException e) {
            logger.error("Token JWT malformado: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("Token JWT expirado: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("Token JWT no soportado: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string está vacío: {}", e.getMessage());
        } catch (JwtException e) {
            logger.error("Error de validación JWT: {}", e.getMessage());
        }

        return false;
    }

    /**
     * Obtiene el tiempo restante de vida del token en milisegundos.
     * 
     * @param token token JWT
     * @return tiempo restante en milisegundos, 0 si está expirado
     */
    public long getTokenRemainingTime(String token) {
        Date expirationDate = getExpirationDateFromJwtToken(token);
        if (expirationDate == null) {
            return 0;
        }

        long currentTime = new Date().getTime();
        long expirationTime = expirationDate.getTime();

        return Math.max(0, expirationTime - currentTime);
    }

    /**
     * Obtiene información del tiempo de expiración configurado.
     * 
     * @return tiempo de expiración en milisegundos
     */
    public int getJwtExpirationMs() {
        return jwtExpirationMs;
    }
}