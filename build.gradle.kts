import org.gradle.kotlin.dsl.annotationProcessor
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "2.1.20-Beta1"
    id("me.champeau.jmh") version "0.6.8"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.openjdk.jmh:jmh-core:1.37")
    annotationProcessor("org.openjdk.jmh:jmh-generator-annprocess:1.37")
    annotationProcessor("org.openjdk.jmh:jmh-generator-bytecode:1.37")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.named<KotlinCompile>("compileJmhKotlin") {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_1_8)
    }
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(8)
    }
}


kotlin {
    jvmToolchain(8)
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_1_8)
    }
}