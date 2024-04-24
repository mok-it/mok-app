@Suppress("S6624")
plugins {
    id("com.android.application")
    id("kotlin-android")
    id("com.google.gms.google-services")
    id("kotlin-parcelize")
    id("androidx.navigation.safeargs.kotlin")
    id("com.google.firebase.crashlytics")
}

android {
    signingConfigs {

    }
    compileSdk = 34

    defaultConfig {
        applicationId = "mok.it.app.mokapp"
        minSdk = 26
        targetSdk = 34
        versionCode = 5
        versionName = "4.1.1"

        testInstrumentationRunner =
            "androidx.test.runner.AndroidJUnitRunner"
        signingConfig = signingConfigs.getByName("debug")
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.8"
    }
    namespace = "mok.it.app.mokapp"
}

dependencies {
    // Androidx
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.compose.runtime:runtime-livedata:1.6.5")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.recyclerview:recyclerview:1.3.2")

    // Google
    implementation("com.google.android.material:material:1.11.0")
    implementation("com.google.android.gms:play-services-auth:21.0.0")

    // Kotlinx
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")

    // Testing
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    //Glide
    implementation("com.github.bumptech.glide:glide:4.14.2")

    //Compose
    val composeVersion = "1.6.5"
    implementation("androidx.compose.ui:ui-android:$composeVersion")
    implementation("androidx.compose.ui:ui-tooling:$composeVersion")
    implementation("androidx.compose.material:material-icons-extended:$composeVersion")
    implementation("androidx.compose.material3:material3:1.2.1")

    //Coil
    implementation("io.coil-kt:coil-compose:2.5.0")

    //Firebase SDK
    implementation(platform("com.google.firebase:firebase-bom:32.8.0"))
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-database")
    implementation("com.google.firebase:firebase-crashlytics")
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-config")
    implementation("com.google.firebase:firebase-dynamic-links")
    implementation("com.google.firebase:firebase-messaging")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-functions")

    //FirebaseUI
    implementation("com.firebaseui:firebase-ui-firestore:8.0.0")
    implementation("com.firebaseui:firebase-ui-database:8.0.0")

    //Picasso
    implementation("com.squareup.picasso:picasso:2.71828")

    //Circle imageView
    implementation("de.hdodenhof:circleimageview:3.1.0")

    //Material
    implementation("com.google.android.material:material:1.11.0")

    //Klaxon (JSON parsing lib)
    implementation("com.beust:klaxon:5.5")

    //Shimmer
    implementation("com.facebook.shimmer:shimmer:0.5.0")

    //Android about page
    implementation("io.github.medyo:android-about-page:2.0.0")

    // Material Dialog Library
    implementation("dev.shreyaspatil.MaterialDialog:MaterialDialog:2.2.3")

    // Lottie
    val lottieVersion = "6.4.0"
    implementation("com.airbnb.android:lottie:$lottieVersion")
    implementation("com.airbnb.android:lottie-compose:$lottieVersion")

    //Fragment
    implementation("androidx.fragment:fragment-ktx:1.7.0-alpha10")

    // Chip text field
    implementation("io.github.dokar3:chiptextfield-m3:0.7.0-alpha02")
}

apply(plugin = "com.google.gms.google-services")