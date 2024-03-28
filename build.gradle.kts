import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    alias(lulz.plugins.org.springframework.boot)
    alias(lulz.plugins.io.spring.dependency.management)

    alias(lulz.plugins.ltd.lulz.plugin.common.library)

    alias(lulz.plugins.kotlin.jvm)
    alias(lulz.plugins.kotlin.spring)
}

dependencies {
    implementation(lulz.org.jetbrains.kotlin.reflect)
    implementation(lulz.org.springframework.spring.boot.starter)

    testImplementation(lulz.org.junit.jupiter.api)
    testImplementation(lulz.org.junit.jupiter.params)
    testImplementation(lulz.org.springframework.spring.boot.starter.test)

    testRuntimeOnly(lulz.org.junit.platform.launcher)
}

description = "Lulz Ltd Test Library Test Utility"
group = "ltd.lulz.library"

tasks.withType<BootJar> { enabled = false }
