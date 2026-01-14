# 🎖️ Ranks & Permissions System (Система Рангов и Прав Доступа)

Complete rank and permission management system for Minecraft servers with Web-Dashboard integration.

## ✨ Features

### 🏆 Rank System
- **Purchasable Ranks**: Buy ranks with in-game coins via Economy integration
- **Admin-Assigned Ranks**: Administrators can manually assign ranks
- **Temporary Ranks**: Time-limited ranks with automatic expiration
- **Rank Inheritance**: Support for primary and additional ranks
- **Rank History**: Track all rank assignments and changes

### 🎨 Customization
- **Prefix/Suffix System**: Colorful chat prefixes with color code support (&a, &b, etc.)
- **Tab List Display**: Custom player list format with rank indicators
- **Chat Format**: Configurable rank display in chat messages
- **Priorities**: Display highest priority rank (prevents rank overlap)

### 🔐 Permission Management
- **Node-Based System**: Standard permission nodes (rank.vip.fly, rank.premium.custom-prefix)
- **Permission Categories**: Organize permissions by type (movement, gameplay, economy, admin)
- **Feature Toggles**: Enable/disable features per rank (fly, extended-slots, particle-effects)
- **Default Permissions**: Mark permissions available to all players

### 📊 Administration
- **Web Dashboard**: Full admin panel for rank management
- **REST API**: Complete API for programmatic access
- **Automatic Expiry**: Built-in rank expiration checking
- **Statistics**: View rank distribution and player statistics

## 📦 Installation

1. Build the ranks package:
```bash
gradle clean buildAllPlugins
```

2. Copy `ranks.jar` to your server's `plugins/` folder

3. Restart server to generate configuration files

## 🎮 Commands

### Player Commands
```bash
/rank check [player]      # View player's current rank
/ranklist                 # List all available ranks
/rankinfo <rankId>        # View detailed rank information
```

### Admin Commands
```bash
/rank set <player> <rankId> [reason]     # Assign permanent rank
/rank give <player> <rankId> [hours]     # Assign temporary rank
/rank remove <player>                    # Remove rank
/rank create <id> <name> [priority]      # Create new rank
/rank edit <rankId> <property> <value>   # Edit rank property
```

## 📋 Permissions

### Player Permissions
```yaml
rank.vip.fly                    # Access to /fly command
rank.vip.extended-slots         # Additional inventory/clan slots
rank.vip.priority-join          # Join when server is full
rank.vip.bypass-afk             # Not kicked for AFK
rank.vip.global-chat            # Access to global chat
rank.vip.market-discount        # Reduced marketplace prices
rank.premium.custom-prefix      # Custom chat prefix
rank.premium.particle-effects   # Cosmetic particle effects
rank.premium.market-commission  # Lower marketplace commission
```

### Admin Permissions
```yaml
rank.admin.manage-ranks         # Assign/remove ranks
rank.admin.edit-ranks           # Create/edit rank configurations
rank.admin.view-logs            # View admin action logs
```

## 🏠 Default Ranks

### Member
- **Base rank**: Free, assigned to all players
- **Prefix**: `&7[Member]&r` (gray)
- **Purchasable**: No
- **Price**: N/A

### VIP
- **Priority**: 10
- **Prefix**: `&b[VIP]&r` (cyan)
- **Price**: 5,000 coins
- **Permissions**:
  - rank.vip.fly
  - rank.vip.extended-slots
  - rank.vip.priority-join
- **Features**:
  - Fly enabled
  - Extended clan slots

### Premium
- **Priority**: 20
- **Prefix**: `&6[PREMIUM]&r` (gold)
- **Price**: 15,000 coins
- **Permissions**: All VIP + custom-prefix + particle-effects
- **Features**: All VIP + custom-prefix + particle-effects

### Founder
- **Priority**: 100
- **Prefix**: `&c[FOUNDER]&r` (red)
- **Not purchasable**: Admin-only
- **Permissions**: `*` (all permissions)

## 🛒 Economy Integration

### Rank Purchase Flow
```
Player executes: /shop
Shop shows purchasable ranks
Player clicks on rank
System deducts coins from wallet
Rank assigned automatically
Player receives confirmation
```

### Configuration in shop.json
```json
{
  "id": "vip_rank",
  "name": "VIP Rank",
  "material": "EMERALD_BLOCK",
  "amount": 1,
  "price": 5000.0,
  "lore": [
    "Access to VIP features",
    "Get /fly command",
    "Extended clan slots"
  ],
  "permission": "shop.buy.ranks"
}
```

## 🌐 Web Dashboard API

### Base URL
```
http://localhost:9092/api/v1/ranks
```

### Rank Endpoints

#### Get all ranks
```bash
GET /api/v1/ranks
```

Response:
```json
{
  "success": true,
  "message": "Ranks retrieved successfully",
  "data": [
    {
      "id": "vip",
      "displayName": "VIP",
      "priority": 10,
      "prefix": "&b[VIP]&r",
      "purchasable": true,
      "purchasePrice": 5000,
      "permissions": ["rank.vip.fly", "rank.vip.extended-slots"],
      "features": {
        "fly": true,
        "extended-clan-slots": true
      }
    }
  ]
}
```

#### Get specific rank
```bash
GET /api/v1/ranks/{rankId}
```

#### Update rank
```bash
PUT /api/v1/ranks/{rankId}
Content-Type: application/json

{
  "displayName": "VIP Member",
  "prefix": "&b[VIP]&r",
  "purchasable": true,
  "purchasePrice": 5000
}
```

#### Delete rank
```bash
DELETE /api/v1/ranks/{rankId}
```

### Player Rank Endpoints

#### Get player's rank
```bash
GET /api/v1/ranks/players/{playerUUID}
```

