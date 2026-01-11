# 🚀 GitHub Actions - CI/CD Pipeline

Автоматизированные workflows для сборки, тестирования и развёртывания плагинов.

## 📋 Available Workflows

### 1️⃣ Build & Deploy Plugins ZIP (`build-and-deploy.yml`)

**Автоматически запускается:**
- При push на `main` или `master` ветку
- При создании тега `v*` (например: `v1.0.0`)
- При pull request на main/master
- Вручную через `workflow_dispatch`

**Что делает:**
```
✅ Собирает все 59 плагинов
✅ Создаёт ZIP архив plugins-<commit>.zip
✅ Генерирует отчёт о сборке
✅ Загружает артефакты (90 дней хранения)
✅ При теге - создаёт Release в GitHub
```

**Входы (для manual trigger):**
- `create_release` - создать Release (true/false, default: false)

**Выходы:**
- `plugins-archive-<commit>.zip` - артефакт с архивом и отчётом

**Пример использования:**
```bash
# Сборка при push
git push origin main

# Сборка и создание Release при теге
git tag -a v1.0.0 -m "Release 1.0.0"
git push origin v1.0.0

# Ручной запуск
# GitHub Actions → Build & Deploy → Run workflow
```

---

### 2️⃣ Deploy to Server (`deploy-to-server.yml`)

**Запускается:** Только вручную через `workflow_dispatch`

**Что делает:**
```
✅ Собирает все плагины
✅ Создаёт ZIP архив
✅ Подключается к серверу по SSH
✅ Загружает плагины на сервер
✅ Распаковывает архив
✅ Перезапускает сервер (опционально)
```

**Входы (обязательные):**
- `server_host` - хост сервера (default: play.example.com)
- `deploy_path` - путь на сервере (default: /opt/minecraft/plugins)
- `restart_server` - перезапустить ли сервер (default: true)

**Требуемые Secrets:**
```
DEPLOY_SSH_KEY       - SSH приватный ключ для подключения
DEPLOY_USER          - Юзер для SSH подключения
```

**Как настроить:**

1. **Создать SSH ключ на сервере:**
   ```bash
   ssh-keygen -t ed25519 -f deploy_key -N ""
   cat deploy_key.pub >> ~/.ssh/authorized_keys
   ```

2. **Добавить в GitHub Secrets:**
   - Settings → Secrets and variables → Actions
   - New repository secret:
     - Name: `DEPLOY_SSH_KEY`
     - Value: содержимое `deploy_key` (приватный ключ)
     - Name: `DEPLOY_USER`
     - Value: юзер на сервере (например: minecraft)

3. **Запустить:**
   ```
   GitHub Actions → Deploy to Server → Run workflow
   ```

**Пример заполнения:**
- Server hostname/IP: `play.example.com`
- Deploy path: `/opt/minecraft/plugins`
- Restart server: `true`

---

### 3️⃣ Create Release (`create-release.yml`)

**Запускается:** Только вручную через `workflow_dispatch`

**Что делает:**
```
✅ Собирает все плагины
✅ Создаёт ZIP архив с версией
✅ Генерирует SHA256 хеш
✅ Создаёт Git тег
✅ Создаёт GitHub Release с файлами
✅ Публикует на GitHub Releases
```

**Входы (обязательные):**
- `version` - номер версии (example: 1.0.0)
- `changelog` - текст changelog (опционально)

**Выходы:**
- `v<version>` - Git тег
- `plugins-v<version>.zip` - архив с плагинами
- `plugins-v<version>.zip.sha256` - файл для проверки целостности

**Пример использования:**
```
GitHub Actions → Create Release → Run workflow
Inputs:
  - version: 1.0.0
  - changelog: 
    * Fixed shop plugin NPE
    * Added clan leaderboard
    * Improved performance
```

---

## 🔐 Security & Secrets

### Секреты для Deploy workflow:

