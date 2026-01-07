#!/bin/bash
# WebSocket Metrics Examples
# This script demonstrates WebSocket metrics streaming

echo "╔════════════════════════════════════════════════════════════════╗"
echo "║         WebSocket Metrics - Testing Examples                   ║"
echo "╚════════════════════════════════════════════════════════════════╝"
echo ""

# Colors
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Example 1: Using wscat
echo -e "${GREEN}Example 1: Using wscat to connect to metrics${NC}"
echo ""
echo "Installation:"
echo "  npm install -g wscat"
echo ""
echo "Connection:"
echo "  wscat -c ws://localhost:9092/metrics"
echo ""
echo "Output (example):"
echo '  {"timestamp":1704667200000,"cpuUsage":45.5,"memoryUsage":72.3,"onlinePlayers":12,"memUsed":3840,"memMax":5120,"diskUsed":250,"diskTotal":500,"status":"online"}'
echo ""
echo "---"
echo ""

# Example 2: Using Node.js
echo -e "${BLUE}Example 2: Using Node.js WebSocket client${NC}"
echo ""
echo "File: metrics-client.js"
echo ""
cat << 'EOF'
const WebSocket = require('ws');

const ws = new WebSocket('ws://localhost:9092/metrics');

// Track statistics
let messageCount = 0;
const startTime = Date.now();

ws.on('open', () => {
  console.log('✅ Connected to WebSocket');
  console.log('Waiting for metrics...\n');
});

ws.on('message', (data) => {
  try {
    const metrics = JSON.parse(data);
    messageCount++;
    
    // Format output
    const elapsed = ((Date.now() - startTime) / 1000).toFixed(1);
    const timestamp = new Date(metrics.timestamp).toLocaleTimeString();
    
    console.clear();
    console.log('📊 Metrics Streaming Status');
    console.log('═══════════════════════════════════════════');
    console.log(`⏱️  Elapsed: ${elapsed}s | Messages: ${messageCount}`);
    console.log(`📅 Server Time: ${timestamp}`);
    console.log('');
    console.log('System Metrics:');
    console.log(`  CPU:     ${metrics.cpuUsage.toFixed(2)}%`);
    console.log(`  Memory:  ${metrics.memUsed}MB / ${metrics.memMax}MB (${metrics.memoryUsage.toFixed(2)}%)`);
    console.log(`  Players: ${metrics.onlinePlayers}`);
    console.log(`  Disk:    ${metrics.diskUsed}GB / ${metrics.diskTotal}GB`);
    console.log(`  Status:  ${metrics.status}`);
    console.log('═══════════════════════════════════════════');
  } catch (e) {
    console.error('Error parsing metrics:', e);
  }
});

ws.on('error', (error) => {
  console.error('❌ WebSocket error:', error);
});

ws.on('close', () => {
  console.log('🔌 Disconnected from WebSocket');
  console.log(`Total messages received: ${messageCount}`);
  process.exit(0);
});

// Graceful shutdown
process.on('SIGINT', () => {
  console.log('\nClosing connection...');
  ws.close();
});
EOF
echo ""
echo "Run with:"
echo "  npm install ws"
echo "  node metrics-client.js"
echo ""
echo "---"
echo ""

# Example 3: Browser Console
echo -e "${GREEN}Example 3: Browser Console JavaScript${NC}"
echo ""
echo "Run in browser Developer Tools Console:"
echo ""
cat << 'EOF'
// Connect to metrics
const ws = new WebSocket('ws://localhost:9092/metrics');

ws.onopen = () => {
  console.log('✅ Connected to metrics WebSocket');
};

ws.onmessage = (event) => {
  const metrics = JSON.parse(event.data);
  console.table({
    'CPU Usage': metrics.cpuUsage.toFixed(2) + '%',
    'Memory': metrics.memUsed + 'MB / ' + metrics.memMax + 'MB',
    'Memory %': metrics.memoryUsage.toFixed(2) + '%',
    'Online Players': metrics.onlinePlayers,
    'Disk Usage': metrics.diskUsed + 'GB / ' + metrics.diskTotal + 'GB',
    'Status': metrics.status,
    'Timestamp': new Date(metrics.timestamp).toLocaleTimeString()
  });
};

ws.onerror = (error) => {
  console.error('❌ Error:', error);
};

ws.onclose = () => {
  console.log('🔌 Disconnected');
};
EOF
echo ""
echo "---"
echo ""

# Example 4: Testing with curl (check REST API availability)
echo -e "${BLUE}Example 4: Check API availability with curl${NC}"
echo ""
echo "Test REST API endpoint:"
echo "  curl http://localhost:9092/api/settings"
echo ""
echo "Expected response (200 OK):"
echo '  {"authPlayer":{...},"sqlConfig":{...},"redisConfig":{...}}'
echo ""
echo "---"
echo ""

