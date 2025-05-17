plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")

    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.alertify"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.alertify"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true
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

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.6.10"
    }

    buildToolsVersion = "35.0.0"
}

configurations.all {
    exclude(group = "org.jetbrains.kotlin", module = "kotlin-stdlib-iosarm32")
    exclude(group = "org.jetbrains.kotlinx", module = "kotlinx-coroutines-core-iosarm32")
}

dependencies {
    // Core
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.3.1")
    implementation("com.google.android.material:material:1.4.0")

    // Lifecycle
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")

    // Compose UI
    implementation(platform("androidx.compose:compose-bom:2024.05.00"))
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.compose.material3:material3-window-size-class")

    // Navigation
    implementation("androidx.navigation:navigation-compose:2.7.7")

    // Google Maps
    implementation("com.google.maps.android:maps-compose:4.2.0")
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    implementation("com.google.android.gms:play-services-location:21.0.1")

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:33.13.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-firestore-ktx:24.7.0")

    // Hilt (DI)
    implementation("com.google.dagger:hilt-android:2.48")
    kapt("com.google.dagger:hilt-compiler:2.48")

    implementation("androidx.hilt:hilt-navigation-compose:1.0.0")
    kapt("androidx.hilt:hilt-compiler:1.0.0")
    implementation ("com.google.firebase:firebase-auth:22.3.1") // ou la dernière version stable
    implementation ("com.google.accompanist:accompanist-permissions:0.34.0")
    implementation ("com.google.firebase:firebase-auth-ktx")
    implementation ("androidx.activity:activity-compose:1.9.0")
    implementation("io.coil-kt:coil-compose:2.5.0")
    implementation("com.google.firebase:firebase-storage-ktx:20.3.0") // ou plus récent
    implementation ("androidx.compose.material3:material3:1.0.0")
    implementation ("androidx.compose.ui:ui:1.0.5")
    implementation ("androidx.compose.material:material:1.0.5")
    implementation ("com.google.android.gms:play-services-auth:20.5.0")

    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")



    implementation ("com.google.accompanist:accompanist-permissions:0.23.0")


    implementation("androidx.compose.ui:ui:1.6.0")
    implementation("androidx.compose.material3:material3:1.2.0") // ou la dernière version stable
    implementation("androidx.compose.foundation:foundation:1.6.0")
    implementation("androidx.compose.ui:ui-text:1.6.0") // ← indispensable pour KeyboardOptions

    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")

    // Debug & test
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    implementation("androidx.compose.compiler:compiler:1.5.11")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.05.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
}

kapt {
    correctErrorTypes = true
}
