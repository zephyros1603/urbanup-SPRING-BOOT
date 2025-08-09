# UrbanUp Task Marketplace API Documentation

## Overview

This document outlines all the API endpoints required for the UrbanUp Task Marketplace application. The API follows RESTful conventions and returns JSON responses with consistent error handling and pagination where applicable.

**Base URL:** `https://api.urbanup.com/v1`

**Authentication:** Bearer token authentication using JWT tokens.

---

## Response Format

### Standard API Response
```json
{
  "success": boolean,
  "message": string,
  "data": object | array,
  "timestamp": string,
  "path": string (optional)
}
```

### Paginated Response
```json
{
  "success": boolean,
  "message": string,
  "data": {
    "content": array,
    "page": {
      "size": number,
      "number": number,
      "totalElements": number,
      "totalPages": number
    }
  }
}
```

### Error Response
```json
{
  "success": false,
  "message": string,
  "errors": array (optional),
  "timestamp": string,
  "path": string
}
```

---

## Authentication Endpoints

### 1. User Registration
**POST** `/auth/register`

**Description:** Register a new user account

**Request Body:**
```json
{
  "firstName": "string",
  "lastName": "string", 
  "email": "string",
  "password": "string",
  "phoneNumber": "string"
}
```

**Response:**
```json
{
  "success": true,
  "message": "User registered successfully",
  "data": {
    "user": {User object},
    "accessToken": "string",
    "refreshToken": "string"
  }
}
```

### 2. User Login
**POST** `/auth/login`

**Description:** Authenticate user and get access tokens

**Request Body:**
```json
{
  "email": "string",
  "password": "string"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "user": {User object},
    "accessToken": "string",
    "refreshToken": "string"
  }
}
```

### 3. Refresh Token
**POST** `/auth/refresh`

**Description:** Get new access token using refresh token

**Request Body:**
```json
{
  "refreshToken": "string"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Token refreshed successfully",
  "data": {
    "accessToken": "string",
    "refreshToken": "string"
  }
}
```

### 4. Logout
**POST** `/auth/logout`

**Description:** Invalidate user session and tokens

**Headers:** `Authorization: Bearer {token}`

**Response:**
```json
{
  "success": true,
  "message": "Logout successful"
}
```

### 5. Forgot Password
**POST** `/auth/forgot-password`

**Description:** Send password reset email

**Request Body:**
```json
{
  "email": "string"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Password reset email sent"
}
```

### 6. Reset Password
**POST** `/auth/reset-password`

**Description:** Reset password using reset token

**Request Body:**
```json
{
  "resetToken": "string",
  "newPassword": "string"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Password reset successful"
}
```

---

## User Management Endpoints

### 1. Get User Profile
**GET** `/users/{userId}`

**Description:** Get user profile information

**Headers:** `Authorization: Bearer {token}`

**Response:**
```json
{
  "success": true,
  "message": "User profile retrieved successfully",
  "data": {User object with UserProfile}
}
```

### 2. Update User Profile
**PUT** `/users/{userId}/profile`

**Description:** Update user profile information

**Headers:** `Authorization: Bearer {token}`

**Request Body:**
```json
{
  "firstName": "string",
  "lastName": "string",
  "phoneNumber": "string",
  "profilePictureUrl": "string",
  "dateOfBirth": "string",
  "userProfile": {
    "bio": "string",
    "address": "string",
    "city": "string",
    "state": "string",
    "pincode": "string",
    "latitude": number,
    "longitude": number,
    "gender": "MALE|FEMALE|OTHER|PREFER_NOT_TO_SAY",
    "skills": ["string"],
    "interests": ["string"],
    "languagesSpoken": ["string"],
    "emergencyContactName": "string",
    "emergencyContactPhone": "string",
    "preferredWorkRadius": number,
    "isAvailableForWork": boolean,
    "workingHours": "string"
  }
}
```

**Response:**
```json
{
  "success": true,
  "message": "Profile updated successfully",
  "data": {User object}
}
```

### 3. Update User Theme
**PUT** `/users/{userId}/theme`

**Description:** Update user's theme preference

**Headers:** `Authorization: Bearer {token}`

**Request Body:**
```json
{
  "theme": "LIGHT|DARK"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Theme updated successfully",
  "data": {User object}
}
```

### 4. Verify Email
**POST** `/users/{userId}/verify-email`

**Description:** Verify user's email address

**Headers:** `Authorization: Bearer {token}`

**Request Body:**
```json
{
  "code": "string"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Email verified successfully"
}
```

