# Sigma Gym Backend - Security Testing Guide

Este documento describe cómo probar la seguridad de los endpoints del backend de Sigma Gym.

## Configuración de Seguridad

### Rutas Públicas
- `OPTIONS /**` - CORS preflight requests (todas las rutas)
- `/auth/**` - Autenticación (login, register, etc.)
- `/actuator/**` - Health checks y métricas
- `/docs/**` - Documentación
- `/swagger-ui/**` - Swagger UI
- `/v3/api-docs/**` - OpenAPI docs
- `/static/**` - Recursos estáticos
- `/api/plans/**` - Planes públicos
- `/api/webhook/**` - Webhooks de terceros

### Rutas Protegidas
- `/api/**` - Todos los endpoints de la API requieren autenticación
- `anyRequest()` - Cualquier otra ruta requiere autenticación

### CORS
- Orígenes permitidos: `http://localhost:5173`, `http://localhost:3000`
- Métodos: `GET`, `POST`, `PUT`, `DELETE`, `PATCH`, `OPTIONS`
- Headers: `*` (todos)
- Credenciales: `true`

### JWT Token Validation
- Regex pattern: `^Bearer\\s+([A-Za-z0-9\\-\\._]+)$`
- Logs a nivel TRACE para tokens inválidos (no ruidosos)
- shouldNotFilter implementado para rutas públicas y requests sin Authorization

## Pruebas de Endpoints

### 1. Endpoint sin token (debe devolver 401)

```bash
# Test endpoint /api/waitlist/me sin token
curl -i http://localhost:8080/api/waitlist/me

# Respuesta esperada:
# HTTP/1.1 401 Unauthorized
# Content-Type: application/json
# {"error":"unauthorized"}
```

```bash
# Test cualquier endpoint /api/** sin token
curl -i http://localhost:8080/api/bookings/me

# Respuesta esperada:
# HTTP/1.1 401 Unauthorized
# Content-Type: application/json
# {"error":"unauthorized"}
```

### 2. Obtener token de autenticación

```bash
# Login para obtener token
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "usuario@ejemplo.com",
    "password": "password123"
  }'

# Respuesta esperada:
# {
#   "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
#   "type": "Bearer",
#   "user": {
#     "id": 1,
#     "email": "usuario@ejemplo.com",
#     "role": "MEMBER"
#   }
# }
```

### 3. Endpoint con token válido (debe devolver 200)

```bash
# Reemplazar <TOKEN> con el token obtenido del login
curl -i http://localhost:8080/api/waitlist/me \
  -H "Authorization: Bearer <TOKEN>"

# Respuesta esperada:
# HTTP/1.1 200 OK
# Content-Type: application/json
# [
#   {
#     "id": 1,
#     "classSessionId": 123,
#     "position": 1,
#     "status": "QUEUED",
#     ...
#   }
# ]
```

```bash
# Test otro endpoint protegido
curl -i http://localhost:8080/api/bookings/me \
  -H "Authorization: Bearer <TOKEN>"

# Respuesta esperada:
# HTTP/1.1 200 OK
# Content-Type: application/json
# [...datos del usuario...]
```

### 4. Endpoint con token inválido (debe devolver 401)

```bash
# Test con token malformado
curl -i http://localhost:8080/api/waitlist/me \
  -H "Authorization: Bearer token-invalido"

# Respuesta esperada:
# HTTP/1.1 401 Unauthorized
# Content-Type: application/json
# {"error":"unauthorized"}
```

```bash
# Test con Bearer header malformado
curl -i http://localhost:8080/api/waitlist/me \
  -H "Authorization: InvalidBearer eyJhbGc..."

# Respuesta esperada:
# HTTP/1.1 401 Unauthorized
# Content-Type: application/json
# {"error":"unauthorized"}
```

### 5. Test de CORS Preflight

```bash
# Test OPTIONS request para CORS
curl -i -X OPTIONS http://localhost:8080/api/waitlist/me \
  -H "Origin: http://localhost:5173" \
  -H "Access-Control-Request-Method: GET" \
  -H "Access-Control-Request-Headers: Authorization"

# Respuesta esperada:
# HTTP/1.1 200 OK
# Access-Control-Allow-Origin: http://localhost:5173
# Access-Control-Allow-Methods: GET,POST,PUT,DELETE,PATCH,OPTIONS
# Access-Control-Allow-Headers: *
# Access-Control-Allow-Credentials: true
```

### 6. Test de rutas públicas (deben devolver 200 o respuesta apropiada)

```bash
# Test health check
curl -i http://localhost:8080/actuator/health

# Respuesta esperada:
# HTTP/1.1 200 OK
# {"status":"UP"}
```

```bash
# Test login endpoint (sin credentials devuelve 400/401, no 403)
curl -i http://localhost:8080/auth/login \
  -H "Content-Type: application/json"

# Respuesta esperada: NO debe ser 403 Forbidden
# HTTP/1.1 400 Bad Request (missing body) o similar
```

## Logging

### Logs de Autenticación 
- **INFO level**: Configurado para `com.sigma.gym.security.JwtAuthenticationFilter`
- **TRACE level**: Tokens JWT inválidos, usuarios no encontrados, tokens expirados
- **DEBUG level**: Autenticaciones exitosas

### Configuración de Logs
En `application.properties`:

```properties
# Logs específicos para el filtro JWT
logging.level.com.sigma.gym.security.JwtAuthenticationFilter=INFO

# Para debugging durante desarrollo, cambiar a DEBUG o TRACE:
# logging.level.com.sigma.gym.security.JwtAuthenticationFilter=TRACE
```

## Mejoras Implementadas

### JwtAuthenticationFilter
- ✅ **shouldNotFilter()** implementado para rutas públicas y requests sin Authorization
- ✅ **Regex pattern** `^Bearer\\s+([A-Za-z0-9\\-\\._]+)$` para validación de token
- ✅ **Logs silenciosos** - solo TRACE para tokens inválidos
- ✅ **Sin logs ruidosos** para requests normales sin token

### SecurityConfig
- ✅ **Rutas públicas** claramente definidas con patrones específicos
- ✅ **AuthenticationEntryPoint** personalizado que devuelve JSON `{"error":"unauthorized"}`
- ✅ **CORS mejorado** para desarrollo frontend

### PaymentInvoicingListener
- ✅ **Prevención de duplicados** con cache de eventos procesados
- ✅ **Event ID único** para evitar procesamientos múltiples
- ✅ **Cleanup de cache** en caso de errores

## Resolución de Problemas

### Error 401 inesperado
1. Verificar que el token no esté expirado
2. Verificar formato del header: `Authorization: Bearer <token>` (con regex correcta)
3. Verificar que el usuario existe en la base de datos
4. Revisar logs de TRACE para más información (cambiar nivel de log temporalmente)

### Error CORS  
1. Verificar que el origen esté en la lista de permitidos
2. Para desarrollo local usar `http://localhost:5173` o `http://localhost:3000`
3. Verificar que se incluyan las credenciales en las requests del frontend

### Logs excesivos
- Los logs de tokens inválidos están en nivel TRACE
- El filtro JWT está configurado en nivel INFO
- Para producción, usar WARN o ERROR level para el filtro JWT

### PaymentInvoicingListener loops
- Implementada prevención de duplicados con ConcurrentMap
- Cache de eventos procesados evita re-ejecución
- Método de cleanup manual disponible
