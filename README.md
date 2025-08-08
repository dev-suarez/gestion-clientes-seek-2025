# 🏢 Microservicio de Gestión de Clientes

Sistema de gestión de clientes desarrollado con **Spring Boot 3** y desplegado en **Google Cloud Run**. Proporciona una API REST completa para el registro, consulta y análisis estadístico de clientes.

## 🌐 **API Desplegada**

- **URL Base:** https://clientes-api-22564720431.us-central1.run.app/api/v1
- **Documentación:** https://clientes-api-22564720431.us-central1.run.app/swagger-ui.html
- **Health Check:** https://clientes-api-22564720431.us-central1.run.app/api/v1/clients/health

## 🚀 **Características**

- ✅ **API REST** con operaciones CRUD completas
- ✅ **Autenticación JWT** con roles (USER, ADMIN)
- ✅ **Métricas estadísticas** (promedio de edad, desviación estándar)
- ✅ **Cálculos derivados** (esperanza de vida estimada)
- ✅ **Documentación interactiva** con Swagger UI
- ✅ **Base de datos MySQL** con migraciones Flyway
- ✅ **Validaciones** de negocio y entrada
- ✅ **Logging** estructurado y monitoreo

## 🛠️ **Tecnologías**

- **Java 21** + **Spring Boot 3.5.4**
- **Spring Data JPA** + **MySQL 8**
- **Spring Security** + **JWT**
- **Flyway** para migraciones
- **OpenAPI/Swagger** para documentación
- **Google Cloud Run** + **Cloud SQL**

## 🏗️ **Arquitectura y Patrones Implementados**

### **🎯 Patrones de Diseño**
- **🔄 Repository Pattern** - Abstracción de acceso a datos (`ClientRepository`)
- **📦 DTO Pattern** - Separación entre entidades y objetos de transferencia
- **🔄 Mapper Pattern** - Conversión limpia entre entidades y DTOs (`ClientMapper`)
- **⚙️ Strategy Pattern** - Cálculos estadísticos especializados (`StatisticsService`)
- **🏭 Factory Pattern** - Creación de respuestas API (`ApiResponse.success()`, `.error()`)
- **🛡️ Singleton Pattern** - Servicios Spring gestionados por contenedor IoC

### **🏛️ Patrones de Arquitectura**
- **📚 Layered Architecture** - Separación clara en capas (Controller → Service → Repository → Entity)
- **🔵 Hexagonal Architecture** - Dependencias apuntando hacia el dominio
- **🌐 RESTful API** - Endpoints siguiendo convenciones REST y códigos HTTP apropiados
- **📋 Service Layer** - Lógica de negocio centralizada y transaccional

### **✅ Buenas Prácticas Implementadas**
- **🔒 Separation of Concerns** - Cada clase tiene una responsabilidad específica
- **🎭 Dependency Injection** - Inyección de dependencias con Spring IoC
- **📝 Bean Validation** - Validaciones declarativas con anotaciones JSR-303
- **🛡️ Security by Design** - Autenticación JWT stateless y autorización basada en roles
- **📊 Exception Handling** - Manejo centralizado de errores con códigos HTTP apropiados
- **📖 Self-Documenting API** - Documentación automática con OpenAPI/Swagger
- **🔍 Observability** - Health checks, actuator endpoints y logging estructurado
- **🌍 Configuration Management** - Perfiles por ambiente (development, production)
- **💾 Database Versioning** - Migraciones controladas con Flyway
- **🧪 Testability** - Arquitectura preparada para testing unitario e integración

## 📋 **Requisitos Previos**

### **Para desarrollo local:**
- Java 21 o superior
- Maven 3.6+
- MySQL 8.0+

### **Para despliegue en GCP:**
- Cuenta de Google Cloud Platform
- Google Cloud CLI instalado

## 🏃‍♂️ **Ejecución Local**

### **1. Configurar Base de Datos**
```sql
CREATE DATABASE client_management;
```

### **2. Configurar Credenciales**
Editar `src/main/resources/application-development.properties`:
```properties
spring.datasource.username=tu_usuario
spring.datasource.password=tu_password
```

### **3. Ejecutar Aplicación**
```bash
# Compilar y ejecutar
mvn clean install
mvn spring-boot:run

# O usar perfil específico
mvn spring-boot:run -Dspring.profiles.active=development
```

La aplicación estará disponible en: http://localhost:8080

## 🔐 **Autenticación**

### **Obtener Token de Prueba**
```bash
curl -X POST http://localhost:8080/api/v1/auth/token
```

### **Usar Token en Requests**
```bash
curl -H "Authorization: Bearer TU_TOKEN" \
     http://localhost:8080/api/v1/clients
```

## 📊 **Endpoints Principales**

| Método | Endpoint | Descripción | Permisos |
|--------|----------|-------------|----------|
| `POST` | `/api/v1/auth/token` | Obtener token JWT | Público |
| `POST` | `/api/v1/clients` | Crear cliente | USER+ |
| `GET` | `/api/v1/clients` | Listar clientes | USER+ |
| `GET` | `/api/v1/clients/{id}` | Obtener cliente | USER+ |
| `PUT` | `/api/v1/clients/{id}` | Actualizar cliente | ADMIN |
| `DELETE` | `/api/v1/clients/{id}` | Eliminar cliente | ADMIN |
| `GET` | `/api/v1/clients/metrics` | Métricas estadísticas | USER+ |
| `GET` | `/api/v1/clients/search` | Buscar clientes | USER+ |

