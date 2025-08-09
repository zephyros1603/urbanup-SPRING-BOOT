# Task API Documentation

This document provides comprehensive documentation for all Task-related API endpoints in the UrbanUp application.

## Base URL
- **Backend Base**: `http://localhost:8080/api`
- **Task Endpoints Base**: `http://localhost:8080/api/tasks`

## Authentication
All endpoints require JWT authentication via `Authorization: Bearer <jwt_token>` header.

---

## Task Endpoints

### 1. Create Task
**POST** `/api/tasks`

Creates a new task for the authenticated user.

**Request Body:**
```json
{
    "posterId": 1,
    "title": "Help with grocery shopping",
    "description": "Need someone to help with weekly grocery shopping. Should take about 2 hours.",
    "category": "SHOPPING",
    "price": 25.00,
    "pricingType": "FIXED",
    "location": "Downtown Portland",
    "cityArea": "Pearl District",
    "fullAddress": "123 Main St, Portland, OR 97205",
    "deadline": "2024-12-31T18:00:00",
    "estimatedDurationHours": 2,
    "isUrgent": false,
    "specialRequirements": "Need someone with car",
    "skillsRequired": ["Driving", "Organization"]
}
```

**Response:**
```json
{
    "success": true,
    "message": "Task created successfully",
    "data": {
        "id": 1,
        "title": "Help with grocery shopping",
        "description": "Need someone to help with weekly grocery shopping. Should take about 2 hours.",
        "category": "SHOPPING",
        "status": "OPEN",
        "pricingType": "FIXED",
        "price": 25.00,
        "location": "Downtown Portland",
        "deadline": "2024-12-31T18:00:00",
        "estimatedDurationHours": 2,
        "isUrgent": false,
        "specialRequirements": "Need someone with car",
        "skillsRequired": ["Driving", "Organization"],
        "poster": {
            "id": 1,
            "username": "john_doe",
            "firstName": "John",
            "lastName": "Doe"
        },
        "createdAt": "2024-08-08T12:00:00",
        "updatedAt": "2024-08-08T12:00:00"
    }
}
```

---

### 2. Get Task by ID
**GET** `/api/tasks/{taskId}`

Retrieves details of a specific task.

**Path Parameters:**
- `taskId`: Long - ID of the task

**Response:**
```json
{
    "success": true,
    "message": "Task found",
    "data": {
        "id": 1,
        "title": "Help with grocery shopping",
        "description": "Need someone to help with weekly grocery shopping. Should take about 2 hours.",
        "category": "SHOPPING",
        "status": "OPEN",
        "pricingType": "FIXED",
        "price": 25.00,
        "location": "Downtown Portland",
        "deadline": "2024-12-31T18:00:00",
        "estimatedDurationHours": 2,
        "isUrgent": false,
        "specialRequirements": "Need someone with car",
        "skillsRequired": ["Driving", "Organization"],
        "poster": {
            "id": 1,
            "username": "john_doe",
            "firstName": "John",
            "lastName": "Doe"
        },
        "fulfiller": null,
        "createdAt": "2024-08-08T12:00:00",
        "updatedAt": "2024-08-08T12:00:00"
    }
}
```

---

### 3. Update Task
**PUT** `/api/tasks/{taskId}`

Updates an existing task. Only the task poster can update their task.

**Path Parameters:**
- `taskId`: Long - ID of the task to update

**Request Body:** (Same structure as Create Task)
```json
{
    "posterId": 1,
    "title": "Updated: Help with grocery shopping",
    "description": "Updated description: Need someone to help with weekly grocery shopping. Should take about 2-3 hours.",
    "category": "SHOPPING",
    "price": 30.00,
    "pricingType": "FIXED",
    "location": "Downtown Portland",
    "cityArea": "Pearl District",
    "fullAddress": "123 Main St, Portland, OR 97205",
    "deadline": "2024-12-31T18:00:00",
    "estimatedDurationHours": 3,
    "isUrgent": true,
    "specialRequirements": "Need someone with car and experience",
    "skillsRequired": ["Driving", "Organization", "Experience"]
}
```

**Response:** (Same structure as Create Task response)

---

### 4. Delete Task
**DELETE** `/api/tasks/{taskId}`

Deletes a task. Only the task poster can delete their task. Task can only be deleted if it's in OPEN status and has no applications.

