plugins {
    alias(libs.plugins.androidApplication)
}

android {
    namespace = "com.bluewhaleyt.codewhale.code.app2"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.bluewhaleyt.codewhale.code.app2"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

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
}

dependencies {
    implementation(libs.bundles.core)
    testImplementation(libs.bundles.test)
    androidTestImplementation(libs.bundles.test)
//    debugImplementation("com.github.CodeWhaleOfficial:CrashWhale:1.0.0")

    implementation("com.android.tools:r8:8.2.47")
    implementation(project(":core"))
    implementation(project(":language-java"))
}