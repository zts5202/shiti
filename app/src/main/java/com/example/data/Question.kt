package com.example.data

enum class QuestionType(val label: String) {
    SINGLE("单选题"),
    MULTIPLE("多选题"),
    JUDGMENT("判断题")
}

data class Option(
    val key: String,
    val text: String
)

data class Question(
    val id: Int,
    val originalId: Int,
    val type: QuestionType,
    val title: String,
    val options: List<Option>,
    val answer: String,
    val score: Int = 2,
    val category: String = "热处理综合技术"
)
