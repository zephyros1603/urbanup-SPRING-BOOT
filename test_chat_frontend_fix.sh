#!/bin/bash

# Chat Frontend Fix Validation Test
echo "=== Chat Frontend Fix Validation ==="
echo "Testing updated chat data structure..."

# Login both users
echo "Logging in users..."
USER1_TOKEN=$(curl -s -X POST "http://localhost:8080/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "testuser1@urbanup.com",
    "password": "Password123"
  }' | jq -r '.data.accessToken')

USER2_TOKEN=$(curl -s -X POST "http://localhost:8080/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "testuser2@urbanup.com",
    "password": "Password123"
  }' | jq -r '.data.accessToken')

echo "✅ Users logged in successfully"

# Test 1: User chats endpoint
echo ""
echo "=== Test 1: User Chats Endpoint ==="
response=$(curl -s -H "Authorization: Bearer $USER1_TOKEN" "http://localhost:8080/api/chats/user/51")
poster_id=$(echo "$response" | jq -r '.data[0].poster.id // "null"')
fulfiller_id=$(echo "$response" | jq -r '.data[0].fulfiller.id // "null"')

if [[ "$poster_id" != "null" && "$fulfiller_id" != "null" ]]; then
    echo "✅ PASS: poster.id = $poster_id, fulfiller.id = $fulfiller_id"
else
    echo "❌ FAIL: Missing poster/fulfiller id fields"
    echo "$response" | jq .
fi

# Test 2: Individual chat endpoint
echo ""
echo "=== Test 2: Individual Chat Endpoint ==="
response=$(curl -s -H "Authorization: Bearer $USER1_TOKEN" "http://localhost:8080/api/chats/12?userId=51")
poster_id=$(echo "$response" | jq -r '.data.poster.id // "null"')
fulfiller_id=$(echo "$response" | jq -r '.data.fulfiller.id // "null"')

if [[ "$poster_id" != "null" && "$fulfiller_id" != "null" ]]; then
    echo "✅ PASS: poster.id = $poster_id, fulfiller.id = $fulfiller_id"
else
    echo "❌ FAIL: Missing poster/fulfiller id fields"
    echo "$response" | jq .
fi

# Test 3: Task-specific chat endpoint
echo ""
echo "=== Test 3: Task-Specific Chat Endpoint ==="
response=$(curl -s -H "Authorization: Bearer $USER1_TOKEN" "http://localhost:8080/api/chats/task/24?userId=51")
poster_id=$(echo "$response" | jq -r '.data.poster.id // "null"')
fulfiller_id=$(echo "$response" | jq -r '.data.fulfiller.id // "null"')

if [[ "$poster_id" != "null" && "$fulfiller_id" != "null" ]]; then
    echo "✅ PASS: poster.id = $poster_id, fulfiller.id = $fulfiller_id"
else
    echo "❌ FAIL: Missing poster/fulfiller id fields"
    echo "$response" | jq .
fi

# Test 4: Data structure completeness
echo ""
echo "=== Test 4: Complete Data Structure ==="
response=$(curl -s -H "Authorization: Bearer $USER1_TOKEN" "http://localhost:8080/api/chats/user/51")
chat_data=$(echo "$response" | jq '.data[0]')

# Check all required fields
fields=("id" "taskTitle" "otherParticipantId" "otherParticipantName" "poster" "fulfiller" "messages" "lastActivity")
all_present=true

for field in "${fields[@]}"; do
    if echo "$chat_data" | jq -e ".$field" > /dev/null; then
        echo "✅ Field '$field' present"
    else
        echo "❌ Field '$field' missing"
        all_present=false
    fi
done

if $all_present; then
    echo "✅ ALL TESTS PASSED: Chat data structure is complete"
    echo ""
    echo "Frontend should now be able to access:"
    echo "- chat.poster.id"
    echo "- chat.fulfiller.id"
    echo "- All other required fields"
else
    echo "❌ SOME TESTS FAILED: Check missing fields above"
fi

echo ""
echo "=== Sample Chat Data Structure ==="
echo "$response" | jq '.data[0] | {id, taskTitle, poster: {id, firstName, lastName}, fulfiller: {id, firstName, lastName}}'
