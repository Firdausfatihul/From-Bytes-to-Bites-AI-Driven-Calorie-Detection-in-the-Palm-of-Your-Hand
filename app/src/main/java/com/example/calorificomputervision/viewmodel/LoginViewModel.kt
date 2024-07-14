package com.example.calorificomputervision.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.calorificomputervision.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginUiState (
    val username: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false,
    val isLoggedIn: Boolean = false,
    val loggedInUsername: String? = null
)

class LoginViewModel(private val userRepository: UserRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState

    fun updateUsername(username: String){
        _uiState.update {
            it.copy(username = username)
        }
    }

    fun updatePassword(password: String){
        _uiState.update {
            it.copy(password = password)
        }
    }

    fun register() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = true, error = null)
            }
            try {
                userRepository.registerUser(_uiState.value.username, _uiState.value.password)
                _uiState.update {
                    it.copy(isLoading = false, success = true)
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, error = e.message)
                }
            }
        }
    }

    fun login() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = true, error = null)
            }
            try {
                val user = userRepository.loginUser(_uiState.value.username, _uiState.value.password)
                if (user != null) {
                    _uiState.update {
                        it.copy(isLoading = false, success = true, isLoggedIn = true, loggedInUsername = user.username)
                    }
                } else {
                    _uiState.update {
                        it.copy(isLoading = false, error = "Invalid Credentials")
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, error = e.message)
                }
            }
        }
    }

}