### 5. Verify Phone
**POST** `/users/{userId}/verify-phone`

**Description:** Verify user's phone number

**Headers:** `Authorization: Bearer {token}`

**Request Body:**
```json
{
  "code": "string"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Phone verified successfully"
}
```

### 6. Search Users
**GET** `/users/search`

**Description:** Search for users by name or skills

**Headers:** `Authorization: Bearer {token}`

**Query Parameters:**
- `query` (string): Search term
- `page` (number, default: 0): Page number
- `size` (number, default: 20): Page size

**Response:**
```json
{
  "success": true,
  "message": "Users retrieved successfully",
  "data": {
    "content": [User objects],
    "page": {
      "size": number,
      "number": number,
      "totalElements": number,
      "totalPages": number
    }
  }
}
```

### 7. Get User Count
**GET** `/users/count`

**Description:** Get total number of registered users

**Response:**
```json
{
  "success": true,
  "message": "User count retrieved successfully",
  "data": number
}
```

### 8. Upload Profile Picture
**POST** `/users/{userId}/profile-picture`

**Description:** Upload user profile picture

**Headers:** `Authorization: Bearer {token}`

**Request:** Form-data with file upload

**Response:**
```json
{
  "success": true,
  "message": "Profile picture uploaded successfully",
  "data": {
    "profilePictureUrl": "string"
  }
}
```

### 9. KYC Verification
**POST** `/users/{userId}/kyc-verify`

**Description:** Submit KYC documents for verification

**Headers:** `Authorization: Bearer {token}`

**Request Body:**
```json
{
  "kycDocumentType": "AADHAAR|PAN|PASSPORT|DRIVING_LICENSE",
  "kycDocumentNumber": "string",
  "documentFrontUrl": "string",
  "documentBackUrl": "string",
  "selfieUrl": "string"
}
```

**Response:**
```json
{
  "success": true,
  "message": "KYC documents submitted for verification",
  "data": {
    "verificationId": "string",
    "status": "PENDING"
  }
}
```

---

## Task Management Endpoints

### 1. Get All Tasks (Browse)
**GET** `/tasks`

**Description:** Get paginated list of tasks with filters

**Query Parameters:**
- `title` (string): Search by title/description
- `category` (string): Filter by category
- `minPrice` (number): Minimum price filter
- `maxPrice` (number): Maximum price filter
- `location` (string): Filter by location
- `isUrgent` (boolean): Filter urgent tasks only
- `status` (string): Filter by task status
- `latitude` (number): User's latitude for distance calculation
- `longitude` (number): User's longitude for distance calculation
- `radius` (number): Search radius in kilometers
- `sortBy` (string): Sort criteria (price, date, distance)
- `page` (number, default: 0): Page number
- `size` (number, default: 10): Page size

**Response:**
```json
{
  "success": true,
  "message": "Tasks retrieved successfully",
  "data": {
    "content": [Task objects],
    "page": {
      "size": number,
      "number": number,
      "totalElements": number,
      "totalPages": number
    }
  }
}
```

### 2. Create Task
**POST** `/tasks`

**Description:** Create a new task posting

**Headers:** `Authorization: Bearer {token}`

**Request Body:**
```json
{
  "title": "string",
  "description": "string",
  "category": "PERSONAL_ERRANDS|PROFESSIONAL_TASKS|HOUSEHOLD_HELP|MICRO_GIGS",
  "price": number,
  "pricingType": "FIXED|HOURLY",
  "location": "string",
  "address": "string",
  "latitude": number,
  "longitude": number,
  "deadline": "string (ISO date)",
  "isUrgent": boolean,
  "requirements": "string",
  "images": ["string"],
  "files": ["string"],
  "estimatedDuration": number,
  "skillsRequired": ["string"]
}
```

**Response:**
```json
{
  "success": true,
  "message": "Task created successfully",
  "data": {Task object}
}
```

### 3. Get Task by ID
**GET** `/tasks/{taskId}`

**Description:** Get detailed information about a specific task

**Response:**
```json
{
  "success": true,
  "message": "Task retrieved successfully",
  "data": {Task object with applications and reviews}
}
```

### 4. Update Task
**PUT** `/tasks/{taskId}`

**Description:** Update task information (only by task poster)

**Headers:** `Authorization: Bearer {token}`

**Request Body:** (Partial Task object)
```json
{
  "title": "string",
  "description": "string",
  "price": number,
  "deadline": "string",
  "isUrgent": boolean,
  "requirements": "string"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Task updated successfully",
  "data": {Task object}
}
```

