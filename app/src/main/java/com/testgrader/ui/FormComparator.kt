package com.testgrader.ui

import com.formscanner.Form

object FormComparator {
    fun calculateSimilarity(current: Form, captured: Form): Similarity {
        val score = current.answers
            .zip(captured.answers) { f1, f2 -> f1 == f2 }
            .count { same -> same }
        return Similarity(score, current.answers.size)
    }

    data class Similarity(
        val score: Int,
        val total: Int
    )
}