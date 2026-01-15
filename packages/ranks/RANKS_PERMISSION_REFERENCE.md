// Permission Node Structure and Reference
// ==========================================

// Format: "rank.<rank_id>.<feature>"
// Example: "rank.vip.fly"

// PERMISSION CATEGORIES:
// ======================

// 1. MOVEMENT PERMISSIONS
// ------------------------
"rank.vip.fly"                    // Allow using /fly command
"rank.vip.speed"                  // Increased movement speed bonus
"rank.vip.teleport"               // Access special teleport commands

// 2. GAMEPLAY PERMISSIONS
// -----------------------
"rank.vip.extended-slots"         // Additional inventory/clan slots
"rank.vip.priority-join"          // Join when server is full
"rank.premium.custom-prefix"      // Ability to set custom chat prefix
"rank.premium.particle-effects"   // Access to cosmetic particle effects
"rank.premium.custom-skin"        // Custom player skin support

// 3. SOCIAL & COMMUNICATION
// -------------------------
"rank.vip.bypass-afk"             // Not kicked for being AFK
"rank.vip.global-chat"            // Access to global chat channels
"rank.premium.custom-color"       // Custom chat message color

// 4. ECONOMY PERMISSIONS
// ----------------------
"rank.vip.market-discount"        // Reduced marketplace prices (5-10% discount)
"rank.premium.market-commission"  // Lower marketplace commission when selling
"rank.premium.bank-interest"      // Interest on bank deposits

// 5. CLAN PERMISSIONS
// -------------------
"rank.vip.extra-clan-slots"       // Additional member slots in clan
"rank.vip.clan-treasure"          // Access to clan treasure chest
"rank.premium.clan-colors"        // Custom clan member colors

// 6. QUEST & MISSIONS
// -------------------
"rank.vip.quest-boost"            // Double quest rewards
"rank.premium.mission-skip"       // Skip missions (limited times per day)

// 7. COSMETICS & APPEARANCE
// -------------------------
"rank.premium.pet-selection"      // Additional pet options
"rank.premium.mount-selection"    // Premium mount skins
"rank.premium.cosmetic-armor"     // Special cosmetic armor effects

// 8. ADMIN & MANAGEMENT
// ----------------------
"rank.admin.manage-ranks"         // Can assign/remove ranks from players
"rank.admin.edit-ranks"           // Can create and edit rank configurations
"rank.admin.view-logs"            // Can view administration logs
"rank.admin.override"             // Override other rank restrictions

// 9. SPECIAL FEATURES
// -------------------
"rank.premium.pet-revive"         // Pet respawn without cost
"rank.premium.mount-speed"        // Increased mount movement speed
"rank.premium.treasure-multiplier" // Increased treasure find rates

// WILDCARD PERMISSIONS:
// =====================

"rank.vip.*"                      // All VIP permissions
"rank.premium.*"                  // All premium permissions
"rank.founder.*"                  // All founder permissions
"*"                               // All permissions (for Founder rank)

// PERMISSION CHECKING EXAMPLES:
// =============================

// Check if player has specific permission:
if (player.hasPermission("rank.vip.fly")) {
    enableFlyForPlayer(player);
}

// Check multiple permissions:
if (player.hasPermission("rank.premium.custom-prefix") ||
    player.hasPermission("rank.premium.*")) {
    allowCustomPrefix(player);
}

// Hierarchy check:
if (player.hasPermission("rank.admin.manage-ranks")) {
    player.setOp(true); // Grant admin powers
}

// DEFAULT RANK HIERARCHY:
// =======================

Member (Base)
├─ No special permissions
├─ Only default game permissions
└─ Prefix: [Member]

VIP (Priority 10)
├─ All VIP permissions
├─ Costs: 5,000 coins
├─ Prefix: [VIP]
└─ Permissions:
   ├─ rank.vip.fly
   ├─ rank.vip.extended-slots
   └─ rank.vip.priority-join

