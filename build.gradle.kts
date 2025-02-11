plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("kotlinx-serialization")
    id("maven-publish")
    id("signing")
    id("org.jetbrains.dokka")
}

android {
    namespace = "io.fusionauth.mobilesdk"
    compileSdk = 35

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")

        manifestPlaceholders["appAuthRedirectScheme"] = "io.fusionauth.app"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    lint {
        sarifReport = true
    }

    // Include sources and javadoc .jar files as secondary artifacts
    publishing {
        singleVariant("release") {
            withSourcesJar()
            withJavadocJar()
        }
    }
}

// Configure the maven-publish plugin.
publishing {
    publications {
        // This says: defer this block until all of the other stuff has run first.
        // This is required since components["release"] is generated by the Android
        // plugin in `afterEvaluate` itself, which forces us to do the same.
        afterEvaluate {
            // Create a new publication called "release". The maven-publish plugin
            // creates tasks named publish${name}PublicationTo${target}, where
            // ${name} is a capitalized form of the name and ${target} is an output
            // repository. By default a MavenLocal target is automatically added,
            // which outputs to ~/.m2/repository.
            create<MavenPublication>("release") {
                // Include all artifacts from the "release" component. This is the
                // .aar file along with the sources and javadoc .jars.
                from(components["release"])

                // Here we configure some properties of the publication (these are
                // automatically applied to the pom file). Your library will be
                // referenced as ${groupId}:${artifactId}.
                groupId = "io.fusionauth"
                artifactId = "fusionauth-android-sdk"
                // x-release-please-start-version
                version = "1.1.13-rc.4"
                // x-release-please-end

                // And here are some more properties that go into the pom file.
                // For a full list of required metadata fields, see:
                // https://central.sonatype.org/publish/requirements/#sufficient-metadata
                pom {
                    packaging = "aar"
                    name.set("FusionAuth Android SDK")
                    description.set("Android SDK for FusionAuth")
                    url.set("https://github.com/FusionAuth/fusionauth-android-sdk/")
                    licenses {
                        license {
                            name.set("MIT License")
                            url.set("https://opensource.org/license/mit/")
                        }
                    }
                    developers {
                        developer {
                            name.set("FusionAuth engineering")
                            email.set("dev@fusionauth.io")
                        }
                    }
                    scm {
                        url.set(pom.url.get())
                        connection.set("scm:git:${url.get()}.git")
                        developerConnection.set("scm:git:${url.get()}.git")
                    }
                }
            }
        }
    }

    // Here we define some repositories that we can publish our outputs to.
    repositories {
        // Specifying that this is a custom maven repository.
        maven {
            // This is the name of the repo that is used as the value of ${target}
            // from above.
            name = "OSSRH"

            // Self-explanatory.
            setUrl {
                val repositoryId =
                    System.getenv("SONATYPE_REPOSITORY_ID") ?: error("Missing env variable: SONATYPE_REPOSITORY_ID")
                "https://oss.sonatype.org/service/local/staging/deployByRepositoryId/${repositoryId}/"
            }

            // These need to be defined in ~/.gradle/gradle.properties:
            // ossrhUsername=<your sonatype jira username>
            // ossrhPassword=<your sonatype jira password>
            credentials {
                username = project.findProperty("ossrhUsername") as String?
                password = project.findProperty("ossrhPassword") as String?
            }
        }
    }
}

// Configure the signing plugin.
signing {
    // Use the external gpg binary instead of the built-in PGP library.
    // This lets us use gpg-agent and avoid having to hard-code our PGP key
    // password somewhere.
    //
    // Note that you will need to add this in your ~/.gradle/gradle.properties:
    // signing.gnupg.keyName=<last 8 characters of your PGP key>
    //
    // Additionally, for users who have gpg instead of gpg2:
    // signing.gnupg.useLegacyGpg=true
    //useGpgCmd()

    // Using in-memory keys https://docs.gradle.org/current/userguide/signing_plugin.html#sec:in-memory-keys
    // Requires ORG_GRADLE_PROJECT_signingKey and ORG_GRADLE_PROJECT_signingPassword environment variables
    // The secret key is exported with gpg --output private.pgp --armor --export-secret-key

    val signingKey: String? by project
    val signingPassword: String? by project
    useInMemoryPgpKeys(signingKey, signingPassword)

    // Since the publication itself was created in `afterEvaluate`, we must
    // do the same here.
    afterEvaluate {
        // This adds a signing stage to the publish task in-place (so we keep
        // using the same task name; it just also performs signing now).
        sign(publishing.publications["release"])
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.androidbrowserhelper:androidbrowserhelper:2.5.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-cbor:1.8.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0")
    implementation("net.openid:appauth:0.11.1")
    implementation("androidx.security:security-crypto-ktx:1.1.0-alpha06")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
}

tasks.dokkaGfm {
    outputDirectory.set(layout.projectDirectory.dir("docs"))
}
