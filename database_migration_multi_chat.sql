-- UrbanUp Multi-Applicant Chat System - Database Migration Script
-- This script updates the database schema to support multiple chats per task

-- Step 1: Remove the old unique constraint on task_id
-- This constraint prevented multiple chats per task
ALTER TABLE chats DROP CONSTRAINT IF EXISTS uk2pqsc2hibgwbg7m1a0rp8ww3w;
ALTER TABLE chats DROP CONSTRAINT IF EXISTS chats_task_id_unique;
ALTER TABLE chats DROP CONSTRAINT IF EXISTS chats_task_id_key;

-- Step 2: Add new composite unique constraint
-- This ensures each poster-applicant pair per task has only one chat
ALTER TABLE chats ADD CONSTRAINT chats_task_poster_fulfiller_unique 
    UNIQUE(task_id, poster_id, fulfiller_id);

-- Step 3: Verify the changes
-- Check current constraints
SELECT 
    conname AS constraint_name,
    contype AS constraint_type,
    pg_get_constraintdef(oid) AS definition
FROM pg_constraint 
WHERE conrelid = 'chats'::regclass
AND contype IN ('u', 'p');

-- Step 4: Test data integrity
-- Ensure no duplicate poster-applicant pairs per task
SELECT 
    task_id, 
    poster_id, 
    fulfiller_id, 
    COUNT(*) as chat_count
FROM chats 
GROUP BY task_id, poster_id, fulfiller_id 
HAVING COUNT(*) > 1;

-- Should return 0 rows if migration is successful

-- Step 5: Check for multiple chats per task (this should now be allowed)
SELECT 
    task_id, 
    COUNT(*) as chat_count 
FROM chats 
GROUP BY task_id 
HAVING COUNT(*) > 1
ORDER BY chat_count DESC;

-- This query should show tasks with multiple chats after the migration

COMMIT;
