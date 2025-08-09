#!/bin/bash

# Test Chat Performance Optimization
echo "=== Chat Performance Optimization Test ==="

# Login
USER1_TOKEN=$(curl -s -X POST "http://localhost:8080/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "testuser1@urbanup.com",
    "password": "Password123"
  }' | jq -r '.data.accessToken')

echo "✅ User logged in"

# Test 1: User chats (should be fast, no messages)
echo ""
echo "=== Test 1: User Chats Endpoint (Fast) ==="
start_time=$(date +%s.%N)
response=$(curl -s -H "Authorization: Bearer $USER1_TOKEN" "http://localhost:8080/api/chats/user/51")
end_time=$(date +%s.%N)
duration=$(echo "$end_time - $start_time" | bc)

message_count=$(echo "$response" | jq '.data[0].messages | length // 0')
echo "Response time: ${duration}s"
echo "Messages included: $message_count"

if (( $(echo "$duration < 0.5" | bc -l) )); then
    echo "✅ FAST: User chats endpoint performing well"
else
    echo "❌ SLOW: User chats endpoint taking too long"
fi

# Test 2: Individual chat with messages
echo ""
echo "=== Test 2: Individual Chat Endpoint (With Messages) ==="
start_time=$(date +%s.%N)
response=$(curl -s -H "Authorization: Bearer $USER1_TOKEN" "http://localhost:8080/api/chats/12?userId=51")
end_time=$(date +%s.%N)
duration=$(echo "$end_time - $start_time" | bc)

message_count=$(echo "$response" | jq '.data.messages | length // 0')
echo "Response time: ${duration}s"
echo "Messages included: $message_count"

if (( $(echo "$duration < 1.0" | bc -l) )); then
    echo "✅ ACCEPTABLE: Individual chat endpoint performing well"
else
    echo "❌ SLOW: Individual chat endpoint taking too long"
fi

# Test 3: Messages-only endpoint
echo ""
echo "=== Test 3: Messages-Only Endpoint (Recommended) ==="
start_time=$(date +%s.%N)
response=$(curl -s -H "Authorization: Bearer $USER1_TOKEN" "http://localhost:8080/api/chats/12/messages?userId=51")
end_time=$(date +%s.%N)
duration=$(echo "$end_time - $start_time" | bc)

message_count=$(echo "$response" | jq '.data | length // 0')
echo "Response time: ${duration}s"
echo "Messages returned: $message_count"

if (( $(echo "$duration < 0.5" | bc -l) )); then
    echo "✅ FAST: Messages-only endpoint performing well"
else
    echo "❌ SLOW: Messages-only endpoint taking too long"
fi

echo ""
echo "=== Recommendation ==="
echo "Frontend should use:"
echo "1. /chats/user/{userId} for chat list (fast, no messages)"
echo "2. /chats/{chatId}/messages for loading messages separately"
echo "3. Avoid repeated calls to /chats/{chatId}?userId={userId}"
