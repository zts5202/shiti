package com.example.data

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.io.BufferedReader
import java.io.InputStreamReader

class QuestionRepository(
    private val context: Context,
    private val dao: QuestionDao
) {
    @Volatile
    private var cachedQuestions: List<Question> = emptyList()

    suspend fun getQuestions(): List<Question> {
        if (cachedQuestions.isNotEmpty()) return cachedQuestions
        return withContext(Dispatchers.IO) {
            val list = mutableListOf<Question>()
            try {
                context.assets.open("questions.json").use { stream ->
                    val reader = BufferedReader(InputStreamReader(stream, Charsets.UTF_8))
                    val jsonStr = reader.readText()
                    val array = JSONArray(jsonStr)
                    for (i in 0 until array.length()) {
                        val obj = array.getJSONObject(i)
                        val id = obj.getInt("id")
                        val originalId = obj.getInt("originalId")
                        val typeStr = obj.getString("type")
                        val type = when (typeStr) {
                            "SINGLE" -> QuestionType.SINGLE
                            "MULTIPLE" -> QuestionType.MULTIPLE
                            "JUDGMENT" -> QuestionType.JUDGMENT
                            else -> QuestionType.SINGLE
                        }
                        val title = obj.getString("title")
                        val answer = obj.getString("answer").trim().uppercase()
                        val score = obj.optInt("score", 2)
                        val category = obj.optString("category", "热处理综合技术")

                        val optionsArr = obj.getJSONArray("options")
                        val options = mutableListOf<Option>()
                        for (j in 0 until optionsArr.length()) {
                            val optObj = optionsArr.getJSONObject(j)
                            options.add(
                                Option(
                                    key = optObj.getString("key").trim().uppercase(),
                                    text = optObj.getString("text").trim()
                                )
                            )
                        }

                        list.add(
                            Question(
                                id = id,
                                originalId = originalId,
                                type = type,
                                title = title,
                                options = options,
                                answer = answer,
                                score = score,
                                category = category
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            cachedQuestions = list
            list
        }
    }

    suspend fun getCategories(): List<String> {
        val list = getQuestions()
        return list.map { it.category }.distinct()
    }

    suspend fun generateMockExam(): List<Question> {
        val all = getQuestions()
        val singles = all.filter { it.type == QuestionType.SINGLE }.shuffled().take(50)
        val multiples = all.filter { it.type == QuestionType.MULTIPLE }.shuffled().take(25)
        val judgments = all.filter { it.type == QuestionType.JUDGMENT }.shuffled().take(25)
        return singles + multiples + judgments
    }

    fun getAllRecordsMapFlow(): Flow<Map<Int, QuestionRecord>> {
        return dao.getAllRecordsFlow().map { list ->
            list.associateBy { it.questionId }
        }
    }

    suspend fun getWrongQuestions(): List<Question> {
        val all = getQuestions().associateBy { it.id }
        // We'll read from DB
        return emptyList()
    }

    fun getFavoriteQuestionsFlow(): Flow<List<Question>> {
        return dao.getFavoriteRecordsFlow().map { records ->
            val favIds = records.map { it.questionId }.toSet()
            val all = getQuestions()
            all.filter { it.id in favIds }
        }
    }

    fun getWrongQuestionsFlow(): Flow<List<Question>> {
        return dao.getWrongRecordsFlow().map { records ->
            val wrongIds = records.filter { it.wrongCount > 0 }.map { it.questionId }.toSet()
            val all = getQuestions()
            all.filter { it.id in wrongIds }
        }
    }

    fun getExamRecordsFlow(): Flow<List<ExamRecord>> = dao.getExamRecordsFlow()

    suspend fun saveAnswer(questionId: Int, userChoice: String, isCorrect: Boolean) {
        withContext(Dispatchers.IO) {
            val existing = dao.getRecord(questionId)
            val updated = if (existing != null) {
                existing.copy(
                    userChoice = userChoice,
                    isCorrect = isCorrect,
                    wrongCount = if (!isCorrect) existing.wrongCount + 1 else existing.wrongCount,
                    correctCount = if (isCorrect) existing.correctCount + 1 else existing.correctCount,
                    lastAnsweredTimestamp = System.currentTimeMillis()
                )
            } else {
                QuestionRecord(
                    questionId = questionId,
                    userChoice = userChoice,
                    isCorrect = isCorrect,
                    wrongCount = if (!isCorrect) 1 else 0,
                    correctCount = if (isCorrect) 1 else 0,
                    lastAnsweredTimestamp = System.currentTimeMillis()
                )
            }
            dao.upsertRecord(updated)
        }
    }

    suspend fun toggleFavorite(questionId: Int, currentFav: Boolean) {
        withContext(Dispatchers.IO) {
            val existing = dao.getRecord(questionId)
            val updated = if (existing != null) {
                existing.copy(isFavorite = !currentFav)
            } else {
                QuestionRecord(
                    questionId = questionId,
                    isFavorite = !currentFav
                )
            }
            dao.upsertRecord(updated)
        }
    }

    suspend fun clearWrongRecords() {
        withContext(Dispatchers.IO) {
            dao.clearAllWrongRecords()
        }
    }

    suspend fun clearSingleWrongRecord(questionId: Int) {
        withContext(Dispatchers.IO) {
            dao.clearWrongForQuestion(questionId)
        }
    }

    suspend fun insertExamRecord(record: ExamRecord): Long {
        return withContext(Dispatchers.IO) {
            dao.insertExamRecord(record)
        }
    }
}
