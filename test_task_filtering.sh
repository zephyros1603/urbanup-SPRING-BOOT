#!/bin/bash

# Test script to verify task filtering works correctly
# This script tests that IN_PROGRESS tasks are not shown in the general task list

BASE_URL="http://localhost:8080"

echo "=== Testing Task Filtering Fix ==="
echo ""

# Test 1: Get all tasks (should only show OPEN tasks)
echo "1. Testing GET /tasks (should only show OPEN tasks):"
curl -s -X GET "$BASE_URL/tasks" \
  -H "Content-Type: application/json" | jq '.'
echo ""
echo "---"

# Test 2: Get all tasks with all statuses (admin endpoint)
echo "2. Testing GET /tasks/all (should show all tasks regardless of status):"
curl -s -X GET "$BASE_URL/tasks/all" \
  -H "Content-Type: application/json" | jq '.'
echo ""
echo "---"

# Test 3: Search for specific task by ID to check its status
echo "3. Testing GET /tasks/2 (check status of task 2):"
curl -s -X GET "$BASE_URL/tasks/2" \
  -H "Content-Type: application/json" | jq '.'
echo ""
echo "---"

# Test 4: Search with status filter for IN_PROGRESS tasks
echo "4. Testing GET /tasks/search?status=IN_PROGRESS (should show IN_PROGRESS tasks):"
curl -s -X GET "$BASE_URL/tasks/search?status=IN_PROGRESS" \
  -H "Content-Type: application/json" | jq '.'
echo ""
echo "---"

# Test 5: Search with status filter for OPEN tasks
echo "5. Testing GET /tasks/search?status=OPEN (should show OPEN tasks):"
curl -s -X GET "$BASE_URL/tasks/search?status=OPEN" \
  -H "Content-Type: application/json" | jq '.'
echo ""
echo "---"

# Test 6: Search by category (should only show OPEN tasks in that category)
echo "6. Testing GET /tasks/category/REPAIR_MAINTENANCE (should only show OPEN tasks):"
curl -s -X GET "$BASE_URL/tasks/category/REPAIR_MAINTENANCE" \
  -H "Content-Type: application/json" | jq '.'
echo ""

echo "=== Test Complete ==="
echo ""
echo "Expected behavior:"
echo "- GET /tasks should only return tasks with status 'OPEN'"
echo "- GET /tasks/all should return all tasks regardless of status"
echo "- GET /tasks/search without status should default to 'OPEN' tasks"
echo "- GET /tasks/category/{category} should only return 'OPEN' tasks"
echo "- Only the poster and assigned fulfiller should see IN_PROGRESS tasks"
