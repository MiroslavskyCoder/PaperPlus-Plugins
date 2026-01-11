plugins {
    id("base")
}

tasks.register<Exec>("bunInstall") {
    workingDir = file(".")
    commandLine("bun", "install")
}

tasks.register<Exec>("bunBuild") {
    workingDir = file(".")
    commandLine("bun", "run", "bunBuild")
    dependsOn("bunInstall")
}