## 🧪 **Ejemplo de Uso**

### **1. Crear Cliente**
```bash
curl -X POST http://localhost:8080/api/v1/clients \
  -H "Authorization: Bearer TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Juan",
    "lastName": "Pérez",
    "age": 30,
    "birthDate": "1994-03-15"
  }'
```

### **2. Obtener Métricas**
```bash
curl -H "Authorization: Bearer TOKEN" \
     http://localhost:8080/api/v1/clients/metrics
```

**Respuesta:**
```json
{
  "success": true,
  "data": {
    "totalClients": 10,
    "averageAge": 31.8,
    "ageStandardDeviation": 5.2,
    "youngestAge": 25,
    "oldestAge": 42
  }
}
```

## ☁️ **Despliegue en GCP**

### **Requisitos:**
- Proyecto GCP configurado
- Google Cloud CLI instalado y autenticado

### **Comandos de Despliegue:**

```bash
# Primer despliegue (configuración completa)
gcloud run deploy clientes-api \
  --source . \
  --platform managed \
  --region us-central1 \
  --allow-unauthenticated

# Deployments posteriores (solo código)
gcloud run deploy clientes-api --source . --region us-central1
```

### **Variables de Entorno Requeridas:**
- `SPRING_PROFILES_ACTIVE=production`
- `DB_PASSWORD` (vía Secret Manager)
- `JWT_SECRET` (vía Secret Manager)
- `CLOUD_SQL_CONNECTION_NAME`

## 🔧 **Configuración**

### **Perfiles Disponibles:**
- **`development`** - Para desarrollo local con MySQL local
- **`production`** - Para GCP Cloud Run con Cloud SQL

### **Configuración por Archivos:**
- `application.properties` - Configuración base
- `application-development.properties` - Desarrollo local
- `application-production.properties` - Producción GCP

## 📁 **Estructura del Proyecto**

```
src/main/java/com/desafio/clientes/
├── application/          # Capa de aplicación
│   ├── dto/             # Objetos de transferencia (DTO Pattern)
│   ├── mapper/          # Conversores entidad-DTO (Mapper Pattern)
│   └── service/         # Servicios de negocio (Service Layer Pattern)
├── controller/          # Controladores REST (Controller Layer)
├── domain/             # Entidades de dominio (Domain Layer)
└── infrastructure/     # Configuración e infraestructura
    ├── repository/     # Repositorios JPA (Repository Pattern)
    └── security/       # Configuración de seguridad
```

### **🎯 Principios SOLID Aplicados**
- **S** - Single Responsibility: Cada clase tiene una responsabilidad específica
- **O** - Open/Closed: Extensible via interfaces (ej: `ClientRepository`)
- **L** - Liskov Substitution: Implementaciones intercambiables de repositorios
- **I** - Interface Segregation: Interfaces específicas por funcionalidad
- **D** - Dependency Inversion: Dependencias de abstracciones, no implementaciones

### **📋 Validaciones Multicapa**
```java
// 1. Validación de entrada (Controller)
@Valid @RequestBody ClientDTO.CreateClientRequest request

// 2. Validación de negocio (Service)
if (existingClient.isPresent()) {
    throw new IllegalArgumentException("Cliente ya existe");
}

// 3. Validación de datos (Entity)
@NotBlank(message = "El nombre es obligatorio")
private String firstName;

// 4. Restricciones de BD (Migration)
CONSTRAINT chk_age_positive CHECK (age > 0)
```

## 🧪 **Testing**

```bash
# Ejecutar tests unitarios
mvn test

# Ejecutar con cobertura
mvn test jacoco:report
```

## 📈 **Monitoreo**

### **Health Checks:**
- **Local:** http://localhost:8080/api/v1/clients/health
- **Producción:** https://clientes-api-22564720431.us-central1.run.app/api/v1/clients/health

### **Actuator Endpoints:**
- `/actuator/health` - Estado de la aplicación
- `/actuator/info` - Información del sistema
- `/actuator/metrics` - Métricas de rendimiento

## 💰 **Costos**

El microservicio utiliza los **Free Tiers** de Google Cloud:
- **Cloud Run:** 2M requests/mes gratis
- **Cloud SQL:** db-f1-micro gratis
- **Costo estimado:** $0.00/mes (dentro de límites gratuitos)

## 🤝 **Contribución**

### **Flujo de Desarrollo:**
1. Desarrollar localmente con perfil `development`
2. Probar endpoints con Swagger UI
3. Deploy a GCP con `gcloud run deploy`

### **Estándares:**
- Código en inglés, comentarios en español
- Validaciones en múltiples capas
- DTOs para toda comunicación externa
- Logging estructurado

---

## 📞 **Soporte**

Para problemas o dudas:
1. Verificar logs en Google Cloud Console
2. Revisar health checks
3. Consultar documentación en Swagger UI