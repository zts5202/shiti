package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "question_records")
data class QuestionRecord(
    @PrimaryKey
    val questionId: Int,
    val userChoice: String = "",
    val isCorrect: Boolean = false,
    val isFavorite: Boolean = false,
    val wrongCount: Int = 0,
    val correctCount: Int = 0,
    val lastAnsweredTimestamp: Long = 0L,
    val notes: String = ""
)

@Entity(tableName = "exam_records")
data class ExamRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val timestamp: Long = System.currentTimeMillis(),
    val score: Int,
    val totalScore: Int = 100,
    val totalQuestions: Int = 100,
    val correctCount: Int,
    val wrongCount: Int,
    val durationSeconds: Int,
    val passed: Boolean
)
