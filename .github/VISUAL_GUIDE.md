# 🎬 GitHub Actions Visual Guide

## 🎯 Three Workflows at a Glance

```
┌─────────────────────────────────────────────────────────────┐
│                   GitHub Actions Workflows                  │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│  AUTOMATIC (Runs without manual trigger)                   │
│  ─────────────────────────────────────────────────────────   │
│                                                              │
│  📦 Build & Deploy ZIP                                     │
│  ┌──────────────────────────────────────┐                  │
│  │ Triggers:                             │                  │
│  │  • git push origin main               │  Auto ✅         │
│  │  • git push origin v1.0.0 (tag)       │  Auto ✅         │
│  │  • Pull Request to main               │  Auto ✅         │
│  │  • Manual via Actions tab             │  Manual ✅       │
│  │                                        │                  │
│  │ Output:                               │                  │
│  │  • ZIP: plugins-<commit>.zip          │                  │
│  │  • Report: build statistics           │                  │
│  │  • Release (if tag pushed)            │                  │
│  │  • Artifacts: 90-day storage          │                  │
│  └──────────────────────────────────────┘                  │
│                                                              │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│  MANUAL ONLY (Requires user action)                        │
│  ─────────────────────────────────────────────────────────   │
│                                                              │
│  🚀 Deploy to Server                                       │
│  ┌──────────────────────────────────────┐                  │
│  │ Trigger: Manual only                  │  Never auto!    │
│  │ Go to: Actions → Deploy to Server     │                  │
│  │ Click: Run workflow                   │                  │
│  │                                        │                  │
│  │ Inputs:                               │                  │
│  │  • server_host:   play.server.com     │                  │
│  │  • deploy_path:   /opt/minecraft/...  │                  │
│  │  • restart_server: true/false         │                  │
│  │                                        │                  │
│  │ Requires:                             │                  │
│  │  • Secrets: DEPLOY_SSH_KEY            │                  │
│  │  • Secrets: DEPLOY_USER               │                  │
│  │                                        │                  │
│  │ Output:                               │                  │
│  │  • Plugins deployed ✅                │                  │
│  │  • Server (maybe) restarted ✅        │                  │
│  └──────────────────────────────────────┘                  │
│                                                              │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│  🏷️ Create Release                                         │
│  ┌──────────────────────────────────────┐                  │
│  │ Trigger: Manual only                  │  Never auto!    │
│  │ Go to: Actions → Create Release       │                  │
│  │ Click: Run workflow                   │                  │
│  │                                        │                  │
│  │ Inputs:                               │                  │
│  │  • version:     1.0.0                 │                  │
│  │  • changelog:   Your changes...       │                  │
│  │                                        │                  │
│  │ Output:                               │                  │
│  │  • Release v1.0.0 created             │                  │
│  │  • ZIP: plugins-v1.0.0.zip            │                  │
│  │  • SHA256: checksum file              │                  │
│  │  • Git tag: v1.0.0                    │                  │
│  │  • Release notes published            │                  │
│  └──────────────────────────────────────┘                  │
│                                                              │
└─────────────────────────────────────────────────────────────┘
```

---

## 🔄 Complete Build Flow

```
Your Local Machine
│
├─ Edit files
├─ git add .
├─ git commit -m "feat: my changes"
└─ git push origin main
   │
   └──────────────────────────────────────────────────┐
                                                      │
                                         GitHub Server │
                                                      ▼
┌─────────────────────────────────────────────────────────────┐
│        GitHub Actions: Build & Deploy Triggered            │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│  1. Checkout Code                  ✅ 5 sec                │
│     Clone repository from git       └─ ready               │
│                                                              │
│  2. Setup Java 21                  ✅ 15 sec               │
│     Download and setup JDK          └─ ready               │
│                                                              │
│  3. Build All Plugins              ✅ 60 sec               │
│     gradle buildAllPlugins          └─ compiling...        │
│                                                              │
│  ├─ Compile 59 plugins                                     │
│  ├─ Run unit tests                                         │
│  ├─ Package JAR files                                      │
│  └─ Output to out/plugins/                                 │
│                                                              │
│  4. Create ZIP Archive             ✅ 10 sec               │
│     Zip all JAR files              └─ plugins-abc.zip      │
│                                                              │
│  5. Generate Report                ✅ 5 sec                │
│     List plugins & statistics      └─ build-report.md      │
│                                                              │
│  6. Upload Artifacts               ✅ 10 sec               │
│     Store ZIP for 90 days          └─ ready for download   │
│                                                              │
│                           Total Time: ~2 minutes            │
│                                                              │
└─────────────────────────────────────────────────────────────┘
                             │
                             ▼
                 GitHub Actions Results
                             │
                    ┌────────┴────────┐
                    │                 │
                    ▼                 ▼
            Actions Tab          Releases Tab
            (Artifacts)          (if tagged)
            90-day storage       Forever
```

