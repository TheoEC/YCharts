@Suppress("DSL_SCOPE_VIOLATION") // scope violation issue: work around suggested from: https://github.com/gradle/gradle/issues/22797
plugins {
    id("ycharts.android.library")
    id("ycharts.android.library.compose")
    id("ycharts.android.test")
    id("maven-publish")
    id("signing")
    alias(versionCatalogLibs.plugins.dokka)
}

android {
    compileSdk = 33
    namespace = "co.yml.charts.components"
    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}
val dokkaOutputDir = "$buildDir/dokka"

tasks.dokkaHtml {
    outputDirectory.set(file(dokkaOutputDir))
}

val deleteDokkaOutputDir by tasks.register<Delete>("deleteDokkaOutputDirectory") {
    delete(dokkaOutputDir)
}
val javadocJar = tasks.register<Jar>("javadocJar") {
    dependsOn(deleteDokkaOutputDir, tasks.dokkaHtml)
    archiveClassifier.set("javadoc")
    from(dokkaOutputDir)
}
publishing {
    repositories {
        maven {
            name = "YCharts"
            setUrl("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2")
            credentials {
                username = project.findProperty("mavenCentralUsername")?.toString() ?: System.getenv("MAVEN_USERNAME")
                password = project.findProperty("mavenCentralPassword")?.toString() ?: System.getenv("MAVEN_PASSWORD")
            }
        }
    }
    publications {
        register<MavenPublication>("release") {
            groupId = "com.github.TheoEC" // Altere para o seu grupo
            artifactId = "YCharts" // O nome do seu artefato
            version = "v1.0.0" // Altere para corresponder à tag que você criou

            afterEvaluate {
                from(components["release"])
            }
            artifact(javadocJar)
            pom {
                name.set("YCharts")
                description.set("YCharts is a light and extensible chart library for Jetpack Compose system.")
                url.set("https://github.com/TheoEC/YCharts") // URL do seu repositório
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                        distribution.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        id.set("TheoEC") // Seu ID de desenvolvedor
                        name.set("Theo EC") // Seu nome
                        url.set("https://github.com/TheoEC") // Seu perfil no GitHub
                    }
                }
                scm {
                    url.set("https://github.com/TheoEC/YCharts")
                    connection.set("scm:git:git://github.com/TheoEC/YCharts.git")
                    developerConnection.set("scm:git:ssh://git@github.com:TheoEC/YCharts.git")
                }
            }
        }
    }
}
if (project.hasProperty("signing.keyId")) {
    signing {
        sign(publishing.publications["release"])
    }
}
//
//signing {
//
//    publications {
//        create<MavenPublication>("release") {
//            // sua configuração de publicação
//        }
//    }
//    if (project.hasProperty("signing.keyId")) {
//        signing {
//            sign(publications["release"])
//        }
//    }
//    useInMemoryPgpKeys(
//        project.findProperty("signing.keyId")?.toString() ?: System.getenv("SIGNINGKEY"),
//        project.findProperty("signing.InMemoryKey")?.toString() ?: System.getenv("MEMORY_KEY"),
//        project.findProperty("signing.password")?.toString()?:System.getenv("SIGNINGPASSWORD")
//    )
//    sign(publishing.publications)
//}
