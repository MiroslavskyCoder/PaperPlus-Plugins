# Settings Management - Quick Start Guide

## 📋 What Was Added

A complete settings management system with UI for:
- ✅ **Auth Player** - Player authentication configuration
- ✅ **SQL Database** - PostgreSQL connection settings  
- ✅ **Redis Cache** - Redis server configuration

All with real-time persistence and connection testing.

## 🚀 Getting Started

### 1. Frontend Setup

The React component is ready to use at:
```
frontend-panel/src/components/dashboard/settings-tab.tsx
```

#### Configuration
If your API runs on a different port, update:
```typescript
const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:9092/api';
```

Add to your `.env.local`:
```
NEXT_PUBLIC_API_URL=http://your-server:9092/api
```

### 2. Backend Setup

Three new Java files were created:

1. **SettingsConfig.java** - Data models for settings
2. **SettingsService.java** - Service for persistence and testing
3. **RouterProvider.java** - Updated with new API endpoints

#### Dependencies Required

Add to your `build.gradle.kts`:

```kotlin
dependencies {
    // PostgreSQL driver for SQL connection testing
    implementation("org.postgresql:postgresql:42.6.0")
    
    // Jedis for Redis connection testing
    implementation("redis.clients:jedis:5.0.0")
}
```

### 3. Verify Installation

#### Check Frontend Component
```bash
# Navigate to settings tab
cd frontend-panel
npm run dev
# Go to dashboard > Settings tab
```

#### Check Backend Endpoints
```bash
# Test API
curl http://localhost:9092/api/settings

# Should return:
# {
#   "authPlayer": {...},
#   "sqlConfig": {...},
#   "redisConfig": {...}
# }
```

## 📝 Usage

### Frontend (React Component)

The component automatically:
1. Loads settings on mount
2. Displays three configuration sections
3. Saves changes in real-time
4. Shows connection status
5. Provides test buttons

```tsx
// The component is self-contained
<SettingsTab />

// It manages:
// - Fetching settings from API
// - Displaying forms
// - Validating input
// - Saving changes
// - Testing connections
```

### Backend (REST API)

**Get Settings:**
```bash
curl http://localhost:9092/api/settings
```

**Update Settings:**
```bash
curl -X PUT http://localhost:9092/api/settings \
  -H "Content-Type: application/json" \
  -d '{
    "authPlayer": {"isAuthPlayerEnabled": true, "inputMask": "XXX-000"},
    "sqlConfig": {"host": "db.example.com", ...},
    "redisConfig": {"host": "redis.example.com", ...}
  }'
```

**Test Connection:**
```bash
curl -X POST http://localhost:9092/api/settings/test-connection \
  -H "Content-Type: application/json" \
  -d '{
    "type": "sql",
    "config": {...full settings...}
  }'
```

## 🔧 Configuration Files

### Settings Storage Location
```
plugins/PolyglotPlugin/settings.json
```

### Default Settings
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

## 🎨 UI Features

### Auth Player Section
- Toggle enable/disable
- Input mask configuration field
- Auto-saves when changed

### SQL Configuration Section
- Host and port fields
- Database name field
- Username and password fields
- SSL toggle
- Test connection button
- Status badge showing connection state

### Redis Configuration Section
- Host and port fields
- Password field (optional)
- Database selection (0-15)
- Test connection button
- Status badge showing connection state

## 🧪 Testing

### Manual Testing

1. **Test Getting Settings:**
   ```bash
   curl http://localhost:9092/api/settings | jq
   ```

2. **Test Updating Auth Player:**
   - Go to Settings tab
   - Toggle "Enable Auth Player"
   - Change input mask
   - Verify it updates

3. **Test SQL Connection:**
   - Enter valid PostgreSQL credentials
   - Click "Test Connection"
   - Should show ✓ or ✗

4. **Test Redis Connection:**
   - Enter valid Redis server info
   - Click "Test Connection"
   - Should show ✓ or ✗

### Automated Testing (Optional)

Use the provided `api-examples.sh`:
```bash
chmod +x api-examples.sh
./api-examples.sh
```

## 📚 Documentation Files

Created for reference:

1. **SETTINGS_API_DOCUMENTATION.md** - Full API reference
2. **IMPLEMENTATION_SUMMARY.md** - What was changed
3. **api-examples.sh** - curl command examples
4. **src/types/settings.ts** - TypeScript types and helpers
5. **This file** - Quick start guide

## 🔍 TypeScript Types

Optional: Import types in other components:

```typescript
import { 
  AllSettings, 
  SQLConfig, 
  RedisConfig,
  SettingsApiClient 
} from '@/types/settings'

// Use the client class
const client = new SettingsApiClient()
const settings = await client.getSettings()
```

## ⚠️ Important Notes

1. **Passwords**: Currently stored in plaintext. Add encryption for production.
2. **Permissions**: Add authentication/authorization to the API endpoints.
3. **File Permissions**: Secure `settings.json` file permissions on the server.
4. **Validation**: Add more robust validation of connection parameters.
5. **Logging**: Monitor logs for connection test attempts.

## 🐛 Troubleshooting

### Settings not loading
- Check if server is running on port 9092
- Check browser console for network errors
- Verify NEXT_PUBLIC_API_URL is correct

### Connection tests failing
- Ensure PostgreSQL/Redis drivers are in classpath
- Verify connection parameters are correct
- Check server firewall rules
- Look for errors in server logs

### Settings not saving
- Check file write permissions on server
- Verify `plugins/PolyglotPlugin/` directory exists
- Look for serialization errors in logs

## 🚀 Next Steps

### Recommended Enhancements

1. **Add Authentication**
   - Protect API endpoints with auth tokens
   - Add role-based access control

2. **Add Encryption**
   - Encrypt passwords in settings.json
   - Use secure key management

3. **Add Validation**
   - Validate database credentials before save
   - Add regex validation for input masks

4. **Add Audit Logging**
   - Log all settings changes
   - Track who changed what and when

5. **Add Backup/Restore**
   - Export settings as JSON
   - Import from backup

6. **Add SSL/TLS**
   - Use HTTPS for API endpoints
   - Certificate management

## 📞 Support

For issues or questions:
1. Check the documentation files
2. Review the API examples
3. Check server logs
4. Verify all dependencies are installed

## ✅ Checklist

- [ ] Frontend component is displaying
- [ ] API endpoints respond to requests
- [ ] Settings load on page load
- [ ] Changes save to settings.json
- [ ] SQL connection test works
- [ ] Redis connection test works
- [ ] Status badges update correctly
- [ ] Error messages display properly

---

**Implementation Date**: January 7, 2026
**Status**: Complete and Ready to Use
