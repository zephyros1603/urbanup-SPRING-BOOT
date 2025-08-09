# Frontend Chat Integration Fix

## Problem
Frontend is experiencing timeout errors due to infinite loop requests:
```
Error loading chat: AxiosError {message: 'timeout of 10000ms exceeded'}
```

## Root Cause
The frontend ChatRoom.tsx is likely making repeated API calls in a useEffect loop, causing the request to timeout after 10 seconds.

## Backend Status ✅
- All endpoints respond in < 0.03 seconds
- Chat data structure includes poster and fulfiller objects
- Performance optimized

## Frontend Solution

### 1. Check useEffect Dependencies
The issue is likely in ChatRoom.tsx around line 61-81. Check for:

```typescript
// ❌ BAD: This causes infinite loop
useEffect(() => {
  loadChatData();
}, []); // Missing dependencies or wrong dependencies

// ✅ GOOD: Proper dependencies
useEffect(() => {
  if (chatId && userId) {
    loadChatData();
  }
}, [chatId, userId]); // Only re-run when these change
```

### 2. Prevent Multiple Simultaneous Requests
```typescript
const [isLoading, setIsLoading] = useState(false);

const loadChatData = async () => {
  if (isLoading) return; // Prevent multiple requests
  
  setIsLoading(true);
  try {
    const chatData = await getChat(chatId, userId);
    // Handle success
  } catch (error) {
    // Handle error
  } finally {
    setIsLoading(false);
  }
};
```

### 3. Use Optimized API Pattern
```typescript
// For chat list page
const getUserChats = async (userId: number) => {
  // Uses /api/chats/user/{userId} - fast, no messages
  return await api.get(`/chats/user/${userId}`);
};

// For chat room page  
const getChatWithMessages = async (chatId: number, userId: number) => {
  // Uses /api/chats/{chatId}/messages - separate, faster
  const [chatInfo, messages] = await Promise.all([
    api.get(`/chats/user/${userId}`).then(r => r.data.find(c => c.id === chatId)),
    api.get(`/chats/${chatId}/messages?userId=${userId}`)
  ]);
  
  return {
    ...chatInfo,
    messages: messages.data
  };
};
```

### 4. Add Request Timeout Handling
```typescript
// In your API configuration
const api = axios.create({
  baseURL: '/api',
  timeout: 30000, // Increase from 10s to 30s
});

// Or for specific chat requests
const getChatWithRetry = async (chatId: number, userId: number, retries = 3) => {
  for (let i = 0; i < retries; i++) {
    try {
      return await api.get(`/chats/${chatId}?userId=${userId}`, {
        timeout: 15000 // 15 second timeout
      });
    } catch (error) {
      if (i === retries - 1) throw error;
      await new Promise(resolve => setTimeout(resolve, 1000 * (i + 1))); // Exponential backoff
    }
  }
};
```

### 5. Debug the Issue
Add logging to identify the loop:

```typescript
const loadChatData = async () => {
  console.log('🔍 loadChatData called', { chatId, userId, timestamp: new Date() });
  
  try {
    const response = await getChat(chatId, userId);
    console.log('✅ Chat loaded successfully', response.data);
  } catch (error) {
    console.error('❌ Chat loading failed', error);
  }
};
```

## Expected Data Structure
The backend now returns this structure (poster/fulfiller objects included):

```json
{
  "success": true,
  "data": {
    "id": 12,
    "poster": { "id": 51, "firstName": "test", "lastName": "user" },
    "fulfiller": { "id": 52, "firstName": "test", "lastName": "user2" },
    "messages": [...],
    "otherParticipantId": 52,
    "otherParticipantName": "test user2"
  }
}
```

## Quick Fix
Most likely fix is in ChatRoom.tsx useEffect:

```typescript
// Current problematic code (likely):
useEffect(() => {
  loadChatData(); // This runs on every render!
}, []); // Wrong or missing dependencies

// Fixed code:
useEffect(() => {
  if (chatId && userId && !isLoading) {
    loadChatData();
  }
}, [chatId, userId]); // Proper dependencies
```
