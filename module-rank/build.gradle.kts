plugins {
    id("java")
    id("maven-publish")
}

dependencies {
    compileOnly(libs.quala.bom)
    compileOnly(libs.cloud.api)
    compileOnly(libs.cloud.base)
    compileOnly(libs.cloud.plugin)
    compileOnly(libs.lombok)
    compileOnly(libs.paper)
    compileOnly(libs.authlib)
    compileOnly(libs.netty.api)
    annotationProcessor(libs.lombok)
}

publishing {
    repositories {
        maven {
            name = "claymc-network"
            url = uri(
                if (version.toString().endsWith("SNAPSHOT"))
                    "https://repository02.lumesolutions.de/repository/claymc-network-dev/" else
                    "https://repository02.lumesolutions.de/repository/claymc-network-production/"
            )
            credentials {
                username = project.findProperty("lumesolutions_user") as String?
                password = project.findProperty("lumesolutions_password") as String?
            }
        }
    }

    publications {
        create<MavenPublication>("lumesolutions") {
            groupId = groupId
            artifactId = artifactId
            version = version

            from(components["java"])
        }
    }
}

tasks.shadowJar {
    archiveFileName.set("golgocloud-rank-module.jar")
}