Premium (Priority 20)
├─ All VIP permissions
├─ All Premium permissions
├─ Costs: 15,000 coins
├─ Prefix: [PREMIUM]
└─ Permissions:
   ├─ rank.vip.* (all VIP)
   ├─ rank.premium.custom-prefix
   ├─ rank.premium.particle-effects
   └─ rank.premium.market-commission

Founder (Priority 100)
├─ All permissions (*)
├─ Admin only (not purchasable)
├─ Prefix: [FOUNDER]
└─ Permissions: * (all)

// CUSTOM PERMISSION REGISTRATION:
// ================================

// Register new permission nodes:
rankManager.registerPermission(
    "rank.custom.ability",           // Permission node
    "Description of the ability",    // Human-readable description
    "gameplay"                       // Category
);

// Add permission to rank:
rank.addPermission("rank.custom.ability");

// Check if rank has permission:
boolean hasPermission = rank.hasPermission("rank.custom.ability");

// PERMISSION FEATURES MAPPING:
// =============================

// Features are boolean toggles tied to permissions:

{
  "id": "vip",
  "permissions": [
    "rank.vip.fly",
    "rank.vip.extended-slots"
  ],
  "features": {
    "fly": true,
    "extended-clan-slots": true,
    "particle-effects": false
  }
}

// Check feature status:
if (rank.hasFeature("fly")) {
    enableFlyFeature(player);
}

// INTEGRATION WITH OTHER PLUGINS:
// ================================

// Check rank permission in other plugins:

// In Combat Plugin:
if (player.hasPermission("rank.vip.quest-boost")) {
    rewards *= 2; // Double rewards
}

// In Shop Plugin:
if (player.hasPermission("rank.vip.market-discount")) {
    price *= 0.9; // 10% discount
}

// In Clans Plugin:
if (player.hasPermission("rank.vip.extra-clan-slots")) {
    maxSlots += 5; // 5 additional slots
}

// BEST PRACTICES:
// ===============

✓ Always check permission before granting feature
✓ Use permission categories for organization
✓ Document custom permissions
✓ Use permission wildcards for admin permissions
✓ Test permission inheritance
✓ Log permission denials for debugging
✓ Use consistent naming: "rank.<rankId>.<feature>"

// TESTING PERMISSIONS:
// ====================

// Test in-game:
/rank check                        // Show my rank
/rankinfo vip                      // Show VIP permissions
/rank set Player1 vip              // Assign VIP

// Verify permission:
if (player.hasPermission("rank.vip.fly")) {
    player.sendMessage("You have fly permission!");
}

// Test API:
curl -H "Authorization: Bearer TOKEN" \
  http://localhost:9092/api/v1/ranks/vip

// Should return all permissions for VIP rank

// TROUBLESHOOTING:
// ================

Q: Permission not working?
A: 1. Check rank is assigned: /rank check
   2. Check permission exists: /rankinfo <rankId>
   3. Check rank has permission
   4. Check server has Ranks plugin enabled

Q: Permission denied to admin?
A: 1. Grant rank with admin permissions
   2. Check "rank.admin.*" permission
   3. Verify admin rank exists

Q: Can't purchase rank?
A: 1. Check rank is purchasable
   2. Check player has enough coins
   3. Verify Economy plugin is active
   4. Check Ranks and Economy are integrated

// MIGRATION FROM OTHER SYSTEMS:
// ==============================

// If migrating from LuckPerms:
// 1. Export player data
// 2. Create equivalent Ranks
// 3. Map LuckPerms groups to Ranks
// 4. Use Ranks API to bulk assign
// 5. Test thoroughly before going live

// Example mapping:
LuckPerms Group  →  Ranks System
member           →  member
vip              →  vip
premium          →  premium
admin            →  founder

// Bulk migration script:
for each player in database:
    legacyRank = getLuckPermsGroup(player)
    newRank = mapToRanksSystem(legacyRank)
    rankService.setPlayerRank(player.uuid, newRank, "SYSTEM", "Migrated from LuckPerms")

---

**Permission Node Reference v1.0**
**Updated: January 2026**
**For: Ranks 1.0.0+**
