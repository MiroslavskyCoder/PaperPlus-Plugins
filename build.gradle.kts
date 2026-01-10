// Root build file

plugins {
    base
}

// Aggregate task to build all plugins
tasks.register("buildAllPlugins") {
    group = "build"
    description = "Build all PvP and region plugins"
    dependsOn(
        ":webx-dashboard:build",
        ":webx-dashboard-panel:bunBuild",
        ":regionigroks-map:build",
        ":pvp-base:build"
    )
}

// Task to list all available projects
tasks.register("listProjects") {
    group = "help"
    description = "List all subprojects"
    doLast {
        println("\n=== Available Subprojects ===")
        println(":webx-dashboard       - Main Java plugin with web server and dashboard")
        println(":webx-dashboard-panel - Next.js frontend dashboard (npm/bun)")
        println(":regionigroks-map     - Region management and minimap plugin")
        println(":pvp-base             - PvP game modes: SkyWars, BedWars, Duels, Siege")
        println("\n=== Build Commands ===")
        println("gradle buildAllPlugins          - Build all plugins")
        println("gradle :PROJECT:build           - Build a specific plugin")
        println("gradle :regionigroks-map:build  - Build only regionigroks-map")
        println("gradle :pvp-base:build          - Build only pvp-base")
        println("gradle clean                    - Clean all build outputs")
        println()
    }
}

// Wrap clean to clean all subprojects
tasks.clean {
    dependsOn(
        ":webx-dashboard:clean",
        ":webx-dashboard-panel:clean",
        ":regionigroks-map:clean",
        ":pvp-base:clean"
    )
}
