plugins {
    base
    kotlin("jvm") version "1.4.31"
    kotlin("plugin.serialization") version "1.4.31"
    id("org.jetbrains.dokka") version "0.10.1"
    signing
}

val caffeine_version: String by project
val kDataLoader_version: String by project
val kotlin_version: String by project
val serialization_version: String by project
val coroutine_version: String by project
val jackson_version: String by project
val ktor_version: String by project

val netty_version: String by project
val hamcrest_version: String by project
val kluent_version: String by project
val junit_version: String by project

val isReleaseVersion = !version.toString().endsWith("SNAPSHOT")


dependencies {
    implementation(kotlin("stdlib-jdk8"))
    api(project(":kgraphql"))
    implementation("io.ktor:ktor-server-core:$ktor_version")
    implementation("io.ktor:ktor-serialization:$ktor_version")
    implementation("com.github.salomonbrys.kotson:kotson:2.5.0")

    testImplementation("org.junit.jupiter:junit-jupiter-api:$junit_version")
    testImplementation("org.amshove.kluent:kluent:$kluent_version")
    testImplementation("me.lazmaid.kraph:kraph:0.6.1")
    testImplementation("io.ktor:ktor-server-test-host:$ktor_version")
    testImplementation("io.ktor:ktor-auth:$ktor_version")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junit_version")
}


tasks {
    compileKotlin { kotlinOptions { jvmTarget = "1.8" } }
    compileTestKotlin { kotlinOptions { jvmTarget = "1.8" } }

    test {
        useJUnitPlatform()
    }
    dokka {
        outputFormat = "html"
        outputDirectory = "$buildDir/javadoc"
        impliedPlatforms = mutableListOf("JVM")
        configuration {
            jdkVersion = 8
            reportUndocumented = true
        }
    }
}

val sourcesJar by tasks.creating(Jar::class) {
    classifier = "sources"
    from(sourceSets.main.get().allSource)
}

val dokkaJar by tasks.creating(Jar::class) {
    group = JavaBasePlugin.DOCUMENTATION_GROUP
    classifier = "javadoc"
    from(tasks.dokka)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = project.name
            from(components["java"])
            artifact(sourcesJar)
            artifact(dokkaJar)
            pom {
                name.set("KGraphQL")
                description.set("KGraphQL is a Kotlin implementation of GraphQL. It provides a rich DSL to set up the GraphQL schema.")
                url.set("https://kgraphql.io/")
                organization {
                    name.set("aPureBase")
                    url.set("http://apurebase.com/")
                }
                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://github.com/aPureBase/KGraphQL/blob/main/LICENSE.md")
                    }
                }
                developers {
                    developer {
                        id.set("jeggy")
                        name.set("Jógvan Olsen")
                        email.set("jol@apurebase.com")
                    }
                }
                scm {
                    connection.set("scm:git:https://github.com/aPureBase/KGraphQL.git")
                    developerConnection.set("scm:git:https://github.com/aPureBase/KGraphQL.git")
                    url.set("https://github.com/aPureBase/KGraphQL/")
                    tag.set("HEAD")
                }
            }
        }
    }
}

signing {
    isRequired = isReleaseVersion
    useInMemoryPgpKeys(
        System.getenv("ORG_GRADLE_PROJECT_signingKey"),
        System.getenv("ORG_GRADLE_PROJECT_signingPassword")
    )
    sign(publishing.publications["maven"])
}
