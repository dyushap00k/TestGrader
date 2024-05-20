package com.formscanner

import com.formscanner.detector.Detector
import com.formscanner.detector.FormPosition
import org.opencv.core.Mat
import org.opencv.core.MatOfPoint2f
import org.opencv.core.Point
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc

object Scanner {
    fun scan(frame: Mat): ScanResult? {
        val preprocessingResult = FramePreprocessor.preprocess(frame)
        val formPosition = Detector.detect(frame) ?: return null
        val form = temporaryMat { form ->
            prepareForm(frame, form, formPosition)
            Evaluator.evaluate(form)
        }
        val originalFormPosition = formPosition
            .toOriginalScale(preprocessingResult.scaleFactor)
        return ScanResult(form, originalFormPosition)
    }

    private fun prepareForm(
        frame: Mat, form: Mat,
        formPosition: FormPosition
    ) {
        val formSize = calculateFormSize(formPosition)
        form.create(formSize, frame.type())
        correctPerspective(frame, form, formPosition)
        FormPreprocessor.preprocess(form)
    }

    private fun calculateFormSize(formPosition: FormPosition): Size {
        val formBoundingBox = formPosition.getBoundingBox()
        val isVerticallyConstrained =
            formBoundingBox.width / formBoundingBox.height.toDouble() > FormMetrics.RATIO

        val width: Double
        val height: Double
        if (isVerticallyConstrained) {
            width = formBoundingBox.width.toDouble()
            height = formBoundingBox.width / FormMetrics.RATIO
        } else {
            width = formBoundingBox.height * FormMetrics.RATIO
            height = formBoundingBox.height.toDouble()
        }
        return Size(width, height)
    }

    private fun correctPerspective(
        frame: Mat, form: Mat,
        formPosition: FormPosition
    ) {
        val sourceCoordinates = MatOfPoint2f(*formPosition.corners.toTypedArray())
        val destinationCoordinates = MatOfPoint2f(
            Point(0.0, 0.0),
            Point(form.width().toDouble(), 0.0),
            Point(form.width().toDouble(), form.height().toDouble()),
            Point(0.0, form.height().toDouble())
        )
        val transformation = Imgproc.getPerspectiveTransform(
            sourceCoordinates, destinationCoordinates
        )
        Imgproc.warpPerspective(frame, form, transformation, form.size())
    }

    private fun FormPosition.toOriginalScale(scaleFactor: Double): FormPosition {
        val scaledCorners = corners.map {
            Point(it.x / scaleFactor, it.y / scaleFactor)
        }
        return FormPosition(*scaledCorners.toTypedArray())
    }
}