---

## 📦 Build & Deploy - Detailed Steps

```
┌─────────────────────────────────────────────┐
│  Build & Deploy Workflow Execution          │
└─────────────────────────────────────────────┘

MANUAL TRIGGER (optional):
Actions → Build & Deploy → Run workflow

AUTOMATIC TRIGGERS:
  - Push to main/master
  - Pull Request to main/master
  - Tag push (v1.0.0)

EXECUTION STEPS:

  Step 1: Checkout Code
  ┌────────────────────┐
  │ Clone repository   │
  └────────────────────┘
             │
             ▼
  Step 2: Setup Java
  ┌────────────────────┐
  │ Install Java 21    │
  │ Setup Gradle cache │
  └────────────────────┘
             │
             ▼
  Step 3: Build Plugins
  ┌────────────────────┐
  │ gradle buildAll... │
  │ 59 plugins compile │
  │ Output: *.jar      │
  └────────────────────┘
             │
             ▼
  Step 4: Create ZIP
  ┌────────────────────┐
  │ zip plugins-*.zip  │
  │ ~50-100 MB         │
  └────────────────────┘
             │
             ▼
  Step 5: Build Report
  ┌────────────────────┐
  │ List all plugins   │
  │ Show statistics    │
  │ build-report.md    │
  └────────────────────┘
             │
             ▼
  Step 6: Upload Artifacts
  ┌────────────────────┐
  │ Save to Actions    │
  │ 90-day retention   │
  │ Ready to download  │
  └────────────────────┘
             │
             ▼
  CONDITIONAL: If Tag Pushed
  ┌────────────────────┐
  │ Create Release     │
  │ Attach ZIP file    │
  │ Publish on GitHub  │
  └────────────────────┘
```

---

## 🚀 Deploy to Server - Detailed Steps

```
┌─────────────────────────────────────────────┐
│  Deploy to Server Workflow Execution        │
└─────────────────────────────────────────────┘

MANUAL TRIGGER ONLY:
Actions → Deploy to Server → Run workflow
  Fill in: hostname, path, restart? → Submit

EXECUTION STEPS:

  Step 1: Build Plugins (same as above)
  ┌────────────────────────────────────┐
  │ gradle buildAllPlugins             │
  │ → 59 JAR files ready               │
  └────────────────────────────────────┘
             │
             ▼
  Step 2: Create ZIP
  ┌────────────────────────────────────┐
  │ zip plugins-deploy.zip             │
  │ Ready to upload                    │
  └────────────────────────────────────┘
             │
             ▼
  Step 3: Setup SSH Connection
  ┌────────────────────────────────────┐
  │ Load DEPLOY_SSH_KEY secret         │
  │ Load DEPLOY_USER secret            │
  │ Verify server SSH key              │
  └────────────────────────────────────┘
             │
             ▼
  Step 4: Upload to Server
  ┌────────────────────────────────────┐
  │ scp plugins-deploy.zip             │
  │ → user@server:/deploy/path/        │
  │ ~50-100 MB upload time             │
  └────────────────────────────────────┘
             │
             ▼
  Step 5: Extract Plugins
  ┌────────────────────────────────────┐
  │ ssh user@server                    │
  │ unzip plugins-deploy.zip           │
  │ Plugins now in place               │
  └────────────────────────────────────┘
             │
             ▼
  CONDITIONAL: If restart_server=true
  ┌────────────────────────────────────┐
  │ systemctl restart minecraft        │
  │ OR: service minecraft restart      │
  │ OR: /opt/minecraft/restart.sh      │
  │ Server restarting with new plugins │
  └────────────────────────────────────┘
             │
             ▼
  ✅ DEPLOYMENT COMPLETE
  Plugins active on server!
```

