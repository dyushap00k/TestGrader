package com.testgrader

import org.opencv.android.OpenCVLoader
import javax.inject.Inject

class OpenCvInitializer @Inject constructor() : Initializer {
    override fun initialize() {
        OpenCVLoader.initLocal()
    }
}