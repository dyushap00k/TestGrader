package com.formscanner.detector

import org.opencv.core.Point
import org.opencv.core.Rect

data class FormPosition(
    val topLeft: Point,
    val topRight: Point,
    val bottomRight: Point,
    val bottomLeft: Point
) {
    val corners: List<Point> =
        listOf(topLeft, topRight, bottomRight, bottomLeft)

    constructor(vararg corners: Point) : this(corners[0], corners[1], corners[2], corners[3]) {
        require(corners.size == 4) { "The number of points should be strictly 4." }
    }

    fun getBoundingBox(): Rect {
        val x1 = corners.minOf { it.x }.toInt()
        val y1 = corners.minOf { it.y }.toInt()
        val x2 = corners.maxOf { it.x }.toInt()
        val y2 = corners.maxOf { it.y }.toInt()
        return Rect(x1, y1, x2 - x1, y2 - y1)
    }

    companion object {
        val Empty = FormPosition(Point(), Point(), Point(), Point())
    }
}