### 5. Delete Task
**DELETE** `/tasks/{taskId}`

**Description:** Delete a task (only by task poster)

**Headers:** `Authorization: Bearer {token}`

**Response:**
```json
{
  "success": true,
  "message": "Task deleted successfully"
}
```

### 6. Update Task Status
**PUT** `/tasks/{taskId}/status`

**Description:** Update task status

**Headers:** `Authorization: Bearer {token}`

**Request Body:**
```json
{
  "status": "OPEN|ACCEPTED|IN_PROGRESS|COMPLETED|CONFIRMED|CANCELLED"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Task status updated successfully",
  "data": {Task object}
}
```

### 7. Get User's Posted Tasks
**GET** `/tasks/user/{userId}`

**Description:** Get tasks posted by a specific user

**Headers:** `Authorization: Bearer {token}`

**Query Parameters:**
- `status` (string): Filter by status
- `page` (number, default: 0): Page number
- `size` (number, default: 10): Page size

**Response:**
```json
{
  "success": true,
  "message": "User tasks retrieved successfully",
  "data": {
    "content": [Task objects],
    "page": {
      "size": number,
      "number": number,
      "totalElements": number,
      "totalPages": number
    }
  }
}
```

### 8. Get Assigned Tasks
**GET** `/tasks/assigned/{userId}`

**Description:** Get tasks assigned to a specific user

**Headers:** `Authorization: Bearer {token}`

**Query Parameters:**
- `status` (string): Filter by status
- `page` (number, default: 0): Page number
- `size` (number, default: 10): Page size

**Response:**
```json
{
  "success": true,
  "message": "Assigned tasks retrieved successfully",
  "data": {
    "content": [Task objects],
    "page": {
      "size": number,
      "number": number,
      "totalElements": number,
      "totalPages": number
    }
  }
}
```

### 9. Upload Task Media
**POST** `/tasks/{taskId}/media`

**Description:** Upload images or files for a task

**Headers:** `Authorization: Bearer {token}`

**Request:** Form-data with file upload

**Response:**
```json
{
  "success": true,
  "message": "Media uploaded successfully",
  "data": {
    "mediaUrls": ["string"]
  }
}
```

### 10. Search Tasks
**GET** `/tasks/search`

**Description:** Search for tasks with advanced filters

**Query Parameters:**
- `keyword` (string): Search term
- `category` (string): Filter by category
- `minPrice` (number): Minimum price filter
- `maxPrice` (number): Maximum price filter
- `latitude` (number): User's latitude for distance calculation
- `longitude` (number): User's longitude for distance calculation
- `radius` (number): Search radius in kilometers
- `page` (number, default: 0): Page number
- `size` (number, default: 10): Page size

**Response:**
```json
{
  "success": true,
  "message": "Search results retrieved successfully",
  "data": {
    "content": [Task objects],
    "page": {
      "size": number,
      "number": number,
      "totalElements": number,
      "totalPages": number
    }
  }
}
```

---

## Task Application Endpoints

### 1. Apply for Task
**POST** `/tasks/{taskId}/apply`

**Description:** Submit an application for a task

**Headers:** `Authorization: Bearer {token}`

**Request Body:**
```json
{
  "proposalText": "string",
  "proposedPrice": number,
  "estimatedCompletionTime": "string (ISO date)"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Application submitted successfully",
  "data": {TaskApplication object}
}
```

### 2. Get Task Applications
**GET** `/tasks/{taskId}/applications`

**Description:** Get all applications for a task (only task poster)

**Headers:** `Authorization: Bearer {token}`

**Response:**
```json
{
  "success": true,
  "message": "Applications retrieved successfully",
  "data": [TaskApplication objects]
}
```

### 3. Accept Application
**PUT** `/tasks/{taskId}/applications/{applicationId}/accept`

**Description:** Accept a task application

**Headers:** `Authorization: Bearer {token}`

**Response:**
```json
{
  "success": true,
  "message": "Application accepted successfully",
  "data": {Task object}
}
```

### 4. Reject Application
**PUT** `/tasks/{taskId}/applications/{applicationId}/reject`

**Description:** Reject a task application

**Headers:** `Authorization: Bearer {token}`

**Request Body:**
```json
{
  "reason": "string"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Application rejected successfully"
}
```

### 5. Withdraw Application
**DELETE** `/tasks/{taskId}/applications/{applicationId}`

**Description:** Withdraw a task application

**Headers:** `Authorization: Bearer {token}`

