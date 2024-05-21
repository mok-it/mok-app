import java.util.Date

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("com.google.gms.google-services")
    id("kotlin-parcelize")
    id("androidx.navigation.safeargs.kotlin")
    id("com.google.firebase.crashlytics")
}

android {
    lint {
        abortOnError = true
    }
    signingConfigs {
    }
    compileSdk = 34

    defaultConfig {
        applicationId = "mok.it.app.mokapp"
        minSdk = 26
        targetSdk = 34
        versionCode = (((Date().time / 1000) - 1451606400) / 10).toInt()
        versionName = "5.1.0"

        testInstrumentationRunner =
            "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false //TODO: investigate why minify breaks the app in release build
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro" //contents were edited by trial and error to fix release build
                //modify the file so that it minimizes app size while keeping the app functional
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
val material3Verion = "1.2.1"
val androidxCoreVersion = "1.13.1"
val androidxAppcompatVersion = "1.6.1"
val androidxConstraintlayoutVersion = "2.1.4"
val androidxNavigationVersion = "2.7.7"
val androidxLegacySupportVersion = "1.0.0"
val androidxLifecycleVersion = "2.7.0"
val androidxComposeRuntimeVersion = "1.6.7"
val androidxCardviewVersion = "1.0.0"
val androidxRecyclerviewVersion = "1.3.2"
val googleMaterialVersion = "1.12.0"
val googlePlayServicesAuthVersion = "21.1.1"
val kotlinxCoroutinesVersion = "1.7.3"
val glideVersion = "4.14.2"
val composeVersion = "1.6.7"
val coilComposeVersion = "2.5.0"
val firebaseBomVersion = "33.0.0"
val firebaseUiVersion = "8.0.0"
val picassoVersion = "2.71828"
val circleimageviewVersion = "3.1.0"
val klaxonVersion = "5.5"
val shimmerVersion = "0.5.0"
val androidAboutPageVersion = "2.0.0"
val materialDialogVersion = "2.2.3"
val lottieVersion = "6.4.0"
val androidxFragmentVersion = "1.7.0-alpha10"
val chiptextfieldVersion = "0.7.0-alpha02"
val playUpdateVersion = "2.1.0"
val csvVersion = "1.8"

dependencies {
    // Androidx
    implementation("androidx.core:core-ktx:$androidxCoreVersion")
    implementation("androidx.appcompat:appcompat:$androidxAppcompatVersion")
    implementation("androidx.constraintlayout:constraintlayout:$androidxConstraintlayoutVersion")
    implementation("androidx.navigation:navigation-fragment-ktx:$androidxNavigationVersion")
    implementation("androidx.navigation:navigation-ui-ktx:$androidxNavigationVersion")
    implementation("androidx.legacy:legacy-support-v4:$androidxLegacySupportVersion")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$androidxLifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$androidxLifecycleVersion")
    implementation("androidx.compose.runtime:runtime-livedata:$androidxComposeRuntimeVersion")
    implementation("androidx.cardview:cardview:$androidxCardviewVersion")
    implementation("androidx.recyclerview:recyclerview:$androidxRecyclerviewVersion")

    // Google
    implementation("com.google.android.material:material:$googleMaterialVersion")
    implementation("com.google.android.gms:play-services-auth:$googlePlayServicesAuthVersion")
    implementation("androidx.compose.ui:ui-text-google-fonts:$androidxComposeRuntimeVersion")

    // Kotlinx
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$kotlinxCoroutinesVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinxCoroutinesVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:$kotlinxCoroutinesVersion")

    // Testing
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    //Glide
    implementation("com.github.bumptech.glide:glide:$glideVersion")

    //Compose
    implementation("androidx.compose.ui:ui-android:$composeVersion")
    implementation("androidx.compose.ui:ui-tooling:$composeVersion")
    implementation("androidx.compose.material:material-icons-extended:$composeVersion")
    implementation("androidx.compose.material3:material3:$material3Verion")

    //Coil
    implementation("io.coil-kt:coil-compose:$coilComposeVersion")

    //Play store updates
    implementation("com.google.android.play:app-update:$playUpdateVersion")
    implementation("com.google.android.play:app-update-ktx:$playUpdateVersion")

    //Firebase SDK
    implementation(platform("com.google.firebase:firebase-bom:$firebaseBomVersion"))
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
    implementation("com.firebaseui:firebase-ui-firestore:$firebaseUiVersion")
    implementation("com.firebaseui:firebase-ui-database:$firebaseUiVersion")

    //Picasso
    implementation("com.squareup.picasso:picasso:$picassoVersion")

    //Circle imageView
    implementation("de.hdodenhof:circleimageview:$circleimageviewVersion")

    //Material
    implementation("com.google.android.material:material:$googleMaterialVersion")

    //Klaxon (JSON parsing lib)
    implementation("com.beust:klaxon:$klaxonVersion")

    //Shimmer
    implementation("com.facebook.shimmer:shimmer:$shimmerVersion")

    //Android about page
    implementation("io.github.medyo:android-about-page:$androidAboutPageVersion")

    // Material Dialog Library
    implementation("dev.shreyaspatil.MaterialDialog:MaterialDialog:$materialDialogVersion")

    // Lottie
    implementation("com.airbnb.android:lottie:$lottieVersion")
    implementation("com.airbnb.android:lottie-compose:$lottieVersion")

    //Fragment
    implementation("androidx.fragment:fragment-ktx:$androidxFragmentVersion")

    // Chip text field
    implementation("io.github.dokar3:chiptextfield-m3:$chiptextfieldVersion")

    //Parse csv
    implementation("org.apache.commons:commons-csv:$csvVersion")
}
apply(plugin = "com.google.gms.google-services")
