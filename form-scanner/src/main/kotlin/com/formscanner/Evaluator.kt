package com.formscanner

import com.formscanner.FormMetrics.Option
import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.core.Rect
import kotlin.math.PI
import kotlin.math.pow

private const val OPTION_CHECKED_TOLERANCE = 0.3
private const val OPTION_DETECTION_EXTRA_SCALE = 0.3

internal object Evaluator {
    fun evaluate(form: Mat): Form {
        val formMetrics = FormMetrics.fromActual(form.width())
        val answers = formMetrics.blocks.flatMap { block ->
            block.optionGroups.map { optionGroup ->
                resolveAnswer(form, optionGroup)
            }
        }
        return Form(answers)
    }

    private fun resolveAnswer(
        form: Mat,
        optionGroup: FormMetrics.OptionGroup
    ): Form.Answer {
        val checkedOptionNumbers = optionGroup.options
            .withIndex()
            .filter { isOptionChecked(form, it.value) }
            .map { it.index + 1 }

        return when (checkedOptionNumbers.size) {
            0 -> Form.Answer.Empty
            1 -> Form.Answer.Option(checkedOptionNumbers.single())
            else -> Form.Answer.Ambiguous
        }
    }

    private fun isOptionChecked(
        form: Mat,
        option: Option
    ): Boolean {
        val extraSize = option.size * OPTION_DETECTION_EXTRA_SCALE
        val optionBoundary = Rect(
            (option.position.x - extraSize).toInt(),
            (option.position.y - extraSize).toInt(),
            (option.size + extraSize * 2.0).toInt(),
            (option.size + extraSize * 2.0).toInt()
        )
        val nonZeroArea = form.submat(optionBoundary) { optionMat ->
            Core.countNonZero(optionMat)
        }
        val optionArea = PI * (option.size / 2.0).pow(2)
        val areaDifference = optionArea - nonZeroArea
        return areaDifference < optionArea * OPTION_CHECKED_TOLERANCE
    }
}