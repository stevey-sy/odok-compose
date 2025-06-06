pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}

rootProject.name = "OdokCompose"
include(":app")
include(":feature:mylibrary")
include(":feature:search")
include(":core:designsystem")
include(":core:network")
include(":core:database")
include(":core:model")
include(":core:data")
include(":core:domain")
include(":feature:timer")
include(":feature:memo")
