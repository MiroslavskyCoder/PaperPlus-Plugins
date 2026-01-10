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

include(":shop")
project(":shop").projectDir = File("packages/shop")

include(":clans")
project(":clans").projectDir = File("packages/clans")

include(":quests")
project(":quests").projectDir = File("packages/quests")
