# Task Applications & Chat Integration Guide

This guide provides endpoints and integration steps for showing task applicants and enabling chat functionality between task posters and applicants.

## 📋 Task Applications Endpoint

### Get Task Applications
**GET** `/api/tasks/{taskId}/applications`

Gets all applications for a specific task. Only the task poster can view applications.

**Path Parameters:**
- `taskId`: Long - ID of the task

**Authentication:** JWT Bearer token required (will use current authenticated user)

**Example Request:**
```
GET /api/tasks/1/applications
Authorization: Bearer <jwt_token>
```

**Response:**
```json
{
    "success": true,
    "message": "Applications retrieved",
    "data": [
        {
            "id": 1,
            "task": {
                "id": 1,
                "title": "Help with grocery shopping"
            },
            "applicant": {
                "id": 2,
                "username": "jane_doe",
                "firstName": "Jane",
                "lastName": "Doe",
                "email": "jane@example.com",
                "profilePicture": "profile_pic_url"
            },
            "proposedPrice": 25.00,
            "message": "I have 3 years of experience with grocery shopping and have my own car. I can complete this task efficiently.",
            "estimatedCompletionTime": "2024-08-10T16:00:00",
            "status": "PENDING",
            "appliedAt": "2024-08-08T14:30:00",
            "respondedAt": null
        },
        {
            "id": 2,
            "task": {
                "id": 1,
                "title": "Help with grocery shopping"
            },
            "applicant": {
                "id": 3,
                "username": "bob_smith",
                "firstName": "Bob",
                "lastName": "Smith",
                "email": "bob@example.com",
                "profilePicture": "profile_pic_url"
            },
            "proposedPrice": 22.00,
            "message": "I live nearby and can do this quickly. Available this weekend.",
            "estimatedCompletionTime": "2024-08-09T14:00:00",
            "status": "PENDING",
            "appliedAt": "2024-08-08T15:45:00",
            "respondedAt": null
        }
    ]
}
```

**Error Responses:**
- `400 Bad Request` - If user is not the task poster
- `404 Not Found` - If task doesn't exist
- `401 Unauthorized` - If not authenticated

---

## 💬 Chat Integration Endpoints

### 1. Create Chat Between Poster and Applicant
**POST** `/api/tasks/{taskId}/chat-with-applicant/{applicantId}`

Creates or retrieves existing chat between task poster and a specific applicant.

**Path Parameters:**
- `taskId`: Long - ID of the task
- `applicantId`: Long - ID of the user who applied for the task

**Authentication:** JWT Bearer token required (only task poster can initiate)

**Example Request:**
```
POST /api/tasks/1/chat-with-applicant/2
Authorization: Bearer <jwt_token>
```

**Response:**
```json
{
    "success": true,
    "message": "Chat created/retrieved successfully",
    "data": {
        "id": 1,
        "taskTitle": "Help with grocery shopping",
        "otherParticipantId": 2,
        "otherParticipantName": "Jane Doe",
        "messages": [],
        "lastActivity": "2024-08-08T16:30:00"
    }
}
```

**Error Responses:**
- `403 Forbidden` - If current user is not the task poster
- `400 Bad Request` - If the applicant hasn't applied for this task
- `404 Not Found` - If task or applicant doesn't exist

### 2. Get All Chats for User
**GET** `/api/chats/user/{userId}`

Gets all chats for a specific user.

**Path Parameters:**
- `userId`: Long - ID of the user

**Response:**
```json
{
    "success": true,
    "message": "Chats retrieved successfully",
    "data": [
        {
            "id": 1,
            "taskTitle": "Help with grocery shopping",
            "otherParticipantId": 2,
            "otherParticipantName": "Jane Doe",
            "messages": [],
            "lastActivity": "2024-08-08T16:30:00"
        }
    ]
}
```

### 3. Get Specific Chat
**GET** `/api/chats/{chatId}`

Gets details of a specific chat.

**Path Parameters:**
- `chatId`: Long - ID of the chat

**Query Parameters:**
- `userId`: Long - ID of the current user (for access control)

**Response:**
```json
{
    "success": true,
    "message": "Chat found",
    "data": {
        "id": 1,
        "taskTitle": "Help with grocery shopping",
        "otherParticipantId": 2,
        "otherParticipantName": "Jane Doe",
        "messages": [],
        "lastActivity": "2024-08-08T16:30:00"
    }
}
```

### 4. Send Message
**POST** `/api/chats/{chatId}/messages`

Sends a message in a chat.

**Path Parameters:**
- `chatId`: Long - ID of the chat

**Request Body:**
```json
{
    "senderId": 1,
    "content": "Hi Jane, I saw your application for the grocery shopping task. When would you be available?",
    "messageType": "TEXT"
}
```

**Response:**
```json
{
    "success": true,
    "message": "Message sent successfully",
    "data": {
        "id": 1,
        "content": "Hi Jane, I saw your application for the grocery shopping task. When would you be available?",
        "messageType": "TEXT",
        "senderId": 1,
        "senderName": "John Doe",
        "timestamp": "2024-08-08T16:45:00",
        "isRead": false
    }
}
```

### 5. Get Chat Messages
**GET** `/api/chats/{chatId}/messages`

Gets all messages in a chat.

**Path Parameters:**
- `chatId`: Long - ID of the chat

