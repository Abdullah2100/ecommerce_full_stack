package com.example.e_commerc_delivery_man.viewModel

import android.util.Log
import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.example.e_commerc_delivery_man.Dto.AuthResultDto
import com.example.e_commerc_delivery_man.Util.General
import com.example.e_commerc_delivery_man.data.Room.AuthDao
import com.example.e_commerc_delivery_man.data.Room.AuthModleEntity
import com.example.e_commerc_delivery_man.data.Room.IsPassOnBoardingScreen
import com.example.e_commerc_delivery_man.dto.request.LoginDto
import com.example.e_commerc_delivery_man.data.repository.AuthRepository
import com.example.e_commerc_delivery_man.dto.request.SignupDto
import com.example.e_commerc_delivery_man.ui.Screens
import com.example.hotel_mobile.Modle.NetworkCallHandler
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


class AuthViewModel(val authRepository: AuthRepository, val dao: AuthDao) : ViewModel() {
    private val _isLoading = MutableStateFlow<Boolean>(false)
    val isLoading = _isLoading.asStateFlow()

    val errorMesssage = MutableStateFlow<String?>(null)


    private val _cuurentScreen = MutableStateFlow<Int?>(null);
    val currentScreen = _cuurentScreen.asStateFlow();

    suspend fun clearErrorMessage() {
        errorMesssage.emit(null)
    }

    val _coroutinExption = CoroutineExceptionHandler { _, message ->
        Log.d("ErrorMessageIs", message.message.toString())
        viewModelScope.launch(Dispatchers.Main) {
            _isLoading.emit(false)
            errorMesssage.update { "لا بد من تفعيل الانترنت لاكمال العملية" }
        }
    }

    init {
        getStartedScreen()
    }

    fun getStartedScreen() {
        viewModelScope.launch(Dispatchers.Default + _coroutinExption) {
            val authData = dao.getAuthData()
            when (authData != null) {
                false -> {
                    _cuurentScreen.emit(0)
                }

                else -> {
                    _cuurentScreen.emit(1)
                }
            }

        }
    }


    fun loginUser(
        username: String,
        password: String,
        nav: NavHostController,
    ) {
        viewModelScope.launch(Dispatchers.Default + _coroutinExption) {
            _isLoading.emit(true);
            val token = FirebaseMessaging.getInstance().token.await() ?: ""

            val result = authRepository.login(
                LoginDto(
                    username = username,
                    password = password,
                    deviceToken = token
                )
            )
            when (result) {
                is NetworkCallHandler.Successful<*> -> {
                    _isLoading.emit(false);

                    val authData = result.data as AuthResultDto;
                    var authDataHolder = AuthModleEntity(
                        id = 0,
                        token = authData.accessToken,
                        refreshToken = authData.refreshToken
                    )
                    dao.saveAuthData(
                        authDataHolder
                    )
                    General.authData.emit(authDataHolder)
                    viewModelScope.launch(Dispatchers.Main) {
                        nav.navigate(Screens.HomeGraph) {
                            popUpTo(nav.graph.id) {
                                inclusive = true
                            }
                        }
                    }

                }

                is NetworkCallHandler.Error -> {
                    _isLoading.emit(false);

                    var errorMessage = (result.data.toString())
                    if (errorMessage.contains(General.BASED_URL)) {
                        errorMessage.replace(General.BASED_URL, " Server ")
                    }
                    errorMesssage.emit(errorMessage)
                }

            }
        }


    }
}