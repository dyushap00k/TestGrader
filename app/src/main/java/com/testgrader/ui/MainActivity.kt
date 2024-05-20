package com.testgrader.ui

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.testgrader.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import org.opencv.android.CameraBridgeViewBase.CAMERA_ID_BACK
import timber.log.Timber

@SuppressLint("ClickableViewAccessibility")
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val cameraListener = CameraListener()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        hideSystemUi()
        setupUi()
        collectCameraEvents()
    }

    private fun collectCameraEvents() {
        cameraListener.eventStream.onEach {
            Timber.tag("lol").d(it.toString())
            when (it) {
                is CameraListenerEvent.Form -> {
                    binding.detectionBoundsView.isFormDetected = it.isDetected
                }
                CameraListenerEvent.Started -> binding.cameraView.enableView()
                CameraListenerEvent.Stopped -> binding.cameraView.disableView()
            }
        }.launchIn(lifecycleScope)
    }

    private fun setupUi() {
        binding.grantPermissionButton.setOnClickListener {
            openAppSettings()
        }
        binding.formCaptureButton.setOnClickListener {
            cameraListener.captureScan()
        }
    }

    override fun onStart() {
        super.onStart()
        requestCameraPermission()
    }

    private fun requestCameraPermission() {
        requestPermission(
            permission = Manifest.permission.CAMERA,
            onGranted = ::onCameraPermissionGranted,
            onNotGranted = ::onCameraPermissionNotGranted
        )
    }

    private fun onCameraPermissionNotGranted() {
        binding.cameraFeedLayout.visibility = View.GONE
        binding.cameraWarningLnLayout.visibility = View.VISIBLE
    }

    private fun onCameraPermissionGranted() {
        binding.cameraFeedLayout.visibility = View.VISIBLE
        binding.cameraWarningLnLayout.visibility = View.GONE
        setupCameraView()
    }

    private fun setupCameraView() = with(binding.cameraView) {
        setCameraIndex(CAMERA_ID_BACK)
        setMaxFrameSize(0, 0)
        setCameraPermissionGranted()
        setCvCameraViewListener(cameraListener)
    }

    public override fun onResume() {
        super.onResume()
        binding.cameraView.enableView()
    }

    public override fun onPause() {
        super.onPause()
        binding.cameraView.disableView()
    }
}