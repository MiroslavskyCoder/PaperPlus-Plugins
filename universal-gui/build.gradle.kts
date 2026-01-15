plugins {
    `java-library`
}

group = "com.webx"
version = "0.1.0"

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.20.4-R0.1-SNAPSHOT")
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}
