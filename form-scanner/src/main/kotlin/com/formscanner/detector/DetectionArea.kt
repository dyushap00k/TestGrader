package com.formscanner.detector

import com.formscanner.FormMetrics

private const val MARGIN_FRACTION = 0.015
private const val PADDING_FRACTION = 0.15

data class DetectionArea<T : Number>(
    val x: T,
    val y: T,
    val width: T,
    val height: T,
    val horizontalPadding: T,
    val verticalPadding: T
) {
    companion object {
        fun calculate(frameWidth: Int, frameHeight: Int): DetectionArea<Double> {
            require(MARGIN_FRACTION in 0.0..50.0)
            require(PADDING_FRACTION in 0.0..50.0)

            val paddedWidth = frameWidth - frameWidth * MARGIN_FRACTION * 2.0
            val paddedHeight = frameHeight - frameHeight * MARGIN_FRACTION * 2.0

            val isVerticallyConstrained = paddedWidth / paddedHeight > FormMetrics.RATIO

            return if (isVerticallyConstrained) DetectionArea(
                x = (frameWidth - paddedHeight * FormMetrics.RATIO) / 2.0,
                y = frameHeight * MARGIN_FRACTION,
                width = paddedHeight * FormMetrics.RATIO,
                height = paddedHeight,
                verticalPadding = paddedHeight * PADDING_FRACTION,
                horizontalPadding = paddedWidth * PADDING_FRACTION
            ) else DetectionArea(
                x = frameWidth * MARGIN_FRACTION,
                y = (frameHeight - paddedWidth / FormMetrics.RATIO) / 2.0,
                width = paddedWidth,
                height = paddedWidth / FormMetrics.RATIO,
                verticalPadding = paddedHeight * PADDING_FRACTION,
                horizontalPadding = paddedWidth * PADDING_FRACTION
            )
        }
    }
}