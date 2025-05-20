import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt.plugin)
}

android {
    namespace = "com.sy.odokcompose.core.network"
    compileSdk = 35

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        debug {
            buildConfigField("String", "ALADIN_TTB_KEY", gradleLocalProperties(rootDir, providers).getProperty("ALADIN_TTB_KEY"))
        }
        release {
            buildConfigField("String", "ALADIN_TTB_KEY", gradleLocalProperties(rootDir, providers).getProperty("ALADIN_TTB_KEY"))
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
}

dependencies {
    implementation(libs.androidx.core.ktx)
    
    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:${libs.versions.retrofit.get()}")
    implementation("com.squareup.retrofit2:converter-moshi:${libs.versions.retrofit.get()}")
    
    // OkHttp
    implementation("com.squareup.okhttp3:okhttp:${libs.versions.okHttp.get()}")
    implementation("com.squareup.okhttp3:logging-interceptor:${libs.versions.okHttp.get()}")
    
    // Moshi
    implementation(libs.moshi)
    ksp(libs.moshi.codegen)

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${libs.versions.coroutines.get()}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:${libs.versions.coroutines.get()}")
    
    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}