package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ui.screens.ExamResultScreen
import com.example.ui.screens.ExamScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.PracticeScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.ThemeManager
import com.example.viewmodel.ExamViewModel
import com.example.viewmodel.PracticeMode

class MainActivity : ComponentActivity() {
    private val viewModel: ExamViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ThemeManager.init(this)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ExamApp(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun ExamApp(viewModel: ExamViewModel) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HomeScreen(
                viewModel = viewModel,
                onNavigatePractice = { mode, category ->
                    viewModel.startPractice(mode, category)
                    navController.navigate("practice")
                },
                onNavigateExam = {
                    viewModel.startMockExam()
                    navController.navigate("exam")
                }
            )
        }

        composable("practice") {
            PracticeScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable("exam") {
            ExamScreen(
                viewModel = viewModel,
                onExamSubmitted = {
                    navController.navigate("exam_result") {
                        popUpTo("home") { inclusive = false }
                    }
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable("exam_result") {
            ExamResultScreen(
                viewModel = viewModel,
                onReviewWrong = {
                    viewModel.startPractice(PracticeMode.WRONG)
                    navController.navigate("practice")
                },
                onRetakeExam = {
                    viewModel.startMockExam()
                    navController.navigate("exam") {
                        popUpTo("home") { inclusive = false }
                    }
                },
                onGoHome = {
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }
    }
}