Response:
```json
{
  "success": true,
  "data": {
    "playerId": "12345678-1234-1234-1234-123456789012",
    "playerName": "PlayerName",
    "primaryRank": "vip",
    "additionalRanks": [],
    "assignedAt": 1704067200000,
    "assignedBy": "AdminName",
    "reason": "Purchased from shop",
    "expiresAt": 0,
    "active": true
  }
}
```

#### Assign rank to player
```bash
POST /api/v1/ranks/players/{playerUUID}/assign
Content-Type: application/json

{
  "rankId": "vip",
  "assignedBy": "AdminName",
  "reason": "Purchased from shop"
}
```

#### Remove player's rank
```bash
POST /api/v1/ranks/players/{playerUUID}/remove
```

### Statistics Endpoints

#### Get rank distribution
```bash
GET /api/v1/ranks/statistics/distribution
```

Response:
```json
{
  "success": true,
  "data": {
    "member": 145,
    "vip": 23,
    "premium": 8,
    "founder": 1
  }
}
```

## 🎨 Web Dashboard Components

### Ranks Management Tab
- View all ranks in interactive table
- Create new ranks with full configuration
- Edit rank properties (prefix, price, permissions)
- Delete ranks (with protection for default ranks)
- Real-time updates

### Player Ranks Tab
- Search players by name
- Assign ranks to players
- View player rank history
- Set temporary ranks (with duration)
- Monitor rank expiration
- Remove ranks from players

## 💾 Configuration Files

### ranks.json
```json
{
  "vip": {
    "id": "vip",
    "displayName": "VIP",
    "priority": 10,
    "prefix": "&b[VIP]&r",
    "suffix": "",
    "permissions": ["rank.vip.fly", "rank.vip.extended-slots"],
    "purchasePrice": 5000,
    "purchasable": true,
    "features": {
      "fly": true,
      "extended-clan-slots": true
    }
  }
}
```

### permissions.json
```json
[
  {
    "node": "rank.vip.fly",
    "description": "Allow flying with fly command",
    "category": "movement",
    "isDefault": false
  }
]
```

### player-ranks.json
```json
{
  "12345678-1234-1234-1234-123456789012": {
    "playerId": "12345678-1234-1234-1234-123456789012",
    "playerName": "PlayerName",
    "primaryRank": "vip",
    "additionalRanks": [],
    "assignedAt": 1704067200000,
    "assignedBy": "Admin",
    "reason": "Purchased",
    "expiresAt": 0,
    "active": true
  }
}
```

## 🔧 Advanced Usage

### Custom Rank with Permissions

```java
// Via command
/rank create elite Elite 50
/rank edit elite prefix &5[ELITE]&r
/rank edit elite price 25000
/rank edit elite permission rank.elite.custom-effects
/rank edit elite permission rank.elite.priority-spawn
```

### Temporary Rank Assignment

```bash
# Give rank for 24 hours
/rank give PlayerName premium 24

# Rank will expire automatically after 24 hours
# Player receives warning message 24 hours before expiry
```

### Economy Integration

Ranks plugin automatically hooks into Economy plugin:
- Coins deducted automatically on purchase
- Purchase history tracked
- Refunds supported through web dashboard
- Transaction logging

## 📚 Integration with Other Plugins

### With Clans Plugin
- Check if player has VIP to give extended clan slots
- Display rank in clan member list
- Apply rank color to clan chat

### With Shop Plugin
- Add rank items to shop inventory
- Automatic coin deduction and rank assignment
- Purchase confirmation messages

### With Chat Formatting Plugin
- Use rank prefix in formatted messages
- Color coordination with other plugins
- Priority handling for multiple tags

## 🔍 Troubleshooting

### Ranks not appearing in chat
1. Check player has active rank: `/rank check <player>`
2. Verify rank has prefix configured: `/rankinfo vip`
3. Restart server to reload event listeners

### Economy integration not working
1. Ensure Economy plugin is installed and enabled
2. Check console for integration errors
3. Verify player has sufficient coins

### Web Dashboard not showing ranks
1. Verify webx-dashboard is running on port 9092
2. Check API endpoints are registered
3. Review dashboard logs for errors

## 📝 Examples

### Create Custom Rank with Features

```bash
# Create Noble rank
/rank create noble Noble 25

# Add permissions
/rank edit noble permission rank.noble.custom-prefix
/rank edit noble permission rank.noble.colored-chat
/rank edit noble permission rank.noble.special-items

# Set as purchasable
/rank edit noble price 10000
```

### Manage Player Ranks

```bash
# Set permanent rank
/rank set Player1 vip "Reward for being active"

# Give temporary VIP (7 days)
/rank give Player1 vip 168

# Remove rank
/rank remove Player1

# Check their rank
/rank check Player1
```

### API: Bulk Rank Assignment

```bash
#!/bin/bash
PLAYERS=("uuid1" "uuid2" "uuid3")

for uuid in "${PLAYERS[@]}"; do
  curl -X POST "http://localhost:9092/api/v1/ranks/players/$uuid/assign" \
    -H "Content-Type: application/json" \
    -d '{"rankId":"vip","assignedBy":"System","reason":"Monthly reward"}'
done
```

## 📈 Statistics & Monitoring

The system provides comprehensive statistics through the Web Dashboard:
- Total players with ranks
- Rank distribution chart
- Active vs inactive ranks
- Expiring ranks list
- Assignment history timeline

## 🚀 Performance

- **Asynchronous operations**: Rank loading/saving doesn't block server
- **Caching**: Ranks cached in memory for fast access
- **Efficient searches**: O(1) rank lookups by ID
- **Scalable**: Supports thousands of players and ranks

## 📄 License

Part of the My Polyglot Project ecosystem.

## 💬 Support

For issues and feature requests, check the project's GitHub repository.
