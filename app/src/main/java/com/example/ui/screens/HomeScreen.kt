package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
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
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    viewModel: ExamViewModel,
    onNavigatePractice: (PracticeMode, String?) -> Unit,
    onNavigateExam: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val isDark = ThemeManager.isDarkMode

    val allQuestions by viewModel.allQuestions.collectAsState()
    val recordsMap by viewModel.recordsMap.collectAsState()
    val wrongQuestions by viewModel.wrongQuestions.collectAsState()
    val favoriteQuestions by viewModel.favoriteQuestions.collectAsState()
    val examRecords by viewModel.examRecords.collectAsState()
    val categories by viewModel.categories.collectAsState()

    val totalCount = allQuestions.size.coerceAtLeast(490)
    val answeredCount = recordsMap.size
    val correctCount = recordsMap.values.count { it.isCorrect }
    val accuracy = if (answeredCount > 0) (correctCount * 100 / answeredCount) else 0
    val bestScore = examRecords.maxOfOrNull { it.score } ?: 0

    // Custom background options dialog
    var showBgOptionsDialog by remember { mutableStateOf(false) }

    // Android zero-permission Photo Picker for custom folder wallpaper
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            coroutineScope.launch {
                val success = ThemeManager.saveCustomBackground(context, uri)
                if (success) {
                    Toast.makeText(context, "已成功设置自定义界面背景", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "背景图片加载失败，请重试", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    val primaryTextColor = if (isDark) Color.White else Color(0xFF0F172A)
    val secondaryTextColor = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)

    Box(modifier = Modifier.fillMaxSize()) {
        // Living dynamic background (or user custom background from phone files) placed underneath all buttons
        ThermalFurnaceBackground()

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 36.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // App Header with Liquid Glass Badge & Dark/Light Switch & Custom BG button
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocalFireDepartment,
                                contentDescription = null,
                                tint = Color(0xFFF97316),
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "热处理中级工",
                                color = primaryTextColor,
                                fontSize = 23.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                        }
                        Text(
                            text = "国家题库490题 • 液态玻璃视效",
                            color = secondaryTextColor,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }

                    // Action Controls: Theme Switch & Background Wallpaper Picker
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Light / Dark Mode Toggle Button
                        LiquidGlassCard(
                            modifier = Modifier
                                .testTag("btn_toggle_theme")
                                .clickable {
                                    ThemeManager.toggleDarkMode(context)
                                },
                            cornerRadius = 14.dp,
                            intensity = GlassIntensity.LIGHT
                        ) {
                            Box(
                                modifier = Modifier.padding(8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isDark) Icons.Default.LightMode else Icons.Default.DarkMode,
                                    contentDescription = if (isDark) "切换为亮色模式" else "切换为暗色模式",
                                    tint = if (isDark) Color(0xFFFBBF24) else Color(0xFF0284C7),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        // Custom Background Image Picker from Phone Files
                        LiquidGlassCard(
                            modifier = Modifier
                                .testTag("btn_pick_background")
                                .clickable {
                                    if (ThemeManager.hasCustomBackground) {
                                        showBgOptionsDialog = true
                                    } else {
                                        photoPickerLauncher.launch(
                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                        )
                                    }
                                },
                            cornerRadius = 14.dp,
                            intensity = GlassIntensity.LIGHT
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Image,
                                    contentDescription = "选择背景",
                                    tint = Color(0xFF38BDF8),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (ThemeManager.hasCustomBackground) "自定义背景" else "换背景",
                                    color = primaryTextColor,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }

            // Hero Liquid Glass Stats Card
            item {
                LiquidGlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("hero_stats_card"),
                    cornerRadius = 24.dp,
                    intensity = GlassIntensity.HERO
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "备考进度",
                                    color = secondaryTextColor,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "$answeredCount / $totalCount 题",
                                    color = primaryTextColor,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            // Correct rate pill
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isDark) Color(0x330284C7) else Color(0x200284C7)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = Color(0xFF0284C7),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "正确率 $accuracy%",
                                        color = if (isDark) Color(0xFFE0F2FE) else Color(0xFF0284C7),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Progress Bar
                        LinearProgressIndicator(
                            progress = { (answeredCount.toFloat() / totalCount.toFloat()).coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(7.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = Color(0xFF0284C7),
                            trackColor = if (isDark) Color(0x33334155) else Color(0x20CBD5E1),
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Sub stats
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            StatItem(title = "已掌握", value = "$correctCount 题", color = Color(0xFF10B981), isDark = isDark)
                            StatItem(title = "错题积累", value = "${wrongQuestions.size} 题", color = Color(0xFFEF4444), isDark = isDark)
                            StatItem(title = "模考最高分", value = "$bestScore 分", color = Color(0xFFF59E0B), isDark = isDark)
                        }
                    }
                }
            }

            // Quick Start: Mock Exam Hero Card
            item {
                LiquidGlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("mock_exam_banner")
                        .clickable { onNavigateExam() },
                    cornerRadius = 20.dp,
                    intensity = GlassIntensity.STANDARD
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = Color(0x33F97316),
                                modifier = Modifier.size(48.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Quiz,
                                        contentDescription = null,
                                        tint = Color(0xFFF97316),
                                        modifier = Modifier.size(26.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "全真模拟考试",
                                        color = primaryTextColor,
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = Color(0x33EF4444)
                                    ) {
                                        Text(
                                            text = "仿真考场",
                                            color = Color(0xFFEF4444),
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = "100题 • 60分钟限时 • 60分及格",
                                    color = secondaryTextColor,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                        }

                        Button(
                            onClick = { onNavigateExam() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFF97316)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(text = "开考", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }

            // Core Modes Section Title
            item {
                SectionTitle(title = "训练模式", isDark = isDark)
            }

            // 4 Grid Main Practice Modes
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ModeCard(
                            modifier = Modifier
                                .weight(1f)
                                .testTag("btn_sequential_practice"),
                            title = "顺序练习",
                            desc = "1-490题完整刷题",
                            icon = Icons.Default.FormatListNumbered,
                            accentColor = Color(0xFF0284C7),
                            isDark = isDark,
                            onClick = { onNavigatePractice(PracticeMode.SEQUENTIAL, null) }
                        )

                        ModeCard(
                            modifier = Modifier
                                .weight(1f)
                                .testTag("btn_random_practice"),
                            title = "随机练习",
                            desc = "打乱顺序专项冲刺",
                            icon = Icons.Default.Shuffle,
                            accentColor = Color(0xFF8B5CF6),
                            isDark = isDark,
                            onClick = { onNavigatePractice(PracticeMode.RANDOM, null) }
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ModeCard(
                            modifier = Modifier
                                .weight(1f)
                                .testTag("btn_wrong_notebook"),
                            title = "错题巩固",
                            desc = "${wrongQuestions.size}道待复习题目",
                            icon = Icons.Default.Error,
                            accentColor = Color(0xFFEF4444),
                            badge = if (wrongQuestions.isNotEmpty()) "${wrongQuestions.size}" else null,
                            isDark = isDark,
                            onClick = { onNavigatePractice(PracticeMode.WRONG, null) }
                        )

                        ModeCard(
                            modifier = Modifier
                                .weight(1f)
                                .testTag("btn_favorite_questions"),
                            title = "重点收藏",
                            desc = "${favoriteQuestions.size}道收藏题目",
                            icon = Icons.Default.Bookmark,
                            accentColor = Color(0xFFF59E0B),
                            badge = if (favoriteQuestions.isNotEmpty()) "${favoriteQuestions.size}" else null,
                            isDark = isDark,
                            onClick = { onNavigatePractice(PracticeMode.FAVORITE, null) }
                        )
                    }
                }
            }

            // Question Type Specialization Title
            item {
                SectionTitle(title = "题型专项突破", isDark = isDark)
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    TypeChip(
                        modifier = Modifier.weight(1f),
                        title = "单选题",
                        count = "240题",
                        isDark = isDark,
                        onClick = { onNavigatePractice(PracticeMode.TYPE_SINGLE, null) }
                    )
                    TypeChip(
                        modifier = Modifier.weight(1f),
                        title = "多选题",
                        count = "120题",
                        isDark = isDark,
                        onClick = { onNavigatePractice(PracticeMode.TYPE_MULTIPLE, null) }
                    )
                    TypeChip(
                        modifier = Modifier.weight(1f),
                        title = "判断题",
                        count = "130题",
                        isDark = isDark,
                        onClick = { onNavigatePractice(PracticeMode.TYPE_JUDGMENT, null) }
                    )
                }
            }

            // Categories Section Title
            item {
                SectionTitle(title = "按工艺章节分类", isDark = isDark)
            }

            // Category Cards
            items(categories) { categoryName ->
                val categoryQuestions = allQuestions.filter { it.category == categoryName }
                val count = categoryQuestions.size
                LiquidGlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigatePractice(PracticeMode.CATEGORY, categoryName) },
                    cornerRadius = 16.dp,
                    intensity = GlassIntensity.LIGHT
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0x200284C7),
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Category,
                                        contentDescription = null,
                                        tint = Color(0xFF0284C7),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = categoryName,
                                    color = primaryTextColor,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "共 $count 题",
                                    color = secondaryTextColor,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = Color(0xFF94A3B8),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        // Custom Background Management Dialog
        if (showBgOptionsDialog) {
            AlertDialog(
                onDismissRequest = { showBgOptionsDialog = false },
                title = { Text("背景设置", color = primaryTextColor, fontWeight = FontWeight.Bold) },
                text = {
                    Text("您可以更换手机文件夹中的其他图片，或恢复默认淬火动态背景。", color = secondaryTextColor)
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showBgOptionsDialog = false
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7))
                    ) {
                        Text("从相册/文件夹重选", color = Color.White)
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showBgOptionsDialog = false
                            ThemeManager.resetToDefaultBackground(context)
                            Toast.makeText(context, "已恢复默认背景", Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Icon(imageVector = Icons.Default.Restore, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("恢复默认背景", color = Color(0xFFEF4444))
                    }
                },
                containerColor = if (isDark) Color(0xFF0F172A) else Color.White
            )
        }
    }
}

