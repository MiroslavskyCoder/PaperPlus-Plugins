# Ranks & Permissions System Integration Guide

## 📋 Overview

This guide explains how to integrate the Ranks & Permissions System into your existing Minecraft server ecosystem.

## 🔗 Integration Points

### 1. **Web-Dashboard Integration** ✅

The Ranks system is fully integrated with the Web Dashboard through REST API endpoints.

#### Location
- **Service**: `packages/webx-dashboard/src/main/java/com/webx/api/services/RankService.java`
- **Endpoints**: `packages/webx-dashboard/src/main/java/com/webx/api/endpoints/RanksEndpoints.java`

#### Registration

The RanksEndpoints must be registered in the API router. Update `packages/webx-dashboard/src/main/java/com/webx/api/API.java`:

```java
// Add import
import com.webx.api.endpoints.RanksEndpoints;
import com.webx.api.services.RankService;

// In your API initialization method, add:
RankService rankService = new RankService();
new RanksEndpoints(rankService).register(app);
```

#### Available Routes
- `GET /api/v1/ranks` - List all ranks
- `GET /api/v1/ranks/{rankId}` - Get rank details
- `PUT /api/v1/ranks/{rankId}` - Update rank
- `DELETE /api/v1/ranks/{rankId}` - Delete rank
- `GET /api/v1/ranks/players/{playerUUID}` - Get player rank
- `POST /api/v1/ranks/players/{playerUUID}/assign` - Assign rank
- `POST /api/v1/ranks/players/{playerUUID}/remove` - Remove rank
- `GET /api/v1/ranks/statistics/distribution` - Rank statistics

### 2. **Economy Integration** ✅

Ranks automatically integrate with the Economy plugin for rank purchases.

#### Location
- **Integration Class**: `packages/ranks/src/main/java/com/webx/ranks/economy/RankShopIntegration.java`

#### How It Works

```
Player executes /shop
  ↓
Shop displays purchasable ranks (from Ranks plugin)
  ↓
Player clicks on rank item
  ↓
RankShopIntegration.purchaseRank() is called
  ↓
System checks if player has enough coins
  ↓
Coins are deducted from wallet
  ↓
Rank is assigned to player
  ↓
Confirmation message displayed
```

#### Integration in Shop Plugin

Update the Shop plugin to include ranks. In `packages/shop/src/main/java/com/webx/shop/ShopPlugin.java`:

```java
// Add method to load purchasable ranks
public void loadPurchasableRanks() {
    try {
        Plugin ranksPlugin = Bukkit.getPluginManager().getPlugin("Ranks");
        if (ranksPlugin != null && ranksPlugin.isEnabled()) {
            // Get ranks via reflection
            Method getRankManagerMethod = ranksPlugin.getClass().getMethod("getRankManager");
            Object rankManager = getRankManagerMethod.invoke(ranksPlugin);
            
            Method getAllRanksMethod = rankManager.getClass().getMethod("getAllRanks");
            Map<String, Object> ranks = (Map<String, Object>) getAllRanksMethod.invoke(rankManager);
            
            // Convert to shop items and add to inventory
            for (Map.Entry<String, Object> entry : ranks.entrySet()) {
                // Create shop item from rank
            }
        }
    } catch (Exception e) {
        getLogger().warning("Failed to load purchasable ranks: " + e.getMessage());
    }
}
```

### 3. **Chat Formatting Integration**

The system provides rank prefixes for chat formatting.

#### Location
- **Event Listener**: `packages/ranks/src/main/java/com/webx/ranks/RankEventListener.java`

#### How to Use in Other Plugins

```java
// Get player's rank prefix
Plugin ranksPlugin = Bukkit.getPluginManager().getPlugin("Ranks");
if (ranksPlugin != null && ranksPlugin.isEnabled()) {
    Method getRankManagerMethod = ranksPlugin.getClass().getMethod("getRankManager");
    Object rankManager = getRankManagerMethod.invoke(ranksPlugin);
    
    Method formatPrefixMethod = rankManager.getClass()
            .getMethod("formatPrefix", Object.class);
    String prefix = (String) formatPrefixMethod.invoke(rankManager, playerRank);
    
    // Use prefix in your chat message
    String chatMessage = prefix + " " + player.getName() + ": " + message;
}
```