**Response:**
```json
{
  "success": true,
  "message": "Application withdrawn successfully"
}
```

### 6. Get User Applications
**GET** `/users/{userId}/applications`

**Description:** Get all applications submitted by a user

**Headers:** `Authorization: Bearer {token}`

**Query Parameters:**
- `status` (string): Filter by status
- `page` (number, default: 0): Page number
- `size` (number, default: 10): Page size

**Response:**
```json
{
  "success": true,
  "message": "User applications retrieved successfully",
  "data": {
    "content": [TaskApplication objects],
    "page": {
      "size": number,
      "number": number,
      "totalElements": number,
      "totalPages": number
    }
  }
}
```

---

## Chat and Messaging Endpoints

### 1. Get User Chats
**GET** `/chats/user/{userId}`

**Description:** Get all chats for a user

**Headers:** `Authorization: Bearer {token}`

**Response:**
```json
{
  "success": true,
  "message": "Chats retrieved successfully",
  "data": [Chat objects]
}
```

### 2. Create Task Chat
**POST** `/chats/task/{taskId}`

**Description:** Create a chat for a task

**Headers:** `Authorization: Bearer {token}`

**Response:**
```json
{
  "success": true,
  "message": "Chat created successfully",
  "data": {Chat object}
}
```

### 3. Get Chat Messages
**GET** `/chats/{chatId}/messages`

**Description:** Get messages in a chat

**Headers:** `Authorization: Bearer {token}`

**Query Parameters:**
- `page` (number, default: 0): Page number
- `size` (number, default: 50): Page size

**Response:**
```json
{
  "success": true,
  "message": "Messages retrieved successfully",
  "data": {
    "content": [Message objects],
    "page": {
      "size": number,
      "number": number,
      "totalElements": number,
      "totalPages": number
    }
  }
}
```

### 4. Send Message
**POST** `/chats/{chatId}/messages`

**Description:** Send a message in a chat

**Headers:** `Authorization: Bearer {token}`

**Request Body:**
```json
{
  "content": "string",
  "type": "TEXT|IMAGE|FILE|LOCATION|SYSTEM",
  "attachmentUrl": "string"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Message sent successfully",
  "data": {Message object}
}
```

### 5. Mark Messages as Read
**PUT** `/chats/{chatId}/messages/read`

**Description:** Mark all messages in a chat as read

**Headers:** `Authorization: Bearer {token}`

**Response:**
```json
{
  "success": true,
  "message": "Messages marked as read"
}
```

### 6. Upload Chat Media
**POST** `/chats/{chatId}/media`

**Description:** Upload media for chat messages

**Headers:** `Authorization: Bearer {token}`

**Request:** Form-data with file upload

**Response:**
```json
{
  "success": true,
  "message": "Media uploaded successfully",
  "data": {
    "mediaUrl": "string"
  }
}
```

---

## Review and Rating Endpoints

### 1. Submit Review
**POST** `/tasks/{taskId}/review`

**Description:** Submit a review after task completion

**Headers:** `Authorization: Bearer {token}`

**Request Body:**
```json
{
  "rating": number,
  "comment": "string",
  "revieweeId": number
}
```

**Response:**
```json
{
  "success": true,
  "message": "Review submitted successfully",
  "data": {Review object}
}
```

### 2. Get Task Reviews
**GET** `/tasks/{taskId}/reviews`

**Description:** Get reviews for a task

**Response:**
```json
{
  "success": true,
  "message": "Reviews retrieved successfully",
  "data": [Review objects]
}
```

### 3. Get User Reviews
**GET** `/users/{userId}/reviews`

**Description:** Get reviews for a user

**Query Parameters:**
- `type` (string): "given" or "received"
- `page` (number, default: 0): Page number
- `size` (number, default: 10): Page size

**Response:**
```json
{
  "success": true,
  "message": "User reviews retrieved successfully",
  "data": {
    "content": [Review objects],
    "page": {
      "size": number,
      "number": number,
      "totalElements": number,
      "totalPages": number
    }
  }
}
```

### 4. Update Review
**PUT** `/reviews/{reviewId}`

**Description:** Update a review (within 24 hours)

**Headers:** `Authorization: Bearer {token}`

**Request Body:**
```json
{
  "rating": number,
  "comment": "string"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Review updated successfully",
  "data": {Review object}
}
```

### 5. Delete Review
**DELETE** `/reviews/{reviewId}`

**Description:** Delete a review (within 24 hours)

**Headers:** `Authorization: Bearer {token}`

**Response:**
```json
{
  "success": true,
  "message": "Review deleted successfully"
}
```

