package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.ExamRecord
import com.example.data.Question
import com.example.data.QuestionRecord
import com.example.data.QuestionRepository
import com.example.data.QuestionType
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class PracticeMode(val title: String) {
    SEQUENTIAL("顺序练习"),
    RANDOM("随机抽题"),
    CATEGORY("分类练习"),
    WRONG("错题专练"),
    FAVORITE("我的收藏"),
    TYPE_SINGLE("单选题专练"),
    TYPE_MULTIPLE("多选题专练"),
    TYPE_JUDGMENT("判断题专练"),
    EXAM("全真模拟考试")
}

data class ExamResultData(
    val score: Int,
    val totalScore: Int = 100,
    val totalQuestions: Int = 100,
    val correctCount: Int,
    val wrongCount: Int,
    val unansweredCount: Int,
    val singleScore: Int,
    val multiScore: Int,
    val judgeScore: Int,
    val durationSeconds: Int,
    val passed: Boolean,
    val wrongQuestions: List<Question> = emptyList()
)

class ExamViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    val repository = QuestionRepository(application, database.questionDao())

    val recordsMap: StateFlow<Map<Int, QuestionRecord>> = repository.getAllRecordsMapFlow()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyMap())

    val favoriteQuestions: StateFlow<List<Question>> = repository.getFavoriteQuestionsFlow()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val wrongQuestions: StateFlow<List<Question>> = repository.getWrongQuestionsFlow()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val examRecords: StateFlow<List<ExamRecord>> = repository.getExamRecordsFlow()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _allQuestions = MutableStateFlow<List<Question>>(emptyList())
    val allQuestions: StateFlow<List<Question>> = _allQuestions.asStateFlow()

    private val _categories = MutableStateFlow<List<String>>(emptyList())
    val categories: StateFlow<List<String>> = _categories.asStateFlow()

    // Practice / Exam state
    private val _currentMode = MutableStateFlow(PracticeMode.SEQUENTIAL)
    val currentMode: StateFlow<PracticeMode> = _currentMode.asStateFlow()

    private val _currentQuestionList = MutableStateFlow<List<Question>>(emptyList())
    val currentQuestionList: StateFlow<List<Question>> = _currentQuestionList.asStateFlow()

    private val _currentIndex = MutableStateFlow(0)
    val currentIndex: StateFlow<Int> = _currentIndex.asStateFlow()

    // Map of questionId -> userSelectedKeys (e.g. "B" or "ABD")
    private val _userAnswers = MutableStateFlow<Map<Int, String>>(emptyMap())
    val userAnswers: StateFlow<Map<Int, String>> = _userAnswers.asStateFlow()

    // Map of questionId -> isSubmitted (for practice mode immediate validation)
    private val _submittedQuestions = MutableStateFlow<Set<Int>>(emptySet())
    val submittedQuestions: StateFlow<Set<Int>> = _submittedQuestions.asStateFlow()

    // Multi-choice draft selection for current question before submitting
    private val _multiChoiceDraft = MutableStateFlow<Set<String>>(emptySet())
    val multiChoiceDraft: StateFlow<Set<String>> = _multiChoiceDraft.asStateFlow()

    // Exam countdown (60 minutes = 3600 seconds)
    private val _examRemainingSeconds = MutableStateFlow(3600)
    val examRemainingSeconds: StateFlow<Int> = _examRemainingSeconds.asStateFlow()

    private var timerJob: Job? = null

    // Exam result
    private val _lastExamResult = MutableStateFlow<ExamResultData?>(null)
    val lastExamResult: StateFlow<ExamResultData?> = _lastExamResult.asStateFlow()

    init {
        viewModelScope.launch {
            val list = repository.getQuestions()
            _allQuestions.value = list
            _categories.value = repository.getCategories()
        }
    }

    fun startPractice(mode: PracticeMode, categoryName: String? = null) {
        viewModelScope.launch {
            val all = if (_allQuestions.value.isEmpty()) repository.getQuestions() else _allQuestions.value
            _currentMode.value = mode
            _currentIndex.value = 0
            _userAnswers.value = emptyMap()
            _submittedQuestions.value = emptySet()
            _multiChoiceDraft.value = emptySet()
            timerJob?.cancel()

            val list = when (mode) {
                PracticeMode.SEQUENTIAL -> all.sortedBy { it.id }
                PracticeMode.RANDOM -> all.shuffled()
                PracticeMode.CATEGORY -> {
                    if (categoryName != null) {
                        all.filter { it.category == categoryName }
                    } else {
                        all
                    }
                }
                PracticeMode.WRONG -> {
                    val wrongIds = recordsMap.value.filter { it.value.wrongCount > 0 }.keys
                    all.filter { it.id in wrongIds }
                }
                PracticeMode.FAVORITE -> {
                    val favIds = recordsMap.value.filter { it.value.isFavorite }.keys
                    all.filter { it.id in favIds }
                }
                PracticeMode.TYPE_SINGLE -> all.filter { it.type == QuestionType.SINGLE }
                PracticeMode.TYPE_MULTIPLE -> all.filter { it.type == QuestionType.MULTIPLE }
                PracticeMode.TYPE_JUDGMENT -> all.filter { it.type == QuestionType.JUDGMENT }
                PracticeMode.EXAM -> {
                    // Handled in startMockExam
                    all
                }
            }
            _currentQuestionList.value = list
        }
    }

    fun startMockExam() {
        viewModelScope.launch {
            _currentMode.value = PracticeMode.EXAM
            _currentIndex.value = 0
            _userAnswers.value = emptyMap()
            _submittedQuestions.value = emptySet()
            _multiChoiceDraft.value = emptySet()
            _examRemainingSeconds.value = 3600 // 60 minutes
            _currentQuestionList.value = repository.generateMockExam()

            timerJob?.cancel()
            timerJob = viewModelScope.launch {
                while (_examRemainingSeconds.value > 0) {
                    delay(1000L)
                    _examRemainingSeconds.value -= 1
                }
                // Auto submit on time out
                submitExam()
            }
        }
    }

    fun goToQuestion(index: Int) {
        if (index in 0 until _currentQuestionList.value.size) {
            _currentIndex.value = index
            val currentQ = _currentQuestionList.value[index]
            val existing = _userAnswers.value[currentQ.id]
            if (currentQ.type == QuestionType.MULTIPLE) {
                _multiChoiceDraft.value = existing?.map { it.toString() }?.toSet() ?: emptySet()
            }
        }
    }

    fun nextQuestion() {
        if (_currentIndex.value < _currentQuestionList.value.size - 1) {
            goToQuestion(_currentIndex.value + 1)
        }
    }

    fun prevQuestion() {
        if (_currentIndex.value > 0) {
            goToQuestion(_currentIndex.value - 1)
        }
    }

    // Toggle multi choice option draft
    fun toggleMultiChoiceOption(key: String) {
        val current = _multiChoiceDraft.value.toMutableSet()
        if (current.contains(key)) {
            current.remove(key)
        } else {
            current.add(key)
        }
        _multiChoiceDraft.value = current
    }

    // Submit answer for current question
    fun selectOption(optionKey: String) {
        val questions = _currentQuestionList.value
        if (_currentIndex.value !in questions.indices) return
        val q = questions[_currentIndex.value]

        if (q.type == QuestionType.SINGLE || q.type == QuestionType.JUDGMENT) {
            val isExam = _currentMode.value == PracticeMode.EXAM
            val updated = _userAnswers.value.toMutableMap()
            updated[q.id] = optionKey
            _userAnswers.value = updated

            val isCorrect = optionKey.equals(q.answer.trim(), ignoreCase = true)
            if (!isExam) {
                _submittedQuestions.value = _submittedQuestions.value + q.id
                viewModelScope.launch {
                    repository.saveAnswer(q.id, optionKey, isCorrect)
                }
            }
        }
    }

    fun submitMultiChoice() {
        val questions = _currentQuestionList.value
        if (_currentIndex.value !in questions.indices) return
        val q = questions[_currentIndex.value]
        if (q.type != QuestionType.MULTIPLE) return

        val sortedChoice = _multiChoiceDraft.value.sorted().joinToString("")
        if (sortedChoice.isEmpty()) return

        val isExam = _currentMode.value == PracticeMode.EXAM
        val updated = _userAnswers.value.toMutableMap()
        updated[q.id] = sortedChoice
        _userAnswers.value = updated

        val isCorrect = sortedChoice.equals(q.answer.trim(), ignoreCase = true)
        if (!isExam) {
            _submittedQuestions.value = _submittedQuestions.value + q.id
            viewModelScope.launch {
                repository.saveAnswer(q.id, sortedChoice, isCorrect)
            }
        }
    }

    fun toggleFavorite(questionId: Int) {
        val isFav = recordsMap.value[questionId]?.isFavorite == true
        viewModelScope.launch {
            repository.toggleFavorite(questionId, isFav)
        }
    }

    fun clearAllWrong() {
        viewModelScope.launch {
            repository.clearWrongRecords()
        }
    }

    fun clearSingleWrong(questionId: Int) {
        viewModelScope.launch {
            repository.clearSingleWrongRecord(questionId)
        }
    }

    fun submitExam(): ExamResultData {
        timerJob?.cancel()
        val questions = _currentQuestionList.value
        val answers = _userAnswers.value

        var singleScore = 0
        var multiScore = 0
        var judgeScore = 0
        var correctCount = 0
        var wrongCount = 0
        var unansweredCount = 0
        val wrongList = mutableListOf<Question>()

        for (q in questions) {
            val userAns = answers[q.id]?.trim()?.uppercase()
            val correctAns = q.answer.trim().uppercase()
            if (userAns == null) {
                unansweredCount++
                wrongList.add(q)
            } else if (userAns == correctAns) {
                correctCount++
                val pts = 1 // Standard exam weighting: 1 pt per question for 100 total
                when (q.type) {
                    QuestionType.SINGLE -> singleScore += pts
                    QuestionType.MULTIPLE -> multiScore += pts
                    QuestionType.JUDGMENT -> judgeScore += pts
                }
                viewModelScope.launch {
                    repository.saveAnswer(q.id, userAns, true)
                }
            } else {
                wrongCount++
                wrongList.add(q)
                viewModelScope.launch {
                    repository.saveAnswer(q.id, userAns, false)
                }
            }
        }

        val totalScore = singleScore + multiScore + judgeScore
        val passed = totalScore >= 60
        val duration = 3600 - _examRemainingSeconds.value

        val result = ExamResultData(
            score = totalScore,
            totalScore = 100,
            totalQuestions = questions.size,
            correctCount = correctCount,
            wrongCount = wrongCount + unansweredCount,
            unansweredCount = unansweredCount,
            singleScore = singleScore,
            multiScore = multiScore,
            judgeScore = judgeScore,
            durationSeconds = duration,
            passed = passed,
            wrongQuestions = wrongList
        )
        _lastExamResult.value = result

        viewModelScope.launch {
            repository.insertExamRecord(
                ExamRecord(
                    score = totalScore,
                    totalScore = 100,
                    totalQuestions = questions.size,
                    correctCount = correctCount,
                    wrongCount = wrongCount + unansweredCount,
                    durationSeconds = duration,
                    passed = passed
                )
            )
        }
        return result
    }
}
