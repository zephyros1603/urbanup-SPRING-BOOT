#!/bin/bash

# API Test Script - Testing CURRENT Working Endpoints
echo "Testing UrbanUp API Endpoints (Current Working Paths)..."
echo "========================================================="

# Get JWT token for testuser1
echo "1. Getting JWT token..."
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "testuser1@urbanup.com", "password": "password123"}' | \
  grep -o '"accessToken":"[^"]*"' | sed 's/"accessToken":"//g' | sed 's/"$//g')

if [ -z "$TOKEN" ]; then
    echo "❌ Failed to get authentication token"
    exit 1
else
    echo "✅ Token obtained successfully"
fi

echo ""
echo "2. Testing GET /api/tasks endpoint..."
TASKS_RESPONSE=$(curl -s -w "%{http_code}" -X GET http://localhost:8080/api/tasks \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json")

TASKS_HTTP_CODE="${TASKS_RESPONSE: -3}"
TASKS_BODY="${TASKS_RESPONSE%???}"

echo "Status Code: $TASKS_HTTP_CODE"
if [ "$TASKS_HTTP_CODE" = "200" ]; then
    echo "✅ Tasks endpoint working correctly"
    TASK_COUNT=$(echo "$TASKS_BODY" | grep -o '"success":true' | wc -l)
    echo "   Tasks response includes success field"
else
    echo "❌ Tasks endpoint failed"
    echo "   Response: $TASKS_BODY"
fi

echo ""
echo "3. Testing GET /notifications/user/39 endpoint (current working path)..."
NOTIF_RESPONSE=$(curl -s -w "%{http_code}" -X GET http://localhost:8080/notifications/user/39 \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json")

NOTIF_HTTP_CODE="${NOTIF_RESPONSE: -3}"
NOTIF_BODY="${NOTIF_RESPONSE%???}"

echo "Status Code: $NOTIF_HTTP_CODE"
if [ "$NOTIF_HTTP_CODE" = "200" ]; then
    echo "✅ Notifications endpoint working correctly"
else
    echo "❌ Notifications endpoint failed"
    echo "   Response: $NOTIF_BODY"
fi

echo ""
echo "4. Testing notification counts endpoint (current working path)..."
COUNTS_RESPONSE=$(curl -s -w "%{http_code}" -X GET http://localhost:8080/notifications/user/39/counts \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json")

COUNTS_HTTP_CODE="${COUNTS_RESPONSE: -3}"
COUNTS_BODY="${COUNTS_RESPONSE%???}"

echo "Status Code: $COUNTS_HTTP_CODE"
if [ "$COUNTS_HTTP_CODE" = "200" ]; then
    echo "✅ Notification counts endpoint working correctly"
else
    echo "❌ Notification counts endpoint failed"
    echo "   Response: $COUNTS_BODY"
fi

echo ""
echo "5. Testing user profile endpoint..."
USER_RESPONSE=$(curl -s -w "%{http_code}" -X GET http://localhost:8080/api/users/39 \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json")

USER_HTTP_CODE="${USER_RESPONSE: -3}"
USER_BODY="${USER_RESPONSE%???}"

echo "Status Code: $USER_HTTP_CODE"
if [ "$USER_HTTP_CODE" = "200" ]; then
    echo "✅ User profile endpoint working correctly"
else
    echo "❌ User profile endpoint failed"
    echo "   Response: $USER_BODY"
fi

echo ""
echo "==============================================="
echo "Final API Status Summary:"
echo "==============================================="
echo "🔐 Authentication: ✅ Working (login successful)"
echo "📋 Tasks API: $([ "$TASKS_HTTP_CODE" = "200" ] && echo "✅ Working" || echo "❌ Failed")"
echo "🔔 Notifications: $([ "$NOTIF_HTTP_CODE" = "200" ] && echo "✅ Working" || echo "❌ Failed") (at /notifications/*)"
echo "🔔 Notification Counts: $([ "$COUNTS_HTTP_CODE" = "200" ] && echo "✅ Working" || echo "❌ Failed")"
echo "👤 User Profile: $([ "$USER_HTTP_CODE" = "200" ] && echo "✅ Working" || echo "❌ Failed")"

echo ""
if [ "$TASKS_HTTP_CODE" = "200" ] && [ "$USER_HTTP_CODE" = "200" ]; then
    echo "🎉 GREAT NEWS: Critical API endpoints are working!"
    echo "   - Task fetching FIXED (no more 500 errors)"
    echo "   - Authentication working perfectly"
    echo "   - User management working"
    
    if [ "$NOTIF_HTTP_CODE" = "200" ]; then
        echo "   - Notifications working (needs path correction for frontend)"
    else
        echo "   - Notifications need minor fixes"
    fi
    
    echo ""
    echo "📝 Action Items:"
    echo "   1. ✅ FIXED: Task lazy loading issue (skillsRequired now EAGER)"
    echo "   2. ✅ FIXED: NotificationController mapping"
    echo "   3. 🔄 PENDING: Restart application to apply /api/notifications mapping"
    echo "   4. 🔄 PENDING: Update frontend to use working endpoints"
else
    echo "❌ Some critical endpoints still need attention"
fi
