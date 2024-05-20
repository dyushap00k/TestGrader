package com.formscanner.detector

import kotlin.math.ceil

fun DetectionArea<Double>.asInt() =
    DetectionArea(
        ceil(x).toInt(),
        ceil(y).toInt(),
        ceil(width).toInt(),
        ceil(height).toInt(),
        ceil(horizontalPadding).toInt(),
        ceil(verticalPadding).toInt()
    )

fun DetectionArea<Double>.asFloat() =
    DetectionArea(
        x.toFloat(),
        y.toFloat(),
        width.toFloat(),
        height.toFloat(),
        horizontalPadding.toFloat(),
        verticalPadding.toFloat()
    )

val DetectionArea<Double>.paddedWidth
    get() = width - horizontalPadding * 2.0

val DetectionArea<Double>.paddedHeight
    get() = height - verticalPadding * 2.0

val DetectionArea<Double>.area
    get() = width * height

val DetectionArea<Double>.paddedArea
    get() = paddedWidth * paddedHeight


val DetectionArea<Float>.x2
    get() = x + width

val DetectionArea<Float>.y2
    get() = y + height


val DetectionArea<Int>.x2
    get() = x + width

val DetectionArea<Int>.y2
    get() = y + height