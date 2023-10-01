plugins {
    id(Plugins.ANDROID_LIBRARY)
    id(Plugins.KOTLIN_ANDROID)
    id(Plugins.DAGGER_HILT)
    id(Plugins.KAPT)
}

fun getProperty(propertyKey: String): String {
    return com.android.build.gradle.internal.cxx.configure.gradleLocalProperties(rootDir).getProperty(propertyKey)
}

android {
    namespace = "com.side.data"
    compileSdk = DefaultConfig.COMPILE_SDK

    defaultConfig {
        minSdk = DefaultConfig.MIN_SDK
        targetSdk = DefaultConfig.TARGET_SDK

        testInstrumentationRunner = DefaultConfig.HILT_TEST_RUNNER


        consumerProguardFiles("consumer-rules.pro")
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled  =  false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation(project(":domain"))

    implementation(Ktx.CORE)

//    implementation(AndroidX.APP_COMPAT)
//    implementation(AndroidX.CONSTRAINT_LAYOUT)
//    implementation(AndroidX.METERIAL)

    // Test
    testImplementation(Test.JUNIT)

    // Dagger Hilt
    implementation(Dependencies.DAGGER_HILT)
    kapt(Dependencies.KAPT_HILT)

    // Retrofit
    implementation(Dependencies.RETROFIT)

    // Converter ( JSON 타입 결과를 객체로 매핑 )
    implementation(Dependencies.GSON)

    // okhttp3
    implementation(Dependencies.OKHTTP)
    implementation(Dependencies.OKHTTP_LOGGING_INTERCEPTOR)

    // Paging
    implementation(Dependencies.PAGING)

    // datastore
    implementation(Dependencies.DATASTORE_PREFERENCES)

    // Room
    implementation(Dependencies.ROOM_RUNTIME)
    annotationProcessor(Dependencies.ROOM_KAPT)
    kapt(Dependencies.ROOM_KAPT)
    implementation(Dependencies.ROOM_COROUTINE)

    implementation(AndroidX.VIEWMODEL)


}

