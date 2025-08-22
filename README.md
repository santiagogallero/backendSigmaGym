# Sigma Gym Backend

Sistema de gestión para gimnasios desarrollado con Spring Boot. Incluye gestión de usuarios, reservas, listas de espera, membresías, pagos y facturación automática.

## Características Principales

- 🔐 **Autenticación JWT** - Sistema de autenticación seguro con tokens JWT
- 👥 **Gestión de Usuarios** - Registro, perfiles, roles (MEMBER, TRAINER, OWNER)
- 📅 **Sistema de Reservas** - Gestión completa de clases y reservas
- ⏳ **Lista de Espera** - Sistema inteligente de espera con notificaciones automáticas
- 💰 **Procesamiento de Pagos** - Integración con MercadoPago
- 📄 **Facturación Automática** - Generación automática de facturas con prevención de duplicados
- 💪 **Gestión de Membresías** - Planes flexibles y renovaciones automáticas
- 📊 **Métricas y Health Checks** - Monitoreo con Spring Boot Actuator

## Tecnologías Utilizadas

- **Framework**: Spring Boot 3.x
- **Base de Datos**: MySQL 8.0
- **Autenticación**: JWT (JSON Web Tokens)
- **ORM**: Hibernate/JPA
- **API Documentation**: OpenAPI 3 (Swagger)
- **Payments**: MercadoPago SDK
- **Email**: Spring Mail
- **Testing**: JUnit 5, Mockito
- **Build Tool**: Gradle

## Configuración del Proyecto

### Prerrequisitos

- Java 21+
- MySQL 8.0+
- Gradle 8.x

### Configuración de Perfiles

El proyecto utiliza **Spring Profiles** para diferentes entornos:

- **dev** (desarrollo): MySQL persistente local
- **prod** (producción): MySQL con variables de entorno
- **test** (testing): H2 en memoria

### Variables de Entorno

#### Desarrollo (dev)
Base de datos MySQL local:
```bash
# Configuración en application-dev.yml
spring.datasource.url=jdbc:mysql://localhost:3306/sigma_gym_dev?createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=root
```

#### Producción (prod)
Configurar las siguientes variables:
```bash
# Base de datos
DB_URL=jdbc:mysql://prod-server:3306/sigma_gym_prod
DB_USER=sigma_prod_user
DB_PASS=secure_production_password

# MercadoPago
MP_ACCESS_TOKEN=your_production_access_token
MP_PUBLIC_KEY=your_production_public_key
MP_WEBHOOK_SECRET=your_webhook_secret

# Seeding (opcional en prod)
SEED_ENABLED=false
```

### Instalación y Ejecución

#### Opción 1: MySQL Local

1. **Instalar MySQL 8.0**
   ```bash
   # macOS con Homebrew
   brew install mysql@8.0
   brew services start mysql@8.0
   
   # Configurar root password
   mysql -u root
   ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY 'root';
   ```

2. **Ejecutar la aplicación**
   ```bash
   ./gradlew bootRun
   ```

#### Opción 2: Docker MySQL

1. **Levantar MySQL con Docker**
   ```bash
   docker-compose up mysql-dev -d
   ```

2. **Ejecutar la aplicación**
   ```bash
   ./gradlew bootRun
   ```

#### Opción 3: Todo con Docker
```bash
docker-compose up --build
```

### Base de Datos y Persistencia

#### ✅ Características de Persistencia

- **ddl-auto: update** - Los datos **NUNCA** se borran al reiniciar
- **MySQL persistente** en desarrollo y producción
- **H2 en memoria** solo para tests (no afecta dev)
- **Seeding automático** de roles y usuarios administrativos
- **Idempotencia** - El seeder solo crea datos que faltan

#### 🔐 Usuarios Pre-creados

Al iniciar la aplicación, se crean automáticamente:

| Role | Email | Password | Descripción |
|------|-------|----------|-------------|
| **OWNER** | owner@sigma.gym | Owner123! | Administrador principal |
| **TRAINER** | trainer@sigma.gym | Trainer123! | Entrenador |

**Nota**: El seeding solo ocurre si los usuarios no existen (idempotente).

#### 🗃️ Estructura de Roles

```
OWNER (priority: 1)    - Máximo nivel de acceso
TRAINER (priority: 2)  - Gestión de clases y miembros
MEMBER (priority: 3)   - Usuario final (creado via /auth/register)
```

