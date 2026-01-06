# WebX Dashboard by LXXV

A professional-grade Web Management Panel for Minecraft Paper servers.

## Architecture

- **Backend**: Java 17 (Paper API 1.19.4 + Javalin Web Server)
- **Frontend**: React, TypeScript, Bun, Next.js (Turbopack), Shadcn UI
- **Communication**: REST API + WebSockets

## Features

- Real-time server monitoring (CPU, RAM, Disk, Players)
- Player management
- Permissions management (basic)
- Vault economy settings (placeholder)
- EssentialsX configuration (placeholder)
- Server control (commands, stop)
- WebSocket for live updates

## Setup

### Prerequisites
- JDK 17
- Bun 1.0+
- Gradle

### Development

1. **Backend**:
   ```bash
   cd backend-plugin
   gradle jar
   # Copy the jar to your Paper server plugins folder
   ```

2. **Frontend**:
   ```bash
   cd frontend-panel
   bun install
   bun run dev
   ```

### Production

1. Build frontend:
   ```bash
   cd frontend-panel
   bun run build
   # Copy out/ to web server
   ```

2. Deploy backend jar to Paper plugins/

## API Key

Default API key: `secret-change-me`

Change in plugin config.yml

## WebSocket

Connect to ws://localhost:8080/ws for real-time stats