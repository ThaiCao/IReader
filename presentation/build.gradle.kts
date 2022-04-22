plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-kapt")
    id("kotlinx-serialization")
    id("dagger.hilt.android.plugin")
}

android {
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = compose.versions.compose.get()
    }
    lint {
        baseline = file("lint-baseline.xml")
    }
}

dependencies {

    implementation(project(Modules.domain))
    implementation(project(Modules.core))
    implementation(project(Modules.coreUi))
    implementation(project(Modules.coreApi))

    implementation(compose.compose.foundation)
    implementation(compose.compose.ui)
    implementation(compose.compose.compiler)
    implementation(compose.compose.activity)

    implementation(compose.compose.material)
    implementation(compose.compose.uiToolingPreview)

    implementation(compose.compose.icons)
    implementation(compose.compose.navigation)
    implementation(compose.compose.coil)
    implementation(compose.compose.hiltNavigation)
    implementation(compose.compose.lifecycle)
    implementation(compose.compose.animations)




    implementation(accompanist.flowlayout)
    implementation(accompanist.navAnimation)
    implementation(accompanist.pagerIndicator)
    implementation(accompanist.systemUiController)
    implementation(accompanist.pager)
    implementation(accompanist.swipeRefresh)
    implementation(accompanist.web)

    implementation(androidx.appCompat)
    implementation(androidx.media)
    implementation(androidx.core)
    implementation(androidx.material)
    implementation(androidx.emoji)

    implementation(androidx.work.runtime)

    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    kapt(libs.room.compiler)

    implementation(libs.hilt.android)
    kapt(libs.hilt.androidcompiler)
   // kapt(libs.hilt.compiler)


    testImplementation(test.bundles.common)
    androidTestImplementation(test.bundles.common)
    androidTestImplementation(compose.compose.uiTestManifest)
    androidTestImplementation(compose.compose.testing)
    androidTestImplementation(compose.compose.composeTooling)
}