@Composable
private fun SectionTitle(title: String, isDark: Boolean) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(2.dp),
            color = Color(0xFF0284C7),
            modifier = Modifier
                .width(4.dp)
                .height(16.dp)
        ) {}
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            color = if (isDark) Color.White else Color(0xFF0F172A),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun StatItem(title: String, value: String, color: Color, isDark: Boolean) {
    Column {
        Text(
            text = title,
            color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B),
            fontSize = 12.sp
        )
        Text(
            text = value,
            color = color,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}

@Composable
private fun ModeCard(
    modifier: Modifier = Modifier,
    title: String,
    desc: String,
    icon: ImageVector,
    accentColor: Color,
    badge: String? = null,
    isDark: Boolean,
    onClick: () -> Unit
) {
    LiquidGlassCard(
        modifier = modifier.clickable { onClick() },
        cornerRadius = 18.dp,
        intensity = GlassIntensity.STANDARD
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = accentColor.copy(alpha = 0.18f),
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = accentColor,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                if (badge != null) {
                    Surface(
                        shape = CircleShape,
                        color = accentColor
                    ) {
                        Text(
                            text = badge,
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = title,
                color = if (isDark) Color.White else Color(0xFF0F172A),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = desc,
                color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B),
                fontSize = 11.sp,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}

@Composable
private fun TypeChip(
    modifier: Modifier = Modifier,
    title: String,
    count: String,
    isDark: Boolean,
    onClick: () -> Unit
) {
    LiquidGlassCard(
        modifier = modifier.clickable { onClick() },
        cornerRadius = 14.dp,
        intensity = GlassIntensity.LIGHT
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                color = if (isDark) Color.White else Color(0xFF0F172A),
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = count,
                color = Color(0xFF0284C7),
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}
