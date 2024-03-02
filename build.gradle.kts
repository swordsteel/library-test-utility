import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("org.springframework.boot") version "3.2.3"
    id("io.spring.dependency-management") version "1.1.4"

    id("ltd.lulz.plugin.common.library") version "0.1.1"

    kotlin("jvm") version "1.9.22"
    kotlin("plugin.spring") version "1.9.22"
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.springframework.boot:spring-boot-starter")

    testImplementation("org.springframework.boot:spring-boot-starter-test")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.2")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.10.2")

    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.2")
}

description = "Lulz Ltd Test Library Test Utility"
group = "ltd.lulz.library"

tasks.withType<BootJar> { enabled = false }
