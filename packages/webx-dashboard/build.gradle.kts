plugins {
    java
}

group = "com.webx"
version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.lucko.me/")
    maven("https://repo.essentialsx.net/releases/")
    maven("https://jitpack.io")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.20.4-R0.1-SNAPSHOT")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7")
    
    // Economy plugin dependency for API integration
    compileOnly(project(":economy"))
    
    // LoaderScript plugin dependency for API integration
    compileOnly(project(":loaderscript"))

    // Javalin for embedded Web Server
    implementation("io.javalin:javalin:6.7.0")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.13.4")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.13.4.2")
    implementation("org.slf4j:slf4j-simple:1.7.36")
    
    // Gson for JSON processing
    implementation("com.google.code.gson:gson:2.10.1")
    
    // Shared database library
    implementation(project(":common"))

    // PostgreSQL and connection pooling
    implementation("org.postgresql:postgresql:42.6.0")
    implementation("com.zaxxer:HikariCP:5.0.1")
    // Password hashing
    implementation("at.favre.lib:bcrypt:0.10.2")
    // JWT
    implementation("io.jsonwebtoken:jjwt-api:0.11.5")
    implementation("io.jsonwebtoken:jjwt-impl:0.11.5")
    implementation("io.jsonwebtoken:jjwt-jackson:0.11.5")

    implementation("redis.clients:jedis:5.1.2") 
    implementation("com.zaxxer:HikariCP:5.1.0")
}

tasks {
    shadowJar {
        archiveClassifier.set("")
        
        // Relocate dependencies to avoid conflicts
        relocate("io.javalin", "com.webx.shaded.javalin")
        relocate("org.eclipse.jetty", "com.webx.shaded.jetty")
        relocate("kotlin", "com.webx.shaded.kotlin")
        relocate("com.fasterxml.jackson", "com.webx.shaded.jackson")
        relocate("org.slf4j", "com.webx.shaded.slf4j")
        relocate("com.google.gson", "com.webx.shaded.gson")
        relocate("org.postgresql", "com.webx.shaded.postgresql")
        relocate("com.zaxxer.hikari", "com.webx.shaded.hikari")
        relocate("at.favre.lib.crypto", "com.webx.shaded.bcrypt")
        relocate("io.jsonwebtoken", "com.webx.shaded.jwt")
        relocate("redis.clients.jedis", "com.webx.shaded.jedis")
        
        manifest {
            attributes["Main-Class"] = "com.webx.PolyglotPlugin"
        }
        
        minimize {
            // Don't minimize these as they're needed at runtime
            exclude(dependency("io.javalin:javalin:.*"))
            exclude(dependency("org.postgresql:postgresql:.*"))
        }
    }
    
    // Use shadowJar instead of jar
    jar {
        enabled = false
        dependsOn(shadowJar)
    }
    
    build {
        dependsOn(shadowJar)
    }
}

// Task to clean old frontend files
tasks.register<Delete>("cleanFrontend") {
    delete("src/main/resources/web")
}

// Task to copy frontend build output to resources
tasks.register<Copy>("copyFrontend") {
    dependsOn("cleanFrontend")
    from("../webx-dashboard-panel/out")
    into("src/main/resources/web")
    dependsOn(":webx-dashboard-panel:bunBuild")
}

// Make sure frontend is built before compiling Java
tasks.compileJava {
    dependsOn("copyFrontend")
}

tasks.processResources {
    dependsOn("copyFrontend")
}

// Clean task should also clean frontend
tasks.clean {
    dependsOn("cleanFrontend")
}
 