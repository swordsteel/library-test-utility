import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.extensions.DetektExtension.Companion.DEFAULT_SRC_DIR_KOTLIN
import io.gitlab.arturbosch.detekt.extensions.DetektExtension.Companion.DEFAULT_TEST_SRC_DIR_KOTLIN
import java.lang.System.getProperty
import java.time.OffsetDateTime.now
import java.time.ZoneId.of
import java.time.format.DateTimeFormatter.ofPattern
import org.gradle.api.JavaVersion.VERSION_17
import org.gradle.kotlin.dsl.support.uppercaseFirstChar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType.SARIF
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("io.gitlab.arturbosch.detekt") version "1.23.5"
    id("org.springframework.boot") version "3.2.3"
    id("org.jlleitschuh.gradle.ktlint") version "12.1.0"
    id("io.spring.dependency-management") version "1.1.4"

    kotlin("jvm") version "1.9.22"
    kotlin("plugin.spring") version "1.9.22"

    `maven-publish`
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.springframework.boot:spring-boot-starter")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

description = "Lulz Ltd Test Library Test Utility"
group = "ltd.lulz.library"

val timestamp = now()
    .atZoneSameInstant(of("UTC"))
    .format(ofPattern("yyyy-MM-dd HH:mm:ss z"))
    .toString()

detekt {
    buildUponDefaultConfig = true
    basePath = projectDir.path
    source.from(DEFAULT_SRC_DIR_KOTLIN, DEFAULT_TEST_SRC_DIR_KOTLIN)
}

java {
    sourceCompatibility = VERSION_17
    targetCompatibility = VERSION_17
    withSourcesJar()
}

ktlint {
    verbose = true
    filter {
        exclude("**/generated/**")
        include("**/kotlin/**")
    }
    kotlinScriptAdditionalPaths {
        include(fileTree("scripts/*"))
    }
    reporters {
        reporter(SARIF)
    }
}

publishing {
    repositories {
        // TODO configuration for publishing packages
        // maven {
        //     url = uri("https://")
        //     credentials {
        //         username =
        //         password =
        //     }
        // }
        publications.register("mavenJava", MavenPublication::class) { from(components["java"]) }
    }
}

repositories {
    mavenLocal()
    mavenCentral()
}

tasks {
    withType<BootJar> {
        enabled = false
    }
    withType<Detekt> {
        reports {
            html.required = false
            md.required = false
            sarif.required = true
            txt.required = false
            xml.required = false
        }
    }
    withType<Jar> {
        manifest.attributes.apply {
            put("Implementation-Title", project.name.uppercaseFirstChar())
            put("Implementation-Version", project.version)
            put("Implementation-Vendor", "Lulz Ltd")
            put("Built-By", getProperty("user.name"))
            put("Built-Gradle", project.gradle.gradleVersion)
            put("Built-JDK", getProperty("java.version"))
            put("Built-OS", "${getProperty("os.name")} v${getProperty("os.version")}")
            put("Built-Time", timestamp)
        }
    }
    withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs += "-Xjsr305=strict"
            jvmTarget = "17"
        }
    }
    withType<Test> {
        useJUnitPlatform()
    }
}
