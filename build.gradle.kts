
plugins {
    id("org.jetbrains.kotlin.jvm") version "1.8.10"
    id("java")
}

group = "org.example"
version = "0.1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
    implementation("org.xerial:sqlite-jdbc:3.46.0.0")
    implementation("org.slf4j:slf4j-nop:1.7.36")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")

    testImplementation(files("C:\\Users\\PhoenixShell\\Documents\\Projects\\Sentry\\.dist\\sentry-0.1.0.jar"))

    testRuntimeOnly ("org.junit.jupiter:junit-jupiter-engine:5.8.1")

}

tasks.test {
    useJUnitPlatform()
}

tasks.jar {
    archiveBaseName.set("akari")
}