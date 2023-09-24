// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {

    dependencies {
        classpath("com.google.dagger:hilt-android-gradle-plugin:${Versions.HILT}")
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:${Versions.NAVIGATION}")
        classpath("com.android.tools.build:gradle:${Versions.AGP}")
        classpath("com.google.gms:google-services:4.3.10")
        classpath("com.google.firebase:firebase-crashlytics-gradle:2.8.1")
    }
    repositories {
        google()
    }
    //    repositories {
//        google()
//        maven(url = "https://jitpack.io")
//    }
}
plugins {
    id(Plugins.ANDROID_APPLICATION) version Versions.AGP apply false
    id(Plugins.ANDROID_LIBRARY) version Versions.AGP apply false
    id(Plugins.KOTLIN_ANDROID) version Versions.KOTLIN apply false
    id(Plugins.SECRETS_GRADLE_PLUGIN) version Versions.SECRETS_GRALDE apply false
    id(Plugins.GOOGLE_SERVICE) version Versions.GOOGLE_SERVICE apply false
}