## Persistencia de Datos (Desarrollo)

### ✅ Configuración Garantizada de Persistencia

- **Perfil dev**: `spring.jpa.hibernate.ddl-auto: update`
- **Perfil prod**: `spring.jpa.hibernate.ddl-auto: validate`
- **Perfil test**: `spring.jpa.hibernate.ddl-auto: create-drop` (solo H2 en memoria)
- **Persistencia**: ✅ Los datos se mantienen entre reinicios de la aplicación
- **Base de datos**: MySQL persistente (no H2 en desarrollo)
- **Seeding**: Automático e idempotente

### Verificación de Persistencia

```bash
# 1. Iniciar aplicación primera vez
./gradlew bootRun

# 2. Verificar que se crearon los usuarios
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "owner@sigma.gym", "password": "Owner123!"}'

# 3. Parar la aplicación (Ctrl+C)

# 4. Reiniciar aplicación
./gradlew bootRun

# 5. Verificar que los datos persisten
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "owner@sigma.gym", "password": "Owner123!"}'
```

**✅ Resultado esperado**: Login exitoso en ambos casos, sin pérdida de datos.

### Logs de Base de Datos

#### Desarrollo
```properties
# SQL queries (solo en caso de debugging)
logging.level.org.hibernate.SQL=WARN
# SQL parameters 
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=WARN
```

#### Producción
```properties
# Logging mínimo para performance
logging.level.org.hibernate.SQL=WARN
logging.level.com.sigma.gym=INFO
```

### Control del Seeding

#### Habilitar/Deshabilitar Seeding
```yaml
# application-dev.yml
sigmagym:
  seed:
    enabled: true # false para deshabilitar

# application-prod.yml  
sigmagym:
  seed:
    enabled: ${SEED_ENABLED:false} # Variable de entorno
```

#### Logs del Seeding
```
2024-08-21 10:15:23.456 INFO  [...] DatabaseSeeder: Starting database seeding...
2024-08-21 10:15:23.567 INFO  [...] DatabaseSeeder: Creating role: OWNER
2024-08-21 10:15:23.678 INFO  [...] DatabaseSeeder: Creating owner user: owner@sigma.gym
2024-08-21 10:15:23.789 INFO  [...] DatabaseSeeder: Database seeding completed successfully
```

### Notas Importantes

- **En desarrollo**: Los datos persisten entre reinicios ✅
- **En producción**: Usar `ddl-auto: validate` + migraciones (Flyway/Liquibase)
- **En tests**: H2 en memoria - no afecta desarrollo
- **Seeding idempotente**: Solo crea datos que faltan
- **No más `create-drop`**: Eliminado completamente de dev y prod

## Sistema de Facturación Automática

### Funcionamiento

El sistema genera facturas automáticamente cuando se procesan pagos exitosos, con las siguientes características:

#### Prevención de Duplicados
- **Idempotencia**: Verificación a nivel de base de datos y aplicación
- **Índice único**: `payment_id` tiene restricción única en la tabla `invoices`
- **Cache de eventos**: Previene el reprocesamiento de eventos duplicados
- **Método seguro**: `generateIfNotExists()` verifica existencia antes de crear

#### Listeners de Eventos

**EventListener**
```java
@EventListener
public void handlePaymentSucceeded(PaymentEvents.PaymentSucceeded event)
```
- Responde **únicamente** a eventos `PaymentEvents.PaymentSucceeded`
- No captura eventos generales de Spring (ServletRequestHandledEvent, etc.)
- Verificación de idempotencia antes de procesar
- Extrae datos del evento: paymentId, amount, description, currency
- Logs a nivel DEBUG para operaciones normales, INFO para éxitos

**Scheduled Task**
```java
@Scheduled(fixedDelay = 60000) // 60 segundos
public void processePendingInvoices()
```
- Sweep periódico de pagos pendientes de facturación
- Prevención de ejecuciones solapadas con `AtomicBoolean`
- Intervalo configurable (actualmente 60 segundos)
- Solo registra actividad cuando hay facturas procesadas

#### Configuración

```properties
# Configuración de facturación
gym.invoicing.timezone=America/Argentina/Buenos_Aires
gym.invoicing.defaultInvoiceType=RECEIPT
gym.invoicing.tax.enabled=true
gym.invoicing.tax.rate=21.0
gym.invoicing.numbering.pos=0001
gym.invoicing.numbering.series=A
gym.invoicing.storage.backend=FS
gym.invoicing.storage.basePath=/var/app/invoices
```

