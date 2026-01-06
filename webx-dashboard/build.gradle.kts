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

    // Javalin for embedded Web Server
    implementation("io.javalin:javalin:6.7.0")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.13.4")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.13.4.2")
    implementation("org.slf4j:slf4j-simple:1.7.36")

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
    implementation("org.postgresql:postgresql:42.6.0") 
    implementation("com.zaxxer:HikariCP:5.1.0")
} 
tasks.jar {
    manifest {
        attributes["Main-Class"] = "com.webx.PolyglotPlugin"
    }
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

// Task to copy frontend build output to resources
tasks.register<Copy>("copyFrontend") {
    from("../frontend-panel/out")
    into("src/main/resources/web")
    dependsOn(":frontend-panel:bunBuild")
}

// Make sure frontend is built before compiling Java
tasks.compileJava {
    dependsOn("copyFrontend")
}

tasks.processResources {
    dependsOn("copyFrontend")
}
 