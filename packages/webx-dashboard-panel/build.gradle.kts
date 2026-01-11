plugins {
    id("base")
}

tasks.register<Exec>("bunBuild") {
    workingDir = file(".")
    commandLine("bun", "run", "bunBuild")
}