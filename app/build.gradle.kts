plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}
android {
    namespace = "com.ronhalder.rgbkeyboard"
    compileSdk = 35
    defaultConfig {
        applicationId = "com.ronhalder.rgbkeyboard"
        minSdk = 23
        targetSdk = 28
        versionCode = 1
        versionName = "1.0"
    }
}
