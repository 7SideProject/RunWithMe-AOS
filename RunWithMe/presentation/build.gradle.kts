import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import com.android.build.gradle.internal.dsl.ManagedVirtualDevice
import java.util.Properties
import java.io.FileInputStream

plugins {
    id(Plugins.ANDROID_APPLICATION)
    id(Plugins.KOTLIN_ANDROID)
    id(Plugins.DAGGER_HILT)
    id(Plugins.SAFEARGS)
    id(Plugins.SECRETS_GRADLE_PLUGIN)
    id(Plugins.KAPT)
    id("kotlin-parcelize")
    id(Plugins.GOOGLE_SERVICE)
//    id("com.google.gms.google-services")
    id(Plugins.FIREBASE_CRASHLYTICS_PLUGINS)
}

val localProperties = Properties().apply {
    // `rootProject.file("local.properties")`를 통해 프로젝트 루트에 위치한 `local.properties` 파일을 참조합니다.
    load(FileInputStream(rootProject.file("local.properties")))
}

android {
    namespace = "com.side.runwithme"
    compileSdk = DefaultConfig.COMPILE_SDK

    defaultConfig {
        applicationId = "com.side.runwithme"
        minSdk = DefaultConfig.MIN_SDK
        targetSdk = DefaultConfig.TARGET_SDK
        versionCode = DefaultConfig.VERSION_CODE
        versionName = DefaultConfig.VERSION_NAME
//        testInstrumentationRunner "androidx.test.runner.AndroidJUnitRunner"
        testInstrumentationRunner = DefaultConfig.HILT_TEST_RUNNER

        buildConfigField("String", "BASEURL", project.properties["BASEURL"].toString())
        buildConfigField("String", "NAVERAPIKEY", project.properties["NAVERAPIKEY"].toString())
        buildConfigField("String", "KAKAOAPIKEY", project.properties["KAKAOAPIKEY"].toString())
        buildConfigField("String", "MAIL_ID", project.properties["MAIL_ID"].toString())
        buildConfigField("String", "MAIL_PASSWORD", project.properties["MAIL_PASSWORD"].toString())

        manifestPlaceholders["NAVERAPIKEY"] = project.properties["NAVERAPIKEY"].toString()
        manifestPlaceholders["KAKAOAPIKEY"] = project.properties["KAKAOAPIKEY"].toString()
    }

//    android {
//        testOptions {
//            managedDevices {
//                devices {
//                    pixel2api30(com.android.build.api.dsl.ManagedVirtualDevice) {
//                        // Use device profiles you typically see in Android Studio.
//                        device = "Pixel 4"
//                        // Use only API levels 27 and higher.
//                        apiLevel = 30
//                        // To include Google services, use "google".
//                        systemImageSource = "aosp"
//                    }
//                }
//            }
//        }
//    }


    testOptions {

        ManagedVirtualDevice("pixel2api30").apply {
            device = "Pixel 2"
            apiLevel = 30
            systemImageSource = "aosp"
        }

    }

    signingConfigs {
        create("release") {
            // keystore
            keyAlias = localProperties.getProperty("KEY_ALIAS") ?: throw IllegalStateException("keyalias not found")
            keyPassword = localProperties.getProperty("KEY_PASSWORD") ?: throw IllegalStateException("keypassword not found")
            storePassword = localProperties.getProperty("STORE_PASSWORD") ?: throw IllegalStateException("storepassword not found")
            storeFile = file(localProperties.getProperty("STORE_FILE") ?: throw IllegalStateException("storefile not found"))
        }
    }

    buildTypes {
        getByName("release") {
            signingConfig = signingConfigs.getByName("release")

            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            ndk {
                debugSymbolLevel = "SYMBOL_TABLE"
            }
            buildConfigField("String", "BASEURL", project.properties["BASEURL"].toString())
            buildConfigField("String", "NAVERAPIKEY", project.properties["NAVERAPIKEY"].toString())
            buildConfigField("String", "KAKAOAPIKEY", project.properties["KAKAOAPIKEY"].toString())
            buildConfigField("String", "MAIL_ID", project.properties["MAIL_ID"].toString())
            buildConfigField(
                "String",
                "MAIL_PASSWORD",
                project.properties["MAIL_PASSWORD"].toString()
            )

            manifestPlaceholders["NAVERAPIKEY"] = project.properties["NAVERAPIKEY"].toString()
            manifestPlaceholders["KAKAOAPIKEY"] = project.properties["KAKAOAPIKEY"].toString()

        }
    }


    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        dataBinding = true
        buildConfig = true
    }
}

