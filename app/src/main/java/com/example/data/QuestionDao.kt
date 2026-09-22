package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface QuestionDao {
    @Query("SELECT * FROM question_records")
    fun getAllRecordsFlow(): Flow<List<QuestionRecord>>

    @Query("SELECT * FROM question_records WHERE isFavorite = 1")
    fun getFavoriteRecordsFlow(): Flow<List<QuestionRecord>>

    @Query("SELECT * FROM question_records WHERE wrongCount > 0")
    fun getWrongRecordsFlow(): Flow<List<QuestionRecord>>

    @Query("SELECT * FROM question_records WHERE questionId = :questionId LIMIT 1")
    suspend fun getRecord(questionId: Int): QuestionRecord?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertRecord(record: QuestionRecord)

    @Query("UPDATE question_records SET isFavorite = :isFavorite WHERE questionId = :questionId")
    suspend fun updateFavorite(questionId: Int, isFavorite: Boolean)

    @Query("UPDATE question_records SET wrongCount = 0 WHERE questionId = :questionId")
    suspend fun clearWrongForQuestion(questionId: Int)

    @Query("UPDATE question_records SET wrongCount = 0")
    suspend fun clearAllWrongRecords()

    @Query("SELECT * FROM exam_records ORDER BY timestamp DESC")
    fun getExamRecordsFlow(): Flow<List<ExamRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExamRecord(record: ExamRecord): Long

    @Query("DELETE FROM exam_records")
    suspend fun clearAllExamRecords()
}
