package com.testgrader.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


fun Activity.hideSystemUi() {
    WindowCompat.setDecorFitsSystemWindows(window, false)
    val insetsController = WindowInsetsControllerCompat(
        window,
        window.decorView.findViewById(androidx.appcompat.R.id.content)
    )
    insetsController.hide(WindowInsetsCompat.Type.systemBars())
    insetsController.systemBarsBehavior =
        WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
}

fun Activity.openAppSettings() {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts("package", packageName, null)
        addCategory(Intent.CATEGORY_DEFAULT)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
        addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
    }
    startActivity(intent)
}

fun ComponentActivity.requestPermission(
    permission: String,
    onGranted: () -> Unit = {},
    onNotGranted: () -> Unit = {},
    onShouldShowRationale: () -> Unit = {}
) {
    val isGranted = ContextCompat.checkSelfPermission(
        this,
        permission
    ) == PackageManager.PERMISSION_GRANTED
    if (isGranted) {
        onGranted()
        return
    }

    val shouldShowRationale = ActivityCompat.shouldShowRequestPermissionRationale(
        this,
        Manifest.permission.CAMERA
    )
    if (shouldShowRationale) {
        onShouldShowRationale()
        return
    }

    registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        if (it) onGranted()
        else onNotGranted()
    }.launch(permission)
}