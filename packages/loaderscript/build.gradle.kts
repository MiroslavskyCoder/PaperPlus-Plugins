plugins {
    java
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
    
    // Javalin for REST API (latest version)
    implementation("io.javalin:javalin:6.7.0")
    
    // GSON for JSON
    implementation("com.google.code.gson:gson:2.10.1")
    
    // Logging
    implementation("org.slf4j:slf4j-api:2.0.5")
    implementation("org.slf4j:slf4j-simple:2.0.5")
}

tasks.jar {
    archiveBaseName.set("loaderscript")
    
    manifest {
        attributes["Main-Class"] = "com.webx.loaderscript.LoaderScriptPlugin"
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

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}