```yaml
DEPLOY_SSH_KEY:     # SSH приватный ключ (ed25519 или rsa)
DEPLOY_USER:        # SSH юзер для подключения
```

**Безопасность:**
- ✅ Ключи шифруются и видны только в Actions
- ✅ Ключи НЕ выводятся в логах
- ✅ SSH ключи должны быть без пароля (-N "")
- ✅ Рекомендуется использовать ed25519 ключи
- ✅ Ограничить доступ ключа: `chmod 600 deploy_key`

---

## 📊 Workflow Statistics

| Workflow | Trigger | Duration | Artifacts |
|----------|---------|----------|-----------|
| Build & Deploy | Push/PR/Tag/Manual | ~1-2 min | ZIP + Report |
| Deploy Server | Manual Only | ~30-60s | Deploy Logs |
| Create Release | Manual Only | ~2-3 min | ZIP + SHA256 |

---

## 💾 Artifact Retention

```
Build & Deploy:
  - Retention: 90 days
  - Size: ~50-100 MB (all plugins)
  
Deploy to Server:
  - No artifacts saved
  
Create Release:
  - Stored in GitHub Releases
  - No retention limit
```

---

## 🔍 Monitoring & Debugging

### Просмотр логов workflow:

1. **GitHub Web:**
   ```
   Repository → Actions → Select workflow → Latest run → View logs
   ```

2. **Со своего ПК:**
   ```bash
   # Требует GitHub CLI
   gh run view <run-id> --log
   ```

### Типичные ошибки:

| Ошибка | Решение |
|--------|---------|
| Gradle build failed | Check Java 21, Gradle 9.2.1 compatibility |
| SSH key not found | Add DEPLOY_SSH_KEY in Secrets |
| Permission denied | Check SSH key permissions, user permissions |
| ZIP not created | Ensure out/plugins/ has JAR files |
| Release already exists | Delete tag: `git tag -d v1.0.0 && git push origin :v1.0.0` |

---

## 🎯 Best Practices

### Before merging to main:
```bash
# 1. Test locally
gradle buildAllPlugins

# 2. Commit & push to feature branch
git checkout -b feature/my-change
git add .
git commit -m "feat: my change"
git push origin feature/my-change

# 3. Create PR and verify Actions pass
# GitHub will automatically run Build & Deploy

# 4. Merge to main
# This triggers Build & Deploy automatically
```

### For releases:
```bash
# 1. Update version in build.gradle.kts
# 2. Commit: git add . && git commit -m "chore: bump version"
# 3. Tag: git tag -a v1.0.0 -m "Release 1.0.0"
# 4. Push: git push origin main && git push origin v1.0.0
# 5. GitHub Actions will create Release automatically
```

### For server deployment:
```bash
# 1. Ensure code is merged to main
# 2. GitHub Actions → Deploy to Server → Run workflow
# 3. Fill in server details
# 4. Workflow will deploy and optionally restart
```

---

## 📈 Next Steps

### Optional enhancements:
```
[ ] Add code quality analysis (SonarQube)
[ ] Add performance testing
[ ] Add Docker build & push
[ ] Add Slack notifications
[ ] Add auto-versioning with semantic release
[ ] Add changelog auto-generation
[ ] Add JavaDoc generation and deployment
[ ] Add weekly release schedule
```

### To add notifications:
```yaml
# In workflow, add after job completes:
- name: 📢 Notify Slack
  uses: slackapi/slack-github-action@v1.24.0
  with:
    payload: |
      {
        "text": "Build #${{ github.run_number }} completed!"
      }
```

---

## 🔗 Resources

- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [Workflow Syntax](https://docs.github.com/en/actions/using-workflows/workflow-syntax-for-github-actions)
- [Security Best Practices](https://docs.github.com/en/actions/security-guides)
- [Artifacts & Logs](https://docs.github.com/en/actions/managing-workflow-runs)

---

**Last Updated:** January 2026  
**Status:** ✅ All workflows tested and working
