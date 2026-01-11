# GitHub Actions Setup Guide

## 🚀 Quick Setup (5 minutes)

### Step 1: Enable GitHub Actions
1. Go to repository Settings
2. Actions → General → Allow all actions and reusable workflows
3. Workflow permissions: Read and write permissions
4. Save

### Step 2: Deploy to Server (Optional)

If you want to deploy to your own server:

**A. Generate SSH Key**
```bash
# On your local machine
ssh-keygen -t ed25519 -f deploy_key -N ""

# Copy public key to server
cat deploy_key.pub
```

**B. Add to Server**
```bash
# On your Minecraft server
cat >> ~/.ssh/authorized_keys << 'EOF'
<paste public key here>
EOF
chmod 600 ~/.ssh/authorized_keys
```

**C. Add to GitHub Secrets**
1. Repository Settings → Secrets and variables → Actions
2. New repository secret:
   - Name: `DEPLOY_SSH_KEY`
   - Value: (paste content of `deploy_key` file - PRIVATE KEY)
3. New repository secret:
   - Name: `DEPLOY_USER`
   - Value: (username on server, e.g., `minecraft`)

### Step 3: Workflows Ready!

Your workflows are now ready:

✅ **Build & Deploy** - Automatic on push/PR/tag
✅ **Deploy to Server** - Manual with SSH
✅ **Create Release** - Manual with versioning

---

## 🎯 Usage Examples

### Example 1: Push and Build
```bash
git add .
git commit -m "feat: add new plugin"
git push origin main

# GitHub Actions automatically:
# 1. Builds all 59 plugins
# 2. Creates ZIP archive
# 3. Saves artifact for 90 days
# View at: Actions → Build & Deploy → Latest run
```

### Example 2: Create a Release
```bash
git tag -a v1.0.0 -m "Release version 1.0.0"
git push origin v1.0.0

# GitHub automatically creates a Release with:
# - plugins-v1.0.0.zip
# - SHA256 checksum
# - Release notes
# View at: Releases → v1.0.0
```

### Example 3: Deploy to Server (Manual)
```
1. Go to Actions tab
2. Select "Deploy to Server"
3. Click "Run workflow"
4. Fill in:
   - server_host: your.server.com (or IP)
   - deploy_path: /opt/minecraft/plugins
   - restart_server: true
5. Click "Run workflow"

Result: Plugins deployed and server restarted!
```

### Example 4: Create Versioned Release (Manual)
```
1. Go to Actions tab
2. Select "Create Release"
3. Click "Run workflow"
4. Fill in:
   - version: 1.0.0
   - changelog: (paste your changes)
5. Click "Run workflow"

Result: GitHub Release created with plugins-v1.0.0.zip
```

---

## 📊 Monitoring Workflows

### View Build Status
```
Repository home page → Show Actions status badge
```

### View Workflow Runs
```
Actions → Workflow name → Select run → View logs
```

### Download Artifacts
```
Actions → Build & Deploy → Latest run → Artifacts
→ Download plugins-archive-<hash>
```

### View Releases
```
Releases → Select version → Download assets
```

---

## 🐛 Troubleshooting

### Build Fails with Java Error
**Cause:** JDK version mismatch
**Fix:**
```yaml
# Verify in workflow file:
java-version: '21'
```

### Deploy Fails - SSH Connection Error
**Cause:** SSH key not configured correctly
**Fix:**
1. Check DEPLOY_SSH_KEY is correct (private key, not public)
2. Check DEPLOY_USER matches actual server user
3. Verify server allows SSH login:
   ```bash
   ssh -i deploy_key user@host
   ```

### Deploy Fails - Permission Denied
**Cause:** User doesn't have write permissions
**Fix:**
```bash
# On server:
sudo chown -R minecraft:minecraft /opt/minecraft/plugins
sudo chmod 755 /opt/minecraft/plugins
```

### Artifact Not Found
**Cause:** Build failed silently
**Fix:**
1. Check Actions tab for build errors
2. Run locally: `gradle buildAllPlugins`
3. Check Java and Gradle versions

---

## 🔐 Security Best Practices

✅ **DO:**
- Use ed25519 SSH keys (most secure)
- Keep deploy_key private (never commit)
- Use specific deploy user (not root)
- Limit SSH key permissions: `chmod 600`
- Rotate keys periodically

❌ **DON'T:**
- Commit SSH keys to repository
- Use passwords in secrets
- Share DEPLOY_SSH_KEY with others
- Use root user for SSH
- Leave old deploy keys active

---

## 📈 Next Steps

### Optional Enhancements:
- [ ] Add Slack notifications
- [ ] Add Discord notifications
- [ ] Add automated versioning
- [ ] Add code analysis (SonarQube)
- [ ] Add Docker image building
- [ ] Add weekly releases schedule

### Example: Add Slack Notification
```yaml
- name: Notify Slack
  uses: slackapi/slack-github-action@v1
  with:
    webhook-url: ${{ secrets.SLACK_WEBHOOK }}
    payload: |
      {
        "text": "✅ Build ${{ github.run_number }} completed!"
      }
```

---

## 📞 Support

If workflows don't work:
1. Check Actions tab for detailed error logs
2. Review this guide
3. Check GitHub Actions documentation
4. Open an issue with logs from Actions tab

---

**Setup Complete! 🎉**

Your GitHub Actions CI/CD pipeline is ready to:
- ✅ Automatically build on every push
- ✅ Create releases with tags
- ✅ Deploy to your Minecraft server
- ✅ Store artifacts for 90 days
