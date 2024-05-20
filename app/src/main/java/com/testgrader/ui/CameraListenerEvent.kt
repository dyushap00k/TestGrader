package com.testgrader.ui

sealed interface CameraListenerEvent {
    data object Started : CameraListenerEvent
    data object Stopped : CameraListenerEvent
    data class Form(val isDetected: Boolean) : CameraListenerEvent
}