**Response:**
```json
{
    "success": true,
    "message": "Messages retrieved successfully",
    "data": [
        {
            "id": 1,
            "content": "Hi Jane, I saw your application for the grocery shopping task. When would you be available?",
            "messageType": "TEXT",
            "senderId": 1,
            "senderName": "John Doe",
            "timestamp": "2024-08-08T16:45:00",
            "isRead": true
        },
        {
            "id": 2,
            "content": "Hello! I'm available this Saturday afternoon. Would that work for you?",
            "messageType": "TEXT",
            "senderId": 2,
            "senderName": "Jane Doe",
            "timestamp": "2024-08-08T17:00:00",
            "isRead": false
        }
    ]
}
```

### 6. Mark Messages as Read
**PUT** `/api/chats/{chatId}/messages/read`

Marks all messages in a chat as read for the current user.

**Path Parameters:**
- `chatId`: Long - ID of the chat

**Query Parameters:**
- `userId`: Long - ID of the current user

**Response:**
```json
{
    "success": true,
    "message": "Messages marked as read",
    "data": null
}
```

---

## 🔄 Frontend Integration Flow

### 1. Display Task Applications

In your "My Tasks" page, for each task, you can add an "Applicants" tab:

```javascript
// Get applications for a task
const getTaskApplications = async (taskId) => {
    const response = await fetch(`/api/tasks/${taskId}/applications`, {
        headers: {
            'Authorization': `Bearer ${jwtToken}`,
            'Content-Type': 'application/json'
        }
    });
    
    if (response.ok) {
        const result = await response.json();
        return result.data; // Array of applications
    }
    throw new Error('Failed to fetch applications');
};

// Display applications in your UI
const displayApplications = (applications) => {
    applications.forEach(app => {
        // Create application card with:
        // - app.applicant.firstName + " " + app.applicant.lastName
        // - app.message
        // - app.proposedPrice
        // - app.estimatedCompletionTime
        // - "Start Chat" button
    });
};
```

### 2. Initiate Chat with Applicant

When the task poster clicks "Start Chat" on an application:

```javascript
const startChatWithApplicant = async (taskId, applicantId) => {
    const response = await fetch(`/api/tasks/${taskId}/chat-with-applicant/${applicantId}`, {
        method: 'POST',
        headers: {
            'Authorization': `Bearer ${jwtToken}`,
            'Content-Type': 'application/json'
        }
    });
    
    if (response.ok) {
        const result = await response.json();
        const chat = result.data;
        
        // Redirect to chat interface with chat.id
        window.location.href = `/chat/${chat.id}`;
        // OR open chat in modal/sidebar
        openChatInterface(chat);
    }
};
```

### 3. Chat Interface Implementation

```javascript
// Load chat messages
const loadChatMessages = async (chatId) => {
    const response = await fetch(`/api/chats/${chatId}/messages`, {
        headers: {
            'Authorization': `Bearer ${jwtToken}`
        }
    });
    
    if (response.ok) {
        const result = await response.json();
        return result.data; // Array of messages
    }
};

// Send message
const sendMessage = async (chatId, content) => {
    const response = await fetch(`/api/chats/${chatId}/messages`, {
        method: 'POST',
        headers: {
            'Authorization': `Bearer ${jwtToken}`,
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            senderId: currentUserId, // Get from JWT or user context
            content: content,
            messageType: 'TEXT'
        })
    });
    
    if (response.ok) {
        const result = await response.json();
        return result.data; // New message object
    }
};

// Mark messages as read
const markMessagesAsRead = async (chatId) => {
    await fetch(`/api/chats/${chatId}/messages/read?userId=${currentUserId}`, {
        method: 'PUT',
        headers: {
            'Authorization': `Bearer ${jwtToken}`
        }
    });
};
```

### 4. WebSocket Integration (Optional for Real-time)

If you want real-time messaging, you can integrate WebSocket:

```javascript
// Connect to WebSocket for real-time updates
const connectToChat = (chatId) => {
    const websocket = new WebSocket(`ws://localhost:8080/api/chats/${chatId}/websocket`);
    
    websocket.onmessage = (event) => {
        const message = JSON.parse(event.data);
        // Add new message to chat interface
        addMessageToChat(message);
    };
    
    websocket.onopen = () => {
        console.log('Connected to chat');
    };
    
    websocket.onclose = () => {
        console.log('Disconnected from chat');
    };
    
    return websocket;
};
```

---

## 🎯 Key Features Summary

✅ **Task Applications Display**: Get all applicants for your posted tasks  
✅ **Chat Initiation**: Start chat with any applicant from the applications list  
✅ **Access Control**: Only task posters can initiate chats with their applicants  
✅ **Real-time Messaging**: Send and receive messages in real-time  
✅ **Message History**: Access full conversation history  
✅ **Read Receipts**: Mark messages as read  
✅ **User Context**: Chat shows task title and participant names  

## 🔧 Implementation Steps

1. **Update your "My Tasks" page** to include an "Applicants" tab
2. **Fetch applications** using `GET /api/tasks/{taskId}/applications`
3. **Add "Start Chat" buttons** for each applicant
4. **Implement chat interface** using the chat endpoints
5. **Optional**: Add real-time updates with WebSocket

The chat system is fully integrated with your task management system, allowing seamless communication between task posters and applicants!
