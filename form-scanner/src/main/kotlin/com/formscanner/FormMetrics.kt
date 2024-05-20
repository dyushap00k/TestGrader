package com.formscanner

import com.formscanner.BuildConfig.BLOCK_COUNT
import com.formscanner.BuildConfig.BLOCK_GROUP_OFFSET_X
import com.formscanner.BuildConfig.BLOCK_GROUP_OFFSET_Y
import com.formscanner.BuildConfig.BLOCK_OFFSET
import com.formscanner.BuildConfig.FORM_HEIGHT
import com.formscanner.BuildConfig.FORM_WIDTH
import com.formscanner.BuildConfig.GRADE_BOX_HEIGHT
import com.formscanner.BuildConfig.GRADE_BOX_OFFSET_X
import com.formscanner.BuildConfig.GRADE_BOX_OFFSET_Y
import com.formscanner.BuildConfig.GRADE_BOX_WIDTH
import com.formscanner.BuildConfig.OPTION_COUNT
import com.formscanner.BuildConfig.OPTION_GROUP_COUNT
import com.formscanner.BuildConfig.OPTION_GROUP_OFFSET
import com.formscanner.BuildConfig.OPTION_OFFSET
import com.formscanner.BuildConfig.OPTION_SIZE
import com.formscanner.BuildConfig.TRIANGLE_LEG_LENGTH

class FormMetrics private constructor(
    actualWidth: Int
) {
    private val scale = actualWidth / FORM_WIDTH.toDouble()
    private val blockCount = BLOCK_COUNT
    private val optionGroupCount = OPTION_GROUP_COUNT
    private val optionCount = OPTION_COUNT
    private val blockOffset = BLOCK_OFFSET to scale
    private val blockGroupOffsetX = BLOCK_GROUP_OFFSET_X to scale
    private val blockGroupOffsetY = BLOCK_GROUP_OFFSET_Y to scale
    private val optionGroupOffset = OPTION_GROUP_OFFSET to scale
    private val optionOffset = OPTION_OFFSET to scale
    private val optionSize = OPTION_SIZE to scale
    private val gradeBoxOffsetX = GRADE_BOX_OFFSET_X to scale
    private val gradeBoxOffsetY = GRADE_BOX_OFFSET_Y to scale
    private val gradeBoxWidth = GRADE_BOX_WIDTH to scale
    private val gradeBoxHeight = GRADE_BOX_HEIGHT to scale

    val blocks by lazy { createBlocks() }
    val gradeBox by lazy { createGradeBox() }

    private infix fun Int.to(scale: Double): Double = this * scale

    private fun createBlocks(): List<Block> =
        (0..<blockCount)
            .map { index -> index * blockOffset }
            .map { horizontalOffset ->
                val position = Position(
                    blockGroupOffsetX + horizontalOffset,
                    blockGroupOffsetY,
                )
                val optionGroups = createOptionGroups(position)
                Block(position, optionGroups)
            }

    private fun createOptionGroups(
        parentPosition: Position
    ): List<OptionGroup> =
        (0..<optionGroupCount)
            .map { index -> index * optionGroupOffset }
            .map { verticalOffset ->
                val position = Position(
                    parentPosition.x,
                    parentPosition.y + verticalOffset
                )
                val options = createOptions(position)
                OptionGroup(position, options)
            }

    private fun createOptions(
        parentPosition: Position
    ): List<Option> =
        (0..<optionCount)
            .map { index -> index * optionOffset }
            .map { horizontalOffset ->
                val position = Position(
                    parentPosition.x + horizontalOffset,
                    parentPosition.y
                )
                Option(position, optionSize)
            }

    private fun createGradeBox(): GradeBox {
        val position = Position(
            gradeBoxOffsetX,
            gradeBoxOffsetY
        )
        return GradeBox(position, gradeBoxWidth.toInt(), gradeBoxHeight.toInt())
    }

    companion object {
        const val RATIO = FORM_WIDTH / FORM_HEIGHT.toDouble()
        const val AREA = FORM_WIDTH * FORM_HEIGHT

        fun fromActual(width: Int): FormMetrics = FormMetrics(width)
    }

    object Triangle {
        private const val AREA = TRIANGLE_LEG_LENGTH * TRIANGLE_LEG_LENGTH / 2.0
        const val TO_FORM_AREA_RATIO = AREA / FormMetrics.AREA
    }

    data class Block(
        val position: Position,
        val optionGroups: List<OptionGroup>
    )

    data class OptionGroup(
        val position: Position,
        val options: List<Option>
    )

    data class Option(
        val position: Position,
        val size: Double
    )

    data class GradeBox(
        val position: Position,
        val width: Int, val height: Int
    )

    data class Position(
        val x: Double, val y: Double
    )
}