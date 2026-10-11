plugins { alias(libs.plugins.android.library); alias(libs.plugins.kotlin.compose) }
android {
    namespace = "com.gymrpg.feature.anatomy"
    compileSdk = 37
    defaultConfig { minSdk = 29; testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner" }
    compileOptions { sourceCompatibility = JavaVersion.VERSION_17; targetCompatibility = JavaVersion.VERSION_17 }
    buildFeatures { compose = true }
    lint { abortOnError = true }
}
kotlin { jvmToolchain(17) }
dependencies {
    implementation(libs.androidx.core.ktx)
    testImplementation(libs.junit)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(project(":core:designsystem"))
}
