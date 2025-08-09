# Chat Endpoints - Complete API Documentation

## Problem Fixed
The frontend was trying to access `GET /api/chats/task/{taskId}?userId={userId}` but this endpoint was **missing** from the ChatController.

## Error Details
```
GET https://9d7539b4f9f6.ngrok-free.app/api/chats/task/24?userId=51 404 (Not Found)
```

## Solution Implemented

### 1. Added Missing Endpoint to ChatController
**File:** `ChatController.java`
```java
@GetMapping("/task/{taskId}")
public ResponseEntity<ApiResponse<ChatResponseDto>> getChatByTask(@PathVariable Long taskId, @RequestParam Long userId) {
    try {
        Optional<Chat> chatOpt = chatService.getChatByTaskAndUser(taskId, userId);
        if (chatOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, "Chat not found for this task and user", null));
        }
        ChatResponseDto chatDto = convertToChatDto(chatOpt.get(), userId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Chat found", chatDto));
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, "Failed to retrieve chat: " + e.getMessage(), null));
    }
}
```

### 2. Added Supporting Service Method
**File:** `ChatService.java`
```java
@Transactional(readOnly = true)
public Optional<Chat> getChatByTaskAndUser(Long taskId, Long userId) {
    return chatRepository.findByTaskId(taskId)
            .filter(chat -> chat.getPoster().getId().equals(userId) ||
                    (chat.getFulfiller() != null && chat.getFulfiller().getId().equals(userId)));
}
```

## Complete Chat API Reference

### 1. Get Chat by Task and User
```bash
GET /api/chats/task/{taskId}?userId={userId}
```

**Purpose:** Get existing chat for a specific task and user  
**Authorization:** Required  
**Response:** 
- **200** - Chat found
- **404** - No chat exists for this task/user combination

**Example:**
```bash
curl -X GET "http://localhost:8080/api/chats/task/24?userId=51" \
  -H "Authorization: Bearer [JWT_TOKEN]"
```

**Success Response:**
```json
{
  "success": true,
  "message": "Chat found",
  "data": {
    "id": 12,
    "taskTitle": "grocerry-kpn",
    "otherParticipantId": 52,
    "otherParticipantName": "test user2",
    "messages": [],
    "lastActivity": "2025-08-08T02:10:01.436027"
  }
}
```

### 2. Create Chat with Applicant (from TaskController)
```bash
POST /api/tasks/{taskId}/chat-with-applicant/{applicantId}
```

**Purpose:** Task poster creates/gets chat with task applicant  
**Authorization:** Required (must be task poster)

**Example:**
```bash
curl -X POST "http://localhost:8080/api/tasks/24/chat-with-applicant/52" \
  -H "Authorization: Bearer [JWT_TOKEN]"
```

### 3. Get All User Chats
```bash
GET /api/chats/user/{userId}
```

**Purpose:** Get all chats for a specific user

### 4. Send Message
```bash
POST /api/chats/{chatId}/messages
```

**Body:**
```json
{
  "senderId": 51,
  "content": "Hello! I'm interested in your task.",
  "messageType": "TEXT"
}
```

### 5. Get Chat Messages
```bash
GET /api/chats/{chatId}/messages?userId={userId}
```

**Purpose:** Get all messages in a specific chat

### 6. Mark Messages as Read
```bash
PUT /api/chats/{chatId}/messages/read?userId={userId}
```

**Purpose:** Mark all messages as read for the user

## Frontend Integration Flow

### Recommended Flow:
1. **Try to get existing chat:** `GET /api/chats/task/{taskId}?userId={userId}`
2. **If 404 response:** Chat doesn't exist yet
3. **Create chat:** `POST /api/tasks/{taskId}/chat-with-applicant/{applicantId}` (if you're the poster)
4. **Start messaging:** Use the returned chat ID for messaging

### Error Handling:
- **404** - Chat doesn't exist, need to create one
- **403** - User not authorized (not task poster/applicant)
- **401** - Invalid/expired JWT token

## Testing Results

✅ **GET /api/chats/task/24?userId=51** - HTTP 200 (Chat exists)  
✅ **GET /api/chats/task/24?userId=52** - HTTP 200 (Chat exists)  
✅ **POST /api/tasks/24/chat-with-applicant/52** - HTTP 200 (Chat creation)  

## Notes
- Both task poster and applicant can access the same chat
- Chat shows "otherParticipant" information relative to current user
- Messages are empty initially until users start chatting
- Authorization ensures only chat participants can access the chat
