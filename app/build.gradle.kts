plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("com.google.gms.google-services")
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.0"
    id("com.google.dagger.hilt.android") version "2.57.2"
}

android {
    namespace = "com.example.plugd"
    compileSdk = 36

    kapt {
        correctErrorTypes = true
    }

    defaultConfig {
        applicationId = "com.example.plugd"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        buildConfigField("String", "API_BASE_URL", "\"https://us-central1-plugdapp.cloudfunctions.net/\"")

        val mapsKey: String = (project.findProperty("MAPS_API_KEY") as String?) ?: ""
        manifestPlaceholders["MAPS_API_KEY"] = mapsKey

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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        viewBinding = true
        buildConfig = true
    }
}

dependencies {

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")

    // ---- AndroidX core / lifecycle / navigation ----
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation("androidx.core:core-splashscreen:1.0.1")

    // ---- Compose (keep versions consistent) ----
    implementation("androidx.activity:activity-compose:1.9.0")
    implementation("androidx.compose.ui:ui:1.9.1")
    implementation(libs.androidx.ui)
    implementation(libs.material3)
    debugImplementation("androidx.compose.ui:ui-tooling:1.9.1")
    implementation("androidx.compose.foundation:foundation:1.9.1")
    implementation("androidx.compose.material3:material3:1.2.1")
    implementation("androidx.compose.material3:material3-window-size-class:1.3.1")

    // Material components (views)
    implementation("com.google.android.material:material:1.9.0")

    // ---- Networking ----
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")

    // ---- Firebase (use BoM; no explicit versions for Firebase artifacts) ----
    implementation(platform("com.google.firebase:firebase-bom:33.1.2"))
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-database-ktx")
    implementation("com.google.firebase:firebase-messaging-ktx")
    implementation("com.google.firebase:firebase-storage-ktx")

    // ---- Google sign-in / credentials ----
    implementation("com.google.android.gms:play-services-auth:20.5.0")
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)

    // ---- Biometric & security ----
    implementation("androidx.biometric:biometric:1.1.0") // pick one stable
    implementation("androidx.security:security-crypto:1.1.0")

    // ---- DataStore (one version) ----
    implementation("androidx.datastore:datastore-preferences:1.1.1")

    // ---- Room ----
    implementation("androidx.room:room-runtime:2.7.2")
    kapt("androidx.room:room-compiler:2.7.2")
    implementation("androidx.room:room-ktx:2.7.2")

    // ---- WorkManager ----
    implementation("androidx.work:work-runtime-ktx:2.8.1")

    // ---- Coroutines / Tasks interop ----
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")

    // ---- Navigation ----
    implementation("androidx.navigation:navigation-compose:2.7.3")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.0")

    // ---- Hilt ----
    implementation("com.google.dagger:hilt-android:2.57.2")
    kapt("com.google.dagger:hilt-android-compiler:2.57.2")

    // ---- Location & Places ----
    implementation("com.google.android.gms:play-services-location:21.3.0")
    implementation("com.google.android.libraries.places:places:3.2.0")

    // ---- Images ----
    implementation("io.coil-kt:coil-compose:2.7.0")

    // ---- Testing ----
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlin:kotlin-test:1.9.0")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // ---- Accompanist (version aligned) ----
    implementation("com.google.accompanist:accompanist-pager:0.36.0")
    implementation("com.google.accompanist:accompanist-pager-indicators:0.36.0")
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.30.1")
}