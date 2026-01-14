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
}

tasks {
    compileJava {
        options.encoding = "UTF-8"
    }

    jar {
        archiveBaseName.set("jobs")
        archiveVersion.set("0.1.0")
    }
}
