# Sigma Gym Automatic Invoicing System

## Overview
The Sigma Gym Automatic Invoicing System provides comprehensive PDF invoice generation, fiscal profile management, and integration with the existing notification system for Argentina-ready tax compliance.

## ✅ Implementation Complete

### 🎯 Core Features Implemented

1. **Automatic PDF Invoice Generation**
   - PDF generation on successful payments
   - Argentina tax compliance (21% IVA)
   - Sequential invoice numbering (A-0001-00000042 format)
   - Configurable invoice types (A, B, C, RECEIPT)

2. **Fiscal Profile Management**
   - User fiscal profiles with CUIT support
   - Document validation (DNI/CUIT/CUIL)
   - Argentina address format
   - Unique constraint validation

3. **Email Integration**
   - PDF delivery via existing notification system
   - Template-based email notifications
   - Multi-channel support (EMAIL, PUSH, WHATSAPP)

4. **API Endpoints**
   - User fiscal profile management
   - Invoice listing and downloading
   - Admin invoice operations

5. **Future AFIP Integration Ready**
   - Extensible model for tax authority integration
   - Fiscal snapshot preservation
   - Sequence management for official numbering

### 🏗️ Architecture Components

#### Entities (3 new)
- `FiscalProfileEntity` - User tax information storage
- `InvoiceEntity` - Invoice records with PDF storage
- `InvoiceSequenceEntity` - Thread-safe invoice numbering

#### Repositories (3 new)
- `FiscalProfileRepository` - Fiscal profile data access
- `InvoiceRepository` - Invoice queries with pagination
- `InvoiceSequenceRepository` - Atomic sequence increment

#### Services (3 new)
- `FiscalProfileService` - Profile CRUD with validation
- `InvoiceService` - Complete invoice lifecycle management
- `InvoiceTemplateService` - HTML template generation

#### Controllers (1 new)
- `InvoicingController` - REST API with 7 endpoints
  - User endpoints: fiscal profile CRUD, invoice listing/download
  - Admin endpoints: invoice voiding, manual creation

#### Supporting Services (2 new)
- `PdfRenderer` - HTML to PDF conversion (extensible)
- `InvoicingMapper` - DTO/Entity mapping

#### Configuration (1 new)
- `InvoicingProperties` - Centralized configuration management
- Argentina timezone, tax rates, file storage options

### 📊 Database Schema

#### fiscal_profile table
```sql
CREATE TABLE fiscal_profile (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    legal_name VARCHAR(200) NOT NULL,
    tax_id VARCHAR(20), -- CUIT format: 20-12345678-3
    document_type VARCHAR(10) NOT NULL, -- DNI, CUIT, CUIL
    document_number VARCHAR(20) NOT NULL,
    address_line VARCHAR(300),
    city VARCHAR(100),
    state VARCHAR(100),
    postal_code VARCHAR(20),
    country VARCHAR(3) DEFAULT 'AR',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_fiscal_profile_user (user_id),
    INDEX idx_fiscal_profile_tax_id (tax_id)
);
```

#### invoice table
```sql
CREATE TABLE invoice (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    payment_id BIGINT NOT NULL UNIQUE,
    invoice_number VARCHAR(50) NOT NULL UNIQUE,
    invoice_type VARCHAR(20) NOT NULL DEFAULT 'RECEIPT',
    currency VARCHAR(3) NOT NULL DEFAULT 'ARS',
    subtotal DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    tax_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    tax_rate DECIMAL(5,2) NOT NULL DEFAULT 21.00,
    total DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    issue_date TIMESTAMP NOT NULL,
    status ENUM('ISSUED', 'VOID') NOT NULL DEFAULT 'ISSUED',
    fiscal_snapshot_json TEXT,
    pdf_path VARCHAR(500), -- For file system storage
    pdf_blob LONGBLOB, -- For database storage
    void_reason VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_invoice_user (user_id),
    INDEX idx_invoice_payment (payment_id),
    INDEX idx_invoice_number (invoice_number),
    INDEX idx_invoice_status (status),
    INDEX idx_invoice_issue_date (issue_date)
);
```

