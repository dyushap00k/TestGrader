package com.formscanner.detector

import org.opencv.core.Mat
import org.opencv.core.MatOfPoint
import org.opencv.imgproc.Imgproc

internal class ContourTree private constructor(
    private val contours: List<Contour>,
    private val contourHierarchy: Mat
) {
    fun asList() = contours

    fun getChildren(contour: Contour): List<Contour> {
        val contourIndex = contours.indexOf(contour)
        val childContourIndex = getRelativeIndex(contourIndex, Relative.CHILD)
        if (childContourIndex == -1) return emptyList()
        return getSiblings(contours[childContourIndex])
    }

    private fun getSiblings(contour: Contour): List<Contour> {
        val contourIndex = contours.indexOf(contour)
        return iterateContourIndices(contourIndex) {
            getRelativeIndex(it, Relative.NEXT_SIBLING)
        }.map { contours[it] }
    }

    private fun iterateContourIndices(
        startIndex: Int,
        computeNextIndex: (currentIndex: Int) -> Int
    ): List<Int> {
        require(startIndex != -1)

        val indices = mutableListOf<Int>()
        var currentIndex = startIndex
        while (currentIndex != -1) {
            indices.add(currentIndex)
            currentIndex = computeNextIndex(currentIndex)
        }
        return indices
    }

    private fun getRelativeIndex(contourIndex: Int, relative: Relative): Int =
        contourHierarchy.get(0, contourIndex)[relative.index].toInt()

    private enum class Relative(val index: Int) {
        NEXT_SIBLING(0),
        PREV_SIBLING(1),
        CHILD(2),
        PARENT(3)
    }

    companion object {
        fun createFrom(mat: Mat): ContourTree {
            val contours = mutableListOf<MatOfPoint>()
            val contourHierarchy = Mat()
            Imgproc.findContours(
                mat, contours, contourHierarchy,
                Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE
            )
            return ContourTree(
                contours.map { Contour(it) },
                contourHierarchy
            )
        }
    }
}