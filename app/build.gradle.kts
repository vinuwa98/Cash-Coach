plugins {
}

android {
    namespace = "com.s22010554.expensemanagerapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.s22010554.expensemanagerapp"
        minSdk = 30
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
}

dependencies {

}