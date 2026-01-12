plugins {
    java
}

group = "lxxv"
version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
}

dependencies {
    // Bukkit/Paper API (provided by plugins that use this library)
    compileOnly("org.bukkit:bukkit:1.20.4-R0.1-SNAPSHOT")
    
    // GSON for JSON
    implementation("com.google.code.gson:gson:2.10.1")
    
    // V8 JavaScript Engine
    implementation("com.eclipsesource.j2v8:j2v8_linux_x86_64:6.2.0")
    implementation("com.eclipsesource.j2v8:j2v8_win32_x86_64:6.2.0")
    implementation("com.eclipsesource.j2v8:j2v8_macosx_x86_64:6.2.0")
    
    // GraalVM JavaScript (alternative/backup)
    implementation("org.graalvm.js:js:22.3.0")
    implementation("org.graalvm.js:js-scriptengine:22.3.0")
}

tasks {
    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

description = "LXXV Shared Database - Centralized JSON storage library for all plugins"
