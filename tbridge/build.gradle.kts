import org.gradle.api.tasks.bundling.Jar
import org.gradle.kotlin.dsl.withType
import org.gradle.api.publish.maven.tasks.PublishToMavenRepository
import java.util.Properties


plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    `maven-publish`
    signing
    id("org.jetbrains.dokka") version "1.9.0"
    id("tech.yanand.maven-central-publish") version ("1.3.0")
}


// ------------------ Android 配置 ------------------
android {
    namespace = "com.kajinl.tbridge"
    compileSdk = 36

    defaultConfig {
        minSdk = 24
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions { jvmTarget = "11" }

    // 生成 sources.jar
    publishing {
        singleVariant("release") { withSourcesJar() }
    }
}

// ------------------ Dependencies ------------------

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation("com.google.code.gson:gson:2.10.1")
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}


// ------------------ Dokka Javadoc Jar ------------------
val dokkaJavadocJar by tasks.registering(Jar::class) {
    dependsOn(tasks.named("dokkaJavadoc"))
    archiveClassifier.set("javadoc")
    from(tasks.named("dokkaJavadoc").get().outputs)
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            groupId = "com.kajlee"
            artifactId = "tbridge"
            version = "0.1.01"

            // AAR 主 artifact
            artifact(layout.buildDirectory.file("outputs/aar/tbridge-release.aar"))

            artifact(dokkaJavadocJar.get())

            pom {
                name.set("TBridge Android Library")
                description.set("TBridge library for Android part")
                url.set("https://github.com/KaJInL/tbridge")
                licenses {
                    license {
                        name.set("Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        id.set("kajinl")
                        name.set("Kajin")
                        email.set("1215302367@qq.com")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/KaJInL/t-bridge")
                    developerConnection.set("scm:git:ssh://github.com/KaJInL/t-bridge")
                    url.set("https://github.com/KaJInL/tbridge")
                }
            }

        }
    }

    repositories {
    }
    tasks.withType<PublishToMavenRepository> {
        dependsOn(dokkaJavadocJar)
    }
}
val localProps = Properties().apply {
    load(rootProject.file("local.properties").inputStream())
}

// 覆盖 gradle.properties 中的变量
project.extra["signing.keyId"] = localProps.getProperty("signing.keyId")
project.extra["signing.password"] = localProps.getProperty("signing.password")
project.extra["signing.secretKeyRingFile"] = localProps.getProperty("signing.secretKeyRingFile")
project.extra["authTokenValue"] = localProps.getProperty("authTokenValue")

signing {
    sign(publishing.publications)
}

mavenCentral {
    authToken = localProps.getProperty("authTokenValue") as String
    publishingType = "USER_MANAGED"
    maxWait = 60
}