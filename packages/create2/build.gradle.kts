plugins {
    id("java")
}

group = "com.webx"
version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.20.4-R0.1-SNAPSHOT")
    implementation("org.joml:joml:1.10.5")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

tasks {
    jar {
        archiveBaseName.set("Create2")
        archiveVersion.set(version.toString())
    }
    
    processResources {
        filesMatching("plugin.yml") {
            expand("version" to version)
        }
    }
}
