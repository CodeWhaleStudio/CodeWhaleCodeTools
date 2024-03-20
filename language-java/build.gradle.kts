plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    `maven-publish`
}

android {
    namespace = "com.bluewhaleyt.codewhale.code.language.java"
    compileSdk = 34

    defaultConfig {
        minSdk = 26

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["release"])
                groupId = "com.github.CodeWhaleStudio"
                artifactId = "language-java"
                version = "1.0.0"
            }
        }
    }
}

dependencies {
    implementation(libs.bundles.core)
    implementation(project(":core"))

    compileOnly("com.android.tools:r8:8.2.47")
    api("io.github.itsaky:nb-javac-android:17.0.0.3")
    implementation("com.google.guava:guava:33.0.0-jre")
    implementation("com.squareup:javapoet:1.13.0")
    implementation("org.javassist:javassist:3.29.2-GA")
    implementation("com.android.tools.smali:smali-dexlib2:3.0.5")
    implementation("com.github.javaparser:javaparser-core:3.25.8")
    implementation("com.github.javaparser:javaparser-symbol-solver-core:3.25.8") {
        exclude(group = "com.google.guava", module = "guava")
    }

    implementation("com.github.Cosmic-Ide.kotlinc-android:kotlinc:2a0a6a7291")

    implementation(platform("io.github.Rosemoe.sora-editor:bom:0.23.4"))
    implementation("io.github.Rosemoe.sora-editor:editor")
}