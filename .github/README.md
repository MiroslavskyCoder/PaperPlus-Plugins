# 🚀 GitHub Actions & CI/CD Documentation

Complete guide to automated building, testing, and deployment of Minecraft plugins.

## 📖 Documentation Index

### 🎯 Quick Start (Start here!)
- **[QUICKSTART.md](QUICKSTART.md)** - 30-second setup guide
  - Enable GitHub Actions in 3 steps
  - Run first build
  - Download results
  
- **[SETUP.md](SETUP.md)** - 5-minute detailed setup guide
  - Step-by-step enable instructions
  - SSH key generation (for optional server deploy)
  - GitHub Secrets configuration
  - Usage examples and troubleshooting

### 🎓 Learning Resources
- **[VISUAL_GUIDE.md](VISUAL_GUIDE.md)** - Diagrams and flowcharts
  - Three workflows at a glance
  - Complete build flow diagram
  - Step-by-step execution flows
  - Time estimates
  - File location map

- **[EXAMPLES.md](EXAMPLES.md)** - Real-world examples
  - Push and build example
  - Server deployment walkthrough
  - Release creation process
  - Complete release timeline
  - Debugging workflow issues

### 📚 Complete Reference
- **[GITHUB_ACTIONS.md](GITHUB_ACTIONS.md)** - Full documentation
  - All 3 workflows explained in detail
  - Trigger types and inputs
  - Output artifacts and retention
  - Security & Secrets setup
  - Troubleshooting guide
  - Best practices
  - Performance statistics

### 📋 Implementation Details
- **[IMPLEMENTATION.md](IMPLEMENTATION.md)** - What was added
  - Summary of all new files
  - How each workflow works
  - Feature comparison table
  - File structure overview
  - Next steps checklist

---

## 🎬 Three GitHub Actions Workflows

### 1. 📦 Build & Deploy ZIP
**Automatic on push/PR/tag**

```
Code Push → Gradle Build → ZIP Archive → Release (if tag)
```

- ✅ Runs automatically on push to main/master
- ✅ Runs on pull requests
- ✅ Runs on tag creation (v1.0.0)
- ✅ Manual trigger available
- 📦 Creates ZIP with all 59 plugins
- 📊 Generates build report
- 💾 Stores artifacts for 90 days
- 📌 Creates GitHub Release on tag

