plugins {
	// Auto-download JDK toolchains via Foojay resolver
	id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

rootProject.name = "webx-dashboard"

include("webx-dashboard-panel")
project(":webx-dashboard-panel").projectDir = file("packages/webx-dashboard-panel")

include("webx-dashboard")
project(":webx-dashboard").projectDir = file("packages/webx-dashboard")

include("regionigroks-map")
project(":regionigroks-map").projectDir = file("packages/regionigroks-map")

include("pvp-base")
project(":pvp-base").projectDir = file("packages/pvp-base")

include("show-health")
project(":show-health").projectDir = file("packages/show-health")

include(":death-mark")
project(":death-mark").projectDir = File("packages/death-mark")

include(":home-tp")
project(":home-tp").projectDir = File("packages/home-tp")

include(":back-tp")
project(":back-tp").projectDir = File("packages/back-tp")

include(":warps")
project(":warps").projectDir = File("packages/warps")

include(":economy")
project(":economy").projectDir = File("packages/economy")

include(":jobs")
project(":jobs").projectDir = File("packages/jobs")

include(":auction")
project(":auction").projectDir = File("packages/auction")

include(":market")
project(":market").projectDir = File("packages/market")

include(":levels")
project(":levels").projectDir = File("packages/levels")

include(":skills")
project(":skills").projectDir = File("packages/skills")

include(":pets")
project(":pets").projectDir = File("packages/pets")

include(":mounts")
project(":mounts").projectDir = File("packages/mounts")

include(":cosmetics")
project(":cosmetics").projectDir = File("packages/cosmetics")

include(":achievements")
project(":achievements").projectDir = File("packages/achievements")

include(":missions")
project(":missions").projectDir = File("packages/missions")

include(":tournaments")
project(":tournaments").projectDir = File("packages/tournaments")

include(":events")
project(":events").projectDir = File("packages/events")

include(":vaults")
project(":vaults").projectDir = File("packages/vaults")

include(":insurance")
project(":insurance").projectDir = File("packages/insurance")

include(":bounties")
project(":bounties").projectDir = File("packages/bounties")

include(":customitems")
project(":customitems").projectDir = File("packages/customitems")

include(":enchanting")
project(":enchanting").projectDir = File("packages/enchanting")

include(":potions")
project(":potions").projectDir = File("packages/potions")

include(":farming")
project(":farming").projectDir = File("packages/farming")

include(":mining")
project(":mining").projectDir = File("packages/mining")

include(":woodcutting")
project(":woodcutting").projectDir = File("packages/woodcutting")

include(":fishing")
project(":fishing").projectDir = File("packages/fishing")

include(":cooking")
project(":cooking").projectDir = File("packages/cooking")

include(":combat")
project(":combat").projectDir = File("packages/combat")

include(":guilds-advanced")
project(":guilds-advanced").projectDir = File("packages/guilds-advanced")

include(":shop")
project(":shop").projectDir = File("packages/shop")

include(":clans")
project(":clans").projectDir = File("packages/clans")

include(":quests")
project(":quests").projectDir = File("packages/quests")

// 25 new plugins from expansion
include(":afk")
project(":afk").projectDir = File("packages/afk")

include(":antispam")
project(":antispam").projectDir = File("packages/antispam")

include(":feed")
project(":feed").projectDir = File("packages/feed")

include(":leveling")
project(":leveling").projectDir = File("packages/leveling")

include(":statistics")
project(":statistics").projectDir = File("packages/statistics")

include(":marketplace")
project(":marketplace").projectDir = File("packages/marketplace")

include(":petsystem")
project(":petsystem").projectDir = File("packages/petsystem")

include(":partysystem")
project(":partysystem").projectDir = File("packages/partysystem")

include(":chatformatting")
project(":chatformatting").projectDir = File("packages/chatformatting")

include(":customenchants")
project(":customenchants").projectDir = File("packages/customenchants")

include(":claims")
project(":claims").projectDir = File("packages/claims")

include(":backups")
project(":backups").projectDir = File("packages/backups")

include(":news")
project(":news").projectDir = File("packages/news")

include(":pvpevents")
project(":pvpevents").projectDir = File("packages/pvpevents")

include(":miningevents")
project(":miningevents").projectDir = File("packages/miningevents")

include(":jumpquests")
project(":jumpquests").projectDir = File("packages/jumpquests")

include(":guilds")
project(":guilds").projectDir = File("packages/guilds")

include(":bedwarsevent")
project(":bedwarsevent").projectDir = File("packages/bedwarsevent")

include(":seasons")
project(":seasons").projectDir = File("packages/seasons")

include(":skyesurvival")
project(":skyesurvival").projectDir = File("packages/skyesurvival")

include(":speedrun")
project(":speedrun").projectDir = File("packages/speedrun")

include(":randomizer")
project(":randomizer").projectDir = File("packages/randomizer")

include(":dungeonraids")
project(":dungeonraids").projectDir = File("packages/dungeonraids")

include(":homesextended")
project(":homesextended").projectDir = File("packages/homesextended")

include(":playerinfo")
project(":playerinfo").projectDir = File("packages/playerinfo")

include(":worldcolors")
project(":worldcolors").projectDir = File("packages/worldcolors")

include(":autoshutdown")
project(":autoshutdown").projectDir = File("packages/autoshutdown")

include(":simpleheal")
project(":simpleheal").projectDir = File("packages/simpleheal")

include(":deathmessage")
project(":deathmessage").projectDir = File("packages/deathmessage")

include(":mobcatch")
project(":mobcatch").projectDir = File("packages/mobcatch")

include(":friendfeed")
project(":friendfeed").projectDir = File("packages/friendfeed")
include(":ranks")
project(":ranks").projectDir = File("packages/ranks")

include(":modernfix")
project(":modernfix").projectDir = File("packages/modernfix")

include(":hdphysicssound")
project(":hdphysicssound").projectDir = File("packages/hdphysicssound")

include(":create2")
project(":create2").projectDir = File("packages/create2")
