package com.testgrader.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.formscanner.detector.DetectionArea
import com.formscanner.detector.asFloat
import com.formscanner.detector.x2
import com.formscanner.detector.y2

class DetectionBoundariesView(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    private val paint = Paint()
    var isFormDetected by invalidatedState(false)

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawBoundaries(canvas)
    }

    private fun drawBoundaries(canvas: Canvas) {
        val detectionArea = DetectionArea.calculate(width, height).asFloat()

        val boundariesColor = if (isFormDetected) Color.GREEN else Color.WHITE
        paint.color = boundariesColor
        paint.strokeWidth = 8f
        val halfStroke = paint.strokeWidth / 2
        val boundaryLength = 100

        val boundaryLines = with(detectionArea) {
            floatArrayOf(
                x - halfStroke, y,
                x + boundaryLength - halfStroke, y,
                x, y,
                x, y + boundaryLength,

                x2 + halfStroke, y,
                x2 - boundaryLength + halfStroke, y,
                x2, y,
                x2, y + boundaryLength,

                x - halfStroke, y2,
                x + boundaryLength - halfStroke, y2,
                x, y2,
                x, y2 - boundaryLength,

                x + width + halfStroke, y2,
                x + width - boundaryLength + halfStroke, y2,
                x + width, y2,
                x + width, y2 - boundaryLength
            )
        }
        canvas.drawLines(boundaryLines, paint)
    }
}