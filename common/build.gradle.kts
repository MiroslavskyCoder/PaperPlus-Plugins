plugins {
    java
}

group = "lxxv"
version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
    maven("https://jitpack.io")
}

dependencies {
    // Paper API (provided by plugins that use this library)
    compileOnly("io.papermc.paper:paper-api:1.20.4-R0.1-SNAPSHOT")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7.1")
    
    // GSON for JSON
    implementation("com.google.code.gson:gson:2.10.1")
    
    // Javet - Java + V8 JavaScript Engine (cross-platform)
    implementation("com.caoccao.javet:javet:3.1.3")
    
    // swc4j - SWC for Java (TypeScript/JSX transpilation, minification)
    implementation("com.caoccao.javet:swc4j:0.8.0")
    
    // GraalVM JavaScript (fallback)
    implementation("org.graalvm.js:js:22.3.0")
    implementation("org.graalvm.js:js-scriptengine:22.3.0")
     

    // Javalin for REST API
    implementation("io.javalin:javalin:6.7.0")
    
    // Logging
    implementation("org.slf4j:slf4j-api:2.0.5")
    implementation("org.slf4j:slf4j-simple:2.0.5")
}

tasks {
    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

sourceSets {
    main {
        java {
            srcDirs("src/main/java")
        }
    }
}

description = "LXXV Common - JavaScript Engine, Database Library, and Server Integration"
