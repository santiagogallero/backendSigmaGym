# Checkout API Documentation

## Endpoint: POST /api/checkout/create-preference

Este endpoint crea una preferencia de checkout de Mercado Pago para un plan de membresía específico.

### URL
```
POST /api/checkout/create-preference
```

### Headers
- `Content-Type: application/json`
- `Authorization: Bearer <JWT_TOKEN>` (requerido)

### Request Body
```json
{
  "planId": 1
}
```

#### Campos del Request:
- `planId` (Long, requerido): ID del plan de membresía para el cual crear la preferencia de checkout
  - Debe ser un número positivo
  - El plan debe existir y estar activo

### Response Success (200)
```json
{
  "success": true,
  "message": "Preferencia de checkout creada exitosamente",
  "data": {
    "preferenceId": "123456789-abcd-1234-5678-123456789abc",
    "initPoint": "https://www.mercadopago.com.ar/checkout/v1/redirect?pref_id=123456789-abcd-1234-5678-123456789abc",
    "sandboxInitPoint": "https://sandbox.mercadopago.com.ar/checkout/v1/redirect?pref_id=123456789-abcd-1234-5678-123456789abc",
    "externalReference": "MEMBERSHIP_1_123_20250818195809"
  }
}
```

#### Campos del Response:
- `preferenceId`: ID único de la preferencia en Mercado Pago
- `initPoint`: URL para redirigir al usuario al checkout de producción
- `sandboxInitPoint`: URL para redirigir al usuario al checkout de sandbox/testing
- `externalReference`: Referencia externa única generada por el sistema

### Error Responses

#### 400 Bad Request - Plan no encontrado
```json
{
  "success": false,
  "message": "Plan de membresía no encontrado",
  "data": null
}
```

#### 400 Bad Request - Plan inactivo
```json
{
  "success": false,
  "message": "El plan de membresía no está disponible",
  "data": null
}
```

#### 400 Bad Request - Validación
```json
{
  "success": false,
  "message": "Plan ID is required",
  "data": null
}
```

#### 401 Unauthorized
```json
{
  "success": false,
  "message": "Access Denied",
  "data": null
}
```

#### 500 Internal Server Error
```json
{
  "success": false,
  "message": "Error interno al crear la preferencia de checkout: [error details]",
  "data": null
}
```

### Autenticación
Este endpoint requiere autenticación JWT. Los roles permitidos son:
- MEMBER
- TRAINER
- OWNER

### Ejemplo de uso (cURL)

1. Primero, obtener un token JWT:
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "member@sigmamgym.com", "password": "member123"}'
```

2. Usar el token para crear la preferencia:
```bash
curl -X POST http://localhost:8080/api/checkout/create-preference \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{"planId": 1}'
```

### Ejemplo de uso (JavaScript)
```javascript
const createCheckoutPreference = async (planId) => {
  const token = localStorage.getItem('jwt_token');
  
  try {
    const response = await fetch('http://localhost:8080/api/checkout/create-preference', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`,
      },
      credentials: 'include',
      body: JSON.stringify({
        planId: planId
      })
    });
    
    const result = await response.json();
    
    if (result.success) {
      // Redirigir al usuario al checkout de Mercado Pago
      window.location.href = result.data.initPoint;
      return result.data;
    } else {
      throw new Error(result.message);
    }
  } catch (error) {
    console.error('Error creating checkout preference:', error);
    throw error;
  }
};

// Uso
createCheckoutPreference(1)
  .then(preference => {
    console.log('Preference created:', preference);
  })
  .catch(error => {
    console.error('Failed to create preference:', error);
  });
```

### Notas importantes:
1. El endpoint utiliza la misma lógica de `MercadoPagoService.createCheckoutPreference()` que el endpoint existente
2. Devuelve exactamente el mismo contrato que el frontend espera mediante `CheckoutPreferenceDTO`
3. La referencia externa se genera automáticamente con el formato: `MEMBERSHIP_{planId}_{userId}_{timestamp}`
4. El endpoint valida que el plan exista y esté activo antes de crear la preferencia
5. Incluye manejo completo de errores con mensajes descriptivos

### Diferencias con /api/membership/checkout/{planId}:
- Este endpoint usa POST con el planId en el body en lugar de como path parameter
- Mantiene la misma funcionalidad y contrato de respuesta
- Está específicamente diseñado para cumplir con las expectativas del frontend
