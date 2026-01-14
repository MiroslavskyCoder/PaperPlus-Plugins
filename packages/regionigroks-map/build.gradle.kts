plugins {
    java
}

group = "com.webx.plugins"
version = "0.1.0"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.20.4-R0.1-SNAPSHOT")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.17.1")
    
    // Shared database library
    implementation(project(":common"))
}

// Expand version into plugin.yml
tasks.processResources {
    filesMatching("plugin.yml") {
        expand(mapOf("version" to project.version))
    }
}
