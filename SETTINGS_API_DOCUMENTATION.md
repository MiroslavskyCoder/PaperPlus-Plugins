# Settings API Documentation

## Overview

The Settings API provides endpoints to manage Auth Player, SQL Database, and Redis configuration for the WebX Dashboard. All configuration changes are persisted to `settings.json` file.

## Base URL

```
http://localhost:9092/api
```

## Endpoints

### 1. GET /api/settings
Retrieve all current settings (Auth Player, SQL Config, Redis Config).

**Request:**
```bash
GET http://localhost:9092/api/settings
```

**Response (200 OK):**
```json
{
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
}
```

### 2. PUT /api/settings
Update all settings. Send the complete settings object with any modifications.

**Request:**
```bash
PUT http://localhost:9092/api/settings
Content-Type: application/json

{
  "authPlayer": {
    "isAuthPlayerEnabled": true,
    "inputMask": "XXX-000-YYY"
  },
  "sqlConfig": {
    "host": "db.example.com",
    "port": 5432,
    "database": "webx_dashboard",
    "username": "admin",
    "password": "secret123",
    "ssl": true
  },
  "redisConfig": {
    "host": "redis.example.com",
    "port": 6379,
    "password": "redis_secret",
    "db": 0
  }
}
```

**Response (200 OK):**
```json
{
  "authPlayer": {
    "isAuthPlayerEnabled": true,
    "inputMask": "XXX-000-YYY"
  },
  "sqlConfig": {
    "host": "db.example.com",
    "port": 5432,
    "database": "webx_dashboard",
    "username": "admin",
    "password": "secret123",
    "ssl": true
  },
  "redisConfig": {
    "host": "redis.example.com",
    "port": 6379,
    "password": "redis_secret",
    "db": 0
  }
}
```

**Error Response (400 Bad Request):**
```json
{
  "error": "Invalid settings format"
}
```

### 3. POST /api/settings/test-connection
Test the connection to SQL database or Redis server.

**Request for SQL:**
```bash
POST http://localhost:9092/api/settings/test-connection
Content-Type: application/json

{
  "type": "sql",
  "config": {
    "authPlayer": {...},
    "sqlConfig": {
      "host": "localhost",
      "port": 5432,
      "database": "webx_dashboard",
      "username": "postgres",
      "password": "password",
      "ssl": false
    },
    "redisConfig": {...}
  }
}
```

**Request for Redis:**
```bash
POST http://localhost:9092/api/settings/test-connection
Content-Type: application/json

{
  "type": "redis",
  "config": {
    "authPlayer": {...},
    "sqlConfig": {...},
    "redisConfig": {
      "host": "localhost",
      "port": 6379,
      "password": "",
      "db": 0
    }
  }
}
```

**Response (200 OK - Success):**
```json
{
  "success": true
}
```

**Response (200 OK - Failure):**
```json
{
  "success": false
}
```

## Auth Player Configuration

The Auth Player settings control player authentication features:

- **isAuthPlayerEnabled** (boolean): Enable/disable player authentication
- **inputMask** (string): Input format mask for authentication (e.g., "NN-AAA-999", "XXX-000-YYY")

## SQL Configuration

PostgreSQL database connection settings:

- **host** (string): Database server hostname or IP
- **port** (number): Database server port (default: 5432)
- **database** (string): Database name
- **username** (string): Database user
- **password** (string): Database password
- **ssl** (boolean): Enable SSL/TLS connection

## Redis Configuration

Redis cache server connection settings:

- **host** (string): Redis server hostname or IP
- **port** (number): Redis server port (default: 6379)
- **password** (string): Redis password (empty if no authentication)
- **db** (number): Redis database number (0-15)

## Frontend Integration

The React component (`settings-tab.tsx`) provides:

1. **Auto-loading**: Settings are fetched from API on component mount
2. **Auto-saving**: Changes are saved to API automatically
3. **Connection Testing**: Test buttons for SQL and Redis connections
4. **Real-time Status**: Connection status indicators for SQL and Redis
5. **Error Handling**: Display error messages if API calls fail

### UI Features

- Auth Player toggle and input mask configuration
- SQL connection parameters form
- Redis connection parameters form
- Test connection buttons with loading states
- Connection status badges
- Auto-refresh on settings update

## Error Handling

### Common Errors

**401 Unauthorized**
- The request requires authentication
- Solution: Ensure you have proper credentials

**400 Bad Request**
- Invalid JSON format or missing required fields
- Solution: Check request body format and field names

**500 Internal Server Error**
- Server error occurred
- Solution: Check server logs for details

## Configuration File Location

Settings are persisted in:
```
plugins/PolyglotPlugin/settings.json
```

## Dependencies

### Frontend
- React
- TypeScript
- Next.js
- shadcn/ui components

### Backend
- Javalin (Web Framework)
- Jackson (JSON serialization)
- PostgreSQL JDBC Driver
- Jedis (Redis client)
- Bukkit/Spigot API

## Example Usage

### cURL Examples

**Get all settings:**
```bash
curl http://localhost:9092/api/settings
```

**Update Auth Player:**
```bash
curl -X PUT http://localhost:9092/api/settings \
  -H "Content-Type: application/json" \
  -d '{
    "authPlayer": {"isAuthPlayerEnabled": true, "inputMask": "XXX-000"},
    "sqlConfig": {...},
    "redisConfig": {...}
  }'
```

**Test SQL connection:**
```bash
curl -X POST http://localhost:9092/api/settings/test-connection \
  -H "Content-Type: application/json" \
  -d '{
    "type": "sql",
    "config": {...}
  }'
```

## Notes

- Settings are saved immediately upon PUT request
- Connection tests do not modify any configuration
- Passwords are stored in plaintext in settings.json - secure this file appropriately
- Server restart is not required for settings to take effect
