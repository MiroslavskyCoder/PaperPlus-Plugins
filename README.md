# WebX Dashboard by LXXV

A professional-grade Web Management Panel for Minecraft Paper servers.

## ⚠️ Important: Building for Production

**Always use `clean` when building to ensure web files are updated!**

```bash
# Windows
gradlew.bat clean build

# Linux/Mac  
./gradlew clean build
```

The JAR will be in `webx-dashboard/build/libs/webx-dashboard-1.0.0.jar`

See [DEPLOY.md](DEPLOY.md) for detailed deployment instructions.

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

1. **Build everything**:
   ```bash
   gradlew.bat build  # Windows
   ./gradlew build    # Linux/Mac
   ```

2. **Frontend dev server** (optional, for live reload):
   ```bash
   cd frontend-panel
   bun install
   bun run dev
   ```

### Production

**Full build with clean** (recommended):
```bash
gradlew.bat clean build
# or
./gradlew clean build
```

The plugin JAR includes all frontend files. Deploy to server:
```bash
# Copy JAR to server plugins folder
cp webx-dashboard/build/libs/webx-dashboard-1.0.0.jar /path/to/server/plugins/

# Restart server or use /reload
```

### Why files don't update?

Web files are packaged inside the JAR. Without `clean`:
- Old files remain in `src/main/resources/web/`
- Old JAR cached in `build/libs/`

**Solution**: Always run `gradlew clean build` before deployment.
   ```

2. Deploy backend jar to Paper plugins/

## API Key

Default API key: `secret-change-me`

Change in plugin config.yml

## WebSocket

Connect to ws://localhost:8080/ws for real-time stats