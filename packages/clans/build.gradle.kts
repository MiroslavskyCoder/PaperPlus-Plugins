plugins {
    java
}

import org.gradle.api.file.DuplicatesStrategy
import org.gradle.jvm.tasks.Jar

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.20.4-R0.1-SNAPSHOT")
    
    // Shared database library
    implementation(project(":common"))
    implementation(project(":universal-gui"))
    implementation(project(":redis-storage"))
}

tasks {
    compileJava {
        options.encoding = "UTF-8"
    }

    jar {
        archiveBaseName.set("clans")
        archiveVersion.set("0.1.0")

        dependsOn(":universal-gui:jar")

        // Shade only universal-gui classes into the plugin JAR so runtime has GuiService/ThemedView
        from(configurations.runtimeClasspath.get()
            .filter { it.name.contains("universal-gui") }
            .map { zipTree(it) }) {
            duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        }
    }
}
