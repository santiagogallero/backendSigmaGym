# Workout Plans V2 API Documentation

## Overview
Enhanced API for managing workout plans with slug-based identification, status management, and drag & drop reordering capabilities.

## Base URLs
- Workout Plans: `/api/v2/workout-plans`
- Plan Days: `/api/v2/plan-days`  
- Plan Exercises: `/api/v2/plan-exercises`

## Authentication
All endpoints require authentication with `USER` role.

---

## 🏋️ Workout Plans Endpoints

### Create Workout Plan
```http
POST /api/v2/workout-plans?ownerId=1
Content-Type: application/json

{
  "name": "My Strength Training",
  "goal": "Build muscle and strength",
  "description": "Focus on compound movements",
  "difficulty": "Intermediate",
  "startDate": "2025-01-01",
  "endDate": "2025-03-31"
}
```

### Get Workout Plan by Slug
```http
GET /api/v2/workout-plans/my-strength-training?ownerId=1
```

### Get All Workout Plans
```http
GET /api/v2/workout-plans?ownerId=1&status=ACTIVE&search=strength
```

### Update Workout Plan
```http
PUT /api/v2/workout-plans/my-strength-training?ownerId=1
Content-Type: application/json

{
  "name": "Advanced Strength Training",
  "difficulty": "Advanced"
}
```

### Change Plan Status
```http
PATCH /api/v2/workout-plans/my-strength-training/status?ownerId=1&status=ACTIVE
```

### Get Plan Statistics
```http
GET /api/v2/workout-plans/my-strength-training/stats?ownerId=1
```

---

## 📅 Plan Days Endpoints

### Create Plan Day
```http
POST /api/v2/plan-days?planId=1
Content-Type: application/json

{
  "title": "Upper Body Day",
  "description": "Focus on chest, back, and arms",
  "orderIndex": 0
}
```

### Get Plan Days
```http
GET /api/v2/plan-days?planId=1
```

### Update Plan Day
```http
PUT /api/v2/plan-days/1
Content-Type: application/json

{
  "title": "Push Day",
  "description": "Chest, shoulders, triceps"
}
```

### Reorder Plan Days (Drag & Drop)
```http
POST /api/v2/plan-days/reorder?planId=1&fromIndex=0&toIndex=2
```

### Insert Plan Day at Position
```http
POST /api/v2/plan-days/insert?planId=1&position=1
Content-Type: application/json

{
  "title": "Rest Day",
  "description": "Active recovery"
}
```

---

## 💪 Plan Exercises Endpoints

### Create Plan Exercise
```http
POST /api/v2/plan-exercises?dayId=1
Content-Type: application/json

{
  "name": "Bench Press",
  "description": "Barbell bench press with proper form",
  "reps": 8,
  "sets": 4,
  "weight": 80.5,
  "weightUnit": "kg",
  "restTimeSeconds": 180,
  "isWarmup": false,
  "notes": "Focus on controlled movement"
}
```

### Get Plan Exercises
```http
GET /api/v2/plan-exercises?dayId=1&type=main
```

### Get Warmup Exercises Only
```http
GET /api/v2/plan-exercises?dayId=1&type=warmup
```

### Update Plan Exercise
```http
PUT /api/v2/plan-exercises/1
Content-Type: application/json

{
  "reps": 10,
  "sets": 3,
  "weight": 85.0
}
```

### Reorder Plan Exercises (Drag & Drop)
```http
POST /api/v2/plan-exercises/reorder?dayId=1&fromIndex=0&toIndex=3
```

### Search Exercises by Name
```http
GET /api/v2/plan-exercises/search?dayId=1&name=press
```

---

## 📊 Response Format

All endpoints return data in this format:

```json
{
  "success": true,
  "message": "Operation completed successfully",
  "data": {
    // Response data here
  }
}
```

Error responses:
```json
{
  "success": false,
  "message": "Error description",
  "data": null
}
```

---

## 🎯 Key Features

### 1. **Slug-Based Identification**
- Plans are identified by URL-friendly slugs (e.g., "my-strength-training")
- Automatic slug generation from plan names
- Unique per owner, allows duplicate names

### 2. **Status Management**
- `DRAFT`: Can be edited freely
- `ACTIVE`: Read-only, cannot be modified
- `ARCHIVED`: Historical plans

### 3. **Drag & Drop Support**
- Reorder days within a plan
- Reorder exercises within a day  
- Insert at specific positions
- Automatic index management

### 4. **Order Management**
- Zero-based indexing (0, 1, 2, ...)
- Automatic gap closing when items are deleted
- Validation and repair functions available

### 5. **Exercise Classification**
- Warmup exercises (`isWarmup: true`)
- Main exercises (`isWarmup: false`)
- Filter by type when retrieving

---

## 🔧 Utilities

### Validate and Fix Order
```http
POST /api/v2/plan-days/validate-order?planId=1
POST /api/v2/plan-exercises/validate-order?dayId=1
```

### Get Next Available Index
```http
GET /api/v2/plan-days/next-order-index?planId=1
GET /api/v2/plan-exercises/next-order-index?dayId=1
```

---

## 🚀 Example Workflow

1. **Create a workout plan**
2. **Add days to the plan** (Day 1, Day 2, etc.)
3. **Add exercises to each day** (warmup + main exercises)
4. **Use drag & drop** to reorder as needed
5. **Change status to ACTIVE** when ready
6. **View statistics** to analyze the plan

This API provides complete flexibility for building complex workout plans with professional drag & drop functionality!

---

## 💰 Checkout Endpoints

### Create Checkout Preference
```http
POST /api/checkout/create-preference
Content-Type: application/json
Authorization: Bearer <JWT_TOKEN>

{
  "planId": 1
}
```

**Response:**
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

**Features:**
- JWT authentication required (MEMBER, TRAINER, or OWNER roles)
- Creates Mercado Pago checkout preference
- Returns exact contract expected by frontend
- Validates plan availability and status
- Generates unique external reference
- Complete error handling

For detailed documentation see: [CHECKOUT_API_DOCUMENTATION.md](CHECKOUT_API_DOCUMENTATION.md)
