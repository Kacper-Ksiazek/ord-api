#!/bin/bash

# Export OpenAPI Specification Script
# This script exports the OpenAPI spec from a running ORD API instance

set -e

# Configuration
API_HOST="${API_HOST:-http://localhost:8080}"
OUTPUT_FILE="${OUTPUT_FILE:-openapi.json}"
SWAGGER_USERNAME="${SWAGGER_USERNAME:-admin}"
SWAGGER_PASSWORD="${SWAGGER_PASSWORD:-admin}"

echo "========================================="
echo "OpenAPI Specification Export Tool"
echo "========================================="
echo ""
echo "API Host: $API_HOST"
echo "Output File: $OUTPUT_FILE"
echo "Username: $SWAGGER_USERNAME"
echo ""

# Check if the API is running
echo "Checking if API is accessible..."
if ! curl -s -f "$API_HOST/api/v1/health-check" > /dev/null 2>&1; then
    echo "ERROR: API is not accessible at $API_HOST"
    echo "Please ensure the application is running before exporting the spec."
    echo ""
    echo "To start the application, run:"
    echo "  mvn spring-boot:run"
    echo ""
    exit 1
fi

echo "✓ API is accessible"
echo ""

# Export OpenAPI spec
echo "Exporting OpenAPI specification..."
if curl -s -f -u "$SWAGGER_USERNAME:$SWAGGER_PASSWORD" "$API_HOST/v3/api-docs" -o "$OUTPUT_FILE"; then
    echo "✓ OpenAPI spec exported successfully to: $OUTPUT_FILE"
    echo ""

    # Display file info
    FILE_SIZE=$(wc -c < "$OUTPUT_FILE")
    echo "File size: $FILE_SIZE bytes"
    echo ""

    # Show a preview
    echo "Preview (first 500 characters):"
    echo "---"
    head -c 500 "$OUTPUT_FILE"
    echo ""
    echo "---"
    echo ""

    echo "========================================="
    echo "Next Steps:"
    echo "========================================="
    echo ""
    echo "1. Generate TypeScript interfaces:"
    echo "   npx openapi-typescript $OUTPUT_FILE -o types.ts"
    echo ""
    echo "2. Generate TypeScript client (axios):"
    echo "   npx @openapitools/openapi-generator-cli generate \\"
    echo "     -i $OUTPUT_FILE \\"
    echo "     -g typescript-axios \\"
    echo "     -o ./generated-client"
    echo ""
    echo "3. View in Swagger Editor:"
    echo "   https://editor.swagger.io/"
    echo "   (Paste the contents of $OUTPUT_FILE)"
    echo ""
else
    echo "ERROR: Failed to export OpenAPI specification"
    echo "Please check that the API is running correctly."
    exit 1
fi