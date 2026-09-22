package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
fun PracticeScreen(
    viewModel: ExamViewModel,
    onNavigateBack: () -> Unit
) {
    val isDark = ThemeManager.isDarkMode
    val primaryText = if (isDark) Color.White else Color(0xFF0F172A)
    val secondaryText = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)

    val questions by viewModel.currentQuestionList.collectAsState()
    val currentIndex by viewModel.currentIndex.collectAsState()
    val userAnswers by viewModel.userAnswers.collectAsState()
    val submittedSet by viewModel.submittedQuestions.collectAsState()
    val multiDraft by viewModel.multiChoiceDraft.collectAsState()
    val recordsMap by viewModel.recordsMap.collectAsState()
    val mode by viewModel.currentMode.collectAsState()

    var showSheetDialog by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    if (questions.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            ThermalFurnaceBackground()
            LiquidGlassCard(
                modifier = Modifier.padding(24.dp),
                cornerRadius = 20.dp,
                intensity = GlassIntensity.STANDARD
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("当前列表暂无题目", color = primaryText, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("请返回首页选择其他练习模式或先做题积累错题", color = secondaryText, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(18.dp))
                    Button(onClick = onNavigateBack) {
                        Text("返回首页")
                    }
                }
            }
        }
        return
    }

    val currentQ = questions.getOrNull(currentIndex) ?: questions[0]
    val isSubmitted = submittedSet.contains(currentQ.id)
    val userAnswer = userAnswers[currentQ.id]
    val isFav = recordsMap[currentQ.id]?.isFavorite == true

    Box(modifier = Modifier.fillMaxSize()) {
        // Live background (or custom background from user phone storage)
        ThermalFurnaceBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            // Liquid Glass Top Navigation Bar
            LiquidGlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .testTag("practice_top_bar"),
                cornerRadius = 16.dp,
                intensity = GlassIntensity.LIGHT
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = onNavigateBack,
                            modifier = Modifier.testTag("practice_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "返回",
                                tint = primaryText
                            )
                        }
                        Text(
                            text = mode.title,
                            color = primaryText,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Current question progress
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isDark) Color(0x3338BDF8) else Color(0x200284C7)
                        ) {
                            Text(
                                text = "${currentIndex + 1} / ${questions.size}",
                                color = if (isDark) Color(0xFFE0F2FE) else Color(0xFF0284C7),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        // Bookmark Star
                        IconButton(
                            onClick = { viewModel.toggleFavorite(currentQ.id) },
                            modifier = Modifier.testTag("btn_favorite_toggle")
                        ) {
                            Icon(
                                imageVector = if (isFav) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = "收藏",
                                tint = if (isFav) Color(0xFFFBBF24) else secondaryText
                            )
                        }

                        // Grid Answer Sheet
                        IconButton(
                            onClick = { showSheetDialog = true },
                            modifier = Modifier.testTag("btn_answer_sheet")
                        ) {
                            Icon(
                                imageVector = Icons.Default.GridView,
                                contentDescription = "答题卡",
                                tint = Color(0xFF0284C7)
                            )
                        }
                    }
                }
            }

            // Question Content Scrollable Area
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Question Header & Title Card
                item {
                    LiquidGlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("question_title_card"),
                        cornerRadius = 20.dp,
                        intensity = GlassIntensity.STANDARD
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                // Question Type Badge
                                val (typeColor, typeBg) = when (currentQ.type) {
                                    QuestionType.SINGLE -> Pair(Color(0xFF0284C7), if (isDark) Color(0x330284C7) else Color(0x200284C7))
                                    QuestionType.MULTIPLE -> Pair(Color(0xFFD97706), if (isDark) Color(0x33D97706) else Color(0x20D97706))
                                    QuestionType.JUDGMENT -> Pair(Color(0xFF059669), if (isDark) Color(0x33059669) else Color(0x20059669))
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = typeBg
                                ) {
                                    Text(
                                        text = currentQ.type.label,
                                        color = typeColor,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }

                                // Category Pill
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isDark) Color(0x20FFFFFF) else Color(0x15000000)
                                ) {
                                    Text(
                                        text = currentQ.category,
                                        color = secondaryText,
                                        fontSize = 11.sp,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Question Title
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

                // Options List
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        currentQ.options.forEach { option ->
                            OptionItem(
                                question = currentQ,
                                option = option,
                                isSubmitted = isSubmitted,
                                userAnswer = userAnswer,
                                multiDraft = multiDraft,
                                isDark = isDark,
                                onSelect = {
                                    if (currentQ.type == QuestionType.MULTIPLE) {
                                        if (!isSubmitted) {
                                            viewModel.toggleMultiChoiceOption(option.key)
                                        }
                                    } else {
                                        if (!isSubmitted) {
                                            viewModel.selectOption(option.key)
                                        }
                                    }
                                }
                            )
                        }

                        // Submit Multi-Choice Button
                        if (currentQ.type == QuestionType.MULTIPLE && !isSubmitted) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Button(
                                onClick = { viewModel.submitMultiChoice() },
                                enabled = multiDraft.isNotEmpty(),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("btn_submit_multi"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFF97316),
                                    disabledContainerColor = Color(0x33F97316)
                                ),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Text(
                                    text = if (multiDraft.isEmpty()) "请勾选选项后提交" else "确认提交答案 (${multiDraft.sorted().joinToString("")})",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            }
                        }
                    }
                }

                // Explanation & Reference Answer Card (Shown after submitted)
                item {
                    AnimatedVisibility(
                        visible = isSubmitted,
                        enter = fadeIn() + slideInVertically()
                    ) {
                        val isUserCorrect = userAnswer.equals(currentQ.answer, ignoreCase = true)
                        LiquidGlassCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("analysis_card"),
                            cornerRadius = 20.dp,
                            intensity = if (isUserCorrect) GlassIntensity.ALERT_CORRECT else GlassIntensity.ALERT_ERROR
                        ) {
                            Column(
                                modifier = Modifier.padding(18.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (isUserCorrect) Icons.Default.Check else Icons.Default.Close,
                                        contentDescription = null,
                                        tint = if (isUserCorrect) Color(0xFF10B981) else Color(0xFFEF4444),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isUserCorrect) "回答正确！" else "回答错误",
                                        color = if (isUserCorrect) Color(0xFF10B981) else Color(0xFFEF4444),
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "正确答案：${currentQ.answer}",
                                        color = Color(0xFF10B981),
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    if (userAnswer != null) {
                                        Text(
                                            text = "你的答案：$userAnswer",
                                            color = if (isUserCorrect) Color(0xFF10B981) else Color(0xFFEF4444),
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isDark) Color(0x2A000000) else Color(0x15000000),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.Lightbulb,
                                                contentDescription = null,
                                                tint = Color(0xFFF59E0B),
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "知识点解析",
                                                color = if (isDark) Color(0xFFFDE68A) else Color(0xFFB45309),
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = getExplanation(currentQ),
                                            color = primaryText,
                                            fontSize = 13.sp,
                                            lineHeight = 20.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Bottom Navigation Bar
            LiquidGlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .testTag("practice_bottom_bar"),
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
                            containerColor = if (isDark) Color(0x330284C7) else Color(0x200284C7),
                            disabledContainerColor = if (isDark) Color(0x15FFFFFF) else Color(0x10000000)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("btn_prev_question")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                            tint = primaryText,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
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
                            Text("答题卡", color = primaryText, fontSize = 13.sp)
                        }
                    }

                    Button(
                        onClick = { viewModel.nextQuestion() },
                        enabled = currentIndex < questions.size - 1,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF0284C7),
                            disabledContainerColor = if (isDark) Color(0x15FFFFFF) else Color(0x10000000)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("btn_next_question")
                    ) {
                        Text("下一题", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        // Answer Sheet Bottom Modal
        if (showSheetDialog) {
            ModalBottomSheet(
                onDismissRequest = { showSheetDialog = false },
                sheetState = sheetState,
                containerColor = if (isDark) Color(0xFF0F172A) else Color.White,
                contentColor = primaryText
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = "答题卡 (共 ${questions.size} 题)",
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
                            val isQSubmitted = submittedSet.contains(q.id)
                            val isCorrect = ans != null && ans.equals(q.answer, ignoreCase = true)

                            val (bg, textCol) = when {
                                idx == currentIndex -> Pair(Color(0xFF0284C7), Color.White)
                                isQSubmitted && isCorrect -> Pair(Color(0x3310B981), Color(0xFF10B981))
                                isQSubmitted && !isCorrect -> Pair(Color(0x33EF4444), Color(0xFFEF4444))
                                ans != null -> Pair(Color(0x330284C7), Color(0xFF0284C7))
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
private fun OptionItem(
    question: Question,
    option: Option,
    isSubmitted: Boolean,
    userAnswer: String?,
    multiDraft: Set<String>,
    isDark: Boolean,
    onSelect: () -> Unit
) {
    val key = option.key
    val isMulti = question.type == QuestionType.MULTIPLE
    val isDraftSelected = isMulti && multiDraft.contains(key)
    val isUserPicked = userAnswer?.contains(key) == true
    val isCorrectOption = question.answer.contains(key)

    val intensity = when {
        isSubmitted && isCorrectOption -> GlassIntensity.ALERT_CORRECT
        isSubmitted && isUserPicked && !isCorrectOption -> GlassIntensity.ALERT_ERROR
        isDraftSelected || (isUserPicked && !isSubmitted) -> GlassIntensity.STANDARD
        else -> GlassIntensity.LIGHT
    }

    val (badgeBg, badgeText) = when {
        isSubmitted && isCorrectOption -> Pair(Color(0xFF10B981), Color.White)
        isSubmitted && isUserPicked && !isCorrectOption -> Pair(Color(0xFFEF4444), Color.White)
        isDraftSelected || (isUserPicked && !isSubmitted) -> Pair(Color(0xFF0284C7), Color.White)
        else -> Pair(if (isDark) Color(0x26FFFFFF) else Color(0x15000000), if (isDark) Color.White else Color(0xFF0F172A))
    }

    val optionTextColor = if (isDark) Color.White else Color(0xFF0F172A)

    LiquidGlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() }
            .testTag("option_${option.key}"),
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

            if (isSubmitted) {
                if (isCorrectOption) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "正确",
                        tint = Color(0xFF10B981),
                        modifier = Modifier.size(20.dp)
                    )
                } else if (isUserPicked) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "错误",
                        tint = Color(0xFFEF4444),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

private fun getExplanation(q: Question): String {
    return when (q.category) {
        "表面与化学热处理" -> "【热处理解析】${q.title}：在工件表面强化工艺中，渗碳、渗氮、感应淬火等工艺通过局部相变或改变表层化学成分，使零件表面获得极高的硬度和耐磨性，同时心部保持良好的强韧性配合。正确答案为【${q.answer}】。"
        "退火与正火工艺" -> "【热处理解析】${q.title}：退火通常加热到临界点以上随炉缓慢冷却，主要消除内应力、降低硬度并改善切削性；正火为空冷，晶粒更细。球化退火针对高碳钢使碳化物球状化。正确答案为【${q.answer}】。"
        "淬火与回火工艺" -> "【热处理解析】${q.title}：淬火以大于临界冷却速度快冷以获得马氏体或下贝氏体；淬火后零件内应力大、脆性高，必须配合回火以消除内应力、稳定尺寸并调节强韧性。正确答案为【${q.answer}】。"
        "设备与安全质检" -> "【热处理解析】${q.title}：热处理车间必须遵守严格的操作规程，严防水与高温盐浴接触引发爆溅，定期校准测温仪表，确保电气接地与通风消防系统可靠。正确答案为【${q.answer}】。"
        "基础理论与金相组织" -> "【热处理解析】${q.title}：铁碳相图中，铁素体为体心立方固溶体，塑韧性好；奥氏体为面心立方；马氏体为碳在α-Fe中的过饱和固溶体，硬度最高。正确答案为【${q.answer}】。"
        else -> "【热处理解析】${q.title}：该题考察热处理中级工核心工艺规范与材料学基础。标准参考答案为【${q.answer}】。"
    }
}
