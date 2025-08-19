# Booking Management API Documentation

## Overview

The Booking Management API extends the Sigma Gym backend to support booking cancellation and rescheduling with configurable policy windows and monthly limits. This system provides comprehensive audit trails, membership status validation, and race condition prevention.

## Features

- **Booking Cancellation**: Cancel bookings within policy windows with monthly limits
- **Booking Rescheduling**: Reschedule bookings to new class sessions with validation
- **Policy Enforcement**: Configurable cancellation/reschedule windows and monthly limits
- **Membership Validation**: Respect frozen membership status and active membership requirements
- **Audit Trail**: Complete action tracking with actor identification and metadata
- **Race Condition Prevention**: Pessimistic locking for atomic operations
- **Admin Override**: Administrative cancellation and rescheduling capabilities

## Configuration Properties

Configure the booking policies in `application-bookings.properties`:

```properties
# Booking Management Properties
booking.timezone=America/Argentina/Buenos_Aires
booking.cancel-window-hours=2
booking.reschedule-window-hours=2
booking.monthly-cancel-limit=6
booking.monthly-reschedule-limit=4
booking.no-show-mark-after-minutes=10
```

## API Endpoints

### User Endpoints

#### Cancel Booking
```http
POST /api/v2/bookings/{bookingId}/cancel
Authorization: Bearer <token>
Content-Type: application/json

{
  "bookingId": 123,
  "reason": "Schedule conflict"
}
```

**Response Success (200):**
```json
{
  "success": true,
  "message": "Booking cancelled successfully",
  "booking": {
    "id": 123,
    "userId": 456,
    "classSessionId": 789,
    "className": "Pilates Beginner",
    "trainerName": "Maria Rodriguez",
    "status": "CANCELLED",
    "cancelledAt": "2025-08-19T14:30:00",
    "createdAt": "2025-08-15T10:00:00"
  }
}
```

**Response Error (400):**
```json
{
  "success": false,
  "message": "Monthly cancellation limit exceeded (6/6)",
  "errorCode": "MONTHLY_CANCEL_LIMIT_EXCEEDED"
}
```

#### Reschedule Booking
```http
POST /api/v2/bookings/{bookingId}/reschedule
Authorization: Bearer <token>
Content-Type: application/json

{
  "bookingId": 123,
  "newClassSessionId": 987,
  "reason": "Schedule change"
}
```

**Response Success (200):**
```json
{
  "success": true,
  "message": "Booking rescheduled successfully",
  "booking": {
    "id": 124,
    "userId": 456,
    "classSessionId": 987,
    "className": "Pilates Advanced",
    "trainerName": "Carlos Martinez",
    "status": "CONFIRMED",
    "rescheduledFromBookingId": 123,
    "createdAt": "2025-08-19T14:30:00"
  }
}
```

#### Get User Bookings
```http
GET /api/v2/bookings/my-bookings
Authorization: Bearer <token>
```

**Response (200):**
```json
[
  {
    "id": 123,
    "userId": 456,
    "classSessionId": 789,
    "className": "Pilates Beginner",
    "status": "CANCELLED",
    "cancelledAt": "2025-08-19T14:30:00"
  },
  {
    "id": 124,
    "userId": 456,
    "classSessionId": 987,
    "className": "Pilates Advanced", 
    "status": "CONFIRMED",
    "rescheduledFromBookingId": 123
  }
]
```

#### Get Specific Booking
```http
GET /api/v2/bookings/{bookingId}
Authorization: Bearer <token>
```

### Admin Endpoints

#### Admin Cancel Booking
```http
POST /api/v2/bookings/{bookingId}/admin-cancel
Authorization: Bearer <admin-token>
Content-Type: application/json

{
  "bookingId": 123,
  "reason": "Class cancelled by gym"
}
```

#### Admin Reschedule Booking
```http
POST /api/v2/bookings/{bookingId}/admin-reschedule
Authorization: Bearer <admin-token>
Content-Type: application/json

{
  "bookingId": 123,
  "newClassSessionId": 987,
  "reason": "Trainer substitution"
}
```

## Error Codes

