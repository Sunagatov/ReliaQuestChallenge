#!/bin/bash

echo "🚀 Testing ReliaQuest Employee API"
echo "=================================="

BASE_URL="http://localhost:8111/api/v1/employee"

echo ""
echo "1️⃣ GET All Employees"
curl -s "$BASE_URL" | jq '.[0:3]' || curl -s "$BASE_URL"

echo ""
echo ""
echo "2️⃣ Search Employees by Name (searching for 'a')"
curl -s "$BASE_URL/search/a" | jq '.[0:2]' || curl -s "$BASE_URL/search/a"

echo ""
echo ""
echo "3️⃣ Get Highest Salary"
curl -s "$BASE_URL/highestSalary"

echo ""
echo ""
echo "4️⃣ Get Top 10 Highest Earning Names"
curl -s "$BASE_URL/topTenHighestEarningEmployeeNames" | jq '.[0:5]' || curl -s "$BASE_URL/topTenHighestEarningEmployeeNames"

echo ""
echo ""
echo "5️⃣ Create New Employee"
NEW_EMPLOYEE=$(curl -s -X POST "$BASE_URL" \
  -H "Content-Type: application/json" \
  -d '{"name":"Test User","salary":75000,"age":30,"title":"Developer"}')
echo "$NEW_EMPLOYEE" | jq '.' || echo "$NEW_EMPLOYEE"

# Extract employee ID for further tests
EMPLOYEE_ID=$(echo "$NEW_EMPLOYEE" | jq -r '.id' 2>/dev/null || echo "")

if [ -n "$EMPLOYEE_ID" ] && [ "$EMPLOYEE_ID" != "null" ]; then
    echo ""
    echo ""
    echo "6️⃣ Get Employee by ID: $EMPLOYEE_ID"
    curl -s "$BASE_URL/$EMPLOYEE_ID" | jq '.' || curl -s "$BASE_URL/$EMPLOYEE_ID"
    
    echo ""
    echo ""
    echo "7️⃣ Delete Employee by ID: $EMPLOYEE_ID"
    curl -s -X DELETE "$BASE_URL/$EMPLOYEE_ID"
else
    echo ""
    echo ""
    echo "6️⃣ Get Employee by ID (using first employee from list)"
    FIRST_ID=$(curl -s "$BASE_URL" | jq -r '.[0].id' 2>/dev/null)
    if [ -n "$FIRST_ID" ] && [ "$FIRST_ID" != "null" ]; then
        curl -s "$BASE_URL/$FIRST_ID" | jq '.' || curl -s "$BASE_URL/$FIRST_ID"
        
        echo ""
        echo ""
        echo "7️⃣ Delete Employee by ID: $FIRST_ID"
        curl -s -X DELETE "$BASE_URL/$FIRST_ID"
    fi
fi

echo ""
echo ""
echo "✅ API Testing Complete!"