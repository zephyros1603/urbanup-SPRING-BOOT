#!/bin/bash

# Test Task Application Endpoint
echo "Testing Task Application with correct JSON payload..."

# First, let's get a JWT token for testuser1
echo "Getting JWT token for testuser1..."
TOKEN_RESPONSE=$(curl -s -X POST "http://localhost:8080/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "testuser1@urbanup.com",
    "password": "password123"
  }')

echo "Token response: $TOKEN_RESPONSE"

# Extract the token from the response (it's nested under data.accessToken)
TOKEN=$(echo $TOKEN_RESPONSE | grep -o '"accessToken":"[^"]*"' | cut -d'"' -f4)

if [ -z "$TOKEN" ]; then
    echo "Failed to get JWT token. Response: $TOKEN_RESPONSE"
    exit 1
fi

echo "JWT Token obtained successfully"
echo "Token: $TOKEN"

# Now test the task application endpoint with the correct JSON structure
echo ""
echo "Testing task application endpoint..."
APPLICATION_RESPONSE=$(curl -s -X POST "http://localhost:8080/api/tasks/2/apply" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "fulfillerId": 39,
    "proposedRate": 500,
    "message": "gdcisbdads",
    "estimatedCompletionTime": "2025-08-12T02:06:00"
  }')

echo "Application Response:"
echo $APPLICATION_RESPONSE | jq '.' || echo $APPLICATION_RESPONSE

echo ""
echo "Task application test completed."
