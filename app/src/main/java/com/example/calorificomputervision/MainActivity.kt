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
import androidx.navigation.NavHost
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.calorificomputervision.data.AppDatabase
import com.example.calorificomputervision.repository.UserRepository
import com.example.calorificomputervision.ui.pages.DashbourdScreen
import com.example.calorificomputervision.ui.pages.LoginScreen
import com.example.calorificomputervision.ui.pages.RegisterScreen
import com.example.calorificomputervision.viewmodel.LoginViewModelFactory


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = AppDatabase.getDatabase(applicationContext)
        val userRepository = UserRepository(database.userDao())
        val loginViewModelFactory = LoginViewModelFactory(userRepository)

        setContent {
            CalorifiComputerVisionTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    NavHost(navController = navController, startDestination = "login") {
                        composable("login") {
                            val viewModel: LoginViewModel = viewModel(factory = loginViewModelFactory)
                            LoginScreen(
                                viewModel = viewModel,
                                onRegisterClick = { navController.navigate("register") },
                                onLoginSuccess = { username ->
                                    navController.navigate("dashboard/$username") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable("register") {
                            val viewModel: LoginViewModel = viewModel(factory = loginViewModelFactory)
                            RegisterScreen(
                                viewModel = viewModel,
                                onLoginClick = {navController.navigate("login")}
                            )
                        }
                        composable("dashboard/{username}") { backStackEntry ->
                            val username = backStackEntry.arguments?.getString("username") ?: ""
                            DashbourdScreen(
                                username = username,
                                onLogout = {
                                    navController.navigate("login") {
                                        popUpTo("dashboard/{username}") { inclusive = true }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

