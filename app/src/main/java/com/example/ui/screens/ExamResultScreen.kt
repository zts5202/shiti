package com.example.ui.screens

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.glass.GlassIntensity
import com.example.ui.glass.LiquidGlassCard
import com.example.ui.glass.ThermalFurnaceBackground
import com.example.ui.theme.ThemeManager
import com.example.viewmodel.ExamViewModel
import com.example.viewmodel.PracticeMode

@Composable
fun ExamResultScreen(
    viewModel: ExamViewModel,
    onReviewWrong: () -> Unit,
    onRetakeExam: () -> Unit,
    onGoHome: () -> Unit
) {
    val isDark = ThemeManager.isDarkMode
    val primaryText = if (isDark) Color.White else Color(0xFF0F172A)
    val secondaryText = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)

    val result by viewModel.lastExamResult.collectAsState()

    if (result == null) {
        onGoHome()
        return
    }

    val res = result!!
    val isPassed = res.passed
    val minutes = res.durationSeconds / 60
    val seconds = res.durationSeconds % 60
    val timeStr = String.format("%d分%02d秒", minutes, seconds)

    Box(modifier = Modifier.fillMaxSize()) {
        ThermalFurnaceBackground()

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "考试成绩单",
                    color = primaryText,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "全国热处理中级工全真模拟考场",
                    color = secondaryText,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            // Certificate Liquid Glass Card
            item {
                LiquidGlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("result_card"),
                    cornerRadius = 24.dp,
                    intensity = if (isPassed) GlassIntensity.HERO else GlassIntensity.STANDARD
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Pass / Fail badge
                        Surface(
                            shape = CircleShape,
                            color = if (isPassed) Color(0x3310B981) else Color(0x33EF4444),
                            modifier = Modifier.size(72.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = if (isPassed) Icons.Default.CheckCircle else Icons.Default.Error,
                                    contentDescription = null,
                                    tint = if (isPassed) Color(0xFF10B981) else Color(0xFFEF4444),
                                    modifier = Modifier.size(44.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = if (isPassed) "考试合格" else "未达到及格线",
                            color = if (isPassed) Color(0xFF10B981) else Color(0xFFEF4444),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = "${res.score}",
                                color = primaryText,
                                fontSize = 54.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                text = " / 100 分",
                                color = secondaryText,
                                fontSize = 16.sp,
                                modifier = Modifier.padding(bottom = 10.dp, start = 4.dp)
                            )
                        }

                        Text(
                            text = "及格线：60分 • 耗时：$timeStr",
                            color = secondaryText,
                            fontSize = 13.sp
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Score breakdown
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            ScoreItem("单选题", "${res.singleScore}/50分", Color(0xFF0284C7), isDark)
                            ScoreItem("多选题", "${res.multiScore}/25分", Color(0xFFF59E0B), isDark)
                            ScoreItem("判断题", "${res.judgeScore}/25分", Color(0xFF10B981), isDark)
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            ScoreItem("答对题目", "${res.correctCount} 题", Color(0xFF10B981), isDark)
                            ScoreItem("答错/未答", "${res.wrongCount} 题", Color(0xFFEF4444), isDark)
                            ScoreItem("正确率", "${res.correctCount}%", primaryText, isDark)
                        }
                    }
                }
            }

            // Action Buttons
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (res.wrongCount > 0) {
                        Button(
                            onClick = onReviewWrong,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("btn_review_wrong"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFEF4444)
                            ),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Visibility, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "复盘本场错题 (${res.wrongCount} 题)",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                    }

                    Button(
                        onClick = onRetakeExam,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("btn_retake_exam"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFF97316)
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Replay, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "再考一次 (重新抽题)",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }

                    OutlinedButton(
                        onClick = onGoHome,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("btn_go_home"),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Home, contentDescription = null, tint = primaryText)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "返回首页",
                            color = primaryText,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ScoreItem(title: String, score: String, color: Color, isDark: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = title,
            color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B),
            fontSize = 12.sp
        )
        Text(
            text = score,
            color = color,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}
