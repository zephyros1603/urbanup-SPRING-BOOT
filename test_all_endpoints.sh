#!/bin/bash

# filepath: /Users/sanjanathyady/Desktop/urbanupMvp/urbanup/test_fixed_endpoints.sh

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

BASE_URL="http://localhost:8080/api"
JWT_TOKEN=""
USER_ID=""

# Function to test endpoint with better error handling
test_endpoint() {
    local method=$1
    local endpoint=$2
    local data=$3
    local expected_status=$4
    local auth_required=$5
    
    echo -e "${BLUE}Testing:${NC} $method $endpoint"
    
    # Prepare curl command
    local curl_cmd="curl -s -w '|%{http_code}' -H 'Content-Type: application/json'"
    
    if [ "$auth_required" = "true" ] && [ -n "$JWT_TOKEN" ]; then
        curl_cmd="$curl_cmd -H 'Authorization: Bearer $JWT_TOKEN'"
    fi
    
    if [ -n "$data" ]; then
        curl_cmd="$curl_cmd -d '$data'"
    fi
    
    curl_cmd="$curl_cmd -X $method $BASE_URL$endpoint"
    
    # Execute curl command and capture response and status code
    local response_with_code=$(eval $curl_cmd)
    local response=$(echo "$response_with_code" | sed 's/|[0-9]*$//')
    local status_code=$(echo "$response_with_code" | grep -o '[0-9]*$')
    
    # Check status
    if [ "$status_code" = "$expected_status" ]; then
        echo -e "${GREEN}✅ PASSED${NC} - Status: $status_code"
        
        # Handle overly verbose responses (likely circular references)
        local response_length=${#response}
        if [ "$response_length" -gt 2000 ]; then
            echo -e "${YELLOW}⚠️  Response too long (${response_length} chars) - likely circular reference${NC}"
            echo -e "${BLUE}Response (truncated):${NC} ${response:0:500}..."
            echo -e "${YELLOW}💡 This indicates a JSON serialization issue in the backend${NC}"
        else
            echo -e "${BLUE}Response:${NC} $response"
        fi
        
        # Extract important data for subsequent tests
        if [[ "$endpoint" == "/auth/register" || "$endpoint" == "/auth/login" ]]; then
            JWT_TOKEN=$(echo "$response" | jq -r '.data.accessToken // empty' 2>/dev/null)
            USER_ID=$(echo "$response" | jq -r '.data.user.id // empty' 2>/dev/null)
            if [ -n "$JWT_TOKEN" ]; then
                echo -e "${GREEN}🔑 JWT Token extracted:${NC} ${JWT_TOKEN:0:50}..."
                echo -e "${GREEN}👤 User ID extracted:${NC} $USER_ID"
            fi
        fi
    else
        echo -e "${RED}❌ FAILED${NC} - Expected: $expected_status, Got: $status_code"
        echo -e "${RED}Response:${NC} $response"
        
        # Additional debugging for 500 errors
        if [ "$status_code" = "500" ]; then
            echo -e "${YELLOW}🔍 Debugging 500 error...${NC}"
            echo "Check application logs for detailed error information"
        fi
    fi
    echo "---"
}

# Function to check application health
check_health() {
    echo -e "${BLUE}🏥 Checking Application Health...${NC}"
    # Try different health endpoints
    echo "Testing health endpoint..."
    local health_response=$(curl -s "$BASE_URL/actuator/health" 2>/dev/null || echo "Health endpoint failed")
    echo "Health Response: $health_response"
    
    # Test if server is running on correct port
    echo "Testing base connectivity..."
    local base_response=$(curl -s -o /dev/null -w "%{http_code}" "http://localhost:8080/" 2>/dev/null || echo "Connection failed")
    echo "Base server response code: $base_response"
    
    # Test if application context is correct
    echo "Testing application context..."
    local context_response=$(curl -s -o /dev/null -w "%{http_code}" "http://localhost:8080/api/" 2>/dev/null || echo "Context failed")
    echo "Context response code: $context_response"
    echo "---"
}

# Function to diagnose authentication issues
diagnose_auth() {
    echo -e "${YELLOW}🔍 Diagnosing Authentication Issues...${NC}"
    
    # Test direct registration with verbose output
    echo "Testing registration with verbose output..."
    local reg_test=$(curl -v -X POST "$BASE_URL/auth/register" \
        -H "Content-Type: application/json" \
        -d '{
            "firstName": "Debug",
            "lastName": "User",
            "email": "debug@example.com",
            "password": "password123",
            "phoneNumber": "+919999999999"
        }' 2>&1)
    
    echo "Registration curl output:"
    echo "$reg_test"
    echo "---"
}

# Main testing sequence
echo -e "${BLUE}🧪 UrbanUp API Testing Suite${NC}"
echo "================================="

# Check health first
check_health

# Run diagnosis
diagnose_auth

# Test Authentication Endpoints
echo -e "${YELLOW}📋 Testing Authentication Endpoints${NC}"

# 1. Register with a unique email each time
TIMESTAMP=$(date +%s)
UNIQUE_EMAIL="testuser$TIMESTAMP@example.com"

test_endpoint "POST" "/auth/register" "{
  \"firstName\": \"Test\",
  \"lastName\": \"User\",
  \"email\": \"$UNIQUE_EMAIL\",
  \"password\": \"SecurePassword123!\",
  \"phoneNumber\": \"+91987654$TIMESTAMP\"
}" "201" "false"

# 2. Login with the same credentials
test_endpoint "POST" "/auth/login" "{
  \"email\": \"$UNIQUE_EMAIL\",
  \"password\": \"SecurePassword123!\"
}" "200" "false"

# Only continue if we have a JWT token
if [ -z "$JWT_TOKEN" ]; then
    echo -e "${RED}🚨 No JWT token available. Cannot test protected endpoints.${NC}"
    echo -e "${YELLOW}Please fix authentication issues first.${NC}"
    exit 1
fi

# Test User Management Endpoints (now with valid token)
echo -e "${YELLOW}👤 Testing User Management Endpoints${NC}"

test_endpoint "GET" "/users/$USER_ID" "" "200" "true"

test_endpoint "PUT" "/users/$USER_ID" "{
  \"firstName\": \"Updated\",
  \"lastName\": \"User\",
  \"phoneNumber\": \"+9198765$TIMESTAMP\"
}" "200" "true"

test_endpoint "GET" "/users/search?searchTerm=test" "" "200" "true"

test_endpoint "GET" "/users/top-posters?minRating=4.0" "" "200" "true"

# Test Task Management Endpoints
echo -e "${YELLOW}📋 Testing Task Management Endpoints${NC}"

test_endpoint "POST" "/tasks" "{
  \"posterId\": $USER_ID,
  \"title\": \"Test Task - Grocery Shopping\",
  \"description\": \"Need someone to buy groceries\",
  \"category\": \"PERSONAL_ERRANDS\",
  \"price\": 500.00,
  \"pricingType\": \"FIXED\",
  \"location\": \"Bangalore\",
  \"address\": \"123 Test Street\",
  \"latitude\": 12.9716,
  \"longitude\": 77.5946,
  \"deadline\": \"2024-12-31T18:00:00Z\",
  \"isUrgent\": false,
  \"requirements\": \"Basic requirements\",
  \"estimatedDuration\": 2,
  \"skillsRequired\": [\"Shopping\"]
}" "201" "true"

test_endpoint "GET" "/tasks" "" "200" "true"

test_endpoint "GET" "/tasks/search?keyword=grocery&category=PERSONAL_ERRANDS" "" "200" "true"

echo -e "${GREEN}🎉 Testing Complete!${NC}"