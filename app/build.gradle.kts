plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
}

android {
    namespace = "com.bluewhaleyt.codewhale.code.app"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.bluewhaleyt.codewhale.code.app"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.androidxComposeCompiler.get()
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    // Enable JNI Libs when compiling Sass
    packagingOptions.jniLibs.useLegacyPackaging = true
}

dependencies {
    implementation(libs.bundles.core)
    implementation(libs.bundles.compose)
    implementation(libs.bundles.composeAccompanist)
    testImplementation(libs.bundles.test)
    androidTestImplementation(libs.bundles.test)

    debugImplementation("com.github.CodeWhaleOfficial:CrashWhale:1.0.0")
    implementation(platform("io.github.Rosemoe.sora-editor:bom:0.23.4"))
    implementation("io.github.Rosemoe.sora-editor:editor")
    implementation("io.github.Rosemoe.sora-editor:language-java")

    // from compiler-java
    implementation("com.android.tools:r8:8.2.47")

    implementation(project(":core"))
    implementation(project(":language-sass"))
    implementation(project(":language-java"))
    implementation(project(":language-kotlin"))
}