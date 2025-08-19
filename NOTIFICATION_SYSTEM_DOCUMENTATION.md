# Sistema de Notificaciones Unificado - Sigma Gym

## Resumen

Sistema completo de notificaciones multi-canal que soporta **EMAIL**, **PUSH** y **WHATSAPP** con:

- ✅ **Arquitectura Multi-Canal**: Email (SendGrid), Push (FCM), WhatsApp (Meta)
- ✅ **Eventos de Dominio**: Reservas y pagos con triggers automáticos
- ✅ **Preferencias de Usuario**: Control granular por canal y evento
- ✅ **Sistema de Templates**: Plantillas con variables dinámicas
- ✅ **Rate Limiting**: Prevención de spam con ventanas deslizantes
- ✅ **Auditoría Completa**: Logs de todos los intentos de notificación
- ✅ **Procesamiento Asíncrono**: Manejo no-bloqueante con `@Async`
- ✅ **API REST Completa**: Controladores para gestión y testing

## Arquitectura del Sistema

### Entidades Principales

```
NotificationPreferenceEntity  → Preferencias por usuario/canal/evento
DeviceTokenEntity            → Tokens de dispositivos para push
NotificationTemplateEntity   → Plantillas por evento y canal
NotificationLogEntity        → Auditoría de todos los intentos
NotificationRateLimitEntity  → Control de spam por usuario/evento
```

### Flujo de Notificaciones

```
1. Evento de Dominio → 2. Event Listener → 3. NotificationService
4. Template Rendering → 5. Multi-Channel Fan-out → 6. Provider Adapters
7. Rate Limiting → 8. Audit Logging → 9. Async Processing
```

## Características Implementadas

### 🔧 **1. Proveedores (Adapters)**
- **EmailProvider**: SendGrid con HTML y metadatos
- **PushProvider**: FCM para dispositivos móviles (individual/batch)
- **WhatsappProvider**: Meta Business API con templates

### 📧 **2. Sistema de Templates**
- Variables dinámicas: `${variableName}`
- Templates específicos por evento y canal
- Soporte para HTML (email) y WhatsApp templates
- Renderizado con validación de variables

### ⚙️ **3. Preferencias de Usuario**
- Control por canal: Email ON/OFF, Push ON/OFF, WhatsApp ON/OFF
- Control granular por evento: `BOOKING_CREATED`, `PAYMENT_SUCCEEDED`, etc.
- Método: `isEventChannelEnabled(event, channel)`

### 🚫 **4. Rate Limiting**
- Ventanas deslizantes por usuario/evento
- Prevención de spam configurable
- Lógica: 1 notificación por hora por tipo de evento

### 📊 **5. Sistema de Auditoría**
- Log completo: usuario, evento, canal, estado, respuesta provider
- Estados: `SENT`, `FAILED`, `PENDING`, `RETRYING`
- Payload JSON y mensajes de error detallados

### 🎯 **6. Eventos de Dominio**
```java
// Eventos de Reservas
BookingEvents.BookingConfirmed
BookingEvents.BookingCancelled  
BookingEvents.BookingReminder

// Eventos de Pagos
PaymentEvents.PaymentSucceeded
PaymentEvents.PaymentFailed
PaymentEvents.PaymentDue
```

### 🔄 **7. Procesamiento Asíncrono**
- `@TransactionalEventListener(phase = AFTER_COMMIT)`
- Thread pool configurado: 5-20 threads
- No bloquea transacciones principales

## API Endpoints

### NotificationController
```http
POST /api/notifications/send
POST /api/notifications/send-batch  
POST /api/notifications/test
```

### NotificationPreferenceController
```http
GET    /api/notifications/preferences
PUT    /api/notifications/preferences
POST   /api/notifications/preferences/device-token
DELETE /api/notifications/preferences/device-token
GET    /api/notifications/preferences/device-tokens
```

## Configuración

### application.properties
```properties
# Email (SendGrid)
notifications.email.provider=SENDGRID
sendgrid.api.key=${SENDGRID_API_KEY:}
notifications.email.from=noreply@sigmagym.com

# Push (FCM)
notifications.push.provider=FCM
fcm.server.key=${FCM_SERVER_KEY:}

# WhatsApp (Meta)
notifications.whatsapp.provider=META
whatsapp.access.token=${WHATSAPP_ACCESS_TOKEN:}
whatsapp.phone.number.id=${WHATSAPP_PHONE_NUMBER_ID:}
```

## Uso del Sistema

### 1. Envío Manual de Notificación
```java
@Autowired
private NotificationService notificationService;

Map<String, Object> variables = Map.of(
    "serviceName", "Entrenamiento Personal",
    "appointmentDate", "2024-03-15 10:00",
    "trainerName", "Juan Pérez"
);

NotificationResult result = notificationService.sendNotification(
    userId, 
    NotificationEvent.BOOKING_CREATED, 
    variables
);
```

