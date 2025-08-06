#!/bin/bash

echo "🔔 Testing Notification System Endpoints"
echo "========================================="

# Wait for server to start
echo "⏳ Waiting for server to start..."
sleep 15

BASE_URL="http://localhost:8080/api"

# Register a test user and get token
echo "📝 Creating test user..."
REGISTER_RESPONSE=$(curl -s -X POST "$BASE_URL/auth/register" \
  -H "Content-Type: application/json" \
  -d "{\"email\": \"notifytest$(date +%s)@example.com\", \"password\": \"password123\", \"firstName\": \"Notify\", \"lastName\": \"Test\", \"phoneNumber\": \"+91$(date +%s)\"}")

echo "Registration Response: $REGISTER_RESPONSE"

# Extract token and user ID
TOKEN=$(echo $REGISTER_RESPONSE | python3 -c "import sys, json; print(json.load(sys.stdin)['data']['accessToken'])" 2>/dev/null)
USER_ID=$(echo $REGISTER_RESPONSE | python3 -c "import sys, json; print(json.load(sys.stdin)['data']['user']['id'])" 2>/dev/null)

if [ -z "$TOKEN" ] || [ -z "$USER_ID" ]; then
    echo "❌ Failed to extract token or user ID"
    exit 1
fi

echo "✅ User created - ID: $USER_ID"
echo "🔑 Token: ${TOKEN:0:20}..."

# Create a task to generate some notifications
echo ""
echo "📋 Creating a task to generate notifications..."
TASK_RESPONSE=$(curl -s -X POST "$BASE_URL/tasks" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "title": "Test Task for Notifications",
    "description": "This will generate notifications",
    "category": "PERSONAL_ERRANDS",
    "pricingType": "FIXED",
    "price": 100.00,
    "location": "Test City",
    "deadline": "2024-12-31T18:00:00"
  }')

echo "Task created: $(echo $TASK_RESPONSE | cut -c1-100)..."

# Wait a moment for notifications to be created
sleep 2

echo ""
echo "🔔 Testing Notification Endpoints:"
echo "=================================="

# Test 1: GET /notifications/user/{userId}
echo ""
echo "1️⃣ Testing: GET /notifications/user/$USER_ID"
NOTIF_RESPONSE=$(curl -s -w "\nHTTP_STATUS:%{http_code}" \
  -H "Authorization: Bearer $TOKEN" \
  "$BASE_URL/notifications/user/$USER_ID?limit=10&offset=0")

HTTP_STATUS=$(echo "$NOTIF_RESPONSE" | grep "HTTP_STATUS:" | cut -d: -f2)
RESPONSE_BODY=$(echo "$NOTIF_RESPONSE" | sed '/HTTP_STATUS:/d')

if [ "$HTTP_STATUS" = "200" ]; then
    echo "✅ GET notifications - Status: $HTTP_STATUS"
    NOTIF_COUNT=$(echo $RESPONSE_BODY | python3 -c "import sys, json; print(len(json.load(sys.stdin)['data']))" 2>/dev/null || echo "0")
    echo "📄 Found $NOTIF_COUNT notifications"
else
    echo "❌ GET notifications - Status: $HTTP_STATUS"
    echo "Response: $RESPONSE_BODY"
fi

# Test 2: GET /notifications/user/{userId}/counts
echo ""
echo "2️⃣ Testing: GET /notifications/user/$USER_ID/counts"
COUNT_RESPONSE=$(curl -s -w "\nHTTP_STATUS:%{http_code}" \
  -H "Authorization: Bearer $TOKEN" \
  "$BASE_URL/notifications/user/$USER_ID/counts")

HTTP_STATUS=$(echo "$COUNT_RESPONSE" | grep "HTTP_STATUS:" | cut -d: -f2)
RESPONSE_BODY=$(echo "$COUNT_RESPONSE" | sed '/HTTP_STATUS:/d')

if [ "$HTTP_STATUS" = "200" ]; then
    echo "✅ GET notification counts - Status: $HTTP_STATUS"
    echo "📊 Response: $(echo $RESPONSE_BODY | python3 -c "import sys, json; d=json.load(sys.stdin)['data']; print(f\"Total: {d['total']}, Unread: {d['unread']}\")" 2>/dev/null || echo "Parse error")"
else
    echo "❌ GET notification counts - Status: $HTTP_STATUS"
    echo "Response: $RESPONSE_BODY"
fi

