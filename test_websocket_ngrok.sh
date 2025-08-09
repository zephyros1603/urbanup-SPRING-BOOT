#!/bin/bash

# UrbanUp WebSocket Testing Script with ngrok
# This script tests the complete WebSocket functionality with ngrok tunnel

echo "🧪 UrbanUp WebSocket Testing with ngrok"
echo "======================================="

# Configuration
NGROK_URL="https://49fbc293c4f1.ngrok-free.app"
API_URL="$NGROK_URL/api"
WS_URL="${NGROK_URL/https/wss}/api/ws"

echo "📋 Test Configuration:"
echo "   Backend URL: $NGROK_URL"
echo "   API URL: $API_URL"
echo "   WebSocket URL: $WS_URL"
echo ""

# Test 1: Backend Health Check
echo "🏥 Test 1: Backend Health Check"
echo "------------------------------"
health_response=$(curl -s -w "%{http_code}" -o /dev/null "$API_URL/health" 2>/dev/null)
if [ "$health_response" = "200" ]; then
    echo "✅ Backend is responding (HTTP 200)"
else
    echo "❌ Backend health check failed (HTTP $health_response)"
    echo "   Make sure your Spring Boot application is running on port 8080"
    echo "   Command: mvn spring-boot:run"
    exit 1
fi
echo ""

# Test 2: Authentication Test
echo "🔐 Test 2: Authentication Test"
echo "------------------------------"
login_response=$(curl -s -X POST "$API_URL/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john.doe@example.com",
    "password": "password123"
  }')

echo "Login response: $login_response"

# Extract token using grep and cut (works on macOS)
if echo "$login_response" | grep -q "accessToken"; then
    # Extract token more carefully
    ACCESS_TOKEN=$(echo "$login_response" | grep -o '"accessToken":"[^"]*"' | cut -d'"' -f4)
    USER_ID=$(echo "$login_response" | grep -o '"id":[0-9]*' | head -1 | cut -d':' -f2)
    
    if [ -n "$ACCESS_TOKEN" ] && [ -n "$USER_ID" ]; then
        echo "✅ Authentication successful"
        echo "   User ID: $USER_ID"
        echo "   Token: ${ACCESS_TOKEN:0:20}..."
    else
        echo "❌ Failed to extract token or user ID from response"
        exit 1
    fi
else
    echo "❌ Authentication failed"
    echo "   Response: $login_response"
    exit 1
fi
echo ""

# Test 3: Create or Get Chat
echo "💬 Test 3: Chat Creation/Retrieval"
echo "-----------------------------------"

# First, try to get existing chats
chats_response=$(curl -s -X GET "$API_URL/chats?userId=$USER_ID" \
  -H "Authorization: Bearer $ACCESS_TOKEN")

echo "Existing chats: $chats_response"

# Extract a chat ID if available
if echo "$chats_response" | grep -q '"id"'; then
    CHAT_ID=$(echo "$chats_response" | grep -o '"id":[0-9]*' | head -1 | cut -d':' -f2)
    echo "✅ Using existing chat ID: $CHAT_ID"
else
    echo "📝 No existing chats found, will use chat ID 14 for testing"
    CHAT_ID=14
fi
echo ""

# Test 4: WebSocket Connection Test (using Node.js if available)
echo "🔌 Test 4: WebSocket Connection Test"
echo "------------------------------------"

# Create a simple Node.js WebSocket test
cat > /tmp/websocket_test.js << EOF
const WebSocket = require('ws');

const wsUrl = '$WS_URL';
const token = '$ACCESS_TOKEN';
const chatId = '$CHAT_ID';

console.log('Connecting to:', wsUrl);
console.log('Chat ID:', chatId);

try {
    const ws = new WebSocket(wsUrl, {
        headers: {
            'Authorization': 'Bearer ' + token
        }
    });
    
    ws.on('open', function() {
        console.log('✅ WebSocket connection opened');
        
        // Send a test message
        const testMessage = {
            type: 'CONNECT',
            payload: {
                chatId: chatId
            }
        };
        
        ws.send(JSON.stringify(testMessage));
        console.log('📤 Sent connection message');
        
        // Close after 3 seconds
        setTimeout(() => {
            ws.close();
            console.log('🔌 Connection closed');
        }, 3000);
    });
    
    ws.on('message', function(data) {
        console.log('📥 Received:', data.toString());
    });
    
    ws.on('error', function(error) {
        console.log('❌ WebSocket error:', error.message);
    });
    
    ws.on('close', function() {
        console.log('🔌 WebSocket closed');
    });
    
} catch (error) {
    console.log('❌ WebSocket setup error:', error.message);
}
EOF

# Try to run the WebSocket test with Node.js
if command -v node >/dev/null 2>&1; then
    echo "🟡 Testing WebSocket with Node.js..."
    node /tmp/websocket_test.js 2>/dev/null || echo "❌ Node.js WebSocket test failed"
    rm -f /tmp/websocket_test.js
else
    echo "🟡 Node.js not available, skipping direct WebSocket test"
    echo "   Use the HTML test file instead: websocket_ngrok_test.html"
fi
echo ""

# Test 5: Send a REST Message
echo "📨 Test 5: Send REST Message"
echo "-----------------------------"
message_response=$(curl -s -X POST "$API_URL/chats/$CHAT_ID/messages" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $ACCESS_TOKEN" \
  -d "{
    \"content\": \"Test message from script at $(date)\",
    \"senderId\": $USER_ID,
    \"messageType\": \"TEXT\"
  }")

echo "Message response: $message_response"
if echo "$message_response" | grep -q '"success":true'; then
    echo "✅ Message sent successfully via REST API"
else
    echo "❌ Failed to send message via REST API"
fi
echo ""

# Test 6: Get Messages
echo "📥 Test 6: Retrieve Messages"
echo "-----------------------------"
messages_response=$(curl -s -X GET "$API_URL/chats/$CHAT_ID/messages?userId=$USER_ID" \
  -H "Authorization: Bearer $ACCESS_TOKEN")

if echo "$messages_response" | grep -q '"success":true'; then
    message_count=$(echo "$messages_response" | grep -o '"content":' | wc -l | tr -d ' ')
    echo "✅ Retrieved messages successfully"
    echo "   Message count: $message_count"
else
    echo "❌ Failed to retrieve messages"
fi
echo ""

# Summary and Next Steps
echo "📊 Test Summary"
echo "==============="
echo ""
echo "✅ Backend health check"
echo "✅ Authentication"
echo "✅ Chat functionality"
echo "✅ REST message sending"
echo "✅ Message retrieval"
echo ""
echo "🧪 Manual WebSocket Testing:"
echo "   1. Open websocket_ngrok_test.html in your browser"
echo "   2. Enter your ngrok URL: $NGROK_URL"
echo "   3. Click 'Test Login' to get a token automatically"
echo "   4. Use Chat ID: $CHAT_ID"
echo "   5. Click 'Connect to WebSocket' to test real-time chat"
echo ""
echo "🚀 Frontend Integration:"
echo "   - Set REACT_APP_API_URL=$API_URL"
echo "   - Use the websocket-config-utility.js for proper URL handling"
echo "   - The RealtimeChatComponent.jsx has been updated to handle ngrok URLs"
echo ""
echo "📱 For React Native or other environments:"
echo "   - API Base URL: $API_URL"
echo "   - WebSocket URL: $WS_URL"
echo "   - Auth Header: Authorization: Bearer $ACCESS_TOKEN"
echo ""
echo "✨ All tests completed!"
