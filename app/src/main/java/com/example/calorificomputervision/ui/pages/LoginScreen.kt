package com.example.calorificomputervision.ui.pages

import android.graphics.Outline
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.calorificomputervision.viewmodel.LoginViewModel
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import com.example.calorificomputervision.ui.utils.NeuromorphicShadowModifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onRegisterClick: () -> Unit,
    onLoginSuccess: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isLoggedIn) {
        if (uiState.isLoggedIn) {
            uiState.loggedInUsername?.let { username ->
                onLoginSuccess(username)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Login",
            style = MaterialTheme.typography.headlineMedium.copy(
                color = Color.Black
            ),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Custom TextField with neuromorphic style
        OutlinedTextField(
            value = uiState.username,
            onValueChange = viewModel::updateUsername,
            label = { Text("Username") },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                containerColor = Color.Transparent, // Make the background transparent
                focusedLabelColor = Color.Black,
                unfocusedLabelColor = Color.Gray
            ),
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Transparent) // Ensure background is transparent
                .then(NeuromorphicShadowModifier())
                .padding(4.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Custom TextField with neuromorphic style
        OutlinedTextField(
            value = uiState.password,
            onValueChange = viewModel::updatePassword,
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                containerColor = Color.Transparent, // Make the background transparent
                focusedLabelColor = Color.Black,
                unfocusedLabelColor = Color.Gray
            ),
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Transparent) // Ensure background is transparent
                .then(NeuromorphicShadowModifier())
                .padding(4.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Neuromorphic style Button
        Button(
            onClick = viewModel::login,
            modifier = Modifier
                .fillMaxWidth()
                .then(NeuromorphicShadowModifier())
                .padding(4.dp),
            enabled = !uiState.isLoading
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary)
            } else {
                Text("Login")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Neuromorphic style TextButton
        TextButton(
            onClick = onRegisterClick,
            modifier = Modifier
                .then(NeuromorphicShadowModifier())
                .padding(4.dp)
        ) {
            Text("Don't have an account? Register here")
        }

        uiState.error?.let { error: String ->
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}