fun getProperty(propertyKey: String): String {
    return gradleLocalProperties(rootProject.rootDir).getProperty(propertyKey)
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":data"))

    implementation(Ktx.CORE)

    implementation(AndroidX.APP_COMPAT)
    implementation(AndroidX.CONSTRAINT_LAYOUT)
    implementation(AndroidX.LEGACY_SUPPORT)
    implementation(AndroidX.METERIAL)
    implementation(AndroidX.VIEWMODEL)
    implementation(files("libs\\activation.jar"))
    implementation(files("libs\\additionnal.jar"))
    implementation(files("libs\\mail.jar"))
//    implementation("androidx.lifecycle:lifecycle-viewmodel:${Versions.VIEWMODEL}")

    // Test
    testImplementation(Test.JUNIT)
    testImplementation(Test.ANDROID_JUNIT)
    testImplementation(Test.ANDROID_TRUTH)
    testImplementation(Test.ANDROID_RUNNER)
    testImplementation(Test.ANDROID_MOCK)
    testImplementation(Test.AGENT_MOCK)
    testImplementation(Test.COROUTINE_TEST)
    testImplementation(Test.HILT_TEST)
    kaptTest(Test.KAPT_HILT_TEST)

    testImplementation(Test.SLF4J_SIMPLE)

    androidTestImplementation(Test.JUNIT)
    androidTestImplementation(Test.ANDROID_JUNIT)
    androidTestImplementation(Test.ANDROID_TRUTH)
    androidTestImplementation(Test.ANDROID_RUNNER)
    androidTestImplementation(Test.ANDROID_MOCK)
    androidTestImplementation(Test.AGENT_MOCK)
    androidTestImplementation(Test.COROUTINE_TEST)
    androidTestImplementation(Test.HILT_TEST)
    kaptAndroidTest(Test.KAPT_HILT_TEST)
    androidTestImplementation(Test.ESPRESSO_CORE)


    // LiveData
    implementation(Dependencies.LIVE_DATA)

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

    // Coroutines
    implementation(Dependencies.COROUTINE_ANDROID)

    // Jetpack Navigation Kotlin
    implementation(Dependencies.NAVIGATION_FRAGMENT)
    implementation(Dependencies.NAVIGATION_UI)

    // Permission
    implementation(Dependencies.TED_PERMISSION_NORMAL)

    // Glide
    implementation(Dependencies.GLIDE)
    kapt(Dependencies.KAPT_GLIDE)

    // Kakao
//    implementation(Dependencies.KAKAO_USER)

    // BaseView and SingleLiveEvent
//    implementation(Dependencies.SEOBASEVIEW)

    // Naver Map
    implementation(Dependencies.NAVER_MAP)

    // google location
    implementation(Dependencies.GOOGLE_LOCATION)

    // SimpleRatingBar
    implementation(Dependencies.SIMPLE_RATING_BAR)

    // lottie
    implementation(Dependencies.LOTTIE)

    // Circle ImageView
    implementation(Dependencies.CIRCLE_IMAGE_VIEW)

    // CalendarView
    implementation(Dependencies.CALENDAR_VIEW)

    // MPAndroid chart
    implementation(Dependencies.MP_ANDROID_CHART)

    // datastore
    implementation(Dependencies.DATASTORE_PREFERENCES)

    // Desugaring 자바 8 호환을 위함
    coreLibraryDesugaring(Dependencies.DESUGARING)

    // Paging
    implementation(Dependencies.PAGING)

    implementation("androidx.annotation:annotation:1.6.0")

    // Room
    implementation(Dependencies.ROOM_RUNTIME)
    annotationProcessor(Dependencies.ROOM_KAPT)
    kapt(Dependencies.ROOM_KAPT)
    implementation(Dependencies.ROOM_COROUTINE)

    // Firebase
    implementation(platform(Dependencies.FIREBASE_PLATFORM))
    implementation(Dependencies.FIREBASE_ANALYTICS)
    implementation(Dependencies.FIREBASE_AUTH)
    implementation(Dependencies.FIREBASE_CRASHLYTICS)

    // PlayStore Integerity
    implementation(Dependencies.PLAY_INTEGRITY)

    implementation("com.google.code.gson:gson:2.10.1")

    // 카카오 로그인
    implementation("com.kakao.sdk:v2-user:2.19.0")
}
