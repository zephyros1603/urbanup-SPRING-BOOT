#!/bin/bash

# Enhanced Task Creation Test Script
echo "=== UrbanUp Enhanced Task Creation Test ==="

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

# Test: Enhanced task creation with all form fields
echo ""
echo "2. Testing enhanced task creation..."
TASK_RESPONSE=$(curl -s -X POST http://localhost:8080/api/tasks \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "posterId": 1,
    "title": "Professional Web Development",
    "description": "Need a responsive website for my business with modern design and functionality",
    "category": "PROFESSIONAL_TASKS",
    "price": 500.00,
    "pricingType": "FIXED",
    "location": "Bangalore, BTM",
    "cityArea": "Bangalore, BTM",
    "fullAddress": "123 Main Street, BTM Layout, Bangalore, Karnataka 560076",
    "deadline": "2025-12-31T18:00:00",
    "estimatedDurationHours": 40,
    "isUrgent": true,
    "specialRequirements": "Must have experience with React and responsive design. Portfolio review required.",
    "skillsRequired": ["React", "JavaScript", "CSS", "HTML", "Responsive Design"]
  }')

echo "Enhanced Task Creation Response:"
echo "$TASK_RESPONSE" | python3 -m json.tool 2>/dev/null || echo "$TASK_RESPONSE"

# Test: Get the created task to verify all fields are saved
echo ""
echo "3. Retrieving created task to verify fields..."
TASK_ID=$(echo $TASK_RESPONSE | grep -o '"id":[0-9]*' | cut -d':' -f2)

if [ ! -z "$TASK_ID" ]; then
  GET_TASK_RESPONSE=$(curl -s -X GET http://localhost:8080/api/tasks/$TASK_ID \
    -H "Authorization: Bearer $TOKEN")
  
  echo "Retrieved Task Details:"
  echo "$GET_TASK_RESPONSE" | python3 -m json.tool 2>/dev/null || echo "$GET_TASK_RESPONSE"
else
  echo "❌ Could not extract task ID from creation response"
fi

# Test: List all tasks to see the new task in the list
echo ""
echo "4. Listing all tasks..."
TASKS_RESPONSE=$(curl -s -X GET http://localhost:8080/api/tasks \
  -H "Authorization: Bearer $TOKEN")

echo "Tasks List Response:"
echo "$TASKS_RESPONSE" | python3 -m json.tool 2>/dev/null || echo "$TASKS_RESPONSE"

echo ""
echo "=== Enhanced Task Creation Test Complete ==="
