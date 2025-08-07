# UrbanUp Real-Time Chat API Documentation

## 📋 Table of Contents
1. [Overview](#overview)
2. [Base Configuration](#base-configuration)
3. [Authentication](#authentication)
4. [Chat Endpoints](#chat-endpoints)
5. [WebSocket Configuration](#websocket-configuration)
6. [Request/Response Examples](#requestresponse-examples)
7. [Frontend Integration Guide](#frontend-integration-guide)
8. [Error Handling](#error-handling)
9. [Message Types](#message-types)
10. [Testing Guide](#testing-guide)

---

## 🔍 Overview

The UrbanUp Chat API provides real-time messaging capabilities for task-based communication between users. It supports text messages, file uploads, system notifications, and real-time updates via WebSocket connections.

**Key Features:**
- ✅ Real-time messaging with WebSocket
- ✅ File/image upload support
- ✅ Message read receipts
- ✅ System messages for task updates
- ✅ Unread message counts
- ✅ JWT-based authentication
- ✅ DTO pattern (no lazy loading issues)

---

## ⚙️ Base Configuration

```typescript
const API_CONFIG = {
  baseURL: 'http://localhost:8080/api',
  chatEndpoint: '/chats',
  contextPath: '/api',
  websocketURL: 'http://localhost:8080/ws',
  maxFileSize: 10 * 1024 * 1024, // 10MB
  supportedFileTypes: ['image/*', '.pdf', '.doc', '.docx', '.txt']
};
```

---

## 🔐 Authentication

All endpoints require JWT authentication via `Authorization` header:

```typescript
const headers = {
  'Content-Type': 'application/json',
  'Authorization': `Bearer ${jwtToken}`
};
```

**JWT Token Structure:**
```json
{
  "userId": 47,
  "sub": "user@example.com",
  "iat": 1754508767,
  "exp": 1754595167
}
```

---

## 📡 Chat Endpoints

### 1. Create or Get Chat
**Purpose:** Creates a new chat or retrieves existing chat between task poster and fulfiller.

```http
POST /api/chats
Content-Type: application/json
Authorization: Bearer {JWT_TOKEN}
```

**Request Body:**
```json
{
  "taskId": 18,
  "fulfillerId": 47
}
```

**Response:**
```json
{
  "success": true,
  "message": "Chat created/retrieved successfully",
  "data": {
    "id": 11,
    "taskTitle": "Help with Moving Furniture",
    "otherParticipantId": 47,
    "otherParticipantName": "Bob Smith",
    "messages": [],
    "lastActivity": "2025-08-07T01:19:39.152234"
  }
}
```

---

### 2. Send Message
**Purpose:** Sends a text message in a chat.

```http
POST /api/chats/{chatId}/messages
Content-Type: application/json
Authorization: Bearer {JWT_TOKEN}
```

**Request Body:**
```json
{
  "senderId": 46,
  "content": "Hi! Are you available this weekend?",
  "messageType": "TEXT"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Message sent successfully",
  "data": {
    "id": 12,
    "content": "Hi! Are you available this weekend?",
    "messageType": "TEXT",
    "createdAt": "2025-08-07T01:19:39.800692",
    "senderName": "Alice Johnson",
    "senderId": 46,
    "read": false
  }
}
```

---

### 3. Get Chat Messages
**Purpose:** Retrieves all messages in a chat for a specific user.

```http
GET /api/chats/{chatId}/messages?userId={userId}
Authorization: Bearer {JWT_TOKEN}
```

**Response:**
```json
{
  "success": true,
  "message": "Messages retrieved successfully",
  "data": [
    {
      "id": 12,
      "content": "Hi! Are you available this weekend?",
      "messageType": "TEXT",
      "createdAt": "2025-08-07T01:19:39.800692",
      "senderName": "Alice Johnson",
      "senderId": 46,
      "read": true
    },
    {
      "id": 13,
      "content": "Yes, I'm available! What time works best?",
      "messageType": "TEXT",
      "createdAt": "2025-08-07T01:20:03.153179",
      "senderName": "Bob Smith",
      "senderId": 47,
      "read": false
    }
  ]
}
```

---

### 4. Get Specific Chat
**Purpose:** Retrieves detailed information about a specific chat.

```http
GET /api/chats/{chatId}?userId={userId}
Authorization: Bearer {JWT_TOKEN}
```

**Response:**
```json
{
  "success": true,
  "message": "Chat found",
  "data": {
    "id": 11,
    "taskTitle": "Help with Moving Furniture",
    "otherParticipantId": 47,
    "otherParticipantName": "Bob Smith",
    "messages": [
      {
        "id": 12,
        "content": "Hi! Are you available this weekend?",
        "messageType": "TEXT",
        "createdAt": "2025-08-07T01:19:39.800692",
        "senderName": "Alice Johnson",
        "senderId": 46,
        "read": true
      }
    ],
    "lastActivity": "2025-08-07T01:19:39.152234"
  }
}
```

---

### 5. Get User's Chats
**Purpose:** Retrieves all chats for a specific user.

```http
GET /api/chats/user/{userId}
Authorization: Bearer {JWT_TOKEN}
```

**Response:**
```json
{
  "success": true,
  "message": "User chats retrieved",
  "data": [
    {
      "id": 11,
      "taskTitle": "Help with Moving Furniture",
      "otherParticipantId": 46,
      "otherParticipantName": "Alice Johnson",
      "messages": [
        {
          "id": 12,
          "content": "Hi! Are you available this weekend?",
          "messageType": "TEXT",
          "createdAt": "2025-08-07T01:19:39.800692",
          "senderName": "Alice Johnson",
          "senderId": 46,
          "read": true
        }
      ],
      "lastActivity": "2025-08-07T01:19:39.152234"
    }
  ]
}
```

---

### 6. Mark Messages as Read
**Purpose:** Marks all unread messages in a chat as read for a specific user.

```http
PUT /api/chats/{chatId}/messages/read?userId={userId}
Authorization: Bearer {JWT_TOKEN}
```

**Response:**
```json
{
  "success": true,
  "message": "Messages marked as read",
  "data": null
}
```

---

### 7. Get Unread Message Count
**Purpose:** Gets the total count of unread messages for a user across all chats.

```http
GET /api/chats/user/{userId}/unread-count
Authorization: Bearer {JWT_TOKEN}
```

**Response:**
```json
{
  "success": true,
  "message": "Unread count retrieved",
  "data": 3
}
```

---

### 8. Upload File/Media
**Purpose:** Uploads a file or image to a chat.

```http
POST /api/chats/{chatId}/media
Content-Type: multipart/form-data
Authorization: Bearer {JWT_TOKEN}
```

**Form Data:**
```javascript
const formData = new FormData();
formData.append('file', fileObject);
formData.append('senderId', '46');
formData.append('caption', 'Here is the document you requested');
```

**Response:**
```json
{
  "success": true,
  "message": "Media uploaded successfully",
  "data": {
    "id": 14,
    "content": "Here is the document you requested::/uploads/chat/11/1754509960651_document.pdf",
    "messageType": "FILE",
    "createdAt": "2025-08-07T01:22:40.657793",
    "senderName": "Alice Johnson",
    "senderId": 46,
    "read": false
  }
}
```

---

### 9. Send System Message
**Purpose:** Sends automated system messages for task status updates.

```http
POST /api/chats/{chatId}/system-message?content={message}
Authorization: Bearer {JWT_TOKEN}
```

**Example URL:**
```
POST /api/chats/11/system-message?content=Task%20has%20been%20accepted%20by%20Bob
```

**Response:**
```json
{
  "success": true,
  "message": "System message sent",
  "data": {
    "id": 15,
    "content": "Task has been accepted by Bob",
    "messageType": "SYSTEM",
    "createdAt": "2025-08-07T01:25:30.123456",
    "senderName": "System",
    "senderId": null,
    "read": false
  }
}
```

---

## 🌐 WebSocket Configuration

### Connection Setup
```javascript
import SockJS from 'sockjs-client';
import { Stomp } from '@stomp/stompjs';

// Create WebSocket connection
const socket = new SockJS('http://localhost:8080/ws');
const stompClient = Stomp.over(socket);

// Connect with authentication
stompClient.connect(
  { Authorization: `Bearer ${jwtToken}` },
  (frame) => {
    console.log('Connected to WebSocket:', frame);
    
    // Subscribe to chat updates
    stompClient.subscribe(`/topic/chat/${chatId}`, (message) => {
      const newMessage = JSON.parse(message.body);
      handleNewMessage(newMessage);
    });
    
    // Subscribe to user notifications
    stompClient.subscribe(`/user/${userId}/queue/messages`, (message) => {
      const notification = JSON.parse(message.body);
      handleNotification(notification);
    });
  },
  (error) => {
    console.error('WebSocket connection error:', error);
  }
);
```

### Send Message via WebSocket
```javascript
const sendMessage = (chatId, senderId, content, messageType = 'TEXT') => {
  const messagePayload = {
    chatId: chatId,
    senderId: senderId,
    content: content,
    messageType: messageType,
    timestamp: new Date().toISOString()
  };
  
  stompClient.send(
    `/app/chat/${chatId}/send`,
    { Authorization: `Bearer ${jwtToken}` },
    JSON.stringify(messagePayload)
  );
};
```

---

## 📱 Frontend Integration Guide

### React Component Integration
```jsx
import React, { useState, useEffect, useRef } from 'react';
import SockJS from 'sockjs-client';
import { Stomp } from '@stomp/stompjs';

const ChatComponent = ({ chatId, currentUserId, jwtToken }) => {
  const [messages, setMessages] = useState([]);
  const [newMessage, setNewMessage] = useState('');
  const [isConnected, setIsConnected] = useState(false);
  const stompClientRef = useRef(null);

  // Initialize WebSocket connection
  useEffect(() => {
    const socket = new SockJS('http://localhost:8080/ws');
    const stompClient = Stomp.over(socket);
    
    stompClient.connect(
      { Authorization: `Bearer ${jwtToken}` },
      () => {
        setIsConnected(true);
        
        // Subscribe to chat messages
        stompClient.subscribe(`/topic/chat/${chatId}`, (message) => {
          const receivedMessage = JSON.parse(message.body);
          setMessages(prev => [...prev, receivedMessage]);
        });
      },
      (error) => {
        console.error('Connection error:', error);
        setIsConnected(false);
      }
    );
    
    stompClientRef.current = stompClient;
    
    return () => {
      if (stompClient.connected) {
        stompClient.disconnect();
      }
    };
  }, [chatId, jwtToken]);

  // Load initial messages
  useEffect(() => {
    loadChatMessages();
  }, [chatId]);

  const loadChatMessages = async () => {
    try {
      const response = await fetch(
        `/api/chats/${chatId}/messages?userId=${currentUserId}`,
        {
          headers: {
            'Authorization': `Bearer ${jwtToken}`
          }
        }
      );
      
      const result = await response.json();
      if (result.success) {
        setMessages(result.data);
      }
    } catch (error) {
      console.error('Error loading messages:', error);
    }
  };

  const sendMessage = async () => {
    if (!newMessage.trim() || !isConnected) return;

    try {
      const response = await fetch(`/api/chats/${chatId}/messages`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${jwtToken}`
        },
        body: JSON.stringify({
          senderId: currentUserId,
          content: newMessage,
          messageType: 'TEXT'
        })
      });

      const result = await response.json();
      if (result.success) {
        setNewMessage('');
        // Message will be received via WebSocket
      }
    } catch (error) {
      console.error('Error sending message:', error);
    }
  };

  return (
    <div className="chat-container">
      <div className="connection-status">
        Status: {isConnected ? '🟢 Connected' : '🔴 Disconnected'}
      </div>
      
      <div className="messages-container">
        {messages.map((message) => (
          <div key={message.id} className={`message ${
            message.senderId === currentUserId ? 'sent' : 'received'
          }`}>
            <div className="message-content">
              {message.messageType === 'SYSTEM' ? (
                <div className="system-message">{message.content}</div>
              ) : (
                <>
                  <div className="sender-name">{message.senderName}</div>
                  <div className="message-text">{message.content}</div>
                  <div className="message-time">
                    {new Date(message.createdAt).toLocaleTimeString()}
                  </div>
                </>
              )}
            </div>
          </div>
        ))}
      </div>
      
      <div className="message-input">
        <input
          type="text"
          value={newMessage}
          onChange={(e) => setNewMessage(e.target.value)}
          onKeyPress={(e) => e.key === 'Enter' && sendMessage()}
          placeholder="Type a message..."
        />
        <button onClick={sendMessage} disabled={!isConnected}>
          Send
        </button>
      </div>
    </div>
  );
};
```

### File Upload Integration
```jsx
const FileUploadComponent = ({ chatId, currentUserId, jwtToken }) => {
  const [selectedFile, setSelectedFile] = useState(null);
  const [uploading, setUploading] = useState(false);

  const handleFileUpload = async () => {
    if (!selectedFile) return;

    setUploading(true);
    const formData = new FormData();
    formData.append('file', selectedFile);
    formData.append('senderId', currentUserId);
    formData.append('caption', 'File attachment');

    try {
      const response = await fetch(`/api/chats/${chatId}/media`, {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${jwtToken}`
        },
        body: formData
      });

      const result = await response.json();
      if (result.success) {
        setSelectedFile(null);
        // File message will appear via WebSocket
      }
    } catch (error) {
      console.error('File upload error:', error);
    } finally {
      setUploading(false);
    }
  };

  return (
    <div className="file-upload">
      <input
        type="file"
        onChange={(e) => setSelectedFile(e.target.files[0])}
        accept="image/*,.pdf,.doc,.docx,.txt"
      />
      <button 
        onClick={handleFileUpload} 
        disabled={!selectedFile || uploading}
      >
        {uploading ? 'Uploading...' : 'Upload File'}
      </button>
    </div>
  );
};
```

---

## ❌ Error Handling

### Common Error Responses
```json
{
  "success": false,
  "message": "Chat not found or user lacks access",
  "data": null,
  "timestamp": "2025-08-07T01:36:03.910924",
  "path": "/api/chats/999"
}
```

### Error Status Codes
- `400 Bad Request`: Invalid input data
- `401 Unauthorized`: Invalid or missing JWT token
- `403 Forbidden`: User lacks permission for resource
- `404 Not Found`: Chat or message not found
- `413 Payload Too Large`: File exceeds 10MB limit
- `500 Internal Server Error`: Server-side error

### Frontend Error Handling
```javascript
const handleApiError = (response, fallbackMessage) => {
  if (!response.success) {
    switch (response.message) {
      case 'Chat not found or user lacks access':
        showError('You do not have access to this chat');
        break;
      case 'Unauthorized access. Please provide a valid JWT token.':
        redirectToLogin();
        break;
      default:
        showError(response.message || fallbackMessage);
    }
  }
};
```

---

## 📨 Message Types

### Available Message Types
```typescript
enum MessageType {
  TEXT = "TEXT",           // Regular text message
  IMAGE = "IMAGE",         // Image file
  FILE = "FILE",           // Document/file attachment
  LOCATION = "LOCATION",   // Location sharing (future)
  SYSTEM = "SYSTEM",       // System-generated message
  STATUS_UPDATE = "STATUS_UPDATE"  // Task status updates
}
```

### Message Type Usage
- **TEXT**: Regular chat messages between users
- **IMAGE**: Photo attachments (auto-detected by content type)
- **FILE**: Document attachments (PDF, DOC, TXT, etc.)
- **SYSTEM**: Automated notifications (task accepted, completed, etc.)
- **STATUS_UPDATE**: Task milestone updates
- **LOCATION**: GPS coordinates (planned feature)

---

## 🧪 Testing Guide

### Using the Test Script
```bash
# Make the test script executable
chmod +x test_chat_endpoints.sh

# Run comprehensive chat testing
./test_chat_endpoints.sh
```

### Manual Testing with curl
```bash
# 1. Create chat
curl -X POST "http://localhost:8080/api/chats" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{"taskId": 18, "fulfillerId": 47}'

# 2. Send message
curl -X POST "http://localhost:8080/api/chats/11/messages" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{"senderId": 46, "content": "Hello!", "messageType": "TEXT"}'

# 3. Get messages
curl -X GET "http://localhost:8080/api/chats/11/messages?userId=46" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### WebSocket Testing
Use the provided HTML test files:
- `websocket_chat_test.html` - Basic WebSocket testing
- `websocket_realtime_chat_test.html` - Advanced real-time testing

---

## 🔧 Implementation Notes

### Key Backend Features
- ✅ **DTO Pattern**: Eliminates LazyInitializationException
- ✅ **Eager Loading**: Optimized JPQL queries with `LEFT JOIN FETCH`
- ✅ **JWT Security**: Token-based authentication on all endpoints
- ✅ **File Handling**: 10MB limit with type validation
- ✅ **Real-time Updates**: WebSocket integration for live messaging

### Performance Considerations
- Messages are paginated for large chat histories
- File uploads are validated for size and type
- WebSocket connections are managed per user session
- Database queries use eager loading to prevent N+1 problems

### Security Features
- JWT token validation on all endpoints
- User permission checks for chat access
- File upload validation and sanitization
- XSS protection in message content

---

## 📞 Support

For integration support or API questions:
- Review the `test_chat_endpoints.sh` script for working examples
- Check `RealtimeChatComponent.jsx` for React integration
- Refer to the comprehensive test cases for expected behavior

**Last Updated:** August 7, 2025  
**API Version:** v1.0  
**Status:** ✅ Production Ready
