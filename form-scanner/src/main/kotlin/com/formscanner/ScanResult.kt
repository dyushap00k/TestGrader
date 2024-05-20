package com.formscanner

import com.formscanner.detector.FormPosition

data class ScanResult(
    val form: Form = Form.Empty,
    val formPosition: FormPosition = FormPosition.Empty
) {
    companion object {
        val Empty = ScanResult()
    }
}
