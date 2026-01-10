rootProject.name = "webx-dashboard"

include("webx-dashboard-panel")
project(":webx-dashboard-panel").projectDir = file("packages/webx-dashboard-panel")

include("webx-dashboard")
project(":webx-dashboard").projectDir = file("packages/webx-dashboard")

include("regionigroks-map")
project(":regionigroks-map").projectDir = file("packages/regionigroks-map")

include("pvp-base")
project(":pvp-base").projectDir = file("packages/pvp-base")

include(":from-drop")
project(":from-drop").projectDir = File("packages/from-drop")

include("show-health")
project(":show-health").projectDir = file("packages/show-health")
 
include(":abomination")
project(":abomination").projectDir = File("packages/abomination")

include(":dance")
project(":dance").projectDir = File("packages/dance")

include(":death-mark")
project(":death-mark").projectDir = File("packages/death-mark")
