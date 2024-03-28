@Suppress("UNUSED_PARAMETER")
fun lulzRepository(repositoryHandler: RepositoryHandler) {
    // TODO configuration for stored version catalogs and gradle plugins.
//    repositoryHandler.maven {
//        url = uri("https://")
//        credentials {
//            username =
//            password =
//        }
//    }
}

dependencyResolutionManagement {
    @Suppress("UnstableApiUsage")
    repositories {
        mavenLocal()
        lulzRepository(this)
    }
    val catalog: String by settings
    versionCatalogs.create("lulz").from("ltd.lulz.catalog:lulz-version:$catalog")
}

pluginManagement.repositories {
    mavenLocal()
    gradlePluginPortal()
    lulzRepository(this)
}

rootProject.name = "test-utility"
