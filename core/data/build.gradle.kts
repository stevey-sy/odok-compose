import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt.plugin)
}

android {
    namespace = "com.sy.odokcompose.core.data"
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
            buildConfigField("String", "SUPABASE_URL", gradleLocalProperties(rootDir, providers).getProperty("SUPABASE_URL"))
            buildConfigField("String", "SUPABASE_ANON_KEY", gradleLocalProperties(rootDir, providers).getProperty("SUPABASE_ANON_KEY"))
        }

        release {
            buildConfigField("String", "SUPABASE_URL", gradleLocalProperties(rootDir, providers).getProperty("SUPABASE_URL"))
            buildConfigField("String", "SUPABASE_ANON_KEY", gradleLocalProperties(rootDir, providers).getProperty("SUPABASE_ANON_KEY"))
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

    // Project modules
    implementation(project(":core:model"))
    implementation(project(":core:network"))
    implementation(project(":core:database"))

    // Hilt
    implementation(libs.hilt.android)
    implementation(libs.firebase.auth.ktx)
    ksp(libs.hilt.compiler)
    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${libs.versions.coroutines.get()}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:${libs.versions.coroutines.get()}")

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Supabase 추가
    implementation(platform("io.github.jan-tennert.supabase:bom:3.2.2"))
    implementation("io.github.jan-tennert.supabase:postgrest-kt")
    implementation("io.github.jan-tennert.supabase:auth-kt")
    implementation("io.github.jan-tennert.supabase:realtime-kt")

    // Paging
    implementation(libs.androidx.paging.runtime)
}