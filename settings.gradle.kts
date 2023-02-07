rootProject.name = "cookware"

pluginManagement {
    repositories {
        maven {
            url = uri(System.getenv("CI_NEXUS") ?: extra["nexus"] as String)
            credentials {
                username = System.getenv("CI_NEXUS_USER") ?: extra["nexusUser"] as String
                password = System.getenv("CI_NEXUS_PASSWORD") ?: extra["nexusPassword"] as String
            }
        }
    }
}
