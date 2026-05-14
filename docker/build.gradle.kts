plugins {
    kotlin("jvm") version "1.9.23"
    application
}

group = "com.calculator"
version = "1.0"

repositories {
    mavenCentral()
}

application {
    mainClass.set("CalculatorKt")
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "CalculatorKt"
    }
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}
