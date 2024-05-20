import org.jetbrains.kotlin.gradle.dsl.JvmTarget

val formConfig = mapOf(
    "FORM_WIDTH" to extra["formWidth"].toString(),
    "FORM_HEIGHT" to extra["formHeight"].toString(),
    "TRIANGLE_LEG_LENGTH" to extra["triangleLegLength"].toString(),
    "BLOCK_GROUP_OFFSET_X" to extra["blockGroupOffsetX"].toString(),
    "BLOCK_GROUP_OFFSET_Y" to extra["blockGroupOffsetY"].toString(),
    "BLOCK_COUNT" to extra["blockCount"].toString(),
    "BLOCK_OFFSET" to extra["blockOffset"].toString(),
    "OPTION_GROUP_COUNT" to extra["optionGroupCount"].toString(),
    "OPTION_GROUP_OFFSET" to extra["optionGroupOffset"].toString(),
    "OPTION_COUNT" to extra["optionCount"].toString(),
    "OPTION_SIZE" to extra["optionSize"].toString(),
    "OPTION_OFFSET" to extra["optionOffset"].toString(),
    "GRADE_BOX_OFFSET_X" to extra["gradeBoxOffsetX"].toString(),
    "GRADE_BOX_OFFSET_Y" to extra["gradeBoxOffsetY"].toString(),
    "GRADE_BOX_WIDTH" to extra["gradeBoxWidth"].toString(),
    "GRADE_BOX_HEIGHT" to extra["gradeBoxHeight"].toString()
)

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.formscanner"
    compileSdk = 34
    android.buildFeatures.buildConfig = true

    buildTypes.configureEach {
        for ((key, value) in formConfig) {
            buildConfigField("int", key, value)
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
    }

    kotlinOptions.jvmTarget = JvmTarget.JVM_17.target
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation(libs.timber)
    api(project(":opencv"))
}