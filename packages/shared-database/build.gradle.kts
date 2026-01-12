plugins {
    java
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "com.webx"
version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
}

dependencies {
    // Bukkit/Paper API
    compileOnly("org.bukkit:bukkit:1.20.4-R0.1-SNAPSHOT")
    
    // GSON for JSON
    implementation("com.google.code.gson:gson:2.10.1")
    
    // Shared database library
    implementation(project(":common"))
}

tasks {
    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    shadowJar {
        relocate("com.google.gson", "com.webx.shade.gson")
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

description = "Shared Plugin Database - Centralized JSON storage for all plugins"
