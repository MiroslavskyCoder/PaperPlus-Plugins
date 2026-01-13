# WebX Dashboard Scripts API - Quick Reference

## ✅ Scripts Endpoint is LIVE

All script management endpoints are now available in WebX Dashboard.

---

## Core Endpoints (Most Used)

### Get All Scripts
```javascript
GET /api/loaderscript/scripts
```

### Get Script Details
```javascript
GET /api/loaderscript/scripts/test.js
```

### Load a Script
```javascript
POST /api/loaderscript/scripts/test.js/load
```

### Reload a Script
```javascript
POST /api/loaderscript/scripts/test.js/reload
```

### Execute Code
```javascript
POST /api/loaderscript/execute
{
  "code": "console.log('Hello');"
}
```

### List Script Info
```javascript
GET /api/loaderscript/info
```

---

## Frontend Integration Example (JavaScript/React)

### Using Fetch API:

**Get all scripts:**
```javascript
async function getScripts() {
  const response = await fetch('/api/loaderscript/scripts');
  const data = await response.json();
  console.log(data.scripts);
}
```

**Load a script:**
```javascript
async function loadScript(name) {
  const response = await fetch(`/api/loaderscript/scripts/${name}/load`, {
    method: 'POST'
  });
  const data = await response.json();
  console.log(data.message);
}
```

**Update and reload:**
```javascript
async function updateScript(name, newContent) {
  const response = await fetch(`/api/loaderscript/scripts/${name}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      content: newContent,
      autoReload: true
    })
  });
  const data = await response.json();
  console.log(data.message);
}
```

---

## Response Formats

### Success
```json
{
  "success": true,
  "message": "Script loaded"
}
```

### Error
```json
{
  "error": "Script not found"
}
```

### Script List
```json
{
  "scripts": [
    {
      "name": "test.js",
      "loaded": true,
      "success": true,
      "error": null
    }
  ],
  "total": 1,
  "loaded": 1
}
```

---

## All Available Methods

| Method | Endpoint | Purpose |
|--------|----------|---------|
| GET | `/api/loaderscript/scripts` | List all scripts |
| GET | `/api/loaderscript/scripts/{name}` | Get script content |
| POST | `/api/loaderscript/scripts` | Create new script |
| PUT | `/api/loaderscript/scripts/{name}` | Update script |
| DELETE | `/api/loaderscript/scripts/{name}` | Delete script |
| POST | `/api/loaderscript/scripts/{name}/load` | Load script |
| POST | `/api/loaderscript/scripts/{name}/reload` | Reload script |
| POST | `/api/loaderscript/scripts/{name}/unload` | Unload script |
| POST | `/api/loaderscript/reload-all` | Reload all scripts |
| POST | `/api/loaderscript/execute` | Execute code |
| POST | `/api/loaderscript/transpile` | Transpile TS/JSX |
| GET | `/api/loaderscript/info` | Get system info |

---

## Testing in Browser Console

```javascript
// List all scripts
fetch('/api/loaderscript/scripts').then(r => r.json()).then(console.log);

// Get script details
fetch('/api/loaderscript/scripts/test.js').then(r => r.json()).then(console.log);

// Reload a script
fetch('/api/loaderscript/scripts/test.js/reload', { method: 'POST' })
  .then(r => r.json()).then(console.log);

// Get info
fetch('/api/loaderscript/info').then(r => r.json()).then(console.log);
```

---

## Status
✅ Fully implemented and tested
✅ All 70 plugins built successfully
✅ Javalin 6.7.0 compatible
✅ Ready for dashboard frontend integration

---

## Need Help?

See [SCRIPTS_ENDPOINT_VERIFICATION.md](SCRIPTS_ENDPOINT_VERIFICATION.md) for detailed documentation.
