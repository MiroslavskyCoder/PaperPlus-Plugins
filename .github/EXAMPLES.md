# GitHub Actions Workflow Examples

## 1️⃣ Build & Deploy ZIP Workflow

### Automatic Build on Every Push

```bash
# Your local work
git add .
git commit -m "feat: add new feature"
git push origin main
```

**What GitHub Actions does automatically:**
```
✅ Checkout code
✅ Setup Java 21
✅ Run: gradle buildAllPlugins
✅ Create: plugins-<commit-hash>.zip
✅ Generate: build report with plugin list
✅ Upload: artifact (90-day retention)
```

**Result:**
- Artifact available: `Actions` → `Build & Deploy` → Latest run
- Download: `plugins-archive-<hash>` (contains ZIP + report)
- Report shows: plugin count, sizes, build time

### Automatic Release on Tag

```bash
# Create and push tag
git tag -a v1.2.0 -m "Release 1.2.0"
git push origin v1.2.0
```

**What GitHub Actions does:**
```
✅ Builds all plugins
✅ Creates ZIP: plugins-<commit>.zip
✅ Generates report
✅ Creates GitHub Release
✅ Attaches ZIP to Release
```

**Result:**
- Release: `Releases` → `v1.2.0`
- Files: plugins ZIP + build report
- Public download link for users

### Manual Trigger

1. Go to: `Actions` tab
2. Select: `Build & Deploy Plugins ZIP`
3. Click: `Run workflow` dropdown
4. Select branch: `main`
5. Click: `Run workflow` button

**With Optional Release Creation:**
1. Click: `Run workflow` dropdown
2. Input: `create_release = true`
3. Click: `Run workflow`

**Result:** Creates Release in addition to artifact

---

## 2️⃣ Deploy to Server Workflow

### Prerequisites
```bash
# 1. Create SSH key (don't use password!)
ssh-keygen -t ed25519 -f ~/.ssh/deploy_key -N ""

# 2. Copy public key to server
ssh-copy-id -i ~/.ssh/deploy_key.pub user@your.server.com

# 3. Add to GitHub Secrets:
#    - DEPLOY_SSH_KEY (content of deploy_key, NOT public key)
#    - DEPLOY_USER (e.g., minecraft)
```

### Deploy to Your Minecraft Server

1. Go to: `Actions` tab
2. Select: `Deploy to Server` workflow
3. Click: `Run workflow` dropdown
4. Fill in:
   ```
   server_host: play.myserver.com (or 192.168.1.100)
   deploy_path: /opt/minecraft/plugins
   restart_server: true
   ```
5. Click: `Run workflow`

**What it does:**
```
✅ Builds all 59 plugins
✅ Creates ZIP: plugins-deploy.zip
✅ Connects to server via SSH
✅ Uploads ZIP to server
✅ Extracts plugins to deploy_path
✅ Restarts Minecraft server
```

**Example with real values:**
```
server_host: 192.168.1.50
deploy_path: /data/minecraft/plugins
restart_server: true

# Server must have:
# - SSH enabled
# - User with plugin directory write access
# - systemctl/service command available
# - Or custom restart script at /opt/minecraft/restart.sh
```

### Manual Deploy Steps

If you prefer manual deployment:

```bash
# 1. Download artifact from GitHub
# Actions → Build & Deploy → artifacts

# 2. Extract locally
unzip plugins-<hash>.zip

# 3. Upload to server
scp -r *.jar user@server:/opt/minecraft/plugins/

# 4. SSH to server and restart
ssh user@server
cd /opt/minecraft
./stop.sh
./start.sh
```

---

## 3️⃣ Create Release Workflow

### Create Versioned Release

1. Go to: `Actions` tab
2. Select: `Create Release`
3. Click: `Run workflow` dropdown
4. Fill in:
   ```
   version: 1.2.0
   changelog: 
     - Fixed shop plugin NPC
     - Added clan leveling
     - Improved performance 20%
   ```
5. Click: `Run workflow`

**What it does:**
```
✅ Builds all plugins
✅ Creates ZIP: plugins-v1.2.0.zip
✅ Generates SHA256: plugins-v1.2.0.zip.sha256
✅ Creates Git tag: v1.2.0
✅ Creates GitHub Release
✅ Attaches files to release
✅ Publishes release notes
```

**Release includes:**
- `plugins-v1.2.0.zip` - All compiled plugins
- `plugins-v1.2.0.zip.sha256` - For integrity check
- Release notes with changelog
- Direct download links

### Verify Release Integrity

Users can verify the downloaded ZIP:
```bash
# Download SHA256 file
# From release page

# Verify
sha256sum -c plugins-v1.2.0.zip.sha256

# Output
plugins-v1.2.0.zip: OK
```