**Path Parameters:**
- `taskId`: Long - ID of the task to delete

**Response:**
```json
{
    "success": true,
    "message": "Task deleted successfully",
    "data": null
}
```

**Error Response:**
```json
{
    "success": false,
    "message": "Task cannot be deleted because it has applications or is not in OPEN status",
    "data": null
}
```

---

### 5. Get Available Tasks
**GET** `/api/tasks`

Gets all available tasks for the current user. Excludes:
- Tasks the user has already applied for
- Tasks the user has posted themselves
- Tasks not in OPEN status

**Response:**
```json
{
    "success": true,
    "message": "Available tasks retrieved successfully",
    "data": [
        {
            "id": 1,
            "title": "Help with grocery shopping",
            "description": "Need someone to help with weekly grocery shopping.",
            "category": "SHOPPING",
            "status": "OPEN",
            "pricingType": "FIXED",
            "price": 25.00,
            "location": "Downtown Portland",
            "deadline": "2024-12-31T18:00:00",
            "estimatedDurationHours": 2,
            "isUrgent": false,
            "poster": {
                "id": 2,
                "username": "jane_smith",
                "firstName": "Jane",
                "lastName": "Smith"
            },
            "createdAt": "2024-08-08T12:00:00"
        }
    ]
}
```

---

### 6. Get All Tasks (Admin)
**GET** `/api/tasks/all`

Gets all tasks regardless of status (for admin purposes).

**Response:** (Same structure as Get Available Tasks)

---

### 7. Get Tasks by Poster
**GET** `/api/tasks/poster/{posterId}`

Gets all tasks posted by a specific user.

**Path Parameters:**
- `posterId`: Long - ID of the poster

**Response:** (Same structure as Get Available Tasks)

---

### 8. Get Tasks by Fulfiller
**GET** `/api/tasks/fulfiller/{fulfillerId}`

Gets all tasks assigned to a specific fulfiller.

**Path Parameters:**
- `fulfillerId`: Long - ID of the fulfiller

**Response:** (Same structure as Get Available Tasks)

---

### 9. Search Tasks
**GET** `/api/tasks/search`

Search tasks by keyword in title or description.

**Query Parameters:**
- `keyword`: String - Search keyword
- `category`: String (optional) - Filter by category
- `location`: String (optional) - Filter by location

**Example:** `/api/tasks/search?keyword=shopping&category=SHOPPING&location=Portland`

**Response:** (Same structure as Get Available Tasks)

---

### 10. Get Tasks by Category
**GET** `/api/tasks/category/{category}`

Gets all available tasks in a specific category for the current user.

**Path Parameters:**
- `category`: TaskCategory enum value

**Response:** (Same structure as Get Available Tasks)

---

### 11. Get Urgent Tasks
**GET** `/api/tasks/urgent`

Gets all urgent tasks available for the current user.

**Response:** (Same structure as Get Available Tasks)

---

### 12. Apply for Task
**POST** `/api/tasks/{taskId}/apply`

Apply for a specific task. Creates a new TaskApplication.

**Path Parameters:**
- `taskId`: Long - ID of the task to apply for

**Request Body:**
```json
{
    "proposedPrice": 25.00,
    "message": "I have 3 years of experience with grocery shopping and have my own car. I can complete this task efficiently.",
    "estimatedCompletionTime": "2024-08-10T16:00:00"
}
```

**Response:**
```json
{
    "success": true,
    "message": "Application submitted successfully",
    "data": {
        "id": 1,
        "task": {
            "id": 1,
            "title": "Help with grocery shopping"
        },
        "applicant": {
            "id": 2,
            "username": "jane_doe",
            "firstName": "Jane",
            "lastName": "Doe"
        },
        "proposedPrice": 25.00,
        "message": "I have 3 years of experience with grocery shopping and have my own car.",
        "estimatedCompletionTime": "2024-08-10T16:00:00",
        "status": "PENDING",
        "appliedAt": "2024-08-08T14:30:00"
    }
}
```

---

### 13. Accept Task Application
**PUT** `/api/tasks/{taskId}/applications/{applicationId}/accept`

Accept a specific application for a task. Only the task poster can accept applications.

**Path Parameters:**
- `taskId`: Long - ID of the task
- `applicationId`: Long - ID of the application to accept

