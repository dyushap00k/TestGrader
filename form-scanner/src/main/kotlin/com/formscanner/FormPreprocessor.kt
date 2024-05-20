package com.formscanner

import org.opencv.core.Mat
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc

private const val CLEAN_KERNEL_SIZE = 5.0

internal object FormPreprocessor {
    fun preprocess(form: Mat) = with(form) {
        removeEmptyOptions()
    }

    private fun Mat.removeEmptyOptions() {
        val kernel = Imgproc.getStructuringElement(
            Imgproc.MORPH_RECT,
            Size(CLEAN_KERNEL_SIZE, CLEAN_KERNEL_SIZE)
        )
        Imgproc.erode(this, this, kernel)
        Imgproc.dilate(this, this, kernel)
    }
}