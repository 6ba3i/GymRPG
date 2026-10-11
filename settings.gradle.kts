pluginManagement { repositories { google(); mavenCentral(); gradlePluginPortal() } }
dependencyResolutionManagement { repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS); repositories { google(); mavenCentral() } }
rootProject.name = "GymRPG"
include(":app", ":core:designsystem", ":feature:anatomy", ":core:integrations", ":core:notifications", ":integration:xiaomi-cn")