**Response:**
```json
{
    "success": true,
    "message": "Application accepted successfully",
    "data": {
        "id": 1,
        "title": "Help with grocery shopping",
        "status": "ACCEPTED",
        "fulfiller": {
            "id": 2,
            "username": "jane_doe",
            "firstName": "Jane",
            "lastName": "Doe"
        }
    }
}
```

---

### 14. Mark Task as Complete
**PUT** `/api/tasks/{taskId}/complete`

Mark a task as completed. Only the assigned fulfiller can mark task as complete.

**Path Parameters:**
- `taskId`: Long - ID of the task to complete

**Response:**
```json
{
    "success": true,
    "message": "Task marked as completed",
    "data": {
        "id": 1,
        "title": "Help with grocery shopping",
        "status": "COMPLETED",
        "completedAt": "2024-08-10T16:00:00"
    }
}
```

---

### 15. Confirm Task Completion
**PUT** `/api/tasks/{taskId}/confirm`

Confirm task completion and finalize payment. Only the task poster can confirm completion.

**Path Parameters:**
- `taskId`: Long - ID of the task to confirm

**Response:**
```json
{
    "success": true,
    "message": "Task completion confirmed",
    "data": {
        "id": 1,
        "title": "Help with grocery shopping",
        "status": "CONFIRMED",
        "confirmedAt": "2024-08-10T18:00:00"
    }
}
```

---

### 16. Get Task Applications
**GET** `/api/tasks/{taskId}/applications`

Get all applications for a specific task. Only the task poster can view applications.

**Path Parameters:**
- `taskId`: Long - ID of the task

**Response:**
```json
{
    "success": true,
    "message": "Applications retrieved successfully",
    "data": [
        {
            "id": 1,
            "applicant": {
                "id": 2,
                "username": "jane_doe",
                "firstName": "Jane",
                "lastName": "Doe",
                "email": "jane@example.com",
                "rating": 4.8
            },
            "proposedPrice": 25.00,
            "message": "I have 3 years of experience with grocery shopping and have my own car.",
            "estimatedCompletionTime": "2024-08-10T16:00:00",
            "status": "PENDING",
            "appliedAt": "2024-08-08T14:30:00"
        }
    ]
}
```

---

## Enums and Constants

### TaskCategory Values:
- `PERSONAL_ERRANDS`
- `PROFESSIONAL_TASKS`
- `HOUSEHOLD_HELP`
- `MICRO_GIGS`
- `DELIVERY`
- `CLEANING`
- `REPAIR_MAINTENANCE`
- `SHOPPING`
- `ADMINISTRATIVE`
- `OTHER`

### TaskStatus Values:
- `OPEN`
- `ACCEPTED`
- `IN_PROGRESS`
- `COMPLETED`
- `CONFIRMED`
- `CANCELLED`
- `DISPUTED`

### PricingType Values:
- `FIXED`
- `HOURLY`

### ApplicationStatus Values:
- `PENDING`
- `ACCEPTED`
- `REJECTED`
- `WITHDRAWN`

---

## Error Responses

### Common Error Format:
```json
{
    "success": false,
    "message": "Error description",
    "data": null
}
```

### Common HTTP Status Codes:
- `200 OK` - Success
- `201 Created` - Resource created successfully
- `400 Bad Request` - Invalid request data
- `401 Unauthorized` - Authentication required
- `403 Forbidden` - Access denied
- `404 Not Found` - Resource not found
- `500 Internal Server Error` - Server error

---

## Implementation Notes

### Key Features Implemented:
1. **User Filtering**: Tasks exclude those the user has applied for and their own tasks
2. **Multiple Applications**: Multiple users can apply for the same task
3. **Application Prevention**: Users cannot apply for tasks they've already applied for
4. **Ownership Validation**: Only task posters can update/delete their tasks
5. **Status Management**: Tasks follow proper status workflow (OPEN → ACCEPTED → COMPLETED → CONFIRMED)
6. **Authentication Integration**: All endpoints use JWT authentication via CurrentUserService

### Database Relationships:
- `Task` ↔ `User` (poster relationship)
- `Task` ↔ `User` (fulfiller relationship)
- `Task` ↔ `TaskApplication` (one-to-many)
- `TaskApplication` ↔ `User` (applicant relationship)

This API documentation covers all the implemented endpoints with proper JSON request/response formats for frontend integration.
