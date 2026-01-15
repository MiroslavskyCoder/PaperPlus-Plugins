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
    implementation(project(":redis-storage"))

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

tasks.jar {
    archiveBaseName.set("webx-dashboard")
    dependsOn(":redis-storage:jar")
    
    manifest {
        attributes["Main-Class"] = "com.webx.PolyglotPlugin"
    }
    
    // Include all dependencies
    from(configurations.runtimeClasspath.get().filter { it.exists() }.map { 
        if (it.isDirectory) it else zipTree(it) 
    }) {
        exclude("META-INF/*.SF")
        exclude("META-INF/*.DSA")
        exclude("META-INF/*.RSA")
    }
    
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
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
 