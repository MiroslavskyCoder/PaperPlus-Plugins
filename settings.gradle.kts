rootProject.name = "webx-dashboard"

include("webx-dashboard-panel")
project(":webx-dashboard-panel").projectDir = file("packages/webx-dashboard-panel")

include("webx-dashboard")
project(":webx-dashboard").projectDir = file("packages/webx-dashboard")

include("regionigroks-map")
project(":regionigroks-map").projectDir = file("packages/regionigroks-map")

include("pvp-base")
project(":pvp-base").projectDir = file("packages/pvp-base")
