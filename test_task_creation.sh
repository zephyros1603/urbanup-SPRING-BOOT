#!/bin/bash

# Task Creation Debug Script
echo "=== UrbanUp Task Creation Debug ==="

# First, get a fresh JWT token
echo "1. Getting fresh JWT token..."
LOGIN_RESPONSE=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "johndoe@email.com",
    "password": "password123"
  }')

echo "Login Response: $LOGIN_RESPONSE"

# Extract token from response
TOKEN=$(echo $LOGIN_RESPONSE | grep -o '"accessToken":"[^"]*"' | cut -d'"' -f4)

if [ -z "$TOKEN" ]; then
  echo "❌ Failed to get JWT token"
  echo "Response: $LOGIN_RESPONSE"
  exit 1
fi

echo "✅ JWT Token obtained: ${TOKEN:0:50}..."

# Test 1: Simple task creation
echo ""
echo "2. Testing task creation..."
TASK_RESPONSE=$(curl -s -X POST http://localhost:8080/api/tasks \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "posterId": 1,
    "title": "Test Delivery Task",
    "description": "Need someone to deliver groceries",
    "category": "DELIVERY",
    "price": 25.50,
    "location": "123 Main St, New York, NY"
  }')

echo "Task Creation Response: $TASK_RESPONSE"

# Test 2: Check if user exists
echo ""
echo "3. Checking if user exists..."
USER_RESPONSE=$(curl -s -X GET http://localhost:8080/api/users/1 \
  -H "Authorization: Bearer $TOKEN")

echo "User Response: $USER_RESPONSE"

# Test 3: List all tasks to see if creation worked
echo ""
echo "4. Listing all tasks..."
TASKS_RESPONSE=$(curl -s -X GET http://localhost:8080/api/tasks \
  -H "Authorization: Bearer $TOKEN")

echo "Tasks List Response: $TASKS_RESPONSE"

echo ""
echo "=== Test Complete ==="