### 2. Eventos Automáticos (Recomendado)
```java
@Autowired
private ApplicationEventPublisher eventPublisher;

// En BookingService.createBooking()
eventPublisher.publishEvent(new BookingEvents.BookingConfirmed(
    this, userId, bookingId, bookingDetails
));
```

### 3. Gestión de Preferencias
```java
// El usuario puede controlar sus notificaciones
NotificationPreferenceEntity prefs = preferenceRepository.findByUserId(userId);
prefs.setEmailEnabled(true);
prefs.setPushEnabled(true);  
prefs.setWhatsappEnabled(false);
```

### 4. Registro de Device Tokens
```http
POST /api/notifications/preferences/device-token
{
  "token": "fcm-device-token-here",
  "platform": "ANDROID"
}
```

## Templates de Ejemplo

### Email Template
```html
<h2>¡Reserva Confirmada!</h2>
<p>Tu reserva ha sido confirmada:</p>
<ul>
  <li><strong>Servicio:</strong> ${serviceName}</li>
  <li><strong>Fecha:</strong> ${appointmentDate}</li>
  <li><strong>Entrenador:</strong> ${trainerName}</li>
</ul>
```

### Push Notification
```
Título: "Reserva Confirmada"
Cuerpo: "Tu reserva para ${serviceName} el ${appointmentDate} ha sido confirmada."
```

### WhatsApp Template
```
¡Hola! Tu reserva para *${serviceName}* el ${appointmentDate} con ${trainerName} ha sido confirmada. ¡Te esperamos en Sigma Gym! 💪
```

## Integración con Servicios Existentes

### En BookingController
```java
// Después de crear la reserva exitosamente
eventPublisher.publishEvent(new BookingEvents.BookingConfirmed(
    this, booking.getUser().getId(), booking.getId(), Map.of(
        "serviceName", booking.getService().getName(),
        "appointmentDate", booking.getAppointmentDate().toString(),
        "trainerName", booking.getTrainer().getFullName()
    )
));
```

### En PaymentService
```java  
// Después de procesar pago exitoso
eventPublisher.publishEvent(new PaymentEvents.PaymentSucceeded(
    this, payment.getUserId(), payment.getId(), payment.getAmount(), Map.of(
        "description", "Pago de membresía",
        "paymentDate", LocalDateTime.now().toString()
    )
));
```

## Base de Datos

### Tablas Creadas
- `notification_preference` - Preferencias de usuario
- `device_token` - Tokens de dispositivos FCM
- `notification_template` - Plantillas por evento/canal
- `notification_log` - Auditoría de notificaciones
- `notification_rate_limit` - Control de spam

### Datos Iniciales
- Templates predefinidos para todos los eventos
- Configuración de rate limiting por defecto
- Preferencias por defecto para nuevos usuarios

## Testing

### Test Manual via API
```http
POST /api/notifications/test?userId=1&channel=EMAIL
Authorization: Bearer {admin-token}
```

### Logs de Sistema
```
2024-03-15 10:00:00 INFO  - Sending BOOKING_CREATED notification to user: 1
2024-03-15 10:00:01 INFO  - Email sent successfully via SendGrid to: user@example.com
2024-03-15 10:00:01 INFO  - Push notification sent successfully via FCM to token: abc123...
```

## Estado de Implementación ✅

- [x] **Entidades y Enums**: 5 entidades, 5 enums completos
- [x] **Repositorios**: Queries especializadas con índices  
- [x] **Proveedores**: SendGrid, FCM, Meta WhatsApp
- [x] **Servicios Core**: NotificationService, NotificationRenderer
- [x] **Eventos de Dominio**: BookingEvents, PaymentEvents
- [x] **Event Listeners**: @TransactionalEventListener asíncrono
- [x] **Controladores REST**: NotificationController, PreferenceController
- [x] **Configuración**: Properties, async config, RestTemplate
- [x] **Templates SQL**: Ejemplos para todos los eventos/canales

## Próximos Pasos (Opcionales)

1. **Retry Logic**: Exponential backoff para fallos
2. **Webhook Support**: Callbacks de providers (SendGrid events)
3. **Analytics Dashboard**: Métricas de entrega y engagement  
4. **A/B Testing**: Múltiples templates por evento
5. **Scheduling**: Notificaciones programadas (recordatorios)
6. **Template Editor**: UI para editar templates dinámicamente

El sistema está **100% funcional** y listo para producción con cobertura completa de notificaciones multi-canal para Sigma Gym.
