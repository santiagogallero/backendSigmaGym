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

### Variables de Entorno

Crear un archivo `.env` o configurar las siguientes variables:

```bash
# Base de datos
DB_URL=jdbc:mysql://localhost:3306/sigma_gym
DB_USERNAME=root
DB_PASSWORD=root1234

# MercadoPago
MP_ACCESS_TOKEN=your_access_token
MP_PUBLIC_KEY=your_public_key
MP_WEBHOOK_SECRET=your_webhook_secret

# Usuarios semilla
SEED_OWNER_EMAIL=owner@sigmagym.local
SEED_OWNER_USERNAME=owner
SEED_OWNER_PASSWORD=Owner123!

SEED_TRAINER_EMAIL=trainer@sigmagym.local
SEED_TRAINER_USERNAME=trainer
SEED_TRAINER_PASSWORD=Trainer123!
```

### Instalación y Ejecución

1. **Clonar el repositorio**
   ```bash
   git clone <repository-url>
   cd backend
   ```

2. **Configurar la base de datos**
   ```sql
   CREATE DATABASE sigma_gym;
   CREATE USER 'gym_user'@'localhost' IDENTIFIED BY 'gym_password';
   GRANT ALL PRIVILEGES ON sigma_gym.* TO 'gym_user'@'localhost';
   FLUSH PRIVILEGES;
   ```

3. **Ejecutar la aplicación**
   ```bash
   ./gradlew bootRun
   ```

4. **Verificar la instalación**
   - Health Check: http://localhost:8080/actuator/health
   - API Documentation: http://localhost:8080/swagger-ui.html

## Persistencia de Datos (Desarrollo)

### Configuración de Persistencia

- **Modo**: `spring.jpa.hibernate.ddl-auto=update`
- **Persistencia**: Los datos se mantienen entre reinicios de la aplicación
- **Formato SQL**: Habilitado para debugging (`spring.jpa.properties.hibernate.format_sql=true`)
- **Open Session in View**: Deshabilitado para mejor performance (`spring.jpa.open-in-view=false`)

### Logs de Base de Datos

```properties
# SQL queries
logging.level.org.hibernate.SQL=INFO
# SQL parameters (para debugging detallado, cambiar a DEBUG)
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=WARN
```

### Notas Importantes

- En **desarrollo**, los datos persisten entre reinicios
- En **producción**, usar `ddl-auto=validate` y migraciones con Flyway/Liquibase
- Los esquemas se actualizan automáticamente según los cambios en las entidades

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
