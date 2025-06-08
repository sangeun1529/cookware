group = "io.foreshore.cookware"
version = "0.0.1-SNAPSHOT"

repositories {
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

tasks.withType<Test>().configureEach {
    jacoco {
        // Combined deduction from errors:
        // 1. "Val cannot be reassigned" -> 'excludes' is a val (final reference).
        // 2. "Type mismatch: inferred type is List<String> but (Mutable)Set<String!>! was expected"
        //    -> The collection it points to is expected to be treated as a Set.
        // This implies 'excludes' is something like: val excludes: MutableSet<String>

        // First, clear any existing default excludes if necessary.
        // If this line fails, it means 'excludes' isn't a mutable collection directly.
        excludes.clear()
        excludes.addAll(setOf(
            "sun/util/resources/cldr/provider/CLDRLocaleDataMetaInfo", // Exact match for the problematic class
            "sun/util/resources/cldr/provider/*", // Keep the wildcard too just in case
            "jdk/internal/**",
            "com/sun/**",
            "sun/**"
        ))
    }
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

    // hibernate
    compileOnly("org.hibernate.javax.persistence:hibernate-jpa-2.1-api:1.0.2.Final")
    compileOnly("org.hibernate:hibernate-core")
    // hibernate
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
    // The classDirectories modification for jacocoTestReport might still be useful
    // to ensure reports don't try to process these, but the agent exclusion is key.
    classDirectories.setFrom(
        files(classDirectories.files.map {
            fileTree(it) {
                exclude(
                    "sun/util/resources/cldr/provider/CLDRLocaleDataMetaInfo", // Exact match
                    "sun/util/resources/cldr/provider/**",
                    "jdk/internal/**",
                    "com/sun/**",
                    "sun/**"
                )
            }
        })
    )
}