### Example Release Workflow

```
# Prepare release
git add .
git commit -m "chore: update version to 1.2.0"

# Create release
Actions → Create Release → Run workflow
  version: 1.2.0
  changelog: Major update with new features

# Result
# - GitHub Release v1.2.0 created
# - ZIP available for download
# - SHA256 provided
# - Git tag v1.2.0 created
```

---

## 4️⃣ Complete Release Process

### Step-by-step Release

```bash
# 1. Create feature branch
git checkout -b release/v1.2.0

# 2. Update version in files (if needed)
# Edit: build.gradle.kts, version = "1.2.0"

# 3. Commit
git add .
git commit -m "chore: bump version to 1.2.0"

# 4. Push to main (creates PR, waits for review)
git push origin release/v1.2.0
# Create PR on GitHub, wait for approval

# 5. Merge PR to main
# GitHub Actions automatically builds and zips

# 6. Create Release
git switch main
git pull origin main
git tag -a v1.2.0 -m "Release 1.2.0"
git push origin v1.2.0

# GitHub Actions automatically:
# - Builds plugins
# - Creates Release
# - Attaches ZIP files
```

### Automatic GitHub Release (Tag-based)

Once tag is pushed:
```
GitHub automatically:
1. Runs Build & Deploy workflow
2. Detects tag matches "v*"
3. Creates Release from tag
4. Attaches ZIP and report
```

---

## 5️⃣ Deployment Strategies

### Strategy 1: Automatic on Tags

```
Push tag → GitHub Actions → Create Release → Users download
```

**Pros:**
- ✅ Fully automated
- ✅ Clean release history
- ✅ Easy for users

**Cons:**
- ❌ Need to manage tags manually

### Strategy 2: Manual Release

```
Actions → Create Release (manual) → Release created → Users download
```

**Pros:**
- ✅ Full control when to release
- ✅ Can add custom notes
- ✅ Can update files after build

**Cons:**
- ❌ Manual trigger needed
- ❌ Extra step vs tags

### Strategy 3: Auto-deploy to Server

```
Push to main → Build ZIP → Auto-deploy via SSH → Server updated
```

**Setup:**
```yaml
# After build in workflow:
- name: Auto-deploy if tagged
  if: startsWith(github.ref, 'refs/tags/')
  uses: custom-deploy-action
```

**Pros:**
- ✅ Fully automated
- ✅ Zero downtime updates
- ✅ Instant deployment

**Cons:**
- ❌ Requires SSH setup
- ❌ No rollback on failure

---

## 6️⃣ Workflow Status Badges

Add to your README.md:

```markdown
## Status

[![Build & Deploy](https://github.com/your-org/repo/actions/workflows/build-and-deploy.yml/badge.svg)](https://github.com/your-org/repo/actions)

## Latest Release

[![Latest Release](https://img.shields.io/github/v/release/your-org/repo?label=latest%20release)](https://github.com/your-org/repo/releases/latest)
```

---

## 7️⃣ Real-world Example Timeline

**Day 1:** Development
```bash
git checkout -b feature/economy-fix
# Make changes...
git push origin feature/economy-fix
# GitHub Actions: Runs build, creates artifact
```

**Day 2:** Review & Merge
```bash
# PR approved and merged to main
# GitHub Actions: Builds again, updates artifact
```

**Day 3:** Release
```bash
git tag -a v1.2.0 -m "Release 1.2.0"
git push origin v1.2.0
# GitHub Actions: 
# - Builds plugins
# - Creates Release v1.2.0
# - Attaches ZIP files
# - Posts release notes
```

**Day 4:** Deploy to Server
```
# GitHub Actions → Deploy to Server
# server_host: play.myserver.com
# deploy_path: /opt/minecraft/plugins
# restart_server: true
# Server automatically updated!
```

**Users can:**
- Download ZIP from Releases
- Verify with SHA256
- Extract and deploy to their servers

---

## 📊 Workflow Duration

```
Build & Deploy:     60-120 seconds
Deploy to Server:   30-60 seconds (+ network time)
Create Release:     90-150 seconds
```

---

## 🔍 Debugging Workflow Issues

### Check Logs
```
Actions → Select workflow → Latest run → Click job → View logs
```

### Common Issues

**Build fails:**
```
Check:
- Java version (21+)
- Gradle version (9.2.1+)
- Dependencies available
- All .java files compile locally
```

**Deploy fails:**
```
Check:
- SSH key in Secrets
- Server hostname correct
- User exists on server
- Directory writable
- SSH port (usually 22)
```

**Release fails:**
```
Check:
- ZIP created successfully
- Artifact uploaded
- Git tag created
- GitHub token has permissions
```

---

**Need help?** Check `.github/GITHUB_ACTIONS.md` for full documentation.
