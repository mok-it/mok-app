buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.google.firebase:firebase-crashlytics-gradle:2.9.9")
        classpath("com.android.tools.build:gradle:8.2.2")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.22")
        classpath("com.google.gms:google-services:4.4.1") // Google Services plugin
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.7.7")
    }
}