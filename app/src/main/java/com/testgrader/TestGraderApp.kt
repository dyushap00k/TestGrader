package com.testgrader

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import org.opencv.android.OpenCVLoader
import javax.inject.Inject

@HiltAndroidApp
class TestGraderApp : Application() {
    @Inject
    lateinit var initializers: Set<@JvmSuppressWildcards Initializer>

    override fun onCreate() {
        super.onCreate()
        executeInitializers()
    }

    private fun executeInitializers() {
        initializers.forEach { it.initialize() }
    }
}