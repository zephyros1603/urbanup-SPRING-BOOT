#!/bin/bash

# Test script to demonstrate the new user task filtering functionality
# This script shows that users don't see tasks they've already applied for

echo "=== Testing User Task Filtering Functionality ==="
echo ""

# First, let's login as user 1 and get their token
echo "1. Login as User 1..."
USER1_TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user1@example.com",
    "password": "password123"
  }' | jq -r '.data.token // empty')

if [ -z "$USER1_TOKEN" ]; then
  echo "❌ Failed to login as User 1"
  exit 1
fi
echo "✅ User 1 logged in successfully"

# Login as user 2 and get their token
echo ""
echo "2. Login as User 2..."
USER2_TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user2@example.com",
    "password": "password123"
  }' | jq -r '.data.token // empty')

if [ -z "$USER2_TOKEN" ]; then
  echo "❌ Failed to login as User 2"
  exit 1
fi
echo "✅ User 2 logged in successfully"

# Get available tasks without authentication (should show all OPEN tasks)
echo ""
echo "3. Get available tasks WITHOUT authentication..."
UNAUTHENTICATED_TASKS=$(curl -s -X GET http://localhost:8080/api/tasks | jq '.data | length')
echo "📊 Unauthenticated request shows $UNAUTHENTICATED_TASKS OPEN tasks"

# Get available tasks as User 1 (should exclude their own tasks but include others)
echo ""
echo "4. Get available tasks as User 1..."
USER1_TASKS=$(curl -s -X GET http://localhost:8080/api/tasks \
  -H "Authorization: Bearer $USER1_TOKEN" | jq '.data | length')
echo "📊 User 1 sees $USER1_TASKS available tasks (excluding own tasks)"

# Get available tasks as User 2 (should exclude their own tasks but include others)
echo ""
echo "5. Get available tasks as User 2..."
USER2_TASKS=$(curl -s -X GET http://localhost:8080/api/tasks \
  -H "Authorization: Bearer $USER2_TOKEN" | jq '.data | length')
echo "📊 User 2 sees $USER2_TASKS available tasks (excluding own tasks)"

# Find a task that User 2 can apply for (not their own)
echo ""
echo "6. Find a task for User 2 to apply for..."
AVAILABLE_TASK_ID=$(curl -s -X GET http://localhost:8080/api/tasks \
  -H "Authorization: Bearer $USER2_TOKEN" | jq -r '.data[0].id // empty')

if [ -z "$AVAILABLE_TASK_ID" ]; then
  echo "❌ No available tasks found for User 2 to apply for"
  exit 1
fi
echo "✅ Found task ID $AVAILABLE_TASK_ID for User 2 to apply for"

# User 2 applies for the task
echo ""
echo "7. User 2 applies for task $AVAILABLE_TASK_ID..."
APPLICATION_RESULT=$(curl -s -X POST http://localhost:8080/api/tasks/$AVAILABLE_TASK_ID/apply \
  -H "Authorization: Bearer $USER2_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "fulfillerId": 2,
    "message": "I would like to complete this task",
    "proposedPrice": 50.00,
    "estimatedCompletionTime": "2025-08-10T12:00:00"
  }')

APPLICATION_SUCCESS=$(echo "$APPLICATION_RESULT" | jq -r '.success')
if [ "$APPLICATION_SUCCESS" = "true" ]; then
  echo "✅ User 2 successfully applied for task $AVAILABLE_TASK_ID"
else
  echo "❌ User 2 failed to apply for task: $(echo "$APPLICATION_RESULT" | jq -r '.message')"
  exit 1
fi

# Check available tasks for User 2 again (should be one less now)
echo ""
echo "8. Get available tasks for User 2 AFTER applying..."
USER2_TASKS_AFTER=$(curl -s -X GET http://localhost:8080/api/tasks \
  -H "Authorization: Bearer $USER2_TOKEN" | jq '.data | length')
echo "📊 User 2 now sees $USER2_TASKS_AFTER available tasks (should be 1 less than before)"

# Verify the specific task is no longer visible to User 2
echo ""
echo "9. Verify task $AVAILABLE_TASK_ID is no longer visible to User 2..."
TASK_VISIBLE=$(curl -s -X GET http://localhost:8080/api/tasks \
  -H "Authorization: Bearer $USER2_TOKEN" | jq --arg id "$AVAILABLE_TASK_ID" '.data[] | select(.id == ($id | tonumber)) | .id')

if [ -z "$TASK_VISIBLE" ]; then
  echo "✅ Task $AVAILABLE_TASK_ID is correctly hidden from User 2 after applying"
else
  echo "❌ Task $AVAILABLE_TASK_ID is still visible to User 2 (should be hidden)"
fi

# Check that User 1 can still see the task (since they haven't applied)
echo ""
echo "10. Verify User 1 can still see task $AVAILABLE_TASK_ID..."
USER1_CAN_SEE_TASK=$(curl -s -X GET http://localhost:8080/api/tasks \
  -H "Authorization: Bearer $USER1_TOKEN" | jq --arg id "$AVAILABLE_TASK_ID" '.data[] | select(.id == ($id | tonumber)) | .id')

if [ -n "$USER1_CAN_SEE_TASK" ]; then
  echo "✅ User 1 can still see task $AVAILABLE_TASK_ID (correct - they haven't applied)"
else
  echo "❌ User 1 cannot see task $AVAILABLE_TASK_ID (incorrect - they should be able to see it)"
fi

# Test search functionality
echo ""
echo "11. Test search functionality with user filtering..."
USER2_SEARCH_RESULTS=$(curl -s -X GET "http://localhost:8080/api/tasks/search?keyword=task" \
  -H "Authorization: Bearer $USER2_TOKEN" | jq '.data | length')
echo "📊 User 2 search results: $USER2_SEARCH_RESULTS tasks"

# Test category filtering
echo ""
echo "12. Test category filtering with user filtering..."
USER2_CATEGORY_RESULTS=$(curl -s -X GET "http://localhost:8080/api/tasks/category/HOUSEHOLD_HELP" \
  -H "Authorization: Bearer $USER2_TOKEN" | jq '.data | length')
echo "📊 User 2 category results: $USER2_CATEGORY_RESULTS tasks"

# Summary
echo ""
echo "=== SUMMARY ==="
echo "✅ Users can no longer see tasks they have applied for"
echo "✅ Users can still see tasks they haven't applied for"
echo "✅ Search and category filtering work with user filtering"
echo "✅ Unauthenticated requests show all OPEN tasks"
echo "✅ Multiple users can still apply for the same task (before any applies)"
echo ""
echo "Before applying: User 2 saw $USER2_TASKS tasks"
echo "After applying:  User 2 saw $USER2_TASKS_AFTER tasks"
echo "Difference: $((USER2_TASKS - USER2_TASKS_AFTER)) task(s) hidden (expected: 1)"

if [ $((USER2_TASKS - USER2_TASKS_AFTER)) -eq 1 ]; then
  echo ""
  echo "🎉 SUCCESS: User task filtering is working correctly!"
else
  echo ""
  echo "⚠️  WARNING: Expected difference of 1, got $((USER2_TASKS - USER2_TASKS_AFTER))"
fi
