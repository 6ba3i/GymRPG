plugins { alias(libs.plugins.android.library) }
android {
    namespace = "com.gymrpg.core.integrations"
    compileSdk = 37
    defaultConfig { minSdk = 29; testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner" }
    compileOptions { sourceCompatibility = JavaVersion.VERSION_17; targetCompatibility = JavaVersion.VERSION_17 }
    lint { abortOnError = true }
}
kotlin { jvmToolchain(17) }
dependencies {
    implementation(libs.androidx.core.ktx)
    testImplementation(libs.junit)
}
