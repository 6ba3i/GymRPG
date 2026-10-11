plugins { alias(libs.plugins.android.application); alias(libs.plugins.kotlin.compose) }
android {
    namespace = "com.gymrpg"
    compileSdk = 37
    defaultConfig {
        applicationId = "com.gymrpg"
        minSdk = 29
        targetSdk = 37
        versionCode = 1
        versionName = "0.1.0-m0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    compileOptions { sourceCompatibility = JavaVersion.VERSION_17; targetCompatibility = JavaVersion.VERSION_17 }
    buildFeatures { compose = true; buildConfig = true }
    lint { abortOnError = true }
    packaging { resources.excludes += "/META-INF/{AL2.0,LGPL2.1}" }
}
kotlin { jvmToolchain(17) }
dependencies {
    implementation(project(":core:designsystem"))
    implementation(project(":feature:anatomy"))
    implementation(project(":core:integrations"))
    implementation(project(":core:notifications"))
    implementation(project(":integration:xiaomi-cn"))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation("androidx.navigation:navigation-compose:2.9.0")
    implementation("androidx.datastore:datastore-preferences:1.1.7")
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation("androidx.test.ext:junit:1.3.0")
    androidTestImplementation("androidx.test:runner:1.7.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.7.0")
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