#### invoice_sequence table
```sql
CREATE TABLE invoice_sequence (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    pos VARCHAR(10) NOT NULL DEFAULT '0001',
    series VARCHAR(5) NOT NULL DEFAULT 'A',
    last_number BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_sequence_pos_series (pos, series)
);
```

### 🔧 Configuration Options

```properties
# Invoicing Configuration (application.properties)
gym.invoicing.timezone=America/Argentina/Buenos_Aires
gym.invoicing.defaultInvoiceType=RECEIPT
gym.invoicing.tax.enabled=true
gym.invoicing.tax.rate=21.0
gym.invoicing.numbering.pos=0001
gym.invoicing.numbering.series=A
gym.invoicing.storage.backend=FS  # FS or DB
gym.invoicing.storage.basePath=/var/app/invoices
gym.invoicing.email.from=no-reply@sigmagym.app
gym.invoicing.email.subjectTemplate=Your receipt \${invoiceNumber}
```

### 🚀 API Endpoints

#### User Endpoints
```bash
# Get fiscal profile
GET /api/invoicing/me/profile
Authorization: Bearer {jwt_token}

# Create/Update fiscal profile
PUT /api/invoicing/me/profile
Content-Type: application/json
Authorization: Bearer {jwt_token}
{
  "legalName": "Juan Perez",
  "taxId": "20-12345678-3",
  "documentType": "DNI",
  "documentNumber": "12345678",
  "addressLine": "Av. Siempre Viva 742",
  "city": "CABA",
  "state": "Buenos Aires",
  "postalCode": "1000",
  "country": "AR"
}

# List invoices (paginated)
GET /api/invoicing/me/invoices?page=0&size=10
Authorization: Bearer {jwt_token}

# Get invoice details
GET /api/invoicing/me/invoices/{invoiceId}
Authorization: Bearer {jwt_token}

# Download invoice PDF
GET /api/invoicing/me/invoices/{invoiceId}/pdf
Authorization: Bearer {jwt_token}
```

#### Admin Endpoints
```bash
# Void invoice
POST /api/invoicing/admin/invoices/void/{invoiceId}?reason=Administrative%20correction
Authorization: Bearer {admin_jwt_token}

# Manual invoice creation (testing)
POST /api/invoicing/admin/invoices/issue
Content-Type: application/x-www-form-urlencoded
Authorization: Bearer {admin_jwt_token}
paymentId=12345&description=Test%20Invoice&amount=5000.00&currency=ARS
```

### 🔐 Security & Validation

#### Authentication & Authorization
- **MEMBER role**: Manage own fiscal profile and invoices
- **TRAINER/OWNER roles**: Admin operations (void invoices)
- JWT-based authentication on all endpoints
- User-specific data access controls

#### Validation Rules
- **CUIT format**: XX-XXXXXXXX-X (11 digits with hyphens)
- **Document uniqueness**: Per user validation
- **Tax ID uniqueness**: Prevents duplicates
- **Required fields**: Legal name, document type/number
- **Input sanitization**: XSS protection, length limits

### 📈 Business Logic

#### Invoice Generation Process
1. **Payment Event Trigger**: Automatic on payment success
2. **User & Profile Loading**: Fiscal profile or fallback to user data
3. **Tax Calculation**: 21% IVA (configurable) on subtotal
4. **Sequential Numbering**: Thread-safe invoice number generation
5. **PDF Generation**: HTML template → PDF conversion
6. **Storage**: File system or database (configurable)
7. **Notification**: Email with PDF attachment
8. **Event Publishing**: `InvoiceIssuedEvent` for audit/integration

#### Fiscal Snapshot Strategy
- **Data Preservation**: Complete fiscal profile at issue time
- **JSON Storage**: Denormalized for historical accuracy  
- **Compliance Ready**: Immutable record for tax authorities
- **Fallback Handling**: User data when fiscal profile missing