### 4. **Clans Integration**

Display rank information in clan messages and member lists.

#### Expected Usage in Clans Plugin

```java
// In clan member display
public String formatClanMember(OfflinePlayer player) {
    String rankId = playerRankManager.getPlayerPrimaryRank(player.getUniqueId());
    String prefix = rankManager.formatPrefix(rankManager.getRank(rankId));
    return prefix + " " + player.getName();
}
```

### 5. **Permission Checking**

Use the Ranks system for custom permission checks.

#### Basic Permission Check

```java
public boolean hasRankPermission(Player player, String permissionNode) {
    Plugin ranksPlugin = Bukkit.getPluginManager().getPlugin("Ranks");
    if (ranksPlugin != null && ranksPlugin.isEnabled()) {
        try {
            Method getPlayerRankManagerMethod = ranksPlugin.getClass()
                    .getMethod("getPlayerRankManager");
            Object playerRankManager = getPlayerRankManagerMethod.invoke(ranksPlugin);
            
            Method getRankManagerMethod = ranksPlugin.getClass()
                    .getMethod("getRankManager");
            Object rankManager = getRankManagerMethod.invoke(ranksPlugin);
            
            // Get player's primary rank
            Method getPlayerPrimaryRankMethod = playerRankManager.getClass()
                    .getMethod("getPlayerPrimaryRank", UUID.class);
            String rankId = (String) getPlayerPrimaryRankMethod
                    .invoke(playerRankManager, player.getUniqueId());
            
            // Get rank object
            Method getRankMethod = rankManager.getClass()
                    .getMethod("getRank", String.class);
            Object rank = getRankMethod.invoke(rankManager, rankId);
            
            // Check permission
            Method hasPermissionMethod = rank.getClass()
                    .getMethod("hasPermission", String.class);
            return (boolean) hasPermissionMethod.invoke(rank, permissionNode);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    return false;
}
```

## 🚀 Enabling the System

### Step 1: Build the Project

```bash
gradle clean buildAllPlugins
```

### Step 2: Copy to Server

Copy these files to `plugins/`:
- `build/libs/ranks-1.0.0.jar`
- Ensure `webx-dashboard-1.0.0.jar` and `economy-*.jar` are also present

### Step 3: Start Server

The server will automatically create configuration files:
```
plugins/
├── Ranks/
│   ├── ranks.json              # Rank definitions
│   ├── permissions.json        # Permission registry
│   └── player-ranks/
│       └── player-ranks.json   # Player rank assignments
```

### Step 4: Verify Installation

Check console for messages:
```
✅ Ranks & Permissions System enabled!
✅ RankService initialized successfully
```

## 🔧 Configuration

### Creating Custom Ranks in-game

```bash
# Create a new rank
/rank create elite Elite 50

# Configure it
/rank edit elite prefix &5[ELITE]&r
/rank edit elite price 10000
/rank edit elite permission rank.elite.custom-effects
```

### Setting Default Ranks

Edit `plugins/Ranks/ranks.json`:

```json
{
  "member": {
    "id": "member",
    "displayName": "Member",
    "priority": 1,
    "prefix": "&7[Member]&r",
    "permissions": [],
    "features": {},
    "purchasable": false
  }
}
```

## 📊 Dashboard Setup

### Adding Tabs to Dashboard

Edit `packages/webx-dashboard-panel/src/app/dashboard/page.tsx`:

```tsx
import { RanksTab } from "@/components/dashboard/ranks-tab";
import { PlayerRanksTab } from "@/components/dashboard/player-ranks-tab";

export default function DashboardPage() {
  return (
    <Drawer>
      <Tabs defaultValue="ranks">
        <TabsList>
          <TabsTrigger value="ranks">Ranks</TabsTrigger>
          <TabsTrigger value="player-ranks">Player Ranks</TabsTrigger>
        </TabsList>
        
        <TabsContent value="ranks">
          <RanksTab />
        </TabsContent>
        
        <TabsContent value="player-ranks">
          <PlayerRanksTab />
        </TabsContent>
      </Tabs>
    </Drawer>
  );
}
```

## 🔌 Plugin Dependencies

### Required Plugins
- **Paper Server 1.20.4+** - Base server software
- **webx-dashboard** - REST API and web server
- **economy** - Coin system for rank purchases

### Optional Plugins
- **clans** - Display ranks in clan contexts
- **shop** - Sell purchasable ranks
- **chat-formatting** - Use rank prefixes in chat

### Dependency Declaration

In `packages/ranks/build.gradle.kts`:

```gradle
dependencies {
    compileOnly("io.papermc.paper:paper-api:1.20.4-R0.1-SNAPSHOT")
    
    // Gson for JSON processing
    implementation("com.google.code.gson:gson:2.10.1")
    
    // Optional: Add Economy dependency for compile-time checking
    // compileOnly(project(":economy"))
}
```

## 🧪 Testing

### Test Commands

```bash
# Create test rank
/rank create test Test 5

# Test assignment
/rank set PlayerName test "Testing rank system"

# Verify
/rank check PlayerName

# Test API endpoint
curl http://localhost:9092/api/v1/ranks

# Test player endpoint
curl http://localhost:9092/api/v1/ranks/players/12345678-1234-1234-1234-123456789012
```

### Integration Tests

```bash
# Test economy integration
/shop
# Click on purchasable rank

# Check if coins were deducted
/balance

# Check rank assignment
/rank check

# Verify in dashboard
# Open http://localhost:9092/dashboard
```

## 📈 Monitoring & Maintenance

### Check System Health

```bash
# View server console for startup messages
# Should see "✅ Ranks & Permissions System enabled!"

# Test API connectivity
curl http://localhost:9092/api/v1/ranks

# Check rank distribution
curl http://localhost:9092/api/v1/ranks/statistics/distribution
```

### Regular Maintenance

1. **Backup Rank Data**: Regularly backup `plugins/Ranks/`
2. **Monitor Expiring Ranks**: System automatically removes expired ranks
3. **Review Permissions**: Update permission nodes as features are added

## 🐛 Troubleshooting

### Ranks not showing in dashboard

**Solution**: Ensure `RankService` is initialized in `webx-dashboard/API.java`

```java
RankService rankService = new RankService();
new RanksEndpoints(rankService).register(app);
```

### Economy integration not working

**Solution**: Verify Economy plugin is installed and enabled

```bash
# In console, should see:
# ✅ RankShopIntegration initialized with Economy plugin
```

### Players not getting rank prefixes

**Solution**: Ensure `RankEventListener` is registered

```java
// In RanksPlugin.java
getServer().getPluginManager().registerEvents(
    new RankEventListener(rankManager, playerRankManager), this);
```

## 🔐 Security Considerations

1. **Admin Permissions**: Only admins can create/edit ranks
   - `rank.admin.manage-ranks` - Assign ranks
   - `rank.admin.edit-ranks` - Create/edit rank configs

2. **API Authentication**: Implement dashboard authentication before production

3. **Data Validation**: All inputs are validated before processing

## 📚 Related Documentation

- [Ranks System Documentation](./RANKS_DOCUMENTATION.md)
- [Web Dashboard Documentation](../webx-dashboard/README.md)
- [Economy Plugin Documentation](../economy/README.md)

## 📞 Support

For issues:
1. Check the logs in `plugins/Ranks/logs/`
2. Review configuration files for errors
3. Test with single rank first
4. Verify all plugins are compatible versions

---

**Last Updated**: January 2026
**Compatible Versions**: Paper 1.20.4+, Ranks 1.0.0+