---

## Payment Endpoints

### 1. Create Payment Intent
**POST** `/payments/intent`

**Description:** Create a payment intent for a task

**Headers:** `Authorization: Bearer {token}`

**Request Body:**
```json
{
  "taskId": number,
  "amount": number,
  "paymentMethod": "string"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Payment intent created successfully",
  "data": {
    "paymentIntentId": "string",
    "clientSecret": "string",
    "amount": number
  }
}
```

### 2. Confirm Payment
**POST** `/payments/{paymentId}/confirm`

**Description:** Confirm a payment

**Headers:** `Authorization: Bearer {token}`

**Request Body:**
```json
{
  "paymentIntentId": "string"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Payment confirmed successfully",
  "data": {Payment object}
}
```

### 3. Process Refund
**POST** `/payments/{paymentId}/refund`

**Description:** Process a payment refund

**Headers:** `Authorization: Bearer {token}`

**Request Body:**
```json
{
  "amount": number,
  "reason": "string"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Refund processed successfully",
  "data": {
    "refundId": "string",
    "amount": number,
    "status": "PENDING"
  }
}
```

### 4. Get Payment History
**GET** `/payments/user/{userId}`

**Description:** Get payment history for a user

**Headers:** `Authorization: Bearer {token}`

**Query Parameters:**
- `type` (string): "incoming" or "outgoing"
- `status` (string): Filter by status
- `page` (number, default: 0): Page number
- `size` (number, default: 10): Page size

**Response:**
```json
{
  "success": true,
  "message": "Payment history retrieved successfully",
  "data": {
    "content": [Payment objects],
    "page": {
      "size": number,
      "number": number,
      "totalElements": number,
      "totalPages": number
    }
  }
}
```

### 5. Get Payment Details
**GET** `/payments/{paymentId}`

**Description:** Get detailed payment information

**Headers:** `Authorization: Bearer {token}`

**Response:**
```json
{
  "success": true,
  "message": "Payment details retrieved successfully",
  "data": {Payment object}
}
```

---

## Notification Endpoints

### 1. Get User Notifications
**GET** `/notifications/user/{userId}`

**Description:** Get notifications for a user

**Headers:** `Authorization: Bearer {token}`

**Query Parameters:**
- `type` (string): Filter by notification type
- `read` (boolean): Filter by read status
- `page` (number, default: 0): Page number
- `size` (number, default: 20): Page size

**Response:**
```json
{
  "success": true,
  "message": "Notifications retrieved successfully",
  "data": {
    "content": [Notification objects],
    "page": {
      "size": number,
      "number": number,
      "totalElements": number,
      "totalPages": number
    }
  }
}
```

### 2. Mark Notification as Read
**PUT** `/notifications/{notificationId}/read`

**Description:** Mark a notification as read

**Headers:** `Authorization: Bearer {token}`

**Response:**
```json
{
  "success": true,
  "message": "Notification marked as read"
}
```

### 3. Mark All Notifications as Read
**PUT** `/notifications/user/{userId}/read-all`

**Description:** Mark all notifications as read for a user

**Headers:** `Authorization: Bearer {token}`

**Response:**
```json
{
  "success": true,
  "message": "All notifications marked as read"
}
```

### 4. Delete Notification
**DELETE** `/notifications/{notificationId}`

**Description:** Delete a notification

**Headers:** `Authorization: Bearer {token}`

**Response:**
```json
{
  "success": true,
  "message": "Notification deleted successfully"
}
```

### 5. Get Notification Counts
**GET** `/notifications/user/{userId}/counts`

**Description:** Get unread notification counts by type

**Headers:** `Authorization: Bearer {token}`

**Response:**
```json
{
  "success": true,
  "message": "Notification counts retrieved successfully",
  "data": {
    "total": number,
    "messages": number,
    "taskUpdates": number,
    "payments": number,
    "applications": number
  }
}
```

### 6. Update Notification Preferences
**PUT** `/notifications/user/{userId}/preferences`

**Description:** Update user notification preferences

**Headers:** `Authorization: Bearer {token}`

**Request Body:**
```json
{
  "emailNotifications": boolean,
  "pushNotifications": boolean,
  "smsNotifications": boolean,
  "notificationTypes": {
    "taskUpdates": boolean,
    "messages": boolean,
    "payments": boolean,
    "marketing": boolean
  }
}
```

**Response:**
```json
{
  "success": true,
  "message": "Notification preferences updated successfully"
}
```

---

## Analytics and Dashboard Endpoints

