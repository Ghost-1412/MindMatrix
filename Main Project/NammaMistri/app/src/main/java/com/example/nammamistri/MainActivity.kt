package com.example.nammamistri

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.nammamistri.ui.theme.NammaMistriTheme
import com.example.nammamistri.ui.theme.OrangePrimary

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NammaMistriTheme {
                NammaMistriApp(viewModel)
            }
        }
    }
}

@Composable
fun NammaMistriApp(viewModel: MainViewModel) {
    var currentLanguage by remember { mutableStateOf(Language.ENGLISH) }
    var currentScreen by remember { mutableStateOf(Screen.LOGIN) }
    var isLoggedIn by remember { mutableStateOf(false) }
    var resetPasswordMobile by remember { mutableStateOf("") }
    
    val workers by viewModel.allWorkers.collectAsState(initial = emptyList())
    val sitePhotos by viewModel.allPhotos.collectAsState(initial = emptyList())

    var workerToEdit by remember { mutableStateOf<Worker?>(null) }

    // Navigation logic for back button
    BackHandler(enabled = currentScreen != Screen.LOGIN && currentScreen != Screen.HOME) {
        currentScreen = when (currentScreen) {
            Screen.REGISTER, Screen.FORGOT_PASSWORD -> Screen.LOGIN
            Screen.RESET_PASSWORD -> Screen.FORGOT_PASSWORD
            Screen.ADD_EDIT_WORKER, Screen.ATTENDANCE -> Screen.LABOR
            else -> Screen.HOME
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        if (!isLoggedIn) {
            when (currentScreen) {
                Screen.LOGIN -> LoginScreen(
                    language = currentLanguage,
                    viewModel = viewModel,
                    onLanguageToggle = {
                        currentLanguage = if (currentLanguage == Language.ENGLISH) Language.KANNADA else Language.ENGLISH
                    },
                    onLoginSuccess = { isLoggedIn = true; currentScreen = Screen.HOME },
                    onNavigateToRegister = { currentScreen = Screen.REGISTER },
                    onNavigateToForgotPassword = { currentScreen = Screen.FORGOT_PASSWORD }
                )
                Screen.REGISTER -> RegisterScreen(
                    language = currentLanguage,
                    viewModel = viewModel,
                    onLanguageToggle = {
                        currentLanguage = if (currentLanguage == Language.ENGLISH) Language.KANNADA else Language.ENGLISH
                    },
                    onRegisterSuccess = { currentScreen = Screen.LOGIN },
                    onNavigateToLogin = { currentScreen = Screen.LOGIN }
                )
                Screen.FORGOT_PASSWORD -> ForgotPasswordScreen(
                    language = currentLanguage,
                    viewModel = viewModel,
                    onLanguageToggle = {
                        currentLanguage = if (currentLanguage == Language.ENGLISH) Language.KANNADA else Language.ENGLISH
                    },
                    onNavigateToReset = { mobile -> 
                        resetPasswordMobile = mobile
                        currentScreen = Screen.RESET_PASSWORD 
                    },
                    onNavigateToLogin = { currentScreen = Screen.LOGIN }
                )
                Screen.RESET_PASSWORD -> ResetPasswordScreen(
                    language = currentLanguage,
                    viewModel = viewModel,
                    mobile = resetPasswordMobile,
                    onLanguageToggle = {
                        currentLanguage = if (currentLanguage == Language.ENGLISH) Language.KANNADA else Language.ENGLISH
                    },
                    onResetSuccess = { currentScreen = Screen.LOGIN },
                    onNavigateToLogin = { currentScreen = Screen.LOGIN }
                )
                else -> currentScreen = Screen.LOGIN
            }
        } else {
            Scaffold(
                topBar = {
                    TopBar(
                        language = currentLanguage,
                        onLanguageToggle = {
                            currentLanguage = if (currentLanguage == Language.ENGLISH) Language.KANNADA else Language.ENGLISH
                        },
                        showBack = currentScreen != Screen.HOME,
                        onBack = { 
                            currentScreen = when (currentScreen) {
                                Screen.ADD_EDIT_WORKER, Screen.ATTENDANCE -> Screen.LABOR
                                else -> Screen.HOME
                            }
                        }
                    )
                },
                bottomBar = {
                    if (currentScreen != Screen.ADD_EDIT_WORKER && currentScreen != Screen.ATTENDANCE) {
                        BottomNav(
                            currentScreen = currentScreen,
                            onScreenSelected = { currentScreen = it },
                            language = currentLanguage
                        )
                    }
                },
                floatingActionButton = {
                    if (currentScreen == Screen.LABOR) {
                        FloatingActionButton(
                            onClick = { 
                                workerToEdit = null
                                currentScreen = Screen.ADD_EDIT_WORKER 
                            },
                            containerColor = OrangePrimary,
                            contentColor = Color.White,
                            shape = CircleShape
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add Worker")
                        }
                    }
                }
            ) { innerPadding ->
                Box(modifier = Modifier.padding(innerPadding)) {
                    when (currentScreen) {
                        Screen.HOME -> HomeScreen(currentLanguage, workers, onNavigate = { currentScreen = it })
                        Screen.CALCULATOR -> CalculatorScreen(currentLanguage)
                        Screen.LABOR -> LaborDiaryScreen(
                            language = currentLanguage,
                            workers = workers,
                            onEditWorker = { 
                                workerToEdit = it
                                currentScreen = Screen.ADD_EDIT_WORKER 
                            },
                            onNavigateToAttendance = { currentScreen = Screen.ATTENDANCE }
                        )
                        Screen.ATTENDANCE -> AttendanceScreen(
                            language = currentLanguage,
                            workers = workers,
                            onUpdateAttendance = { updatedWorkers ->
                                viewModel.updateAttendance(updatedWorkers)
                                currentScreen = Screen.LABOR
                            }
                        )
                        Screen.PHOTOS -> SitePhotosScreen(
                            language = currentLanguage,
                            photos = sitePhotos,
                            onAddPhoto = { viewModel.insertPhoto(it) },
                            onDeletePhoto = { viewModel.deletePhoto(it) }
                        )
                        Screen.ADD_EDIT_WORKER -> AddEditWorkerScreen(
                            language = currentLanguage,
                            worker = workerToEdit,
                            onSave = { updatedWorker ->
                                if (workerToEdit == null) {
                                    viewModel.insertWorker(updatedWorker)
                                } else {
                                    viewModel.updateWorker(updatedWorker)
                                }
                                currentScreen = Screen.LABOR
                            },
                            onDelete = { workerToDelete ->
                                viewModel.deleteWorker(workerToDelete)
                                currentScreen = Screen.LABOR
                            },
                            onCancel = { currentScreen = Screen.LABOR }
                        )
                        else -> HomeScreen(currentLanguage, workers, onNavigate = { currentScreen = it })
                    }
                }
            }
        }
    }
}
