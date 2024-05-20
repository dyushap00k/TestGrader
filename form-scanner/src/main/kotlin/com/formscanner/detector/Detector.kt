package com.formscanner.detector

import com.formscanner.FormMetrics
import org.opencv.core.Mat
import org.opencv.core.Point
import java.util.Collections
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

private const val FORM_CORNER_COUNT = 4
private const val TRIANGLE_CORNER_COUNT = 3
private const val TRIANGLE_AREA_TOLERANCE = 0.40
private const val TRIANGLE_TO_CORNER_MAXIMUM_DISTANCE = 10.0

internal object Detector {
    fun detect(frame: Mat): FormPosition? {
        val detectionArea = DetectionArea.calculate(
            frame.width(), frame.height()
        )
        val contourTree = ContourTree.createFrom(frame)

        val formCorners = contourTree.asList()
            .firstNotNullOfOrNull { contour ->
                contour.asFormCornersOrNull(contourTree, detectionArea)
            } ?: return null
        return FormPosition(*formCorners.toTypedArray())
    }

    private fun Contour.asFormCornersOrNull(
        contourTree: ContourTree,
        detectionArea: DetectionArea<Double>
    ): List<Point>? {
        val isOutsideDetectionArea =
            !(area > detectionArea.paddedArea && area < detectionArea.area)
        if (isOutsideDetectionArea) return null

        val boxCorners = detectCorners()
        if (boxCorners.size != FORM_CORNER_COUNT) return null

        val triangleCorners = contourTree.getChildren(this)
            .firstNotNullOfOrNull { contour ->
                contour.asTriangleCornersOrNull(detectionArea)
            } ?: return null

        val (cornerWithTriangle, distance) =
            boxCorners.closestToTriangle(triangleCorners)

        val isTriangleMisplaced = distance > TRIANGLE_TO_CORNER_MAXIMUM_DISTANCE
        if (isTriangleMisplaced) return null

        return correctFormOrientation(boxCorners, cornerWithTriangle)
    }

    private fun Contour.asTriangleCornersOrNull(
        detectionArea: DetectionArea<Double>
    ): List<Point>? {
        val isWrongSize = !isRightSizeTriangle(this, detectionArea)
        if (isWrongSize) return null
        val corners = detectCorners()
        return if (corners.size == TRIANGLE_CORNER_COUNT) corners
        else null
    }

    private fun isRightSizeTriangle(
        contour: Contour,
        detectionArea: DetectionArea<Double>
    ): Boolean {
        val expectedArea = detectionArea.area * FormMetrics.Triangle.TO_FORM_AREA_RATIO
        val areaDifference = abs(contour.area - expectedArea)
        return areaDifference < expectedArea * TRIANGLE_AREA_TOLERANCE
    }

    private fun List<Point>.closestToTriangle(
        triangleCorners: List<Point>
    ): Pair<Point, Double> =
        flatMap { qPoint ->
            triangleCorners.map { tPoint ->
                qPoint to tPoint.distance(qPoint)
            }
        }.minBy { it.second }

    private fun Point.distance(other: Point): Double =
        sqrt((x - other.x).pow(2) + (y - other.y).pow(2))

    private fun correctFormOrientation(
        boxCorners: List<Point>,
        closestCorner: Point
    ): List<Point> {
        val oriented = boxCorners.toList()
        Collections.rotate(oriented, -oriented.indexOf(closestCorner))
        return oriented
    }
}