plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsKotlinAndroid)
}

android {
    namespace = "com.bluewhaleyt.codewhale.code.compiler.java"
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

dependencies {
    implementation(libs.bundles.core)
    testImplementation(libs.bundles.test)
    androidTestImplementation(libs.bundles.test)

    implementation(project(":compiler-core"))

    implementation("io.github.itsaky:nb-javac-android:17.0.0.3")
    implementation("com.google.guava:guava:33.0.0-jre")
    implementation("com.squareup:javapoet:1.13.0")
    implementation("com.android.tools.smali:smali-dexlib2:3.0.5")
    api("com.android.tools:r8:8.2.47")
}