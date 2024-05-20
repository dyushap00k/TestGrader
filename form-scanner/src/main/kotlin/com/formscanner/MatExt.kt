package com.formscanner

import org.opencv.core.Mat
import org.opencv.core.Rect
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@OptIn(ExperimentalContracts::class)
fun <T> Mat.submat(
    rect: Rect,
    transform: (submat: Mat) -> T
): T {
    contract {
        callsInPlace(transform, InvocationKind.EXACTLY_ONCE)
    }

    val submat = submat(rect)
    val result = transform(submat)
    submat.release()
    return result
}

@OptIn(ExperimentalContracts::class)
fun <T> Mat.copy(block: (mat: Mat) -> T): T {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }

    val mat = Mat()
    copyTo(mat)
    val result = block(mat)
    mat.release()
    return result
}

@OptIn(ExperimentalContracts::class)
fun <T> temporaryMat(block: (mat: Mat) -> T): T {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }

    val mat = Mat()
    val result = block(mat)
    mat.release()
    return result
}