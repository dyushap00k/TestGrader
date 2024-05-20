package com.formscanner

import com.formscanner.detector.DetectionArea
import com.formscanner.detector.asInt
import com.formscanner.detector.x2
import com.formscanner.detector.y2
import org.opencv.core.Mat
import org.opencv.core.Rect
import org.opencv.core.Scalar
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc
import kotlin.math.max

private const val SCALING_MINIMUM_FRAME_SIDE = 500.0
private const val THRESHOLD_MAX_VALUE = 255.0
private const val THRESHOLD_BLOCK_SIZE = 25
private const val THRESHOLD_CONSTANT = 50.0

internal object FramePreprocessor {
    fun preprocess(frame: Mat): PreprocessingResult = with(frame) {
        val scaleFactor = scale()
        toGrayscale()
        toBlackAndWhite()
        fillBeyondDetectionArea()

        PreprocessingResult(scaleFactor = scaleFactor)
    }

    private fun Mat.scale(): Double {
        val horizontalScale = SCALING_MINIMUM_FRAME_SIDE / width()
        val verticalScale = SCALING_MINIMUM_FRAME_SIDE / height()
        val scale = max(horizontalScale, verticalScale)
        Imgproc.resize(this, this, Size(), scale, scale)
        return scale
    }

    private fun Mat.toGrayscale() {
        Imgproc.cvtColor(this, this, Imgproc.COLOR_RGB2GRAY)
    }

    private fun Mat.toBlackAndWhite() {
        Imgproc.adaptiveThreshold(
            this, this,
            THRESHOLD_MAX_VALUE,
            Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C,
            Imgproc.THRESH_BINARY_INV,
            THRESHOLD_BLOCK_SIZE, THRESHOLD_CONSTANT
        )
    }

    private fun Mat.fillBeyondDetectionArea() {
        val detectionArea = DetectionArea.calculate(width(), height()).asInt()
        val sections = with(detectionArea) {
            listOf(
                Rect(0, 0, width(), y),
                Rect(0, y2, width(), y),
                Rect(0, 0, x, height()),
                Rect(x2, 0, x, height())
            )
        }
        val blackColor = Scalar(0.0, 0.0, 0.0, 0.0)
        for (section in sections) {
            Imgproc.rectangle(this, section, blackColor, -1)
        }
    }
}