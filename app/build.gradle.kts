plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)

    id ("kotlin-kapt")
}

android {
    namespace = "com.example.skycast"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.skycast"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
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

}

dependencies {

    // Room database
    implementation("androidx.room:room-ktx:2.6.1")
    implementation("androidx.room:room-runtime:2.6.1")
    implementation(libs.play.services.maps)
    implementation(libs.androidx.preference.ktx)
    implementation(libs.androidx.junit.ktx)
    kapt("androidx.room:room-compiler:2.6.1")


    // Retrofit for network requests
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")


    // Kotlin Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")


    // Work Manager
    implementation("androidx.work:work-runtime-ktx:2.7.1")


    // Glide for image loading
    implementation("com.github.bumptech.glide:glide:4.15.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.15.0")

    // Lifecycle components
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.1")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.5.1")


    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.15.2")
    implementation ("com.github.MatteoBattilana:WeatherView:3.0.0")


    implementation ("androidx.navigation:navigation-fragment-ktx:2.6.0")
    implementation ("androidx.navigation:navigation-ui-ktx:2.6.0")


    implementation ("com.github.Dimezis:BlurView:version-2.0.5")


    implementation ("androidx.work:work-runtime-ktx:2.8.0")


    //Location
    implementation("org.osmdroid:osmdroid-android:6.1.10")
//    implementation("libs.play.services.maps")
    implementation("com.google.android.gms:play-services-location:21.0.1")



    // Testing Libraries
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    testImplementation("androidx.arch.core:core-testing:2.1.0")
    testImplementation("com.squareup.okhttp3:mockwebserver:4.9.3")

    // AndroidX Test Libraries
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
    androidTestImplementation("androidx.test:core:1.4.0")
    androidTestImplementation("androidx.test:rules:1.4.0")
    androidTestImplementation("androidx.test:runner:1.4.0")


    implementation("com.google.android.material:material:1.9.0")

    testImplementation("io.mockk:mockk:1.12.0")
    testImplementation ("org.robolectric:robolectric:4.9")




}