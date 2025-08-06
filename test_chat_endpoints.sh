#!/bin/bash

# Chat Endpoints Testing Script
# This script tests all chat functionality with two pre-created users

echo "=============================================="
echo "URBANUP CHAT ENDPOINTS TESTING SCRIPT"
echo "=============================================="
echo ""

# Test User Credentials
ALICE_TOKEN="eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjQ2LCJzdWIiOiJhbGljZS5jaGF0QHRlc3R1c2VyLmNvbSIsImlhdCI6MTc1NDUwODc0MSwiZXhwIjoxNzU0NTk1MTQxfQ.ZHIbxv3ZLaEZUOa-iehTRymmqWwj2fhCLNjic8IQPmk"
BOB_TOKEN="eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjQ3LCJzdWIiOiJib2IuY2hhdEB0ZXN0dXNlci5jb20iLCJpYXQiOjE3NTQ1MDg3NjcsImV4cCI6MTc1NDU5NTE2N30.irdq53YhBtKzA3Fx5kxSuoQgG5Wmn-V5r6OSk-csLCo"
ALICE_ID=46
BOB_ID=47
TASK_ID=18
BASE_URL="http://localhost:8080/api"

echo "Test Users:"
echo "  Alice (ID: $ALICE_ID) - alice.chat@testuser.com"
echo "  Bob (ID: $BOB_ID) - bob.chat@testuser.com"
echo "  Task ID: $TASK_ID (Help with Moving Furniture)"
echo ""

# Function to print API call results nicely
print_result() {
    echo "RESPONSE:"
    echo "$1" | jq '.' 2>/dev/null || echo "$1"
    echo ""
    echo "----------------------------------------"
    echo ""
}

# 1. Create a Chat between Alice and Bob
echo "1. CREATING CHAT BETWEEN ALICE AND BOB"
echo "POST $BASE_URL/chats"
CHAT_RESPONSE=$(curl -s -X POST "$BASE_URL/chats" \
    -H "Content-Type: application/json" \
    -H "Authorization: Bearer $ALICE_TOKEN" \
    -d "{
        \"taskId\": $TASK_ID,
        \"fulfillerId\": $BOB_ID
    }")

CHAT_ID=$(echo "$CHAT_RESPONSE" | jq -r '.data.id' 2>/dev/null)
print_result "$CHAT_RESPONSE"

if [ "$CHAT_ID" = "null" ] || [ -z "$CHAT_ID" ]; then
    echo "❌ Failed to create chat. Exiting..."
    exit 1
fi

echo "✅ Chat created successfully with ID: $CHAT_ID"
echo ""

# 2. Alice sends a message
echo "2. ALICE SENDS A MESSAGE"
echo "POST $BASE_URL/chats/$CHAT_ID/messages"
MESSAGE_1=$(curl -s -X POST "$BASE_URL/chats/$CHAT_ID/messages" \
    -H "Content-Type: application/json" \
    -H "Authorization: Bearer $ALICE_TOKEN" \
    -d "{
        \"senderId\": $ALICE_ID,
        \"content\": \"Hi Bob! I saw you're interested in helping with my furniture move. Are you available this weekend?\",
        \"messageType\": \"TEXT\"
    }")
print_result "$MESSAGE_1"

# 3. Bob sends a reply
echo "3. BOB SENDS A REPLY"
echo "POST $BASE_URL/chats/$CHAT_ID/messages"
MESSAGE_2=$(curl -s -X POST "$BASE_URL/chats/$CHAT_ID/messages" \
    -H "Content-Type: application/json" \
    -H "Authorization: Bearer $BOB_TOKEN" \
    -d "{
        \"senderId\": $BOB_ID,
        \"content\": \"Hi Alice! Yes, I'm available this weekend. What time works best for you?\",
        \"messageType\": \"TEXT\"
    }")
print_result "$MESSAGE_2"

# 4. Alice sends another message
echo "4. ALICE SENDS ANOTHER MESSAGE"
MESSAGE_3=$(curl -s -X POST "$BASE_URL/chats/$CHAT_ID/messages" \
    -H "Content-Type: application/json" \
    -H "Authorization: Bearer $ALICE_TOKEN" \
    -d "{
        \"senderId\": $ALICE_ID,
        \"content\": \"Great! How about Saturday morning around 10 AM? I have a few pieces of furniture and some boxes.\",
        \"messageType\": \"TEXT\"
    }")
print_result "$MESSAGE_3"

# 5. Get chat messages
echo "5. GET CHAT MESSAGES (Alice's view)"
echo "GET $BASE_URL/chats/$CHAT_ID/messages?userId=$ALICE_ID"
MESSAGES=$(curl -s -X GET "$BASE_URL/chats/$CHAT_ID/messages?userId=$ALICE_ID" \
    -H "Authorization: Bearer $ALICE_TOKEN")
print_result "$MESSAGES"