### 1. Get User Dashboard Data
**GET** `/dashboard/user/{userId}`

**Description:** Get dashboard statistics for a user

**Headers:** `Authorization: Bearer {token}`

**Response:**
```json
{
  "success": true,
  "message": "Dashboard data retrieved successfully",
  "data": {
    "tasksPosted": number,
    "tasksCompleted": number,
    "totalEarnings": number,
    "averageRating": number,
    "activeTasksCount": number,
    "recentActivities": [object],
    "upcomingDeadlines": [object],
    "monthlyStats": {
      "tasksCompleted": number,
      "earnings": number,
      "averageCompletionTime": number
    }
  }
}
```

### 2. Get Platform Statistics
**GET** `/analytics/platform-stats`

**Description:** Get general platform statistics

**Response:**
```json
{
  "success": true,
  "message": "Platform statistics retrieved successfully",
  "data": {
    "totalUsers": number,
    "totalTasks": number,
    "completedTasks": number,
    "averageTaskPrice": number,
    "averageCompletionTime": number,
    "popularCategories": [object]
  }
}
```

### 3. Get Task Analytics
**GET** `/analytics/tasks/{taskId}`

**Description:** Get analytics for a specific task

**Headers:** `Authorization: Bearer {token}`

**Response:**
```json
{
  "success": true,
  "message": "Task analytics retrieved successfully",
  "data": {
    "views": number,
    "applications": number,
    "averageProposedPrice": number,
    "timeToFirstApplication": number,
    "geographicDistribution": [object]
  }
}
```

---

## Search and Discovery Endpoints

### 1. Global Search
**GET** `/search`

**Description:** Global search across tasks, users, and content

**Query Parameters:**
- `q` (string): Search query
- `type` (string): "tasks", "users", or "all"
- `filters` (object): Additional filters
- `page` (number, default: 0): Page number
- `size` (number, default: 10): Page size

**Response:**
```json
{
  "success": true,
  "message": "Search results retrieved successfully",
  "data": {
    "tasks": {
      "content": [Task objects],
      "page": {
        "size": number,
        "number": number,
        "totalElements": number,
        "totalPages": number
      }
    },
    "users": {
      "content": [User objects],
      "page": {
        "size": number,
        "number": number,
        "totalElements": number,
        "totalPages": number
      }
    },
    "suggestions": ["string"]
  }
}
```

### 2. Get Search Suggestions
**GET** `/search/suggestions`

**Description:** Get search suggestions based on query

**Query Parameters:**
- `q` (string): Partial search query
- `type` (string): "tasks" or "skills"

**Response:**
```json
{
  "success": true,
  "message": "Search suggestions retrieved successfully",
  "data": {
    "suggestions": ["string"]
  }
}
```

### 3. Get Trending Tasks
**GET** `/tasks/trending`

**Description:** Get trending/popular tasks

**Query Parameters:**
- `timeframe` (string): "day", "week", or "month"
- `category` (string): Filter by category
- `limit` (number, default: 10): Number of results

**Response:**
```json
{
  "success": true,
  "message": "Trending tasks retrieved successfully",
  "data": [Task objects]
}
```

### 4. Get Recommended Tasks
**GET** `/tasks/recommended/{userId}`

**Description:** Get personalized task recommendations

**Headers:** `Authorization: Bearer {token}`

**Query Parameters:**
- `limit` (number, default: 10): Number of recommendations

**Response:**
```json
{
  "success": true,
  "message": "Recommended tasks retrieved successfully",
  "data": [Task objects]
}
```

---

## File Upload Endpoints

### 1. Upload Files
**POST** `/files/upload`

**Description:** Upload files (images, documents, etc.)

**Headers:** `Authorization: Bearer {token}`

**Request:** Form-data with file upload

**Query Parameters:**
- `type` (string): "profile", "task", "chat", "kyc"

**Response:**
```json
{
  "success": true,
  "message": "File uploaded successfully",
  "data": {
    "fileUrl": "string",
    "fileName": "string",
    "fileSize": number,
    "mimeType": "string"
  }
}
```

### 2. Delete File
**DELETE** `/files/{fileId}`

**Description:** Delete an uploaded file

**Headers:** `Authorization: Bearer {token}`

**Response:**
```json
{
  "success": true,
  "message": "File deleted successfully"
}
```

---

## Location and Geocoding Endpoints

### 1. Geocode Address
**GET** `/location/geocode`

**Description:** Convert address to coordinates

**Query Parameters:**
- `address` (string): Address to geocode

