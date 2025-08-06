#!/bin/bash

# Comprehensive Task Endpoints Test Script
echo "🚀 Testing ALL UrbanUp Task API Endpoints..."
echo "=============================================="

# Your JWT token
JWT_TOKEN="eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjM5LCJzdWIiOiJ0ZXN0dXNlcjFAdXJiYW51cC5jb20iLCJpYXQiOjE3NTQ0NjQ3MDQsImV4cCI6MTc1NDU1MTEwNH0.1fuYXFZ549KfV43tzEkU0hwxcU2Fs5ImKJuo3PjPHWw"

# Alternative: Get a fresh JWT token
echo "1. Getting fresh JWT token..."
FRESH_TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "testuser1@urbanup.com", "password": "password123"}' | \
  grep -o '"accessToken":"[^"]*"' | sed 's/"accessToken":"//g' | sed 's/"$//g')

if [ -n "$FRESH_TOKEN" ]; then
    TOKEN="$FRESH_TOKEN"
    echo "✅ Fresh token obtained successfully"
else
    TOKEN="$JWT_TOKEN"
    echo "⚠️  Using provided token"
fi

echo "Token: $TOKEN"
echo ""

# Function to test an endpoint
test_endpoint() {
    local method="$1"
    local url="$2"
    local data="$3"
    local description="$4"
    
    echo "Testing: $description"
    echo "Method: $method"
    echo "URL: $url"
    
    if [ "$method" = "GET" ]; then
        response=$(curl -s -w "%{http_code}" -X GET "$url" \
          -H "Authorization: Bearer $TOKEN" \
          -H "Content-Type: application/json")
    elif [ "$method" = "POST" ]; then
        response=$(curl -s -w "%{http_code}" -X POST "$url" \
          -H "Authorization: Bearer $TOKEN" \
          -H "Content-Type: application/json" \
          -d "$data")
    elif [ "$method" = "PUT" ]; then
        response=$(curl -s -w "%{http_code}" -X PUT "$url" \
          -H "Authorization: Bearer $TOKEN" \
          -H "Content-Type: application/json" \
          -d "$data")
    fi
    
    http_code="${response: -3}"
    body="${response%???}"
    
    echo "Status Code: $http_code"
    if [[ "$http_code" =~ ^2[0-9][0-9]$ ]]; then
        echo "✅ SUCCESS"
    else
        echo "❌ FAILED"
        echo "Response: $body"
    fi
    echo "---"
    echo ""
}

# Test all task endpoints

echo "🔍 TESTING ALL TASK ENDPOINTS"
echo "==============================="

# 1. GET /api/tasks - Get all tasks
test_endpoint "GET" "http://localhost:8080/api/tasks" "" "Get all tasks"

# 2. POST /api/tasks - Create a new task
task_data='{
  "posterId": 39,
  "title": "Fix Laptop Screen",
  "description": "My laptop screen is cracked and needs replacement. Dell Inspiron 15 3000 series.",
  "price": 150.0,
  "pricingType": "FIXED",
  "location": "Downtown",
  "cityArea": "Central District",
  "fullAddress": "123 Main St, Downtown",
  "deadline": "2025-08-15T18:00:00",
  "estimatedDurationHours": 3,
  "isUrgent": false,
  "specialRequirements": "Must bring replacement screen",
  "skillsRequired": ["Electronics repair", "laptop hardware"],
  "category": "REPAIR_MAINTENANCE"
}'
test_endpoint "POST" "http://localhost:8080/api/tasks" "$task_data" "Create a new task"

# 3. GET /api/tasks/{taskId} - Get specific task
test_endpoint "GET" "http://localhost:8080/api/tasks/1" "" "Get task by ID (task 1)"

# 4. PUT /api/tasks/{taskId} - Update task
update_data='{
  "posterId": 39,
  "title": "Fix Laptop Screen - UPDATED",
  "description": "Updated description: My laptop screen is cracked and needs replacement. Dell Inspiron 15 3000 series. Urgent repair needed.",
  "price": 160.0,
  "pricingType": "FIXED",
  "location": "Downtown Updated",
  "deadline": "2025-08-16T18:00:00",
  "category": "REPAIR_MAINTENANCE"
}'
test_endpoint "PUT" "http://localhost:8080/api/tasks/1" "$update_data" "Update task 1"

# 5. POST /api/tasks/{taskId}/apply - Apply for a task
apply_data='{
  "fulfillerId": 40,
  "message": "Hi! I have 5+ years of experience in laptop repairs. I specialize in screen replacements and have all the necessary tools. I can complete this task within 2-3 hours. Available this weekend.",
  "proposedPrice": 140.0
}'
test_endpoint "POST" "http://localhost:8080/api/tasks/1/apply" "$apply_data" "Apply for task 1"

# 6. GET /api/tasks/{taskId}/applications - Get task applications
test_endpoint "GET" "http://localhost:8080/api/tasks/1/applications?posterId=39" "" "Get applications for task 1"

# 7. PUT /api/tasks/{taskId}/applications/{applicationId}/accept - Accept application
test_endpoint "PUT" "http://localhost:8080/api/tasks/1/applications/1/accept?posterId=39" "" "Accept application 1 for task 1"

# 8. PUT /api/tasks/{taskId}/complete - Mark task as completed
test_endpoint "PUT" "http://localhost:8080/api/tasks/1/complete?fulfillerId=40" "" "Mark task 1 as completed"

# 9. PUT /api/tasks/{taskId}/confirm - Confirm task completion
test_endpoint "PUT" "http://localhost:8080/api/tasks/1/confirm?posterId=39" "" "Confirm task 1 completion"

# 10. GET /api/tasks/poster/{posterId} - Get tasks by poster
test_endpoint "GET" "http://localhost:8080/api/tasks/poster/39" "" "Get tasks by poster (user 39)"

# 11. GET /api/tasks/fulfiller/{fulfillerId} - Get tasks by fulfiller
test_endpoint "GET" "http://localhost:8080/api/tasks/fulfiller/40" "" "Get tasks by fulfiller (user 40)"

# 12. GET /api/tasks/search - Search tasks
test_endpoint "GET" "http://localhost:8080/api/tasks/search?keyword=laptop&category=REPAIR_MAINTENANCE&limit=10" "" "Search tasks (keyword: laptop, category: REPAIR_MAINTENANCE)"

# 13. GET /api/tasks/category/{category} - Get tasks by category
test_endpoint "GET" "http://localhost:8080/api/tasks/category/REPAIR_MAINTENANCE" "" "Get tasks by category (REPAIR_MAINTENANCE)"

# 14. GET /api/tasks/urgent - Get urgent tasks
test_endpoint "GET" "http://localhost:8080/api/tasks/urgent" "" "Get urgent tasks"

echo "🎯 TESTING SUMMARY"
echo "=================="
echo "All task endpoints have been tested!"
echo "Check the results above for any failures."
echo ""
echo "💡 Note: Some endpoints may fail if:"
echo "   - Task/Application IDs don't exist"
echo "   - User permissions are insufficient"
echo "   - Required data relationships are missing"
echo ""
echo "✅ Task Controller mapping has been fixed to /api/tasks"