# Example 5: Python WebSocket Client
echo -e "${GREEN}Example 5: Python WebSocket Client${NC}"
echo ""
echo "File: metrics_client.py"
echo ""
cat << 'EOF'
#!/usr/bin/env python3
import websocket
import json
import threading
import time
from datetime import datetime

class MetricsClient:
    def __init__(self, url):
        self.url = url
        self.message_count = 0
        self.start_time = time.time()
        self.ws = websocket.WebSocketApp(
            url,
            on_open=self.on_open,
            on_message=self.on_message,
            on_error=self.on_error,
            on_close=self.on_close
        )
    
    def on_open(self, ws):
        print("✅ Connected to WebSocket")
        print("Receiving metrics every 2 seconds...\n")
    
    def on_message(self, ws, message):
        try:
            metrics = json.loads(message)
            self.message_count += 1
            elapsed = time.time() - self.start_time
            
            timestamp = datetime.fromtimestamp(metrics['timestamp'] / 1000).strftime('%H:%M:%S')
            
            print(f"\r📊 [{elapsed:.1f}s] Messages: {self.message_count}")
            print(f"🕐 {timestamp} | CPU: {metrics['cpuUsage']:.1f}% | "
                  f"Memory: {metrics['memUsed']}MB/{metrics['memMax']}MB | "
                  f"Players: {metrics['onlinePlayers']} | "
                  f"Disk: {metrics['diskUsed']}GB/{metrics['diskTotal']}GB", end='')
        except json.JSONDecodeError:
            print(f"Error parsing JSON: {message}")
    
    def on_error(self, ws, error):
        print(f"❌ WebSocket error: {error}")
    
    def on_close(self, ws, close_status_code, close_msg):
        print(f"\n🔌 Disconnected (received {self.message_count} messages)")
    
    def run(self):
        self.ws.run_forever()

if __name__ == "__main__":
    url = "ws://localhost:9092/metrics"
    client = MetricsClient(url)
    
    try:
        client.run()
    except KeyboardInterrupt:
        print("\nShutting down...")
        client.ws.close()
EOF
echo ""
echo "Install websocket-client:"
echo "  pip install websocket-client"
echo ""
echo "Run:"
echo "  python3 metrics_client.py"
echo ""
echo "---"
echo ""

# Example 6: Performance Testing
echo -e "${YELLOW}Example 6: Load Testing - Multiple Connections${NC}"
echo ""
echo "Test multiple concurrent connections:"
echo ""
cat << 'EOF'
#!/bin/bash
# Open 10 concurrent WebSocket connections

for i in {1..10}; do
  (
    wscat -c ws://localhost:9092/metrics
  ) &
done

wait
EOF
echo ""
echo "Or use Apache Bench (if REST API supported):"
echo "  ab -n 1000 -c 100 http://localhost:9092/api/settings"
echo ""
echo "---"
echo ""

# Example 7: Monitoring Script
echo -e "${GREEN}Example 7: Continuous Monitoring Script${NC}"
echo ""
cat << 'EOF'
#!/bin/bash
# Monitor metrics and log to file

LOG_FILE="metrics_$(date +%Y%m%d_%H%M%S).log"
WS_URL="ws://localhost:9092/metrics"

echo "Logging metrics to: $LOG_FILE"

wscat -c "$WS_URL" | while read -r line; do
  TIMESTAMP=$(date '+%Y-%m-%d %H:%M:%S')
  echo "$TIMESTAMP: $line" >> "$LOG_FILE"
  
  # Print to console
  if command -v jq &> /dev/null; then
    echo "$line" | jq . 2>/dev/null && echo "---"
  fi
done
EOF
echo ""
echo "Run:"
echo "  chmod +x monitor_metrics.sh"
echo "  ./monitor_metrics.sh"
echo ""
echo "---"
echo ""

# Example 8: Real-time Dashboard
echo -e "${BLUE}Example 8: Real-time Dashboard in Terminal${NC}"
echo ""
echo "Using 'watch' command to refresh metrics:"
echo ""
echo "  watch -n 2 'curl http://localhost:9092/api/settings | jq .'"
echo ""
echo "Output updates every 2 seconds"
echo ""
echo "---"
echo ""

# Summary
echo -e "${GREEN}═══════════════════════════════════════════════════════════════${NC}"
echo "Summary of Examples:"
echo ""
echo "1. ✅ wscat             - Simple WebSocket CLI client"
echo "2. ✅ Node.js           - JavaScript WebSocket client"
echo "3. ✅ Browser Console   - Direct browser testing"
echo "4. ✅ curl              - REST API health check"
echo "5. ✅ Python            - Python WebSocket client"
echo "6. ✅ Load Testing      - Multiple connections"
echo "7. ✅ Monitoring        - Log metrics to file"
echo "8. ✅ Dashboard         - Terminal monitoring"
echo ""
echo "Recommended for quick testing:"
echo "  wscat -c ws://localhost:9092/metrics"
echo ""
echo "═══════════════════════════════════════════════════════════════"
echo ""
