# Waitlist System Documentation

## Overview
The Sigma Gym Waitlist System provides a comprehensive first-in-first-out (FIFO) queue management solution for class capacity management with automatic promotion, configurable hold windows, and integrated notifications.

## Features

### Core Functionality
- **FIFO Queue Management**: First-in-first-out queue processing with position tracking
- **Automatic Promotion**: Users are automatically promoted when spots become available
- **Hold Window**: Configurable time window for users to confirm their promoted spot
- **Membership Validation**: Only active members can join waitlists
- **Capacity Management**: Integrated with existing class session capacity limits
- **Audit Trail**: Comprehensive logging of all waitlist actions and promotions

### Notifications
- **Multi-channel Support**: EMAIL, PUSH, and WHATSAPP notifications
- **Event-driven**: Automatic notifications for promotions, confirmations, and expirations
- **Template-based**: Customizable notification templates with variable substitution

## Architecture

### Entities

#### WaitlistEntryEntity
- Manages individual waitlist entries with FIFO positioning
- Status tracking: `QUEUED`, `PROMOTED`, `EXPIRED`, `LEFT`, `CONFIRMED`
- Automatic position management and hold expiration tracking
- Unique constraints per user per class session

#### PromotionAuditEntity
- Comprehensive audit trail for all waitlist actions
- Tracks promotions, confirmations, expirations, and skips
- Maintains timing information and metadata for reporting

### Services

#### WaitlistService
Core business logic including:
- `joinWaitlist()`: Add users to waitlist with validation
- `leaveWaitlist()`: Remove users and reposition queue
- `confirmPromotion()`: Convert promoted entries to bookings
- `promoteNext()`: FIFO promotion with automatic booking creation
- `processExpiredHolds()`: Cleanup expired promotions

#### WaitlistScheduler
- Automated processing of expired hold windows
- Configurable scheduling intervals
- Automatic promotion pipeline maintenance

### API Endpoints

#### User Endpoints
- `POST /api/waitlist/join/{classSessionId}` - Join waitlist
- `DELETE /api/waitlist/leave/{entryId}` - Leave waitlist
- `POST /api/waitlist/confirm/{entryId}` - Confirm promotion
- `GET /api/waitlist/my-entries` - Get user's waitlist entries

#### Admin Endpoints
- `GET /api/waitlist/class/{classSessionId}` - Get class waitlist
- `GET /api/waitlist/class/{classSessionId}/stats` - Get waitlist statistics
- `POST /api/waitlist/admin/promote/{entryId}` - Manual promotion
- `GET /api/waitlist/audit/{classSessionId}` - Get promotion audit trail

### Configuration

```properties
# Waitlist Configuration
gym.waitlist.hold-window-minutes=30
gym.waitlist.max-position=100
gym.waitlist.cleanup-hours=24
gym.waitlist.promotion-batch-size=10
gym.waitlist.enable-auto-promotion=true
gym.waitlist.scheduler.hold-expiration-interval=PT5M
gym.waitlist.scheduler.cleanup-interval=PT1H
```

## Database Schema

### waitlist_entry table
```sql
CREATE TABLE waitlist_entry (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    class_session_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    position INTEGER NOT NULL,
    status ENUM('QUEUED', 'PROMOTED', 'EXPIRED', 'LEFT', 'CONFIRMED') NOT NULL,
    joined_at TIMESTAMP NOT NULL,
    promoted_at TIMESTAMP NULL,
    hold_until TIMESTAMP NULL,
    confirmed_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_waitlist_user_class (user_id, class_session_id),
    INDEX idx_waitlist_class_position (class_session_id, position),
    INDEX idx_waitlist_status (status),
    INDEX idx_waitlist_hold_expiry (hold_until)
);
```

### promotion_audit table
```sql
CREATE TABLE promotion_audit (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    waitlist_entry_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    class_session_id BIGINT NOT NULL,
    action ENUM('PROMOTED', 'CONFIRMED', 'EXPIRED', 'SKIPPED') NOT NULL,
    previous_position INTEGER,
    new_position INTEGER,
    booking_id BIGINT NULL,
    processed_at TIMESTAMP NOT NULL,
    hold_until TIMESTAMP NULL,
    metadata JSON NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

## Event System

### Domain Events
- `WaitlistPromotedEvent`: User promoted from waitlist
- `WaitlistConfirmedEvent`: User confirmed their promoted spot  
- `WaitlistHoldExpiredEvent`: User's hold window expired

### Event Handlers
- `WaitlistNotificationListener`: Processes events and sends notifications
- Integration with existing notification system
- Support for multiple notification channels

## Integration Points

### Booking System
- Automatic booking creation on promotion confirmation
- Integration with existing booking policies and validation
- Seamless transition from waitlist to confirmed booking

### Notification System
- Leverages existing notification infrastructure
- Template-based messaging with variable substitution
- Multi-channel delivery (EMAIL, PUSH, WHATSAPP)

### Membership System
- Validates membership status before allowing waitlist join
- Respects membership type restrictions and policies

## Usage Examples

### Joining a Waitlist
```http
POST /api/waitlist/join/123
Authorization: Bearer {jwt_token}
```

### Confirming a Promotion
```http
POST /api/waitlist/confirm/456
Authorization: Bearer {jwt_token}
```

### Admin: View Waitlist Statistics
```http
GET /api/waitlist/class/123/stats
Authorization: Bearer {admin_jwt_token}
```

## Error Handling

### Custom Exceptions
- `WaitlistNotFoundException`: Entry not found
- `WaitlistFullException`: Waitlist at capacity
- `WaitlistAlreadyJoinedException`: User already on waitlist
- `WaitlistInvalidStateException`: Invalid status transition
- `WaitlistHoldExpiredException`: Hold window expired

### HTTP Status Mapping
- `400 Bad Request`: Invalid requests or state transitions
- `404 Not Found`: Waitlist entry or class not found
- `409 Conflict`: Already joined or capacity issues
- `410 Gone`: Hold window expired

## Monitoring and Analytics

### Metrics
- Average wait times per class
- Promotion success rates
- Hold expiration rates
- Popular class demand patterns

### Audit Trail
- Complete history of all waitlist operations
- Promotion timing and success tracking
- User behavior analysis capabilities

## Security Considerations

### Authentication
- JWT-based authentication for all endpoints
- User-specific data access controls
- Admin role verification for administrative operations

### Data Protection
- Secure handling of personal information
- Audit trail for compliance requirements
- Rate limiting on API endpoints

## Performance Optimizations

### Database Optimizations
- Indexed queries for FIFO processing
- Pessimistic locking for promotion concurrency
- Efficient position management algorithms

### Caching Strategy
- Waitlist statistics caching
- Position lookup optimization
- Reduced database load for frequent operations

## Future Enhancements

### Planned Features
- Priority waitlist tiers based on membership level
- Automatic re-queuing for similar classes
- Predictive analytics for demand forecasting
- Mobile push notification improvements

### Scalability Considerations
- Distributed processing for high-volume scenarios
- Event sourcing for complete audit trails
- Microservice architecture preparation