#### Logging

```properties
# Logs específicos para facturación automática
logging.level.com.sigma.gym.listeners.PaymentInvoicingListener=INFO
# Para debugging detallado:
# logging.level.com.sigma.gym.listeners.PaymentInvoicingListener=DEBUG
```

## API Endpoints

### Autenticación
- `POST /auth/login` - Login de usuario
- `POST /auth/register` - Registro de nuevo usuario

### Usuarios
- `GET /api/users/me` - Perfil del usuario actual
- `PUT /api/users/me` - Actualizar perfil

### Reservas
- `GET /api/bookings/me` - Reservas del usuario
- `POST /api/bookings` - Crear nueva reserva
- `DELETE /api/bookings/{id}` - Cancelar reserva

### Lista de Espera
- `GET /api/waitlist/me` - Entradas en lista de espera
- `POST /api/waitlist` - Unirse a lista de espera
- `DELETE /api/waitlist/{id}` - Salir de lista de espera

### Membresías
- `GET /api/memberships/me` - Membresía actual
- `POST /api/memberships/renew` - Renovar membresía

### Facturación
- `GET /api/invoices/me` - Facturas del usuario
- `GET /api/invoices/{id}` - Detalle de factura
- `GET /api/invoices/{id}/pdf` - Descargar PDF

Ver `README_SECURITY_TESTING.md` para ejemplos detallados de testing con curl.

## Configuración de Seguridad

### Rutas Públicas
- `OPTIONS /**` - CORS preflight requests
- `/auth/**` - Autenticación
- `/actuator/health` - Health check
- `/docs/**`, `/swagger-ui/**`, `/v3/api-docs/**` - Documentación

### Rutas Protegidas
- `/api/**` - Requieren autenticación JWT

### CORS
- Orígenes permitidos: `http://localhost:5173`, `http://localhost:3000`
- Métodos: `GET`, `POST`, `PUT`, `DELETE`, `PATCH`, `OPTIONS`
- Credenciales: Habilitadas

## Testing

### Ejecutar Tests
```bash
# Todos los tests
./gradlew test

# Tests específicos
./gradlew test --tests "com.sigma.gym.services.*"

# Con coverage
./gradlew test jacocoTestReport
```

### Tests de Integración
```bash
# Con base de datos H2 en memoria
./gradlew integrationTest
```

## Deployment

### Construir JAR
```bash
./gradlew build
java -jar build/libs/backend-0.0.1-SNAPSHOT.jar
```

### Docker
```bash
# Build
docker build -t sigma-gym-backend .

# Run
docker-compose up -d
```

### Variables de Producción
```bash
# Configuración de base de datos
SPRING_DATASOURCE_URL=jdbc:mysql://prod-db:3306/sigma_gym
SPRING_DATASOURCE_USERNAME=prod_user
SPRING_DATASOURCE_PASSWORD=secure_password

# JWT
APPLICATION_SECURITY_JWT_SECRETKEY=your-secure-256-bit-secret
APPLICATION_SECURITY_JWT_EXPIRATION=86400000

# MercadoPago Production
MP_ACCESS_TOKEN=PROD-your-access-token
MP_PUBLIC_KEY=PROD-your-public-key

# Logging
LOGGING_LEVEL_COM_SIGMA_GYM=INFO
LOGGING_LEVEL_ORG_HIBERNATE_SQL=WARN
```

## Monitoreo

### Health Checks
- **URL**: `/actuator/health`
- **Métricas**: `/actuator/metrics`
- **Info**: `/actuator/info`

### Logs importantes
```bash
# Seguir logs en tiempo real
tail -f application.log

# Filtrar errores
grep ERROR application.log

# Logs de facturación
grep "Auto-invoicing" application.log
```

## Contribución

1. Fork del proyecto
2. Crear feature branch (`git checkout -b feature/nueva-funcionalidad`)
3. Commit de cambios (`git commit -am 'Agregar nueva funcionalidad'`)
4. Push al branch (`git push origin feature/nueva-funcionalidad`)
5. Crear Pull Request

## Soporte

Para soporte técnico o reportar bugs:
- Crear issue en GitHub
- Email: support@sigmagym.app
- Documentación: `/docs` endpoint cuando la app esté ejecutándose

---

**Sigma Gym** - Sistema de gestión integral para gimnasios 💪
