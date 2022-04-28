plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-kapt")
}
android {
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = compose.versions.compose.get()
    }
}

dependencies {
    implementation(project(Modules.commonModels))
    implementation(project(Modules.commonResources))
    implementation(project(Modules.coreUi))
    implementation(project(Modules.core))
    implementation(project(Modules.coreApi))
    implementation(project(Modules.uiComponents))
    implementation(project(Modules.commonExtensions))
    implementation(project(Modules.coreCatalogs))

    implementation(compose.compose.icons)
    implementation(compose.compose.navigation)
    implementation(compose.compose.foundation)
    implementation(compose.compose.animations)
    implementation(compose.compose.material)
    implementation(compose.compose.uiToolingPreview)
    implementation(accompanist.pagerIndicator)
    implementation(accompanist.pager)
    implementation(accompanist.swipeRefresh)
    implementation(libs.hilt.android)
    implementation(project(mapOf("path" to ":domain")))
    kapt(libs.hilt.androidcompiler)
}
