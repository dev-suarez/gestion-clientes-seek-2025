# Multi-stage build con imagen MEJORADA
FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /workspace

# Copiar archivos de configuración Maven (para cache de dependencias)
COPY pom.xml ./
COPY .mvn .mvn
COPY mvnw ./

# Hacer mvnw ejecutable
RUN chmod +x mvnw

# Descargar dependencias (se cachea si pom.xml no cambia)
RUN mvn dependency:go-offline -B

# Copiar código fuente
COPY src ./src

# Compilar aplicación
RUN mvn clean package -DskipTests

# ========================================
# Imagen final optimizada
# ========================================
FROM eclipse-temurin:21-jdk-jammy

# Instalar curl para health checks
RUN apt-get update && \
    apt-get install -y curl && \
    rm -rf /var/lib/apt/lists/*

# Crear usuario no-root para seguridad
RUN useradd -r -u 1000 appuser

WORKDIR /app

# Copiar JAR desde la etapa de build
COPY --from=build /workspace/target/clientes-0.0.1-SNAPSHOT.jar app.jar

# Cambiar ownership
RUN chown appuser:appuser app.jar
USER appuser

# Variables de entorno
ENV SPRING_PROFILES_ACTIVE=production
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0 -Djava.security.egd=file:/dev/./urandom"

EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/api/v1/clients/health || exit 1

# Comando de inicio
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]