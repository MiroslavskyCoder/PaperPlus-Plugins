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
 
include(":abomination")
project(":abomination").projectDir = File("packages/abomination")

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
