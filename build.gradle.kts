plugins {
    base
}

// Aggregate task to build all plugins
tasks.register("buildAllPlugins") {
    group = "build"
    description = "Build all plugins"
    dependsOn(
        ":common:build",
        ":shared-database:build",
        ":webx-dashboard:build",
        ":webx-dashboard-panel:bunBuild",
        ":regionigroks-map:build",
        ":pvp-base:build",
        ":show-health:build", 
        ":death-mark:build",
        ":home-tp:build",
        ":back-tp:build",
        ":warps:build",
        ":economy:build",
        ":shop:build",
        ":clans:build",
        ":quests:build",
        ":afk:build",
        ":antispam:build",
        ":feed:build",
        ":leveling:build",
        ":statistics:build",
        ":marketplace:build",
        ":petsystem:build",
        ":partysystem:build",
        ":chatformatting:build",
        ":customenchants:build",
        ":claims:build",
        ":backups:build",
        ":news:build",
        ":pvpevents:build",
        ":miningevents:build",
        ":jumpquests:build",
        ":guilds:build",
        ":bedwarsevent:build",
        ":seasons:build",
        ":skyesurvival:build",
        ":speedrun:build",
        ":randomizer:build",
        ":dungeonraids:build",
        ":homesextended:build",
        ":tournaments:build",
        ":jobs:build",
        ":auction:build",
        ":market:build",
        ":levels:build",
        ":skills:build",
        ":pets:build",
        ":mounts:build",
        ":cosmetics:build",
        ":achievements:build",
        ":missions:build",
        ":events:build",
        ":vaults:build",
        ":insurance:build",
        ":bounties:build",
        ":customitems:build",
        ":enchanting:build",
        ":potions:build",
        ":farming:build",
        ":mining:build",
        ":woodcutting:build",
        ":fishing:build",
        ":cooking:build",
        ":combat:build",
        ":guilds-advanced:build",
        ":playerinfo:build",
        ":worldcolors:build",
        ":autoshutdown:build",
        ":simpleheal:build",
        ":deathmessage:build",
        ":mobcatch:build",
        ":friendfeed:build",
        ":ranks:build",
        ":modernfix:build",
        ":hdphysicssound:build",
        ":create2:build",
        ":loaderscript:build",
        ":horrorenginex:build"
    )
    finalizedBy("copyPlugins")
}

// Alias task "all" for buildAllPlugins
tasks.register("all") {
    group = "build"
    description = "Build all plugins (shorthand for buildAllPlugins)"
    dependsOn("buildAllPlugins")
}

