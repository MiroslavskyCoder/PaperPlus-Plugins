# WebSocket and API Configuration

## Environment Variables

Create `.env.local` in the `frontend-panel` directory with these settings:

### WebSocket Configuration

```bash
# WebSocket endpoint for metrics (2-second updates)
NEXT_PUBLIC_WS_URL=ws://localhost:9092/metrics
```

### API Configuration

```bash
# REST API base URL
NEXT_PUBLIC_API_URL=http://localhost:9092/api
```

### Example .env.local

```bash
# Development
NEXT_PUBLIC_WS_URL=ws://localhost:9092/metrics
NEXT_PUBLIC_API_URL=http://localhost:9092/api

# Production example
# NEXT_PUBLIC_WS_URL=ws://your-domain.com:9092/metrics
# NEXT_PUBLIC_API_URL=http://your-domain.com:9092/api
```

## Configuration Details

### WebSocket URL

- **Default**: `ws://localhost:9092/metrics`
- **Production**: `ws://your-server:9092/metrics`
- **Secure**: `wss://your-server:9092/metrics` (with HTTPS/WSS)

### API Base URL

- **Default**: `http://localhost:9092/api`
- **Production**: `http://your-server:9092/api`
- **Secure**: `https://your-server:9092/api` (with HTTPS)

## Ports

- **WebSocket & REST API**: 9092
- **Alternative**: Can be configured in `RouterProvider.java`

## Server Configuration

### Backend Setup

**File**: `webx-dashboard/src/main/java/com/webx/api/RouterProvider.java`

The server is configured to:
1. Listen on port **9092**
2. Serve static files from `/web`
3. Enable WebSocket at `/metrics`
4. Enable REST API at `/api/*`

### Metrics Update Interval

**File**: `webx-dashboard/src/main/java/com/webx/api/RouterProvider.java`

```java
// Metrics sent every 2 seconds (40 Bukkit ticks at 20 TPS)
.runTaskTimer(plugin, 0L, 40L);
```

To change interval:
- 1 second = 20 ticks
- 2 seconds = 40 ticks
- 5 seconds = 100 ticks

## Development Setup

### Starting the Backend

```bash
cd webx-dashboard
./gradlew build
./gradlew run
```

Server will start on `http://localhost:9092`

### Starting the Frontend

```bash
cd frontend-panel
npm install
npm run dev
```

Frontend will start on `http://localhost:3000`

### Testing WebSocket Connection

```bash
# Terminal 1: Start backend
./gradlew run

# Terminal 2: In browser console
const ws = new WebSocket('ws://localhost:9092/metrics');
ws.onmessage = (e) => console.log(JSON.parse(e.data));
```

## Network Configuration

### Local Network

For accessing dashboard from another machine on local network:

```bash
NEXT_PUBLIC_WS_URL=ws://192.168.1.100:9092/metrics
NEXT_PUBLIC_API_URL=http://192.168.1.100:9092/api
```

### Remote Network

For accessing over internet:

```bash
NEXT_PUBLIC_WS_URL=wss://your-domain.com:9092/metrics
NEXT_PUBLIC_API_URL=https://your-domain.com:9092/api
```

Requires:
- SSL/TLS certificate
- Reverse proxy (nginx, Apache)
- Port forwarding

## Docker Configuration

If running in Docker:

```bash
# Docker service name instead of localhost
NEXT_PUBLIC_WS_URL=ws://webx-dashboard:9092/metrics
NEXT_PUBLIC_API_URL=http://webx-dashboard:9092/api
```

## Troubleshooting

### WebSocket Connection Refused

1. Check backend is running: `curl http://localhost:9092/api/settings`
2. Verify .env.local has correct URL
3. Check firewall allows port 9092
4. Check browser console for errors

### Settings Not Loading

1. Verify API_BASE_URL is correct
2. Check backend is running
3. Verify JSON response format
4. Check browser network tab

### Metrics Not Updating

1. Check WebSocket connection in browser DevTools
2. Verify ws:// URL is correct
3. Check server logs for errors
4. Verify interval is 40 ticks (2 seconds)

## Security Notes

### Development

⚠️ Using unencrypted WebSocket and HTTP is fine for local development

### Production

✅ Use encrypted connections:
- `wss://` for WebSocket (requires SSL/TLS)
- `https://` for REST API (requires SSL/TLS)

✅ Implement authentication:
- Add API key requirement
- Implement token-based auth
- Use OAuth2 if needed

## Performance Tips

### Reduce Data Transfer

If bandwidth is limited:
1. Increase metrics interval (e.g., 5 seconds)
2. Reduce chart history size (default 30 points)
3. Implement data compression

### Improve Responsiveness

1. Keep metrics interval at 2 seconds
2. Use local caching
3. Implement optimistic updates

## Monitoring

### Check Backend Health

```bash
curl http://localhost:9092/api/settings
```

Expected response: 200 OK with JSON

### Check WebSocket

```bash
npm install -g wscat
wscat -c ws://localhost:9092/metrics
```

Should see JSON metrics every 2 seconds

### Check Logs

Backend logs (check console/server output):
```
✅ Connected to metrics WebSocket
📊 Metrics updated: CPU, Memory, Players
```

## Configuration Examples

### Quick Start (Local)

```bash
# .env.local
NEXT_PUBLIC_WS_URL=ws://localhost:9092/metrics
NEXT_PUBLIC_API_URL=http://localhost:9092/api
```

### Development (with debugging)

```bash
# .env.local
NEXT_PUBLIC_WS_URL=ws://localhost:9092/metrics
NEXT_PUBLIC_API_URL=http://localhost:9092/api
DEBUG=true
```

### Production (with domain)

```bash
# .env.local
NEXT_PUBLIC_WS_URL=wss://dashboard.example.com/metrics
NEXT_PUBLIC_API_URL=https://dashboard.example.com/api
```

### Docker Compose

```bash
# .env.local
NEXT_PUBLIC_WS_URL=ws://webx-dashboard:9092/metrics
NEXT_PUBLIC_API_URL=http://webx-dashboard:9092/api
```

---

**Version**: 1.0
**Updated**: January 7, 2026
