plugins {
    java
}

group = "com.webx"
version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.20.4-R0.1-SNAPSHOT")
    
    // Gson for JSON processing
    implementation("com.google.code.gson:gson:2.10.1")
    
    // Shared database library
    implementation(project(":common"))
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "com.webx.ranks.RanksPlugin"
    }
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}