| Error Code | Description |
|-----------|-------------|
| `BOOKING_NOT_FOUND` | Booking does not exist |
| `UNAUTHORIZED` | User not authorized to modify booking |
| `INVALID_STATUS` | Booking cannot be modified in current status |
| `CANCEL_WINDOW_EXPIRED` | Cancellation window has expired |
| `RESCHEDULE_WINDOW_EXPIRED` | Reschedule window has expired |
| `MONTHLY_CANCEL_LIMIT_EXCEEDED` | Monthly cancellation limit reached |
| `MONTHLY_RESCHEDULE_LIMIT_EXCEEDED` | Monthly reschedule limit reached |
| `CLASS_FULL` | Target class session is at capacity |
| `NO_ACTIVE_MEMBERSHIP` | User has no active membership |
| `MEMBERSHIP_FROZEN` | User's membership is frozen |
| `INTERNAL_ERROR` | Unexpected system error |

## Business Rules

### Cancellation Rules
1. **Time Window**: Bookings can only be cancelled up to N hours before class start (configurable)
2. **Monthly Limit**: Users can cancel maximum N bookings per month (configurable)
3. **Status Check**: Only CONFIRMED bookings can be cancelled
4. **Membership Status**: User must have active, non-frozen membership

### Rescheduling Rules
1. **Time Window**: Bookings can only be rescheduled up to N hours before class start (configurable)
2. **Monthly Limit**: Users can reschedule maximum N bookings per month (configurable)
3. **Status Check**: Only CONFIRMED bookings can be rescheduled
4. **Capacity Check**: Target class session must have available capacity
5. **Membership Status**: User must have active, non-frozen membership

### Admin Override
- Admins and staff can cancel/reschedule any booking regardless of time windows
- Admin actions are logged with actor identification
- Monthly limits still apply to admin actions on behalf of users

## Audit Trail

All booking actions are logged in the `booking_audit` table with:
- **Action Type**: CREATED, CANCELLED, RESCHEDULED, NO_SHOW_MARKED, ADMIN_OVERRIDE
- **User ID**: The booking owner
- **Actor ID**: Who performed the action (user or admin)
- **Reason**: Optional reason for the action
- **Metadata**: JSON metadata with additional context
- **Timestamp**: When the action occurred

## Database Schema

### BookingEntity
- `id`: Primary key
- `user_id`: Foreign key to UserEntity
- `class_session_id`: Foreign key to ClassSessionEntity
- `status`: CONFIRMED, CANCELLED, REPROGRAMMED, WAITLISTED, NO_SHOW
- `rescheduled_from_booking_id`: Link to original booking when rescheduled
- `cancelled_at`: Timestamp when cancelled
- `rescheduled_at`: Timestamp when rescheduled
- `created_at`, `updated_at`: Audit timestamps

### ClassSessionEntity
- `id`: Primary key
- `trainer_id`: Foreign key to UserEntity
- `class_name`: Name of the class
- `class_type`: Type/category of class
- `starts_at`, `ends_at`: Session timing
- `max_capacity`: Maximum participants
- `booked_count`: Current bookings (maintained atomically)

### BookingAuditEntity
- `id`: Primary key
- `booking_id`: Foreign key to BookingEntity
- `user_id`: Foreign key to UserEntity (booking owner)
- `actor_user_id`: Foreign key to UserEntity (who performed action)
- `action`: Enum of booking actions
- `reason`: Optional reason text
- `metadata_json`: JSON metadata
- `created_at`: When action occurred

## Integration with Notifications

The booking system is designed to integrate with the existing notification system for:
- Booking cancellation confirmations
- Reschedule notifications
- Policy violation alerts
- Admin action notifications

## Race Condition Prevention

The system uses pessimistic locking (`PESSIMISTIC_WRITE`) on:
- BookingEntity for status updates
- ClassSessionEntity for capacity management

This ensures atomic operations and prevents double-booking or capacity overflow.

## Testing

Test the API endpoints using tools like Postman or curl:

1. **Authentication**: All endpoints require JWT token in Authorization header
2. **User Permissions**: Regular users can only modify their own bookings
3. **Admin Permissions**: Admin/staff users can modify any booking
4. **Validation**: Request payloads are validated using Bean Validation
5. **Error Handling**: All errors return appropriate HTTP status codes and error messages

## Future Enhancements

Potential future enhancements include:
- Waitlist management when rescheduling to full classes
- Bulk cancellation/rescheduling operations
- Booking transfer between users
- Advanced notification preferences
- Integration with calendar systems
- Mobile push notifications for booking changes