#### PDF Storage Options
- **File System** (default): Configurable path, better performance
- **Database**: BLOB storage, better backup/replication
- **Future Extensions**: Cloud storage (S3, etc.)

### 🔗 Integration Points

#### Existing Notification System
- Extended `NotificationEvent` enum with `INVOICE_ISSUED`
- `InvoiceNotificationListener` for automatic email delivery
- Template-based notifications with PDF attachments
- Multi-channel support (EMAIL primary, PUSH/WHATSAPP secondary)

#### Payment System Integration
- `PaymentInvoicingListener` for automatic invoice creation
- Configurable for different payment providers
- Error handling and retry mechanisms
- Manual trigger capabilities for testing

#### Event-Driven Architecture
- `InvoiceIssuedEvent` for system integration
- Async processing for better performance
- Audit trail capabilities
- Future webhook support ready

### 🧪 Testing & Quality

#### Automated Testing
- `InvoicingTestService` for integration testing
- Fiscal profile creation and validation
- Invoice generation and PDF creation
- Error handling and edge cases

#### Error Handling
- Custom `InvoicingException` hierarchy:
  - `ProfileInvalidException`: Invalid fiscal data
  - `AlreadyInvoicedException`: Duplicate invoice prevention
  - `SequenceErrorException`: Numbering issues
  - `PdfErrorException`: PDF generation failures

#### Monitoring & Logging
- Comprehensive logging at DEBUG/INFO/ERROR levels
- Performance metrics for PDF generation
- Error tracking for failed invoice creation
- Audit trail for compliance requirements

### 🌟 Argentina Tax Compliance Features

#### CUIT Support
- Format validation (XX-XXXXXXXX-X)
- Uniqueness constraints
- Optional field (for individuals vs businesses)

#### Tax Calculation
- 21% IVA rate (configurable)
- Subtotal/tax/total breakdown
- Currency support (ARS default)

#### Invoice Numbering
- Series and Point of Sale support (A-0001-XXXXXXXX)
- Sequential numbering with concurrency control
- Future AFIP integration ready

#### Document Types
- DNI (Documento Nacional de Identidad)
- CUIT (Clave Única de Identificación Tributaria)  
- CUIL (Clave Única de Identificación Laboral)

### 📋 Future Enhancements Ready

#### AFIP Integration Preparation
- Extensible model for CAE (Código de Autorización Electrónica)
- WebService integration points defined
- Digital signature support structure
- Tax authority validation hooks

#### Advanced Features
- **Batch Processing**: Multiple invoices per payment
- **Credit Notes**: Invoice correction mechanisms
- **Multi-Currency**: Exchange rate integration
- **Digital Signatures**: PKI certificate support
- **Cloud Storage**: AWS S3/Google Cloud integration

## ✅ Production Ready

The invoicing system is now **production-ready** with:

- ✅ **Thread-safe operations** with pessimistic locking
- ✅ **Comprehensive error handling** and validation
- ✅ **Security controls** with role-based access
- ✅ **Audit trail** for compliance requirements
- ✅ **Configurable storage** (FS/DB) options
- ✅ **Argentina tax compliance** features
- ✅ **Integration ready** for existing notification system
- ✅ **PDF generation** with professional templates
- ✅ **API documentation** with cURL examples

The system provides enterprise-grade invoice management with automatic generation, comprehensive fiscal profile support, and full Argentina tax compliance preparation as requested!

## 🔄 Integration with Existing Systems

### Notification System
- Seamless integration with existing `NotificationService`
- Extended event types and templates
- PDF attachment support in emails

### User Management
- Leverages existing user authentication
- Role-based access control integration
- User profile data fallback mechanisms

### Payment System
- Event-driven invoice generation
- Payment ID linking for audit trails
- Support for multiple payment providers

The Sigma Gym Automatic Invoicing System is now complete and ready for production deployment!
