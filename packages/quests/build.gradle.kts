plugins {
    java
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.20.4-R0.1-SNAPSHOT")
    
    // Shared database library
    implementation(project(":common"))
    
    // Javalin for REST API
    implementation("io.javalin:javalin:5.6.2")
    
    // GSON for JSON
    implementation("com.google.code.gson:gson:2.10.1")
    
    // SLF4J for logging
    implementation("org.slf4j:slf4j-simple:2.0.9")
}

tasks {
    compileJava {
        options.encoding = "UTF-8"
    }

    jar {
        archiveBaseName.set("quests")
        archiveVersion.set("0.1.0")
    }
}
