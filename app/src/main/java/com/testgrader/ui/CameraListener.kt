package com.testgrader.ui

import com.formscanner.ScanResult
import com.formscanner.Scanner
import com.formscanner.copy
import com.formscanner.detector.DetectionArea
import com.formscanner.detector.FormPosition
import kotlinx.coroutines.flow.MutableSharedFlow
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2
import org.opencv.core.Core
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.MatOfPoint2f
import org.opencv.core.Point
import org.opencv.core.Scalar
import org.opencv.imgproc.Imgproc

class CameraListener : CvCameraViewListener2 {
    val eventStream = MutableSharedFlow<CameraListenerEvent>(replay = 1)
    private var currentScan = ScanResult.Empty
    private var capturedScan = ScanResult.Empty

    override fun onCameraViewStarted(width: Int, height: Int) {
        eventStream.tryEmit(CameraListenerEvent.Started)
    }

    override fun onCameraViewStopped() {
        eventStream.tryEmit(CameraListenerEvent.Stopped)
    }

    @Synchronized
    fun captureScan() {
        capturedScan = currentScan
    }

    @Synchronized
    override fun onCameraFrame(inputFrame: Mat): Mat {
        currentScan = inputFrame.copy { scanFrame ->
            Scanner.scan(scanFrame)
        }.also { scanResult ->
            eventStream.tryEmit(CameraListenerEvent.Form(isDetected = scanResult != null))
        } ?: return inputFrame

        if (capturedScan == ScanResult.Empty) return inputFrame
        drawOverlay(inputFrame)

        return inputFrame
    }

    private fun drawOverlay(frame: Mat) {
        val detectionArea = DetectionArea
            .calculate(frame.width(), frame.height())
        val overlay = Mat(detectionArea.height.toInt(), detectionArea.width.toInt(), CvType.CV_8UC4)
        val background = Mat.zeros(frame.size(), CvType.CV_8UC4)
        OverlayPrinter.draw(overlay, capturedScan.form, currentScan.form)
        correctOverlayPerspective(overlay, background, currentScan.formPosition)
        val overlayMask = Mat()
        Core.inRange(
            background,
            Scalar(0.0, 0.0, 0.0, 0.0),
            Scalar(0.0, 0.0, 0.0, 0.0),
            overlayMask
        )
        Core.bitwise_not(overlayMask, overlayMask)
        background.copyTo(frame, overlayMask)
        overlayMask.release()
        overlay.release()
        background.release()
    }

    private fun correctOverlayPerspective(
        overlay: Mat, background: Mat,
        formPosition: FormPosition
    ) {
        val sourceCoordinates = MatOfPoint2f(
            Point(0.0, 0.0),
            Point(overlay.width().toDouble(), 0.0),
            Point(overlay.width().toDouble(), overlay.height().toDouble()),
            Point(0.0, overlay.height().toDouble())
        )
        val destinationCoordinates =
            MatOfPoint2f(*formPosition.corners.toTypedArray())
        val transformation = Imgproc.getPerspectiveTransform(
            sourceCoordinates, destinationCoordinates
        )
        Imgproc.warpPerspective(overlay, background, transformation, background.size())
    }
}