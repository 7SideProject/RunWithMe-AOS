plugins {
    id("java-library")
    id("org.jetbrains.kotlin.jvm")
    id("kotlin-kapt")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {

    // hilt
    implementation(Dependencies.DAGGER_HILT_CORE)

    // paging
    implementation(Dependencies.PAGING_COMMON)

    // coroutine
    implementation(Dependencies.COROUTINE_CORE)

    // gson - serializable 사용하기 위함
    implementation(Dependencies.GSON)

    testImplementation(Test.JUNIT)
}