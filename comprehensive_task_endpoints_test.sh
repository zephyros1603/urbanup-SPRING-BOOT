#!/bin/bash

# Comprehensive Test Script for All /tasks Endpoints
echo "=================================="
echo "   TASK ENDPOINTS CROSS-CHECK"
echo "=================================="

# Base URL
BASE_URL="http://localhost:8080/api"

# Color codes for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print test results
print_result() {
    if [ $1 -eq 0 ]; then
        echo -e "${GREEN}✓ PASS${NC}: $2"
    else
        echo -e "${RED}✗ FAIL${NC}: $2"
    fi
}

# Function to test endpoint
test_endpoint() {
    local method=$1
    local endpoint=$2
    local description=$3
    local data=$4
    local headers=$5
    
    echo -e "\n${BLUE}Testing:${NC} $method $endpoint"
    echo -e "${YELLOW}Description:${NC} $description"
    
    if [ -n "$data" ] && [ -n "$headers" ]; then
        response=$(curl -s -w "\n%{http_code}" -X $method "$BASE_URL$endpoint" -H "$headers" -H "Content-Type: application/json" -d "$data")
    elif [ -n "$headers" ]; then
        response=$(curl -s -w "\n%{http_code}" -X $method "$BASE_URL$endpoint" -H "$headers")
    elif [ -n "$data" ]; then
        response=$(curl -s -w "\n%{http_code}" -X $method "$BASE_URL$endpoint" -H "Content-Type: application/json" -d "$data")
    else
        response=$(curl -s -w "\n%{http_code}" -X $method "$BASE_URL$endpoint")
    fi
    
    http_code=$(echo "$response" | tail -n1)
    body=$(echo "$response" | sed '$d')
    
    echo "HTTP Status: $http_code"
    echo "Response: $body" | jq '.' 2>/dev/null || echo "Response: $body"
    
    # Check if status code indicates success (2xx or 3xx)
    if [[ $http_code =~ ^[23][0-9][0-9]$ ]]; then
        print_result 0 "$description"
        return 0
    else
        print_result 1 "$description (HTTP $http_code)"
        return 1
    fi
}

echo -e "\n${BLUE}Step 1: Getting JWT Token${NC}"
# Get JWT token
TOKEN_RESPONSE=$(curl -s -X POST "$BASE_URL/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "testuser1@urbanup.com",
    "password": "password123"
  }')

TOKEN=$(echo $TOKEN_RESPONSE | grep -o '"accessToken":"[^"]*"' | cut -d'"' -f4)

if [ -z "$TOKEN" ]; then
    echo -e "${RED}Failed to get JWT token${NC}"
    echo "Response: $TOKEN_RESPONSE"
    exit 1
fi

echo -e "${GREEN}JWT Token obtained successfully${NC}"
HEADERS="Authorization: Bearer $TOKEN"

# Initialize counters
total_tests=0
passed_tests=0

echo -e "\n${BLUE}Step 2: Testing All Task Endpoints${NC}"

# 1. GET /tasks - Get all tasks
total_tests=$((total_tests + 1))
test_endpoint "GET" "/tasks" "Get all available tasks" "" "$HEADERS"
if [ $? -eq 0 ]; then passed_tests=$((passed_tests + 1)); fi

# 2. POST /tasks - Create a new task
total_tests=$((total_tests + 1))
CREATE_DATA='{
  "posterId": 39,
  "title": "Test Task for API Verification",
  "description": "This is a test task to verify API functionality",
  "price": 100.00,
  "pricingType": "FIXED",
  "location": "Test Location",
  "category": "OTHER",
  "deadline": "2025-08-15T12:00:00",
  "estimatedDurationHours": 2,
  "isUrgent": false
}'
test_endpoint "POST" "/tasks" "Create a new task" "$CREATE_DATA" "$HEADERS"
if [ $? -eq 0 ]; then passed_tests=$((passed_tests + 1)); fi

# 3. GET /tasks/{taskId} - Get specific task
total_tests=$((total_tests + 1))
test_endpoint "GET" "/tasks/1" "Get task by ID" "" "$HEADERS"
if [ $? -eq 0 ]; then passed_tests=$((passed_tests + 1)); fi

