package com.testgrader.ui

import com.formscanner.Form
import com.formscanner.Form.Answer
import com.formscanner.FormMetrics
import com.formscanner.FormMetrics.GradeBox
import com.formscanner.FormMetrics.OptionGroup
import org.opencv.core.Mat
import org.opencv.core.Point
import org.opencv.core.Scalar
import org.opencv.imgproc.Imgproc
import kotlin.math.min

object OverlayPrinter {
    fun draw(overlay: Mat, currentForm: Form, capturedForm: Form) {
        val formMetrics = FormMetrics.fromActual(overlay.width())
        drawOptions(overlay, formMetrics, currentForm, capturedForm)
        drawGrade(overlay, formMetrics, currentForm, capturedForm)
    }

    private fun drawOptions(
        overlay: Mat, formMetrics: FormMetrics,
        currentForm: Form, capturedForm: Form
    ) {
        val optionGroups = formMetrics.blocks.flatMap { block ->
            block.optionGroups.map { it }
        }
        for ((index, optionGroup) in optionGroups.withIndex()) {
            val answer = currentForm.answers[index]
            val correctAnswer = capturedForm.answers[index]
            drawOption(overlay, optionGroup, answer, answer == correctAnswer)
        }
    }

    private fun drawOption(
        overlay: Mat, optionGroup: OptionGroup,
        answer: Answer, isCorrect: Boolean
    ) {
        val green = Scalar(0.0, 255.0, 0.0)
        val red = Scalar(255.0, 0.0, 0.0)
        val optionColor = if (isCorrect) green else red

        val optionsToDraw = when (answer) {
            is Answer.Empty -> optionGroup.options
            is Answer.Ambiguous -> optionGroup.options
            is Answer.Option -> listOf(optionGroup.options[answer.number - 1])
        }

        for (option in optionsToDraw) {
            val optionRadius = option.size / 2.0
            val optionCenter = Point(
                option.position.x + optionRadius, option.position.y + optionRadius
            )
            Imgproc.circle(overlay, optionCenter, optionRadius.toInt(), optionColor, -1)
        }
    }

    private fun drawGrade(
        overlay: Mat, formMetrics: FormMetrics,
        currentForm: Form, capturedForm: Form
    ) {
        val formSimilarity = FormComparator
            .calculateSimilarity(currentForm, capturedForm)
        val score = "${formSimilarity.score}"
        val gradeBox = formMetrics.gradeBox
        val fontScale = calculateFontScale(score, gradeBox)

        Imgproc.putText(
            overlay, score,
            Point(gradeBox.position.x, gradeBox.position.y + gradeBox.height),
            Imgproc.FONT_HERSHEY_SCRIPT_SIMPLEX,
            fontScale, Scalar(0.0, 0.0, 0.0, 255.0), 3
        )
    }

    private fun calculateFontScale(text: String, gradeBox: GradeBox): Double {
        val textSize = Imgproc.getTextSize(
            text, Imgproc.FONT_HERSHEY_SIMPLEX,
            1.0, 3, null
        )
        return min(gradeBox.width / textSize.width, gradeBox.height / textSize.height)
    }
}