#!/bin/bash

# API Test Script - Verifying Fixed Endpoints
echo "Testing UrbanUp API Endpoints..."
echo "================================="

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
else
    echo "❌ Tasks endpoint failed"
    echo "Response: $TASKS_BODY"
fi

echo ""
echo "3. Testing GET /api/notifications endpoint..."
NOTIF_RESPONSE=$(curl -s -w "%{http_code}" -X GET http://localhost:8080/api/notifications/user/39 \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json")

NOTIF_HTTP_CODE="${NOTIF_RESPONSE: -3}"
NOTIF_BODY="${NOTIF_RESPONSE%???}"

echo "Status Code: $NOTIF_HTTP_CODE"
if [ "$NOTIF_HTTP_CODE" = "200" ]; then
    echo "✅ Notifications endpoint working correctly"
else
    echo "❌ Notifications endpoint failed"
    echo "Response: $NOTIF_BODY"
fi

echo ""
echo "4. Testing notification counts endpoint..."
COUNTS_RESPONSE=$(curl -s -w "%{http_code}" -X GET http://localhost:8080/api/notifications/user/39/counts \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json")

COUNTS_HTTP_CODE="${COUNTS_RESPONSE: -3}"
COUNTS_BODY="${COUNTS_RESPONSE%???}"

echo "Status Code: $COUNTS_HTTP_CODE"
if [ "$COUNTS_HTTP_CODE" = "200" ]; then
    echo "✅ Notification counts endpoint working correctly"
else
    echo "❌ Notification counts endpoint failed"
    echo "Response: $COUNTS_BODY"
fi

echo ""
echo "Test Summary:"
echo "============"
if [ "$TASKS_HTTP_CODE" = "200" ] && [ "$NOTIF_HTTP_CODE" = "200" ] && [ "$COUNTS_HTTP_CODE" = "200" ]; then
    echo "✅ All endpoints are working correctly!"
else
    echo "❌ Some endpoints still have issues"
fi