# 4. PUT /tasks/{taskId} - Update task
total_tests=$((total_tests + 1))
UPDATE_DATA='{
  "posterId": 39,
  "title": "Updated Test Task",
  "description": "This is an updated test task",
  "price": 150.00,
  "location": "Updated Location",
  "category": "OTHER"
}'
test_endpoint "PUT" "/tasks/1" "Update task" "$UPDATE_DATA" "$HEADERS"
if [ $? -eq 0 ]; then passed_tests=$((passed_tests + 1)); fi

# 5. POST /tasks/{taskId}/apply - Apply for task
total_tests=$((total_tests + 1))
APPLY_DATA='{
  "fulfillerId": 39,
  "proposedRate": 120.00,
  "message": "I would like to apply for this task",
  "estimatedCompletionTime": "2025-08-12T15:30:00"
}'
test_endpoint "POST" "/tasks/2/apply" "Apply for a task" "$APPLY_DATA" "$HEADERS"
if [ $? -eq 0 ]; then passed_tests=$((passed_tests + 1)); fi

# 6. GET /tasks/{taskId}/applications - Get task applications
total_tests=$((total_tests + 1))
test_endpoint "GET" "/tasks/2/applications?posterId=1" "Get task applications" "" "$HEADERS"
if [ $? -eq 0 ]; then passed_tests=$((passed_tests + 1)); fi

# 7. PUT /tasks/{taskId}/applications/{applicationId}/accept - Accept application
total_tests=$((total_tests + 1))
test_endpoint "PUT" "/tasks/2/applications/1/accept?posterId=1" "Accept application" "" "$HEADERS"
if [ $? -eq 0 ]; then passed_tests=$((passed_tests + 1)); fi

# 8. PUT /tasks/{taskId}/complete - Mark task as completed
total_tests=$((total_tests + 1))
test_endpoint "PUT" "/tasks/2/complete?fulfillerId=39" "Mark task as completed" "" "$HEADERS"
if [ $? -eq 0 ]; then passed_tests=$((passed_tests + 1)); fi

# 9. PUT /tasks/{taskId}/confirm - Confirm task completion
total_tests=$((total_tests + 1))
test_endpoint "PUT" "/tasks/2/confirm?posterId=1" "Confirm task completion" "" "$HEADERS"
if [ $? -eq 0 ]; then passed_tests=$((passed_tests + 1)); fi

# 10. GET /tasks/poster/{posterId} - Get tasks by poster
total_tests=$((total_tests + 1))
test_endpoint "GET" "/tasks/poster/39" "Get tasks by poster" "" "$HEADERS"
if [ $? -eq 0 ]; then passed_tests=$((passed_tests + 1)); fi

# 11. GET /tasks/fulfiller/{fulfillerId} - Get tasks by fulfiller
total_tests=$((total_tests + 1))
test_endpoint "GET" "/tasks/fulfiller/39" "Get tasks by fulfiller" "" "$HEADERS"
if [ $? -eq 0 ]; then passed_tests=$((passed_tests + 1)); fi

# 12. GET /tasks/search - Search tasks
total_tests=$((total_tests + 1))
test_endpoint "GET" "/tasks/search?keyword=test&limit=10" "Search tasks" "" "$HEADERS"
if [ $? -eq 0 ]; then passed_tests=$((passed_tests + 1)); fi

# 13. GET /tasks/category/{category} - Get tasks by category
total_tests=$((total_tests + 1))
test_endpoint "GET" "/tasks/category/OTHER" "Get tasks by category" "" "$HEADERS"
if [ $? -eq 0 ]; then passed_tests=$((passed_tests + 1)); fi

# 14. GET /tasks/urgent - Get urgent tasks
total_tests=$((total_tests + 1))
test_endpoint "GET" "/tasks/urgent" "Get urgent tasks" "" "$HEADERS"
if [ $? -eq 0 ]; then passed_tests=$((passed_tests + 1)); fi

# Print final summary
echo -e "\n=================================="
echo -e "         TEST SUMMARY"
echo -e "=================================="
echo -e "Total Tests: $total_tests"
echo -e "${GREEN}Passed: $passed_tests${NC}"
echo -e "${RED}Failed: $((total_tests - passed_tests))${NC}"

if [ $passed_tests -eq $total_tests ]; then
    echo -e "\n${GREEN}🎉 ALL TESTS PASSED! All task endpoints are working correctly.${NC}"
else
    echo -e "\n${YELLOW}⚠️  Some tests failed. Please check the failing endpoints.${NC}"
fi

echo -e "\n${BLUE}Cross-check completed!${NC}"
