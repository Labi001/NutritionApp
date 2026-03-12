
import org.jetbrains.kotlin.gradle.dsl.JvmTarget


plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.serialization)
    alias(libs.plugins.google.services)
}

kotlin {

    androidTarget() {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }
    
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
            export(libs.kmp.notifier)
        }
    }
    
    sourceSets {
        androidMain.dependencies {
            implementation(libs.ui.tooling.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.splash.screen)
            implementation(libs.koin.android)
            implementation(libs.ktor.android.client)
        }
        iosMain.dependencies {
            implementation(libs.ktor.darwin.client)
        }
        commonMain.dependencies {
            implementation(libs.runtime)
            implementation(libs.foundation)
            implementation(libs.material3)
            implementation(libs.ui)
            implementation(libs.components.resources)
            implementation(libs.ui.tooling.preview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)





            implementation(libs.messagebar.kmp)
            implementation(libs.browser.kmp)


            //Settings
            implementation(libs.multiplatform.settings)
            implementation(libs.multiplatform.settings.no.arg)
            implementation(libs.multiplatform.settings.make.observable)


            //Firebase
            implementation(libs.auth.kmp)
            implementation(libs.auth.firebase.kmp)
            implementation(libs.firebase.firestore)
            implementation(libs.firebase.storage)


            //SupaBase
            implementation(project.dependencies.platform(libs.supabase.bom))
            implementation(libs.supa.realtime)
            implementation(libs.postgrest.kt)
            implementation(libs.supabase.storage)
            implementation(libs.supabase.auth)

            //Navigator
            implementation(libs.jetbrains.navigation3.ui)
            implementation(libs.jetbrains.lifecycle.viewmodel.nav3)
            implementation(libs.jetbrains.lifecycle.viewmodel)
            implementation(libs.jetbrains.navigation3)


            //Coil
            implementation(libs.coil3)
            implementation(libs.coil3.compose)
            implementation(libs.coil3.compose.core)
            implementation(libs.coil3.network.ktor)

            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.serialization)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.kotlinx.coroutines.core)



            //Notification
            api(libs.kmp.notifier)


            //Serialization
            implementation(libs.kotlinx.serialization)
            implementation(libs.kotlinx.serialization.core)

            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)


        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

android {
    namespace = "com.labinot.bajrami.nutritionapp"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.labinot.bajrami.nutritionapp"
        minSdk = libs.versions.android.minSdk.get().toInt()
        //noinspection OldTargetApi
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    debugImplementation(libs.ui.tooling)
}