---

## 🏷️ Create Release - Detailed Steps

```
┌─────────────────────────────────────────────┐
│  Create Release Workflow Execution          │
└─────────────────────────────────────────────┘

MANUAL TRIGGER ONLY:
Actions → Create Release → Run workflow
  Fill in: version, changelog → Submit

EXECUTION STEPS:

  Step 1-4: Same as Build & Deploy
  ├─ Checkout code ✅
  ├─ Setup Java ✅
  ├─ Build plugins ✅
  └─ Create ZIP ✅
             │
             ▼
  Step 5: Generate SHA256
  ┌────────────────────────────────────┐
  │ sha256sum plugins-v1.0.0.zip       │
  │ → plugins-v1.0.0.zip.sha256        │
  │ For integrity verification         │
  └────────────────────────────────────┘
             │
             ▼
  Step 6: Generate Release Notes
  ┌────────────────────────────────────┐
  │ Create markdown release notes       │
  │ Include changelog                  │
  │ Show plugin statistics             │
  │ Add verification instructions      │
  └────────────────────────────────────┘
             │
             ▼
  Step 7: Create Git Tag
  ┌────────────────────────────────────┐
  │ git tag -a v1.0.0                  │
  │ -m "Release 1.0.0"                 │
  │ git push origin v1.0.0             │
  └────────────────────────────────────┘
             │
             ▼
  Step 8: Create GitHub Release
  ┌────────────────────────────────────┐
  │ Create Release: v1.0.0             │
  │ Attach plugins-v1.0.0.zip          │
  │ Attach .sha256 file                │
  │ Add release notes                  │
  │ Publish publicly                   │
  └────────────────────────────────────┘
             │
             ▼
  ✅ RELEASE PUBLISHED
  Users can download from:
  https://github.com/user/repo/releases/v1.0.0
```

---

## 📊 Time Estimates

```
Workflow               Duration    Network  Total
─────────────────────────────────────────────────
Build & Deploy        60-120 sec   ~20s    1-2 min
Deploy to Server      60 sec       2-5min  3-7 min
Create Release        90-150 sec   ~10s    2-3 min

First build:          Longer (no cache)
Subsequent builds:    Shorter (cached)
```

---

## 🎓 Where to Find Everything

```
GitHub Repository
├── .github/
│   ├── workflows/
│   │   ├── build-and-deploy.yml     ← Actual workflow code
│   │   ├── deploy-to-server.yml     ← Actual workflow code
│   │   └── create-release.yml       ← Actual workflow code
│   │
│   ├── QUICKSTART.md                ← Read this first! (1 min)
│   ├── SETUP.md                     ← Detailed setup (5 min)
│   ├── GITHUB_ACTIONS.md            ← Full reference (15 min)
│   ├── EXAMPLES.md                  ← Real examples (10 min)
│   ├── IMPLEMENTATION.md            ← What was added (5 min)
│   └── README.md                    ← This index (3 min)
│
├── README.md                        ← Main project docs
│   └── GitHub Actions section       ← Quick overview
│
├── out/plugins/                     ← Built JAR files
├── QUICK_START.md                  ← General quickstart
├── BUILD_ALL.md                    ← Build instructions
└── ... (other docs)
```

---

## ✅ Checklist

- [ ] Understand Build & Deploy workflow (automatic)
- [ ] Understand Deploy to Server workflow (manual + SSH)
- [ ] Understand Create Release workflow (manual + versioning)
- [ ] Enable GitHub Actions in Settings
- [ ] Push to main and see first build
- [ ] Download artifact from Actions tab
- [ ] (Optional) Setup SSH keys for server deploy
- [ ] (Optional) Try Deploy to Server workflow
- [ ] (Optional) Create first release

---

**Ready to go!** 🚀