# Get a notification ID for further testing
NOTIF_ID=$(echo $RESPONSE_BODY | python3 -c "
import sys, json
try:
    data = json.load(sys.stdin)
    if 'data' in data and len(data['data']) > 0:
        print(data['data'][0]['id'])
    else:
        print('')
except:
    print('')
" 2>/dev/null || echo "")

if [ -z "$NOTIF_ID" ]; then
    # Try to get notification ID from the first request
    NOTIF_ID=$(echo $NOTIF_RESPONSE | sed '/HTTP_STATUS:/d' | python3 -c "
import sys, json
try:
    data = json.load(sys.stdin)
    if 'data' in data and len(data['data']) > 0:
        print(data['data'][0]['id'])
    else:
        print('')
except:
    print('')
" 2>/dev/null || echo "")
fi

# Test 3: PUT /notifications/{notificationId}/read (if we have a notification)
if [ -n "$NOTIF_ID" ]; then
    echo ""
    echo "3️⃣ Testing: PUT /notifications/$NOTIF_ID/read"
    READ_RESPONSE=$(curl -s -w "\nHTTP_STATUS:%{http_code}" \
      -X PUT \
      -H "Authorization: Bearer $TOKEN" \
      "$BASE_URL/notifications/$NOTIF_ID/read?userId=$USER_ID")

    HTTP_STATUS=$(echo "$READ_RESPONSE" | grep "HTTP_STATUS:" | cut -d: -f2)
    RESPONSE_BODY=$(echo "$READ_RESPONSE" | sed '/HTTP_STATUS:/d')

    if [ "$HTTP_STATUS" = "200" ]; then
        echo "✅ Mark notification as read - Status: $HTTP_STATUS"
    else
        echo "❌ Mark notification as read - Status: $HTTP_STATUS"
        echo "Response: $RESPONSE_BODY"
    fi
else
    echo ""
    echo "3️⃣ Testing: PUT /notifications/{id}/read"
    echo "⚠️  Skipping - No notification ID available"
fi

# Test 4: PUT /notifications/user/{userId}/read-all
echo ""
echo "4️⃣ Testing: PUT /notifications/user/$USER_ID/read-all"
READ_ALL_RESPONSE=$(curl -s -w "\nHTTP_STATUS:%{http_code}" \
  -X PUT \
  -H "Authorization: Bearer $TOKEN" \
  "$BASE_URL/notifications/user/$USER_ID/read-all")

HTTP_STATUS=$(echo "$READ_ALL_RESPONSE" | grep "HTTP_STATUS:" | cut -d: -f2)
RESPONSE_BODY=$(echo "$READ_ALL_RESPONSE" | sed '/HTTP_STATUS:/d')

if [ "$HTTP_STATUS" = "200" ]; then
    echo "✅ Mark all notifications as read - Status: $HTTP_STATUS"
    echo "📝 $(echo $RESPONSE_BODY | python3 -c "import sys, json; print(json.load(sys.stdin)['message'])" 2>/dev/null || echo "Success")"
else
    echo "❌ Mark all notifications as read - Status: $HTTP_STATUS"
    echo "Response: $RESPONSE_BODY"
fi

# Test 5: DELETE /notifications/{notificationId} (if we have a notification)
if [ -n "$NOTIF_ID" ]; then
    echo ""
    echo "5️⃣ Testing: DELETE /notifications/$NOTIF_ID"
    DELETE_RESPONSE=$(curl -s -w "\nHTTP_STATUS:%{http_code}" \
      -X DELETE \
      -H "Authorization: Bearer $TOKEN" \
      "$BASE_URL/notifications/$NOTIF_ID?userId=$USER_ID")

    HTTP_STATUS=$(echo "$DELETE_RESPONSE" | grep "HTTP_STATUS:" | cut -d: -f2)
    RESPONSE_BODY=$(echo "$DELETE_RESPONSE" | sed '/HTTP_STATUS:/d')

    if [ "$HTTP_STATUS" = "200" ]; then
        echo "✅ Delete notification - Status: $HTTP_STATUS"
    else
        echo "❌ Delete notification - Status: $HTTP_STATUS"
        echo "Response: $RESPONSE_BODY"
    fi
else
    echo ""
    echo "5️⃣ Testing: DELETE /notifications/{id}"
    echo "⚠️  Skipping - No notification ID available"
fi

# Test 6: PUT /notifications/user/{userId}/preferences
echo ""
echo "6️⃣ Testing: PUT /notifications/user/$USER_ID/preferences"
PREF_RESPONSE=$(curl -s -w "\nHTTP_STATUS:%{http_code}" \
  -X PUT \
  -H "Authorization: Bearer $TOKEN" \
  "$BASE_URL/notifications/user/$USER_ID/preferences")

HTTP_STATUS=$(echo "$PREF_RESPONSE" | grep "HTTP_STATUS:" | cut -d: -f2)
RESPONSE_BODY=$(echo "$PREF_RESPONSE" | sed '/HTTP_STATUS:/d')

if [ "$HTTP_STATUS" = "200" ]; then
    echo "✅ Update notification preferences - Status: $HTTP_STATUS"
    echo "📝 $(echo $RESPONSE_BODY | python3 -c "import sys, json; print(json.load(sys.stdin)['message'])" 2>/dev/null || echo "Success")"
else
    echo "❌ Update notification preferences - Status: $HTTP_STATUS"
    echo "Response: $RESPONSE_BODY"
fi

echo ""
echo "🎉 Notification System Testing Complete!"
echo "========================================"
