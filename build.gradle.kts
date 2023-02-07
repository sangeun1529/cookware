group = "io.foreshore.cookware"
version = "0.0.1-SNAPSHOT"

val nexusUser:String by project
val nexusPassword:String by project
val nexus:String by project
val nexusSnapshot:String by project
val nexusRelease:String by project

val repo:String = System.getenv("CI_NEXUS") ?: nexus
val repoSnapshot:String = System.getenv("CI_NEXUS_SNAPSHOT") ?: nexusSnapshot
val repoRelease:String = System.getenv("CI_NEXUS_RELEASE") ?: nexusRelease
val repoUser:String = System.getenv("CI_NEXUS_USER") ?: nexusUser
val repoPassword:String = System.getenv("CI_NEXUS_PASSWORD") ?: nexusPassword

repositories {
    maven {
        url = uri(repo)
        credentials {
            username = repoUser
            password = repoPassword
        }
    }
    mavenCentral()
}

plugins {
    val kotlinVersion = "1.7.20"    // plugin version declaration must be a constant.
    id("org.jetbrains.dokka") version kotlinVersion
    id("org.sonarqube") version "3.4.0.2513"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion
    `maven-publish`
    jacoco
    idea
}

idea {
    module {
        isDownloadJavadoc = true
        isDownloadSources = true
    }
}

jacoco {
    toolVersion ="0.8.7"
}

dependencies {
    val ver = object {  // Dependency versions.
        val kotest = "5.5.4"
        val koSerialization = "1.4.1"
        val springBoot = "2.4.13"
    }

    api(kotlin("reflect"))
    api(kotlin("stdlib-jdk8"))
    testImplementation(kotlin("test-junit5"))
    testImplementation("io.kotest:kotest-runner-junit5:${ver.kotest}")
    testImplementation("io.kotest:kotest-assertions-core:${ver.kotest}")
    testImplementation("io.kotest:kotest-property:${ver.kotest}")
    testImplementation("com.fasterxml.jackson.core:jackson-databind:2.14.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    testImplementation("org.bouncycastle:bcprov-jdk15on:1.70")
    testImplementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${ver.koSerialization}")
    // cookware SUPPORT

    // interface slf4j , impl logback
    compileOnly(platform("org.springframework.boot:spring-boot-dependencies:${ver.springBoot}"))
    compileOnly("ch.qos.logback:logback-classic")
    compileOnly("ch.qos.logback:logback-core")
    compileOnly("org.slf4j:slf4j-api")
    // interface slf4j , impl logback
}

tasks.compileKotlin {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}

tasks.compileTestKotlin {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}

tasks.test {
    finalizedBy("jacocoTestReport")        // 이게 없으면 jacocoTestReport 가 실행 되지 않는다.
    useJUnitPlatform()                             // 이게 없으면 unit test 자체가 skip 된다.
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        html.required.set(true)
        xml.required.set(true)
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
    repositories {
        maven {
            credentials {
                username = repoUser
                password = repoPassword
            }
            url = uri(if(project.version.toString().contains("SNAPSHOT", true)) repoSnapshot else repoRelease)
        }
    }
}
