# Quick API Test - Scripts Endpoint

## Test the Scripts API

### 1. Using Browser Console

Open your browser's developer console and run:

```javascript
// Test 1: Get all scripts
fetch('/api/loaderscript/scripts')
  .then(response => {
    console.log('Status:', response.status);
    return response.json();
  })
  .then(data => {
    console.log('Scripts response:', data);
    console.log('Total scripts:', data.total);
    console.log('Loaded scripts:', data.loaded);
    console.log('Scripts list:', data.scripts);
  })
  .catch(error => console.error('Error:', error));
```

### 2. Using cURL (if server is running locally)

```bash
# List all scripts
curl -v http://localhost:8080/api/loaderscript/scripts

# Get specific script
curl -v http://localhost:8080/api/loaderscript/scripts/test.js

# Get system info
curl -v http://localhost:8080/api/loaderscript/info
```

### 3. Expected Responses

**If scripts are found:**
```json
{
  "scripts": [
    {
      "name": "test.js",
      "loaded": true,
      "success": true,
      "size": 1024,
      "lastModified": 1704067200000,
      "loadedAt": 1704067205000,
      "error": null
    }
  ],
  "total": 6,
  "loaded": 5
}
```

**If no scripts found (problem):**
```json
{
  "scripts": [],
  "total": 0,
  "loaded": 0
}
```

**If endpoint not registered (404 error):**
```
404 Not Found
```

---

## Debugging

### Check what's happening:

1. **Open browser DevTools** (F12)
2. **Go to Network tab**
3. **Navigate to Scripts in dashboard**
4. **Look for `/api/loaderscript/scripts` request**
5. **Check response:**
   - Status 200 = API working
   - Status 404 = Routes not registered
   - Status 500 = Server error

### Check server logs for:

```
✅ LoaderScript enabled! Scripts folder: /path/to/scripts
✅ LoaderScript API routes registered successfully
```

### If you see errors like:

- "Plugin not found" - LoaderScript didn't load
- "API controller not initialized" - LoaderScript onEnable() failed
- "Scripts folder does not exist" - Wrong path configuration

---

## Fix Applied

The LoaderScript plugin now:
1. ✅ Calls `registerWithDashboard()` after initialization
2. ✅ Checks if WebX Dashboard is available
3. ✅ Logs detailed messages about registration success/failure
4. ✅ Handles exceptions gracefully

The WebX Dashboard now:
1. ✅ Has better error handling in route registration
2. ✅ Logs detailed exceptions
3. ✅ Catches registration errors and continues safely

---

## Next Step

**After rebuilding, restart the server and check:**

1. Do you see "LoaderScript API routes registered successfully" in logs?
2. Does `curl http://localhost:8080/api/loaderscript/scripts` return scripts?
3. Does the dashboard show scripts in the list?

If yes to all: ✅ Fixed!
If no to any: Check [SCRIPTS_TROUBLESHOOTING.md](SCRIPTS_TROUBLESHOOTING.md)
