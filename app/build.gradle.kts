plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.kingdew.recipemaster"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.kingdew.recipemaster"
        minSdk = 24
        targetSdk = 36
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    packagingOptions {
        jniLibs {
            useLegacyPackaging = true
        }
    }

    // Specifically for 16 KB support
    bundle {
        abi {
            enableSplit = true
        }
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.ai)
    implementation(libs.cardview)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    //ai client
    implementation(platform("com.google.firebase:firebase-bom:34.13.0"))
    // Required for ListenableFuture and Futures
    implementation("com.google.guava:guava:33.6.0-android")
    implementation("io.noties.markwon:core:4.6.2")

    testImplementation("com.google.truth:truth:1.4.5")
    androidTestImplementation("com.google.truth:truth:1.4.5")

    implementation("com.airbnb.android:lottie:6.7.1")


}