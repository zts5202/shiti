package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Option
import com.example.data.Question
import com.example.data.QuestionType
import com.example.ui.glass.GlassIntensity
import com.example.ui.glass.LiquidGlassCard
import com.example.ui.glass.ThermalFurnaceBackground
import com.example.ui.theme.ThemeManager
import com.example.viewmodel.ExamViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamScreen(
    viewModel: ExamViewModel,
    onExamSubmitted: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val isDark = ThemeManager.isDarkMode
    val primaryText = if (isDark) Color.White else Color(0xFF0F172A)
    val secondaryText = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)

    val questions by viewModel.currentQuestionList.collectAsState()
    val currentIndex by viewModel.currentIndex.collectAsState()
    val userAnswers by viewModel.userAnswers.collectAsState()
    val multiDraft by viewModel.multiChoiceDraft.collectAsState()
    val remainingSeconds by viewModel.examRemainingSeconds.collectAsState()

    var showSubmitConfirmDialog by remember { mutableStateOf(false) }
    var showSheetDialog by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    BackHandler {
        showSubmitConfirmDialog = true
    }

    if (questions.isEmpty()) return

    val currentQ = questions.getOrNull(currentIndex) ?: questions[0]
    val userAnswer = userAnswers[currentQ.id]

    val minutes = remainingSeconds / 60
    val seconds = remainingSeconds % 60
    val timeFormatted = String.format("%02d:%02d", minutes, seconds)
    val answeredCount = userAnswers.size

    Box(modifier = Modifier.fillMaxSize()) {
        ThermalFurnaceBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            // Liquid Glass Exam Top Bar
            LiquidGlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .testTag("exam_top_bar"),
                cornerRadius = 18.dp,
                intensity = GlassIntensity.HERO
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { showSubmitConfirmDialog = true }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "退出",
                                tint = primaryText
                            )
                        }
                        Text(
                            text = "中级工模考",
                            color = primaryText,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Countdown timer with pulsing liquid glass
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (remainingSeconds < 300) Color(0x33EF4444) else Color(0x330284C7)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Alarm,
                                contentDescription = null,
                                tint = if (remainingSeconds < 300) Color(0xFFEF4444) else Color(0xFF0284C7),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = timeFormatted,
                                color = if (remainingSeconds < 300) Color(0xFFEF4444) else primaryText,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Submit button
                    Button(
                        onClick = { showSubmitConfirmDialog = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFF97316)
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("btn_submit_exam")
                    ) {
                        Text("交卷", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }

            // Question View
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    LiquidGlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        cornerRadius = 20.dp,
                        intensity = GlassIntensity.STANDARD
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isDark) Color(0x330284C7) else Color(0x200284C7)
                                ) {
                                    Text(
                                        text = "${currentQ.type.label} (${currentQ.score}分)",
                                        color = Color(0xFF0284C7),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }

                                Text(
                                    text = "第 ${currentIndex + 1} / ${questions.size} 题",
                                    color = secondaryText,
                                    fontSize = 13.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                text = "${currentIndex + 1}. ${currentQ.title}",
                                color = primaryText,
                                fontSize = 17.sp,
                                lineHeight = 26.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                item {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        currentQ.options.forEach { opt ->
                            ExamOptionItem(
                                question = currentQ,
                                option = opt,
                                userAnswer = userAnswer,
                                multiDraft = multiDraft,
                                isDark = isDark,
                                onSelect = {
                                    if (currentQ.type == QuestionType.MULTIPLE) {
                                        viewModel.toggleMultiChoiceOption(opt.key)
                                    } else {
                                        viewModel.selectOption(opt.key)
                                    }
                                }
                            )
                        }

                        if (currentQ.type == QuestionType.MULTIPLE) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Button(
                                onClick = { viewModel.submitMultiChoice() },
                                enabled = multiDraft.isNotEmpty(),
                                modifier = Modifier.fillMaxWidth().height(46.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF0284C7)
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = if (multiDraft.isEmpty()) "请选择多选选项" else "保存本题答案 (${multiDraft.sorted().joinToString("")})",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // Bottom Bar
            LiquidGlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                cornerRadius = 18.dp,
                intensity = GlassIntensity.LIGHT
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = { viewModel.prevQuestion() },
                        enabled = currentIndex > 0,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isDark) Color(0x330284C7) else Color(0x200284C7)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("上一题", color = primaryText, fontSize = 13.sp)
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isDark) Color(0x200284C7) else Color(0x150284C7),
                        modifier = Modifier.clickable { showSheetDialog = true }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.GridView,
                                contentDescription = null,
                                tint = Color(0xFF0284C7),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("已答 $answeredCount/${questions.size}", color = primaryText, fontSize = 13.sp)
                        }
                    }

                    Button(
                        onClick = { viewModel.nextQuestion() },
                        enabled = currentIndex < questions.size - 1,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF0284C7)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("下一题", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }

        // Confirm Submit Dialog
        if (showSubmitConfirmDialog) {
            AlertDialog(
                onDismissRequest = { showSubmitConfirmDialog = false },
                title = { Text("确认交卷？", color = primaryText, fontWeight = FontWeight.Bold) },
                text = {
                    Text(
                        text = "当前已答 $answeredCount 题，未答 ${questions.size - answeredCount} 题。\n交卷后将立即出分并生成错题复盘报告。",
                        color = secondaryText
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showSubmitConfirmDialog = false
                            viewModel.submitExam()
                            onExamSubmitted()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF97316))
                    ) {
                        Text("确定交卷", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showSubmitConfirmDialog = false }) {
                        Text("继续答题", color = secondaryText)
                    }
                },
                containerColor = if (isDark) Color(0xFF0F172A) else Color.White
            )
        }

        // Answer Sheet Dialog
        if (showSheetDialog) {
            ModalBottomSheet(
                onDismissRequest = { showSheetDialog = false },
                sheetState = sheetState,
                containerColor = if (isDark) Color(0xFF0F172A) else Color.White
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = "模考答题卡 (已答 $answeredCount/${questions.size})",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = primaryText
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 46.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(380.dp)
                    ) {
                        items(questions.size) { idx ->
                            val q = questions[idx]
                            val ans = userAnswers[q.id]
                            val isPicked = ans != null
                            val isCurrent = idx == currentIndex

                            val (bg, textCol) = when {
                                isCurrent -> Pair(Color(0xFF0284C7), Color.White)
                                isPicked -> Pair(Color(0x3310B981), Color(0xFF10B981))
                                else -> Pair(if (isDark) Color(0x15FFFFFF) else Color(0x10000000), secondaryText)
                            }

                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(bg)
                                    .clickable {
                                        viewModel.goToQuestion(idx)
                                        showSheetDialog = false
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${idx + 1}",
                                    color = textCol,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ExamOptionItem(
    question: Question,
    option: Option,
    userAnswer: String?,
    multiDraft: Set<String>,
    isDark: Boolean,
    onSelect: () -> Unit
) {
    val key = option.key
    val isMulti = question.type == QuestionType.MULTIPLE
    val isPicked = if (isMulti) multiDraft.contains(key) else userAnswer == key

    val intensity = if (isPicked) GlassIntensity.STANDARD else GlassIntensity.LIGHT
    val badgeBg = if (isPicked) Color(0xFF0284C7) else (if (isDark) Color(0x26FFFFFF) else Color(0x15000000))
    val badgeText = if (isPicked) Color.White else (if (isDark) Color.White else Color(0xFF0F172A))
    val optionTextColor = if (isDark) Color.White else Color(0xFF0F172A)

    LiquidGlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() }
            .testTag("exam_option_${option.key}"),
        cornerRadius = 16.dp,
        intensity = intensity
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = badgeBg,
                modifier = Modifier.size(32.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = key,
                        color = badgeText,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = option.text,
                color = optionTextColor,
                fontSize = 15.sp,
                lineHeight = 22.sp,
                modifier = Modifier.weight(1f)
            )

            if (isPicked) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color(0xFF0284C7),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
