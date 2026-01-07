#!/bin/bash
# Settings API Usage Examples
# This script demonstrates how to use the Settings API with curl

API_BASE="http://localhost:9092/api"

# Colors for output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}=== Settings API Usage Examples ===${NC}\n"

# Example 1: Get all settings
echo -e "${GREEN}1. GET all settings${NC}"
echo "Command:"
echo "curl ${API_BASE}/settings"
echo ""
echo "Expected Response:"
echo '{
  "authPlayer": {
    "isAuthPlayerEnabled": false,
    "inputMask": "NN-AAA-999"
  },
  "sqlConfig": {
    "host": "localhost",
    "port": 5432,
    "database": "webx_dashboard",
    "username": "postgres",
    "password": "",
    "ssl": false
  },
  "redisConfig": {
    "host": "localhost",
    "port": 6379,
    "password": "",
    "db": 0
  }
}'
echo ""
echo ""

# Example 2: Update Auth Player settings
echo -e "${GREEN}2. UPDATE Auth Player settings${NC}"
echo "Command:"
echo 'curl -X PUT '"${API_BASE}"'/settings \
  -H "Content-Type: application/json" \
  -d '"'"'{
    "authPlayer": {
      "isAuthPlayerEnabled": true,
      "inputMask": "XXX-000-YYY"
    },
    "sqlConfig": {
      "host": "localhost",
      "port": 5432,
      "database": "webx_dashboard",
      "username": "postgres",
      "password": "",
      "ssl": false
    },
    "redisConfig": {
      "host": "localhost",
      "port": 6379,
      "password": "",
      "db": 0
    }
  }'"'"''
echo ""
echo ""

# Example 3: Update SQL configuration
echo -e "${GREEN}3. UPDATE SQL configuration${NC}"
echo "Command:"
echo 'curl -X PUT '"${API_BASE}"'/settings \
  -H "Content-Type: application/json" \
  -d '"'"'{
    "authPlayer": {
      "isAuthPlayerEnabled": false,
      "inputMask": "NN-AAA-999"
    },
    "sqlConfig": {
      "host": "db.example.com",
      "port": 5432,
      "database": "webx_production",
      "username": "admin",
      "password": "secure_password_123",
      "ssl": true
    },
    "redisConfig": {
      "host": "localhost",
      "port": 6379,
      "password": "",
      "db": 0
    }
  }'"'"''
echo ""
echo ""

# Example 4: Update Redis configuration
echo -e "${GREEN}4. UPDATE Redis configuration${NC}"
echo "Command:"
echo 'curl -X PUT '"${API_BASE}"'/settings \
  -H "Content-Type: application/json" \
  -d '"'"'{
    "authPlayer": {
      "isAuthPlayerEnabled": false,
      "inputMask": "NN-AAA-999"
    },
    "sqlConfig": {
      "host": "localhost",
      "port": 5432,
      "database": "webx_dashboard",
      "username": "postgres",
      "password": "",
      "ssl": false
    },
    "redisConfig": {
      "host": "redis.example.com",
      "port": 6379,
      "password": "redis_password",
      "db": 1
    }
  }'"'"''
echo ""
echo ""

# Example 5: Test SQL connection
echo -e "${GREEN}5. TEST SQL connection${NC}"
echo "Command:"
echo 'curl -X POST '"${API_BASE}"'/settings/test-connection \
  -H "Content-Type: application/json" \
  -d '"'"'{
    "type": "sql",
    "config": {
      "authPlayer": {
        "isAuthPlayerEnabled": false,
        "inputMask": "NN-AAA-999"
      },
      "sqlConfig": {
        "host": "localhost",
        "port": 5432,
        "database": "webx_dashboard",
        "username": "postgres",
        "password": "your_password",
        "ssl": false
      },
      "redisConfig": {
        "host": "localhost",
        "port": 6379,
        "password": "",
        "db": 0
      }
    }
  }'"'"''
echo ""
echo "Expected Response (Success):"
echo '{"success": true}'
echo ""
echo "Expected Response (Failure):"
echo '{"success": false}'
echo ""
echo ""

# Example 6: Test Redis connection
echo -e "${GREEN}6. TEST Redis connection${NC}"
echo "Command:"
echo 'curl -X POST '"${API_BASE}"'/settings/test-connection \
  -H "Content-Type: application/json" \
  -d '"'"'{
    "type": "redis",
    "config": {
      "authPlayer": {
        "isAuthPlayerEnabled": false,
        "inputMask": "NN-AAA-999"
      },
      "sqlConfig": {
        "host": "localhost",
        "port": 5432,
        "database": "webx_dashboard",
        "username": "postgres",
        "password": "",
        "ssl": false
      },
      "redisConfig": {
        "host": "localhost",
        "port": 6379,
        "password": "redis_password",
        "db": 0
      }
    }
  }'"'"''
echo ""
echo "Expected Response (Success):"
echo '{"success": true}'
echo ""
echo "Expected Response (Failure):"
echo '{"success": false}'
echo ""
echo ""

# Example 7: Using jq to pretty-print responses
echo -e "${GREEN}7. GET settings with pretty-print${NC}"
echo "Command:"
echo "curl ${API_BASE}/settings | jq ."
echo ""
echo ""

# Example 8: Save response to file
echo -e "${GREEN}8. Save settings to file${NC}"
echo "Command:"
echo "curl ${API_BASE}/settings > current_settings.json"
echo ""
echo ""

# Example 9: Update from file
echo -e "${GREEN}9. Update settings from file${NC}"
echo "Command (requires settings.json file):"
echo 'curl -X PUT '"${API_BASE}"'/settings \
  -H "Content-Type: application/json" \
  -d @settings.json'
echo ""
echo ""

echo -e "${BLUE}=== Notes ===${NC}"
echo "- Replace 'localhost' with your actual server address"
echo "- Ensure the server is running on port 9092"
echo "- Use -v flag with curl for verbose output and debugging"
echo "- Use jq for JSON pretty-printing (install with: apt-get install jq)"
echo "- Passwords should be properly escaped or quoted in JSON"
echo ""
