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
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.serialization.json.Json


class AuthViewModel(val authRepository: AuthRepository, val dao: AuthDao) : ViewModel() {
    private val _isLoadin = MutableStateFlow<Boolean>(false)
    val isLoadin = _isLoadin.asStateFlow()


    private val _cuurentScreen = MutableStateFlow<Int?>(null);
    val currentScreen = _cuurentScreen.asStateFlow();
    private var _coroutinExption = CoroutineExceptionHandler { _, message ->
        Log.d("ErrorMessageIs", message.message.toString())
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




    suspend fun loginUser(
        username: String,
        password: String,
    ): String? {
        _isLoadin.emit(true);
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

                return null;
            }

            is NetworkCallHandler.Error -> {

                var errorMessage = (result.data.toString())
                if (errorMessage.contains(General.BASED_URL)) {
                    errorMessage.replace(General.BASED_URL, " Server ")
                }
                return errorMessage
            }

        }
    }

}