// Task to copy all built plugins to out/plugins
tasks.register<Copy>("copyPlugins") {
    group = "build"
    description = "Copy all built plugin JARs to out/plugins"
    
    from("packages/webx-dashboard/build/libs")
    from("packages/regionigroks-map/build/libs")
    from("packages/pvp-base/build/libs")
    from("packages/show-health/build/libs") 
    from("packages/death-mark/build/libs")
    from("packages/home-tp/build/libs")
    from("packages/back-tp/build/libs")
    from("packages/warps/build/libs")
    from("packages/economy/build/libs")
    from("packages/shop/build/libs")
    from("packages/clans/build/libs")
    from("packages/quests/build/libs")
    from("packages/afk/build/libs")
    from("packages/antispam/build/libs")
    from("packages/feed/build/libs")
    from("packages/leveling/build/libs")
    from("packages/statistics/build/libs")
    from("packages/marketplace/build/libs")
    from("packages/petsystem/build/libs")
    from("packages/partysystem/build/libs")
    from("packages/chatformatting/build/libs")
    from("packages/customenchants/build/libs")
    from("packages/claims/build/libs")
    from("packages/backups/build/libs")
    from("packages/news/build/libs")
    from("packages/pvpevents/build/libs")
    from("packages/miningevents/build/libs")
    from("packages/jumpquests/build/libs")
    from("packages/guilds/build/libs")
    from("packages/bedwarsevent/build/libs")
    from("packages/seasons/build/libs")
    from("packages/skyesurvival/build/libs")
    from("packages/speedrun/build/libs")
    from("packages/randomizer/build/libs")
    from("packages/dungeonraids/build/libs")
    from("packages/homesextended/build/libs")
    from("packages/tournaments/build/libs")
    from("packages/jobs/build/libs")
    from("packages/auction/build/libs")
    from("packages/market/build/libs")
    from("packages/levels/build/libs")
    from("packages/skills/build/libs")
    from("packages/pets/build/libs")
    from("packages/mounts/build/libs")
    from("packages/cosmetics/build/libs")
    from("packages/achievements/build/libs")
    from("packages/missions/build/libs")
    from("packages/events/build/libs")
    from("packages/vaults/build/libs")
    from("packages/insurance/build/libs")
    from("packages/bounties/build/libs")
    from("packages/customitems/build/libs")
    from("packages/enchanting/build/libs")
    from("packages/potions/build/libs")
    from("packages/farming/build/libs")
    from("packages/mining/build/libs")
    from("packages/woodcutting/build/libs")
    from("packages/fishing/build/libs")
    from("packages/cooking/build/libs")
    from("packages/combat/build/libs")
    from("packages/guilds-advanced/build/libs")
    from("packages/playerinfo/build/libs")
    from("packages/worldcolors/build/libs")
    from("packages/autoshutdown/build/libs")
    from("packages/simpleheal/build/libs")
    from("packages/deathmessage/build/libs")
    from("packages/mobcatch/build/libs")
    from("packages/friendfeed/build/libs")
    from("packages/ranks/build/libs")
    from("packages/modernfix/build/libs")
    from("packages/hdphysicssound/build/libs")
    from("packages/create2/build/libs")
    from("packages/loaderscript/build/libs")
    into("out/plugins")
    
    include("*.jar")
    
    doFirst {
        val outDir = file("out/plugins")
        if (!outDir.exists()) {
            outDir.mkdirs()
            println("Created out/plugins directory")
        }
    }
    
    doLast {
        println("✓ Plugins copied to out/plugins/")
        file("out/plugins").listFiles()?.forEach { 
            println("  - ${it.name}")
        }
    }
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
        println("gradle buildAllPlugins          - Build all plugins and copy to out/plugins")
        println("gradle :PROJECT:build           - Build a specific plugin")
        println("gradle :regionigroks-map:build  - Build only regionigroks-map")
        println("gradle :pvp-base:build          - Build only pvp-base")
        println("gradle copyPlugins              - Copy plugin JARs to out/plugins")
        println("gradle clean                    - Clean all build outputs")
        println()
    }
}

// Wrap clean to clean all subprojects
tasks.named("clean") {
    dependsOn(
        ":webx-dashboard:clean",
        ":webx-dashboard-panel:clean",
        ":regionigroks-map:clean",
        ":pvp-base:clean",
        ":show-health:clean", 
        ":death-mark:clean",
        ":home-tp:clean",
        ":warps:clean",
        ":back-tp:clean",
        ":economy:clean",
        ":shop:clean",
        ":clans:clean",
        ":quests:clean",
        ":afk:clean",
        ":antispam:clean",
        ":feed:clean",
        ":leveling:clean",
        ":statistics:clean",
        ":marketplace:clean",
        ":petsystem:clean",
        ":partysystem:clean",
        ":chatformatting:clean",
        ":customenchants:clean",
        ":claims:clean",
        ":backups:clean",
        ":news:clean",
        ":pvpevents:clean",
        ":miningevents:clean",
        ":jumpquests:clean",
        ":guilds:clean",
        ":bedwarsevent:clean",
        ":seasons:clean",
        ":skyesurvival:clean",
        ":speedrun:clean",
        ":randomizer:clean",
        ":dungeonraids:clean",
        ":homesextended:clean",
        ":tournaments:clean",
        ":jobs:clean",
        ":auction:clean",
        ":market:clean",
        ":levels:clean",
        ":skills:clean",
        ":pets:clean",
        ":mounts:clean",
        ":cosmetics:clean",
        ":achievements:clean",
        ":missions:clean",
        ":events:clean",
        ":vaults:clean",
        ":insurance:clean",
        ":bounties:clean",
        ":customitems:clean",
        ":enchanting:clean",
        ":potions:clean",
        ":farming:clean",
        ":mining:clean",
        ":woodcutting:clean",
        ":fishing:clean",
        ":cooking:clean",
        ":combat:clean",
        ":guilds-advanced:clean",
        ":playerinfo:clean",
        ":worldcolors:clean",
        ":autoshutdown:clean",
        ":simpleheal:clean",
        ":deathmessage:clean",
        ":mobcatch:clean",
        ":friendfeed:clean",
        ":ranks:clean",
        ":modernfix:clean",
        ":hdphysicssound:clean",
        ":create2:clean"
    )
}

