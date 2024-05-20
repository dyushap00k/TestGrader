package com.formscanner

data class Form(
    val answers: List<Answer> = emptyList()
) {
    companion object {
        val Empty = Form()
    }

    sealed interface Answer {
        data class Option(val number: Int) : Answer
        data object Ambiguous : Answer
        data object Empty : Answer
    }
}