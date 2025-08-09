#!/bin/bash

# UrbanUp Flyway Database Migration Script
# This script applies database migrations using Flyway

echo "🚀 UrbanUp Database Migration Script"
echo "======================================"
echo ""

# Configuration
DB_URL="jdbc:postgresql://localhost:5432/urbanup"
DB_USER="root"
DB_PASSWORD="sanjanRoot"

echo "📍 Database: $DB_URL"
echo "👤 User: $DB_USER"
echo ""

# Check if PostgreSQL is running
echo "1️⃣  Checking PostgreSQL connection..."
if psql -h localhost -p 5432 -U $DB_USER -d urbanup -c "SELECT 1;" > /dev/null 2>&1; then
    echo "✅ PostgreSQL connection successful"
else
    echo "❌ PostgreSQL connection failed"
    echo "Please ensure PostgreSQL is running and credentials are correct"
    exit 1
fi
echo ""

# Display current migration status
echo "2️⃣  Current migration status:"
echo "mvn flyway:info"
mvn flyway:info
echo ""

# Run migration
echo "3️⃣  Applying database migrations..."
echo "mvn flyway:migrate"
mvn flyway:migrate

if [ $? -eq 0 ]; then
    echo ""
    echo "✅ Migration completed successfully!"
    echo ""
    
    # Display updated status
    echo "4️⃣  Updated migration status:"
    mvn flyway:info
    echo ""
    
    # Verify the changes
    echo "5️⃣  Verifying database changes..."
    psql -h localhost -p 5432 -U $DB_USER -d urbanup -c "
        SELECT 
            conname AS constraint_name,
            contype AS constraint_type,
            pg_get_constraintdef(oid) AS definition
        FROM pg_constraint 
        WHERE conrelid = 'chats'::regclass
        AND contype IN ('u', 'p')
        ORDER BY conname;
    "
    echo ""
    
    echo "🎉 Multi-Applicant Chat System Migration Complete!"
    echo ""
    echo "📋 Summary of changes:"
    echo "- ✅ Removed old unique constraint on task_id"
    echo "- ✅ Added composite unique constraint (task_id, poster_id, fulfiller_id)"
    echo "- ✅ Added performance indexes"
    echo "- ✅ Multiple chats per task now supported"
    echo ""
else
    echo ""
    echo "❌ Migration failed!"
    echo "Please check the error messages above and fix any issues"
    exit 1
fi

echo "📖 Migration files location: src/main/resources/db/migration/"
echo "🔧 To rollback: mvn flyway:undo (if needed)"
echo "ℹ️  For more commands: mvn flyway:help"