**Response:**
```json
{
  "success": true,
  "message": "Address geocoded successfully",
  "data": {
    "latitude": number,
    "longitude": number,
    "formattedAddress": "string"
  }
}
```

### 2. Reverse Geocode
**GET** `/location/reverse-geocode`

**Description:** Convert coordinates to address

**Query Parameters:**
- `latitude` (number): Latitude
- `longitude` (number): Longitude

**Response:**
```json
{
  "success": true,
  "message": "Coordinates reverse geocoded successfully",
  "data": {
    "address": "string",
    "city": "string",
    "state": "string",
    "pincode": "string"
  }
}
```

### 3. Get Nearby Tasks
**GET** `/location/nearby-tasks`

**Description:** Get tasks near a location

**Query Parameters:**
- `latitude` (number): User latitude
- `longitude` (number): User longitude
- `radius` (number): Search radius in km
- `limit` (number, default: 20): Maximum results

**Response:**
```json
{
  "success": true,
  "message": "Nearby tasks retrieved successfully",
  "data": [Task objects with distance]
}
```

---

## Admin Endpoints

### 1. Get All Users (Admin)
**GET** `/admin/users`

**Description:** Get all users with admin filters

**Headers:** `Authorization: Bearer {admin-token}`

**Query Parameters:**
- `status` (string): Filter by user status
- `verified` (boolean): Filter by verification status
- `page` (number, default: 0): Page number
- `size` (number, default: 20): Page size

**Response:**
```json
{
  "success": true,
  "message": "Users retrieved successfully",
  "data": {
    "content": [User objects],
    "page": {
      "size": number,
      "number": number,
      "totalElements": number,
      "totalPages": number
    }
  }
}
```

### 2. Manage User Status (Admin)
**PUT** `/admin/users/{userId}/status`

**Description:** Update user account status

**Headers:** `Authorization: Bearer {admin-token}`

**Request Body:**
```json
{
  "status": "ACTIVE|SUSPENDED|BANNED",
  "reason": "string"
}
```

**Response:**
```json
{
  "success": true,
  "message": "User status updated successfully"
}
```

### 3. Review KYC Submissions (Admin)
**GET** `/admin/kyc/pending`

**Description:** Get pending KYC submissions

**Headers:** `Authorization: Bearer {admin-token}`

**Response:**
```json
{
  "success": true,
  "message": "Pending KYC submissions retrieved successfully",
  "data": [KYC submission objects]
}
```

### 4. Approve/Reject KYC (Admin)
**PUT** `/admin/kyc/{submissionId}`

**Description:** Approve or reject KYC submission

**Headers:** `Authorization: Bearer {admin-token}`

**Request Body:**
```json
{
  "status": "APPROVED|REJECTED",
  "notes": "string"
}
```

**Response:**
```json
{
  "success": true,
  "message": "KYC status updated successfully"
}
```

---

## WebSocket Events

### Real-time Communication

**WebSocket Connection:** `wss://api.urbanup.com/v1/ws`

**Authentication:** Send JWT token in connection query parameter

### Event Types:

1. **new_message** - New chat message received
2. **task_status_update** - Task status changed
3. **new_application** - New task application
4. **application_status_update** - Application accepted/rejected
5. **payment_status_update** - Payment status changed
6. **notification** - New notification
7. **user_online_status** - User online/offline status

### Event Format:
```json
{
  "type": "string",
  "data": object,
  "timestamp": "string",
  "userId": number
}
```

---

## Error Codes

| Code | Description |
|------|-------------|
| 400 | Bad Request - Invalid input data |
| 401 | Unauthorized - Invalid or missing authentication |
| 403 | Forbidden - Access denied |
| 404 | Not Found - Resource not found |
| 409 | Conflict - Resource already exists |
| 422 | Unprocessable Entity - Validation errors |
| 429 | Too Many Requests - Rate limiting |
| 500 | Internal Server Error |
| 503 | Service Unavailable |

---

## Rate Limiting

- **Authentication endpoints:** 5 requests per minute per IP
- **General API endpoints:** 1000 requests per hour per user
- **File upload endpoints:** 100 requests per hour per user
- **Search endpoints:** 200 requests per hour per user

---

## Data Models

