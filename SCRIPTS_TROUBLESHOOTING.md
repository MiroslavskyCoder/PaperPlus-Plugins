# Scripts Not Showing - Troubleshooting Guide

## Issue
The Scripts dashboard shows "No scripts found" even though scripts exist in the `/scripts` folder.

## Verification Steps

### 1. Check Scripts Folder
```bash
# Windows PowerShell
Get-ChildItem "c:\Users\miroslavsky\Downloads\my-polyglot-project\my-polyglot-project\scripts"
```

**Expected output:**
- auto-restart.js
- custom-events.js
- daily-rewards.js
- test.js
- welcome.js
- scoreboard-manager.js
- And more...

### 2. Test API Endpoint Directly

**Using curl:**
```bash
curl http://localhost:8080/api/loaderscript/scripts
```

**Expected response:**
```json
{
  "scripts": [
    { "name": "test.js", "loaded": true, "success": true },
    { "name": "welcome.js", "loaded": true, "success": true }
  ],
  "total": 6,
  "loaded": 5
}
```

### 3. Check Server Logs

Look for messages like:
- ✅ LoaderScript API routes registered successfully
- ✅ LoaderScript enabled! Scripts folder: /path/to/scripts

### 4. Verify Plugin Load Order

The plugins should load in this order:
1. **LoaderScript** - Must load first
2. **WebX Dashboard** - Tries to register LoaderScript routes

---

## Solution Applied

### Changes Made:

**1. LoaderScriptPlugin.java**
- Added `registerWithDashboard()` method
- Called during onEnable() to notify dashboard of availability
- Added logging for debugging

**2. RouterProvider.java**
- Enhanced error handling and logging
- Better exception messages
- Nested try-catch for route registration

**3. LoaderScriptDashboardIntegration.java**
- Added null checks for API controller
- Better error messages

---

## Testing the Fix

### Step 1: Build
```bash
gradle :loaderscript:build :webx-dashboard:build --no-daemon
```

### Step 2: Deploy to Server
```bash
cp out/plugins/loaderscript-1.0.0.jar /path/to/server/plugins/
cp out/plugins/webx-dashboard-1.0.0.jar /path/to/server/plugins/
```

### Step 3: Start Server
- LoaderScript loads and initializes scripts folder
- WebX Dashboard loads and registers LoaderScript routes
- Scripts become available in dashboard

### Step 4: Test API
```bash
# Test in browser console:
fetch('/api/loaderscript/scripts')
  .then(r => r.json())
  .then(console.log)
```

---

## Expected Output After Fix

**Dashboard should show:**
- Total Scripts: 6 (or however many are in scripts folder)
- Loaded: 5 (or number of successfully loaded scripts)
- Success: 5 (scripts that loaded without errors)
- Failed: 0 (or number with errors)

**Scripts list should show:**
- test.js
- welcome.js
- auto-restart.js
- And more...

---

## Common Issues & Solutions

### Issue: Still showing "No scripts found"

**Cause 1: LoaderScript not loading**
- Check that loaderscript-1.0.0.jar is in plugins folder
- Check server logs for LoaderScript errors
- Verify scripts folder exists: `plugins/LoaderScript/scripts/`

**Cause 2: WebX Dashboard loading before LoaderScript**
- Rename files so LoaderScript loads first:
  - `loaderscript-1.0.0.jar` (loads first alphabetically)
  - `zzz-webx-dashboard-1.0.0.jar` or later (loads after)

**Cause 3: API endpoint not registered**
- Check server logs for "LoaderScript API routes registered"
- Try direct curl: `curl http://localhost:8080/api/loaderscript/scripts`
- If 404, routes didn't register

### Issue: Error 500 from API

**Check logs for:**
- NullPointerException - APIController not initialized
- FileNotFoundException - Scripts folder path wrong
- Other exceptions - See full stack trace

---

## Debug Logging

To see detailed logs, add this to your plugin.yml or server properties:
```yaml
logger-level: FINE
```

Then check for messages from LoaderScript and WebX Dashboard.

---

## Build Status

✅ LoaderScript - Compilation successful
✅ WebX Dashboard - Compilation successful
✅ All 70 plugins - Compiled and ready

---

## Next Actions

1. **Rebuild**:
   ```bash
   gradle buildAllPlugins --parallel --no-daemon
   ```

2. **Test in browser**:
   - Open WebX Dashboard
   - Navigate to Scripts section
   - Should show list of scripts

3. **Check server logs** for registration messages

---

## Files Modified

1. `packages/loaderscript/src/main/java/com/webx/loaderscript/LoaderScriptPlugin.java`
   - Added registerWithDashboard() method
   - Added Plugin import

2. `packages/webx-dashboard/src/main/java/com/webx/api/RouterProvider.java`
   - Enhanced error handling
   - Better logging

3. `packages/loaderscript/src/main/java/com/webx/loaderscript/integration/LoaderScriptDashboardIntegration.java`
   - Added API controller null check

---

## Verification Checklist

- [ ] Rebuild all plugins
- [ ] JARs deployed to server
- [ ] Server started with both plugins
- [ ] Check logs for "LoaderScript API routes registered"
- [ ] Test API with curl
- [ ] Test dashboard UI
- [ ] Verify scripts list appears
- [ ] Click on script to view content
- [ ] Test reload script functionality

---

If still not working, provide:
1. Server logs (startup messages)
2. Output of: `curl http://localhost:8080/api/loaderscript/scripts`
3. List of scripts in `/scripts` folder
4. Browser console errors