[Full docs](GITHUB_ACTIONS.md#1️⃣-build--deploy-plugins-zip-build-and-deployyml)

### 2. 🚀 Deploy to Server
**Manual trigger only (workflow_dispatch)**

```
GitHub Actions → SSH → Minecraft Server
```

- 🎮 Deploys plugins to your Minecraft server
- 🔐 Uses SSH key authentication
- 📤 Uploads ZIP to server
- 📦 Extracts plugins automatically
- 🔄 Can auto-restart server
- ⚙️ Requires DEPLOY_SSH_KEY secret

[Full docs](GITHUB_ACTIONS.md#2️⃣-deploy-to-server-deploy-to-serveryml)

### 3. 🏷️ Create Release
**Manual trigger only (workflow_dispatch)**

```
Manual Input → Build → Version ZIP → GitHub Release
```

- 📝 Create versioned releases (v1.0.0, v1.1.0, etc.)
- 🔐 Generate SHA256 checksums
- 📋 Include changelogs
- 🏷️ Creates Git tags
- 📦 Attaches ZIP files to Release
- 🔗 Shareable download links

[Full docs](GITHUB_ACTIONS.md#3️⃣-create-release-create-releaseyml)

---

## ⚡ Quick Start

### 1️⃣ Enable GitHub Actions (30 seconds)
```
Repository Settings → Actions → General
→ Allow all actions → Save
```

### 2️⃣ Test First Build (Automatic)
```bash
git push origin main
# GitHub automatically builds!
# View: Actions → Build & Deploy → Latest run
```

### 3️⃣ Optional: Setup Server Deploy (5 minutes)
```bash
# Generate SSH key
ssh-keygen -t ed25519 -f deploy_key -N ""

# Add to GitHub Secrets
# DEPLOY_SSH_KEY = content of deploy_key (private)
# DEPLOY_USER = your server username

# Test deploy
Actions → Deploy to Server → Run workflow
```

**[See SETUP.md for detailed setup](SETUP.md)**

---

## 📋 Workflow Triggers

| Workflow | Push | PR | Tag | Manual |
|----------|------|----|----|--------|
| Build & Deploy | ✅ | ✅ | ✅ | ✅ |
| Deploy to Server | ❌ | ❌ | ❌ | ✅ |
| Create Release | ❌ | ❌ | ❌ | ✅ |

---

## 🔐 Required Secrets (for Deploy to Server)

| Secret | Value | Example |
|--------|-------|---------|
| `DEPLOY_SSH_KEY` | SSH private key (ed25519) | `-----BEGIN PRIVATE KEY-----...` |
| `DEPLOY_USER` | SSH username | `minecraft` |

**Add at:** Settings → Secrets and variables → Actions → New secret

---

## 📊 Build Statistics

```
Build Time:        1-2 minutes
Artifact Size:     ~50-100 MB
Retention:         90 days
Plugins per build: 59
```

---

## 🎯 Common Tasks

### I want to...

#### 📦 Download latest plugins
```
Releases → Latest → Download plugins-v*.zip
```

#### 🚀 Deploy to my server
```
Actions → Deploy to Server → Run workflow
→ Fill in hostname, path, etc.
```

#### 🏷️ Create a release for version 1.0.0
```
Actions → Create Release → Run workflow
→ version: 1.0.0
→ changelog: your changes
```

#### ✅ Check if build passed
```
Actions tab → See green ✅ or red ❌
→ Click latest run for details
```

#### 📝 View build logs
```
Actions → Select workflow → Latest run
→ Click on job → View log details
```

#### 💾 Download build artifacts
```
Actions → Select workflow → Latest run
→ Artifacts section → Download ZIP
```

---

## 🐛 Troubleshooting Quick Fixes

| Problem | Solution |
|---------|----------|
| Build fails | Check Java 21+, run `gradle buildAllPlugins` locally |
| Deploy fails - SSH | Verify DEPLOY_SSH_KEY and DEPLOY_USER in Secrets |
| Deploy fails - Permission | Ensure user has write access to plugins directory |
| ZIP not created | Check if build succeeded, see build logs |
| Release not found | Check if tag was created: `git tag -l` |

[Full troubleshooting](GITHUB_ACTIONS.md#🐛-workflow-statistics)

---

## 📚 File Reference

| File | Purpose | Audience |
|------|---------|----------|
| `build-and-deploy.yml` | Main build workflow | GitHub Actions |
| `deploy-to-server.yml` | Server deployment workflow | GitHub Actions |
| `create-release.yml` | Release creation workflow | GitHub Actions |
| `SETUP.md` | Quick setup (5 min) | Everyone |
| `GITHUB_ACTIONS.md` | Complete reference | Developers |
| `EXAMPLES.md` | Real-world examples | Developers |
| `README.md` | This file | Everyone |

---

## 🔗 Useful Links

- [Workflows in .github/workflows/](workflows/)
- [GitHub Actions Docs](https://docs.github.com/en/actions)
- [Workflow Syntax](https://docs.github.com/en/actions/using-workflows/workflow-syntax-for-github-actions)
- [Managing Secrets](https://docs.github.com/en/actions/security-guides/encrypted-secrets)

---

## 🎓 Learning Path

1. **New to GitHub Actions?**
   → Start with [SETUP.md](SETUP.md)

2. **Want detailed info?**
   → Read [GITHUB_ACTIONS.md](GITHUB_ACTIONS.md)

3. **See real examples?**
   → Check [EXAMPLES.md](EXAMPLES.md)

4. **Need help?**
   → Review [GITHUB_ACTIONS.md#troubleshooting](GITHUB_ACTIONS.md#🐛-troubleshooting)

---

## ✅ Status Checklist

After setup, verify:

- [ ] GitHub Actions enabled in Settings
- [ ] `build-and-deploy.yml` exists
- [ ] `deploy-to-server.yml` exists (if using)
- [ ] `create-release.yml` exists
- [ ] DEPLOY_SSH_KEY in Secrets (if using deploy)
- [ ] DEPLOY_USER in Secrets (if using deploy)
- [ ] First build triggered and passed
- [ ] Artifact downloaded successfully
- [ ] ZIP contains all 59 plugins

---

## 🚀 Next Steps

1. **Enable Actions** - 30 seconds
2. **Test Build** - Push to main (automatic)
3. **Download Artifact** - From Actions tab
4. **Setup Deploy** (optional) - 5 minutes
5. **Create Release** (optional) - 2 minutes

**Ready to go!** All workflows are pre-configured and ready to use.

---

**Last Updated:** January 2026  
**Version:** 1.0.0  
**Status:** ✅ All workflows tested and working
