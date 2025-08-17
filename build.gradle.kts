import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.util.profile
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("org.springframework.boot") version "2.7.18"
    id("io.spring.dependency-management") version "1.0.15.RELEASE"
    kotlin("jvm") version "1.6.21"
    kotlin("plugin.spring") version "1.6.21"
    kotlin("plugin.jpa") version "1.6.21"
}

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

//    doc
    implementation("org.springdoc:springdoc-openapi-ui:1.6.14")
    implementation("org.springdoc:springdoc-openapi-kotlin:1.6.14")

    //for Date
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")

    //validation
    implementation("org.springframework.boot:spring-boot-starter-validation")
    //cache
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("com.github.ben-manes.caffeine:caffeine")

    runtimeOnly("org.postgresql:postgresql")
    //тест контэйнер
    testImplementation("org.springframework.boot:spring-boot-starter-test:2.7.18")
    testImplementation("org.testcontainers:testcontainers:1.17.6")
    testImplementation("org.testcontainers:junit-jupiter:1.17.6")
    testImplementation("org.testcontainers:postgresql:1.17.6")
    //ендпоинты тест
    testImplementation("io.rest-assured:rest-assured:4.5.1")
    testImplementation("io.rest-assured:kotlin-extensions:4.5.1")
    testImplementation("io.rest-assured:json-path:4.5.1")
    testImplementation("io.rest-assured:xml-path:4.5.1")

    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")

    testImplementation(kotlin("test"))
}

tasks.bootJar {
    mainClass.set("ru.valentin.Application")
}

tasks.named("jar") {
    enabled = false
}

tasks.bootRun {
    mainClass.set("ru.valentin.Application")
    systemProperty("spring.profiles.active", "dev")
}

//для запуска в тестовом окружении
tasks.register<JavaExec>("runWithTestcontainers") {
    classpath = sourceSets.test.get().runtimeClasspath
    mainClass.set("ru.valentin.TestApplication.kt")
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