package com.example.calorificomputervision

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.calorificomputervision.ui.theme.CalorifiComputerVisionTheme
import com.example.calorificomputervision.viewmodel.LoginViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.calorificomputervision.data.AppDatabase
import com.example.calorificomputervision.repository.UserRepository
import com.example.calorificomputervision.ui.pages.LoginScreen
import com.example.calorificomputervision.ui.pages.RegisterScreen
import com.example.calorificomputervision.viewmodel.LoginViewModelFactory


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CalorifiComputerVisionTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var currentScreen by remember { mutableStateOf("login") }

                    val userDao = AppDatabase.getDatabase(this@MainActivity).userDao()

                    val userRepository = UserRepository(userDao)

                    val loginViewModel: LoginViewModel = viewModel(
                        factory = LoginViewModelFactory(userRepository)
                    )

                    when (currentScreen) {
                        "login" -> LoginScreen(
                            viewModel = loginViewModel,
                            onRegisterClick = { currentScreen = "register"}
                        )
                        "register" -> RegisterScreen(
                            viewModel = loginViewModel,
                            onLoginClick = { currentScreen = "login" }
                        )
                    }
                }
            }
        }
    }
}

