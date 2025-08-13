import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.7.18"
    id("io.spring.dependency-management") version "1.0.15.RELEASE"
    kotlin("jvm") version "1.6.21"  // Версия Kotlin для Spring Boot 2.7
    kotlin("plugin.spring") version "1.6.21"
    kotlin("plugin.jpa") version "1.6.21"
    war
}

//allOpen {
//    annotation("javax.persistence.Entity")
//    annotation("javax.persistence.Embeddable")
//    annotation("javax.persistence.MappedSuperclass")
//}

group = "ru.valentin"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    //validation
    implementation("org.springframework.boot:spring-boot-starter-validation")
    //cache
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("com.github.ben-manes.caffeine:caffeine")

    runtimeOnly("org.postgresql:postgresql")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.testcontainers:junit-jupiter:1.19.8")
    testImplementation("org.testcontainers:postgresql:1.19.8")

    testImplementation("io.rest-assured:rest-assured:4.5.1")
    testImplementation("io.rest-assured:kotlin-extensions:4.5.1")
    testImplementation("io.rest-assured:json-path:4.5.1")
    testImplementation("io.rest-assured:xml-path:4.5.1")

    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
//    testImplementation("org.assertj:assertj-core:3.24.2")
//    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
//    testImplementation("org.springframework.boot:spring-boot-testcontainers:3.4.5")
//    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    providedRuntime("org.springframework.boot:spring-boot-starter-tomcat")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()

    testLogging {
        events = setOf(
            org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED,
            org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED,
            org.gradle.api.tasks.testing.logging.TestLogEvent.SKIPPED,
            org.gradle.api.tasks.testing.logging.TestLogEvent.STANDARD_OUT,
            org.gradle.api.tasks.testing.logging.TestLogEvent.STANDARD_ERROR
        )
        showExceptions = true
        showCauses = true
        showStackTraces = true
        showStandardStreams = true
    }
}
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(11)
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        // указание целевой JVM
        jvmTarget = "11"
        // связка котлин null-safety с null аннотациями из java/spring api
        // для обеспечения безопасноти
        freeCompilerArgs = listOf("-Xjsr305=strict")
    }
}