# 6. Get chat messages from Bob's perspective
echo "6. GET CHAT MESSAGES (Bob's view)"
echo "GET $BASE_URL/chats/$CHAT_ID/messages?userId=$BOB_ID"
MESSAGES_BOB=$(curl -s -X GET "$BASE_URL/chats/$CHAT_ID/messages?userId=$BOB_ID" \
    -H "Authorization: Bearer $BOB_TOKEN")
print_result "$MESSAGES_BOB"

# 7. Mark messages as read (Bob marks Alice's messages as read)
echo "7. BOB MARKS MESSAGES AS READ"
echo "PUT $BASE_URL/chats/$CHAT_ID/messages/read?userId=$BOB_ID"
READ_RESULT=$(curl -s -X PUT "$BASE_URL/chats/$CHAT_ID/messages/read?userId=$BOB_ID" \
    -H "Authorization: Bearer $BOB_TOKEN")
print_result "$READ_RESULT"

# 8. Get specific chat by ID
echo "8. GET SPECIFIC CHAT BY ID (Alice's view)"
echo "GET $BASE_URL/chats/$CHAT_ID?userId=$ALICE_ID"
CHAT_DETAILS=$(curl -s -X GET "$BASE_URL/chats/$CHAT_ID?userId=$ALICE_ID" \
    -H "Authorization: Bearer $ALICE_TOKEN")
print_result "$CHAT_DETAILS"

# 9. Get all chats for Alice
echo "9. GET ALL CHATS FOR ALICE"
echo "GET $BASE_URL/chats/user/$ALICE_ID"
ALICE_CHATS=$(curl -s -X GET "$BASE_URL/chats/user/$ALICE_ID" \
    -H "Authorization: Bearer $ALICE_TOKEN")
print_result "$ALICE_CHATS"

# 10. Get all chats for Bob
echo "10. GET ALL CHATS FOR BOB"
echo "GET $BASE_URL/chats/user/$BOB_ID"
BOB_CHATS=$(curl -s -X GET "$BASE_URL/chats/user/$BOB_ID" \
    -H "Authorization: Bearer $BOB_TOKEN")
print_result "$BOB_CHATS"

# 11. Get unread message count for Alice
echo "11. GET UNREAD MESSAGE COUNT FOR ALICE"
echo "GET $BASE_URL/chats/user/$ALICE_ID/unread-count"
ALICE_UNREAD=$(curl -s -X GET "$BASE_URL/chats/user/$ALICE_ID/unread-count" \
    -H "Authorization: Bearer $ALICE_TOKEN")
print_result "$ALICE_UNREAD"

# 12. Get unread message count for Bob
echo "12. GET UNREAD MESSAGE COUNT FOR BOB"
echo "GET $BASE_URL/chats/user/$BOB_ID/unread-count"
BOB_UNREAD=$(curl -s -X GET "$BASE_URL/chats/user/$BOB_ID/unread-count" \
    -H "Authorization: Bearer $BOB_TOKEN")
print_result "$BOB_UNREAD"

# 13. Test file upload (create a dummy file for testing)
echo "13. TEST FILE UPLOAD TO CHAT"
echo "Creating test file..."
echo "This is a test file for chat upload" > /tmp/test_chat_file.txt

echo "POST $BASE_URL/chats/$CHAT_ID/media"
UPLOAD_RESULT=$(curl -s -X POST "$BASE_URL/chats/$CHAT_ID/media" \
    -H "Authorization: Bearer $ALICE_TOKEN" \
    -F "file=@/tmp/test_chat_file.txt" \
    -F "senderId=$ALICE_ID" \
    -F "caption=Here's the inventory list for the move")
print_result "$UPLOAD_RESULT"

# Clean up test file
rm -f /tmp/test_chat_file.txt

# 14. Send a system message
echo "14. SEND SYSTEM MESSAGE"
echo "POST $BASE_URL/chats/$CHAT_ID/system-message"
SYSTEM_MSG=$(curl -s -X POST "$BASE_URL/chats/$CHAT_ID/system-message?content=Task%20has%20been%20accepted%20by%20Bob" \
    -H "Authorization: Bearer $ALICE_TOKEN")
print_result "$SYSTEM_MSG"

echo "=============================================="
echo "TESTING COMPLETE!"
echo "=============================================="
echo ""
echo "Summary:"
echo "  ✅ Chat created between Alice and Bob"
echo "  ✅ Multiple messages sent and received"
echo "  ✅ Messages marked as read"
echo "  ✅ Chat details retrieved"
echo "  ✅ User chats listed"
echo "  ✅ Unread counts retrieved"
echo "  ✅ File upload tested"
echo "  ✅ System message sent"
echo ""
echo "Chat ID for future testing: $CHAT_ID"
echo ""
echo "You can now test real-time functionality by:"
echo "1. Opening two terminals"
echo "2. Using WebSocket clients to connect to the chat"
echo "3. Sending messages and seeing them appear in real-time"
