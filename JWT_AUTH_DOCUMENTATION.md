# JWT Authentication System API Documentation

## Overview
This document describes the JWT authentication system endpoints for the Sigma Gym backend application.

## Base URL
```
http://localhost:8080
```

## Authentication Endpoints

### 1. Login
**Endpoint:** `POST /auth/login`

**Description:** Authenticates a user and returns a JWT token.

**Request Body:**
```json
{
  "email": "owner@sigmamgym.com",
  "password": "owner123"
}
```

**Response (Success - 200 OK):**
```json
{
  "success": true,
  "message": "Success",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "type": "Bearer",
    "user": {
      "id": 1,
      "email": "owner@sigmamgym.com",
      "firstName": "Admin",
      "lastName": "Owner",
      "roles": ["OWNER"],
      "isActive": true,
      "createdAt": "2024-01-15T10:30:00",
      "lastLoginAt": "2024-01-15T15:45:30"
    }
  }
}
```

**Response (Error - 401 Unauthorized):**
```json
{
  "success": false,
  "message": "Invalid email or password",
  "data": null
}
```

### 2. Register
**Endpoint:** `POST /auth/register`

**Description:** Registers a new user and returns a JWT token.

**Request Body:**
```json
{
  "email": "newuser@example.com",
  "password": "password123",
  "firstName": "John",
  "lastName": "Doe",
  "role": "MEMBER"
}
```

**Available Roles:**
- `OWNER` (highest privilege)
- `TRAINER` (medium privilege)
- `MEMBER` (basic privilege)

**Response (Success - 200 OK):**
```json
{
  "success": true,
  "message": "Success",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "type": "Bearer",
    "user": {
      "id": 2,
      "email": "newuser@example.com",
      "firstName": "John",
      "lastName": "Doe",
      "roles": ["MEMBER"],
      "isActive": true,
      "createdAt": "2024-01-15T16:20:00",
      "lastLoginAt": null
    }
  }
}
```

**Response (Error - 409 Conflict):**
```json
{
  "success": false,
  "message": "Email already registered",
  "data": null
}
```

### 3. Get Current User
**Endpoint:** `GET /auth/me`

**Description:** Returns information about the currently authenticated user.

**Headers:**
```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

**Response (Success - 200 OK):**
```json
{
  "success": true,
  "message": "Success",
  "data": {
    "id": 1,
    "email": "owner@sigmamgym.com",
    "firstName": "Admin",
    "lastName": "Owner",
    "roles": ["OWNER"],
    "isActive": true,
    "createdAt": "2024-01-15T10:30:00",
    "lastLoginAt": "2024-01-15T15:45:30"
  }
}
```

**Response (Error - 401 Unauthorized):**
```json
{
  "success": false,
  "message": "User not authenticated",
  "data": null
}
```

### 4. Logout
**Endpoint:** `POST /auth/logout`

**Description:** Logs out the current user (client-side token removal).

**Headers:**
```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

**Response (Success - 200 OK):**
```json
{
  "success": true,
  "message": "Success",
  "data": "Logout successful"
}
```

## Seed Data

The system automatically creates the following test users on startup:

| Email | Password | Role | First Name | Last Name |
|-------|----------|------|------------|-----------|
| owner@sigmamgym.com | owner123 | OWNER | Admin | Owner |
| trainer@sigmamgym.com | trainer123 | TRAINER | John | Trainer |
| member@sigmamgym.com | member123 | MEMBER | Jane | Member |

## JWT Token Usage

### Including JWT in Requests
For protected endpoints, include the JWT token in the Authorization header:

```
Authorization: Bearer <your_jwt_token>
```

### Token Expiration
- JWT tokens expire after 24 hours (86400000 milliseconds)
- After expiration, users need to login again to get a new token

## CORS Configuration

The API is configured to accept requests from:
- `http://localhost:5173` (React development server)

Allowed methods: GET, POST, PUT, DELETE, OPTIONS, PATCH

## Role-Based Access Control

The system implements role-based access control with the following hierarchy:

1. **OWNER** (Priority 1) - Highest privileges
   - Access to `/admin/**` endpoints
   - Full system administration

2. **TRAINER** (Priority 2) - Medium privileges  
   - Access to `/api/trainer/**` endpoints
   - Training and workout management

3. **MEMBER** (Priority 3) - Basic privileges
   - Access to `/api/member/**` endpoints
   - Personal workout and progress tracking

## Error Handling

All endpoints return standardized error responses:

```json
{
  "success": false,
  "message": "Error description",
  "data": null
}
```

Common HTTP status codes:
- `200` - Success
- `400` - Bad Request (validation errors)
- `401` - Unauthorized (invalid credentials or token)
- `403` - Forbidden (insufficient permissions)
- `409` - Conflict (resource already exists)
- `500` - Internal Server Error

## Example Frontend Integration

### Login Example (React/JavaScript)
```javascript
const login = async (email, password) => {
  try {
    const response = await fetch('http://localhost:8080/auth/login', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      credentials: 'include',
      body: JSON.stringify({ email, password })
    });
    
    const result = await response.json();
    
    if (result.success) {
      // Store token in localStorage or secure storage
      localStorage.setItem('jwt_token', result.data.token);
      return result.data;
    } else {
      throw new Error(result.message);
    }
  } catch (error) {
    console.error('Login failed:', error);
    throw error;
  }
};
```

### Making Authenticated Requests
```javascript
const makeAuthenticatedRequest = async (url, options = {}) => {
  const token = localStorage.getItem('jwt_token');
  
  const response = await fetch(url, {
    ...options,
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json',
      ...options.headers,
    },
    credentials: 'include',
  });
  
  return response.json();
};
```
