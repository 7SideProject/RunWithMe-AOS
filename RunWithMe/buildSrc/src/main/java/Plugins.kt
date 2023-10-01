object Plugins {
    const val ANDROID_APPLICATION = "com.android.application"
    const val ANDROID_LIBRARY = "com.android.library"
    const val KOTLIN_ANDROID = "org.jetbrains.kotlin.android"
    const val DAGGER_HILT = "dagger.hilt.android.plugin"
    const val SAFEARGS = "androidx.navigation.safeargs.kotlin"
    const val SECRETS_GRADLE_PLUGIN = "com.google.android.libraries.mapsplatform.secrets-gradle-plugin"
    const val KAPT = "kotlin-kapt"
    const val GOOGLE_SERVICE = "com.google.gms.google-services"
    const val FIREBASE_CRASHLYTICS_PLUGINS = "com.google.firebase.crashlytics"

    // dependency
    const val FIREBASE_CRASHLYTICS = "com.google.firebase:firebase-crashlytics-gradle:${Versions.FIREBASE_CRASHLYTICS}"
}