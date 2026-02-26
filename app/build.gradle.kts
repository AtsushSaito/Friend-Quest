import java.util.Properties // 明示的なimportを追加

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose) // Jetpack Compose Compiler plugin
    alias(libs.plugins.google.devtools.ksp) // KSP Plugin for Room
}

android {
    namespace = "com.example.friendquest"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.friendquest"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    // local.properties を読む
    val localPropsFile = project.rootProject.file("local.properties")
    val properties = Properties() // importしたので java.util を省略可能
    if (localPropsFile.exists()) {
        localPropsFile.inputStream().use { properties.load(it) } // .use でストリームを確実にクローズ
    }
    val geminiApiKey = properties.getProperty("GEMINI_API_KEY", "")
    val geminiModelName = properties.getProperty("GEMINI_MODEL_NAME", "gemini-flash-latest")

    buildTypes {
        debug {
            buildConfigField("String", "GEMINI_API_KEY", "\"$geminiApiKey\"")
            buildConfigField("String", "GEMINI_MODEL_NAME", "\"$geminiModelName\"")
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField("String", "GEMINI_API_KEY", "\"$geminiApiKey\"")
            buildConfigField("String", "GEMINI_MODEL_NAME", "\"$geminiModelName\"")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true // BuildConfig機能を有効化
    }
    // composeOptions {
    //     kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    // }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)

    // Gemini API client library
    implementation("com.google.ai.client.generativeai:generativeai:0.6.0") // Directly specified version

    // Room dependencies
    implementation("androidx.room:room-runtime:2.7.2")
    implementation("androidx.room:room-ktx:2.7.2")
    ksp("androidx.room:room-compiler:2.7.2")

    // Navigation Compose
    implementation("androidx.navigation:navigation-compose:2.7.7")

    // ViewModel Compose
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.9.3")

    // testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
