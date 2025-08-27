# Guía de diagnóstico: /api/waitlist/me se resuelve como anónimo

Este documento explica por qué las solicitudes a `GET /api/waitlist/me` aparecen como anónimas y cómo resolverlo.

## Causa más probable

El filtro `JwtAuthenticationFilter` sólo corre cuando la solicitud trae un header `Authorization` no vacío con formato `Bearer <jwt>`. Si no lo ve, `shouldNotFilter` devuelve `true` y la petición sigue sin autenticación, por lo que Spring muestra:

```
AnonymousAuthenticationFilter : Set SecurityContextHolder to anonymous SecurityContext
```

Otras causas:
- La app se estaba apagando en medio de las requests (logs: “Graceful shutdown”).
- Token mal formado (no tiene 3 partes separadas por `.`) → el filtro intenta correr, pero falla y deja anónimo.
- El frontend no reenvía el token tras el login.

## Cómo verificar rápidamente

1) Asegúrate de tener un token válido (login):

- POST `http://localhost:8080/auth/authenticate`
- Body JSON: `{ "email": "<tu_email>", "password": "<tu_password>" }`
- Copia `access_token` de la respuesta.

2) Llama al endpoint protegido con el token:

- GET `http://localhost:8080/api/waitlist/me`
- Header: `Authorization: Bearer <access_token>`

Ejemplo cURL:
// hola //
```
curl -H "Authorization: Bearer <access_token>" http://localhost:8080/api/waitlist/me
```

Si todo va bien, en logs deberías ver del filtro:
- `shouldNotFilter? ... hasAuthHeader=true`
- `Processing request: /api/waitlist/me`
- `tokenParts=3`, `subject/email=...`, `isValid=true`, `Authentication set in SecurityContext for ...`

## Qué revisar en frontend

- Adjuntar el header exactamente como: `Authorization: Bearer <token>` (con espacio, sin comillas).
- Persistir el token tras login y reusarlo en cada request.

Ejemplo Axios (interceptor global):

```js
axios.interceptors.request.use((config) => {
  const token = localStorage.getItem('access_token');
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});
```

## Configuración relevante en backend

- `SecurityConfig` protege `/api/**` y permite CORS/Authorization headers.
- `JwtAuthenticationFilter` ahora loguea por qué se salta o procesa:
  - `shouldNotFilter? method=..., path=..., uri=..., hasAuthHeader=...`
  - Si `hasAuthHeader=false`, el filtro se salta y quedas anónimo.

## Errores frecuentes y solución

- "AnonymousAuthenticationFilter" continuo:
  - El header Authorization no se envía. Solución: añadirlo en el cliente.
- Token inválido (no 3 partes):
  - Asegúrate de pasar el `access_token` completo.
- App se apaga (“Graceful shutdown”) durante pruebas:
  - No detengas el proceso mientras haces requests.

## Comprobación local rápida

- Activa DEBUG sólo para el filtro:

```
--logging.level.com.sigma.gym.security.JwtAuthenticationFilter=DEBUG
```

- Pruébalo con cURL (reemplaza `<token>`):

```
curl -H "Authorization: Bearer <token>" http://localhost:8080/api/waitlist/me
```

Deberías ver en logs: `Processing request`, `tokenParts=3`, `subject/email=...`, `Authentication set in SecurityContext ...`.
