#!/bin/bash

# Test script for profile picture upload functionality
# Usage: ./test-profile-upload.sh <JWT_TOKEN>

if [ $# -eq 0 ]; then
    echo "Usage: $0 <JWT_TOKEN>"
    echo "Example: $0 eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
    exit 1
fi

JWT_TOKEN=$1
API_URL="http://localhost:8080/api/profiles/me/picture"

echo "Testing Profile Picture Upload Functionality"
echo "============================================="

# Test 1: Upload a valid image file
echo "Test 1: Uploading a valid image file..."
curl -X POST \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -F "file=@test-image.jpg" \
  $API_URL \
  -w "\nHTTP Status: %{http_code}\n" \
  -s

echo -e "\n"

# Test 2: Try to upload without a file
echo "Test 2: Uploading without a file (should fail)..."
curl -X POST \
  -H "Authorization: Bearer $JWT_TOKEN" \
  $API_URL \
  -w "\nHTTP Status: %{http_code}\n" \
  -s

echo -e "\n"

# Test 3: Try to upload a non-image file
echo "Test 3: Uploading a text file (should fail)..."
echo "This is a test file" > test-file.txt
curl -X POST \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -F "file=@test-file.txt" \
  $API_URL \
  -w "\nHTTP Status: %{http_code}\n" \
  -s

# Cleanup
rm -f test-file.txt

echo -e "\n"

# Test 4: Try to upload without authentication
echo "Test 4: Uploading without authentication (should fail)..."
curl -X POST \
  -F "file=@test-image.jpg" \
  $API_URL \
  -w "\nHTTP Status: %{http_code}\n" \
  -s

echo -e "\n"
echo "Testing completed!"
echo "Expected results:"
echo "- Test 1: HTTP 200 with imageUrl in response"
echo "- Test 2: HTTP 400 with error message"
echo "- Test 3: HTTP 400 with error message"
echo "- Test 4: HTTP 401 unauthorized"