### User Model
```typescript
interface User {
  id: number;
  email: string;
  firstName: string;
  lastName: string;
  phoneNumber: string;
  isEmailVerified: boolean;
  isPhoneVerified: boolean;
  isActive: boolean;
  theme: "LIGHT" | "DARK";
  profilePictureUrl: string;
  dateOfBirth: string;
  ratingAsPoster: number;
  ratingAsFulfiller: number;
  ratingsAsPostCount: number;
  ratingsAsFulfillerCount: number;
  totalTasksPosted: number;
  totalTasksCompleted: number;
  totalEarnings: number;
  accountCreatedFrom: string;
  lastLogin: string;
  createdAt: string;
  updatedAt: string;
  userProfile?: UserProfile;
}
```

### Task Model
```typescript
interface Task {
  id: number;
  title: string;
  description: string;
  category: "PERSONAL_ERRANDS" | "PROFESSIONAL_TASKS" | "HOUSEHOLD_HELP" | "MICRO_GIGS";
  price: number;
  pricingType: "FIXED" | "HOURLY";
  location: string;
  address: string;
  latitude: number;
  longitude: number;
  deadline: string;
  isUrgent: boolean;
  status: "OPEN" | "ACCEPTED" | "IN_PROGRESS" | "COMPLETED" | "CONFIRMED" | "CANCELLED";
  requirements: string;
  images: string[];
  files: string[];
  estimatedDuration: number;
  skillsRequired: string[];
  acceptedAt?: string;
  startedAt?: string;
  completedAt?: string;
  confirmedAt?: string;
  createdAt: string;
  updatedAt: string;
  poster: User;
  fulfiller?: User;
  applications?: TaskApplication[];
  chat?: Chat;
  review?: Review;
  payment?: Payment;
}
```

### UserProfile Model
```typescript
interface UserProfile {
  id: number;
  bio: string;
  address: string;
  city: string;
  state: string;
  pincode: string;
  latitude: number;
  longitude: number;
  gender: "MALE" | "FEMALE" | "OTHER" | "PREFER_NOT_TO_SAY";
  isKycVerified: boolean;
  kycDocumentType: "AADHAAR" | "PAN" | "PASSPORT" | "DRIVING_LICENSE";
  kycDocumentNumber: string;
  kycVerificationDate: string;
  skills: string[];
  interests: string[];
  languagesSpoken: string[];
  emergencyContactName: string;
  emergencyContactPhone: string;
  badges: string[];
  totalTasksPosted: number;
  totalTasksCompleted: number;
  totalEarnings: number;
  averageResponseTime: number;
  accountVerificationLevel: "BASIC" | "VERIFIED" | "PREMIUM";
  preferredWorkRadius: number;
  isAvailableForWork: boolean;
  workingHours: string;
  profileCompletionPercentage: number;
  createdAt: string;
  updatedAt: string;
}
```

### TaskApplication Model
```typescript
interface TaskApplication {
  id: number;
  proposalText: string;
  proposedPrice: number;
  estimatedCompletionTime: string;
  status: "PENDING" | "ACCEPTED" | "REJECTED";
  createdAt: string;
  task: Task;
  applicant: User;
}
```

### Chat Model
```typescript
interface Chat {
  id: number;
  isActive: boolean;
  lastMessageAt: string;
  createdAt: string;
  updatedAt: string;
  task: Task;
  poster: User;
  fulfiller: User;
  messages: Message[];
  unreadCount?: number;
}
```

### Message Model
```typescript
interface Message {
  id: number;
  content: string;
  type: "TEXT" | "IMAGE" | "FILE" | "LOCATION" | "SYSTEM";
  isRead: boolean;
  attachmentUrl?: string;
  metadata?: string;
  createdAt: string;
  chat: Chat;
  sender: User;
}
```

### Review Model
```typescript
interface Review {
  id: number;
  rating: number;
  comment: string;
  createdAt: string;
  reviewer: User;
  reviewee: User;
  task: Task;
}
```

### Payment Model
```typescript
interface Payment {
  id: number;
  amount: number;
  status: "PENDING" | "COMPLETED" | "FAILED" | "REFUNDED";
  paymentMethod: string;
  transactionId: string;
  createdAt: string;
  task: Task;
}
```

### Notification Model
```typescript
interface Notification {
  id: string;
  type: 'task_application' | 'task_completed' | 'payment' | 'message' | 'reminder' | 'system';
  title: string;
  message: string;
  timestamp: Date;
  read: boolean;
  actionable?: boolean;
  userId?: string;
  userName?: string;
  userAvatar?: string;
  taskId?: string;
  taskTitle?: string;
  amount?: number;
}
```

This comprehensive API documentation covers all the endpoints needed for the UrbanUp Task Marketplace application, including authentication, user management, task operations, messaging, payments, notifications, and administrative functions.
