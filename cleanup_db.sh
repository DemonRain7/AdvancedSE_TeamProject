#!/bin/bash

# Database connection details from application.properties
DB_HOST="advancedse-db1.cro62egwmoki.us-east-2.rds.amazonaws.com"
DB_PORT="5432"
DB_NAME="coupon_db"
DB_USER="postgres"
export PGPASSWORD="AdvancedSE_TeamProject"

echo "Cleaning up database..."

# Truncate tables and reset identity columns
psql -h "$DB_HOST" -p "$DB_PORT" -U "$DB_USER" -d "$DB_NAME" -c "TRUNCATE TABLE stores, items, coupons RESTART IDENTITY CASCADE;"

if [ $? -eq 0 ]; then
    echo "Database cleanup successful. All data removed and IDs reset."
else
    echo "Error cleaning database."
    exit 1
fi

