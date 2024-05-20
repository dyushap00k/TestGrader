package com.formscanner.detector

import org.opencv.core.CvType
import org.opencv.core.MatOfPoint
import org.opencv.core.MatOfPoint2f
import org.opencv.core.Point
import org.opencv.imgproc.Imgproc
import kotlin.math.abs

@JvmInline
internal value class Contour(private val mat: MatOfPoint) {
    val area: Double
        get() = abs(orientedArea)

    private val orientedArea: Double
        get() = Imgproc.contourArea(mat, true)

    fun detectCorners(): List<Point> {
        val contour2f = MatOfPoint2f()
        mat.convertTo(contour2f, CvType.CV_32F)

        val perimeter = Imgproc.arcLength(contour2f, true)
        val approximation = MatOfPoint2f()
        Imgproc.approxPolyDP(contour2f, approximation, perimeter * 0.04, true)
        val points = approximation.toList()

        val isCounterClockwise = orientedArea < 0
        return if (isCounterClockwise) points.reversed()
        else points
    }
}