plugins {
    java
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "com.webx"
version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://jitpack.io")
}

dependencies {
    // Paper API
    compileOnly("io.papermc.paper:paper-api:1.20.4-R0.1-SNAPSHOT")
    
    // LXXV Common (JavaScript engine + LXXVServer)
    implementation(project(":common"))
    
    // Javalin for REST API
    implementation("io.javalin:javalin:5.6.2")
    
    // GSON for JSON
    implementation("com.google.code.gson:gson:2.10.1")
    
    // Logging
    implementation("org.slf4j:slf4j-api:2.0.5")
    implementation("org.slf4j:slf4j-simple:2.0.5")
}

tasks {
    shadowJar {
        archiveClassifier.set("")
        
        // Relocate dependencies to avoid conflicts
        relocate("io.javalin", "com.webx.loaderscript.libs.javalin")
        relocate("org.eclipse.jetty", "com.webx.loaderscript.libs.jetty")
        relocate("kotlin", "com.webx.loaderscript.libs.kotlin")
        
        // Include LXXV Common
        dependencies {
            include(project(":common"))
        }
    }
    
    build {
        dependsOn(shadowJar)
    }
    
    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}
