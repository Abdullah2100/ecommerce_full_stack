package com.example.eccomerce_app.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.example.eccomerce_app.util.General
import com.example.eccomerce_app.data.Room.AuthDao
import com.example.e_commercompose.data.Room.AuthModleEntity
import com.example.e_commercompose.data.Room.IsPassOnBoardingScreen
import com.example.eccomerce_app.dto.LoginDto
import com.example.eccomerce_app.data.repository.AuthRepository
import com.example.eccomerce_app.ui.Screens
import com.example.eccomerce_app.dto.AuthDto
import com.example.eccomerce_app.dto.SignupDto
import com.example.hotel_mobile.Modle.NetworkCallHandler
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


class AuthViewModel(val authRepository: AuthRepository, val dao: AuthDao) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    val errorMessage = MutableStateFlow<String?>(null)


    private val _currentScreen = MutableStateFlow<Int?>(null)
    val currentScreen = _currentScreen.asStateFlow()

    suspend fun clearErrorMessage() {
        errorMessage.emit(null)
    }

    val _coroutineException = CoroutineExceptionHandler { _, message ->
        Log.d("ErrorMessageIs", message.message.toString())
        viewModelScope.launch(Dispatchers.Main) {
            _isLoading.emit(false)
            when (message.message?.contains("java.net.ConnectException")) {
                true -> {
                    errorMessage.update { "لا بد من تفعيل الانترنت لاكمال العملية" }
                }

                else -> {
                    errorMessage.update { "المستخدم غير موجود" }

                }
            }
        }
    }

    init {
        getStartedScreen()
    }

    fun getStartedScreen() {
        viewModelScope.launch(Dispatchers.IO) {
            val authData = dao.getAuthData()
            val isPassOnBoard = dao.isPassOnBoarding()
            val isLocation = dao.isPassLocationScreen()
            Log.d("AuthDataIs", authData.toString())
            General.authData.emit(authData)
            when (isPassOnBoard) {
                false -> {
                    _currentScreen.emit(1)
                }

                else -> {
                    when (authData == null) {
                        true -> {
                            _currentScreen.emit(2)
                        }

                        else -> {
                            when (!isLocation) {
                                true -> {
                                    _currentScreen.emit(3)
                                }

                                else -> {
                                    _currentScreen.emit(4)
                                }
                            }
                        }
                    }
                }
            }

        }
    }

    fun setIsPassOnBoardingScreen() {
        viewModelScope.launch(Dispatchers.IO) {
            dao.saveIsPassingOnBoarding(IsPassOnBoardingScreen(0, true))
        }
    }


    fun signUpUser(
        email: String,
        name: String,
        phone: String,
        password: String,
        nav: NavHostController,
    ) {
        viewModelScope.launch(Dispatchers.IO + _coroutineException) {
            _isLoading.emit(true)
            val token = FirebaseMessaging.getInstance().token.await() ?: ""
            val result = authRepository.signup(
                SignupDto(
                    Name = name,
                    Password = password,
                    Phone = phone,
                    Email = email,
                    DeviceToken = token
                )
            )
            when (result) {
                is NetworkCallHandler.Successful<*> -> {
                    _isLoading.emit(false)
                    val authData = result.data as AuthDto
                    val authDataHolder = AuthModleEntity(
                        id = 0,
                        token = authData.token,
                        RefreshToken = authData.refreshToken
                    )
                    dao.saveAuthData(
                        authDataHolder
                    )

                    General.authData.emit(authDataHolder)
                    nav.navigate(Screens.LocationGraph) {
                        popUpTo(nav.graph.id) {
                            inclusive = true
                        }
                    }
                }

                is NetworkCallHandler.Error -> {
                    _isLoading.emit(false)

                    val errorResult = (result.data.toString())
                    if (errorResult.contains(General.BASED_URL)) {
                        errorResult.replace(General.BASED_URL, " Server ")
                    }
                    errorMessage.emit(errorResult)
                }

            }

        }
    }

    fun loginUser(
        username: String,
        password: String,
        nav: NavHostController,
    ) {
        viewModelScope.launch(Dispatchers.IO + _coroutineException) {
            _isLoading.emit(true)
            val token = FirebaseMessaging.getInstance().token.await() ?: ""

            val result = authRepository.login(
                LoginDto(
                    Username = username,
                    Password = password,
                    DeviceToken = token
                )
            )
            when (result) {
                is NetworkCallHandler.Successful<*> -> {
                    _isLoading.emit(false)

                    val authData = result.data as AuthDto
                    val authDataHolder = AuthModleEntity(
                        id = 0,
                        token = authData.token,
                        RefreshToken = authData.refreshToken
                    )
                    dao.saveAuthData(
                        authDataHolder
                    )
                    General.authData.emit(authDataHolder)
                    viewModelScope.launch(Dispatchers.Main) {
                        nav.navigate(Screens.LocationGraph) {
                            popUpTo(nav.graph.id) {
                                inclusive = true
                            }
                        }
                    }

                }

                is NetworkCallHandler.Error -> {
                    _isLoading.emit(false)

                    val errorResult = (result.data?.replace("\"", ""))

                    errorMessage.emit(errorResult)
                }

            }
        }

    }


    suspend fun getOtp(
        email: String,
    ): String? {
        _isLoading.emit(true)

        val result = authRepository.getOtp(email)
        when (result) {
            is NetworkCallHandler.Successful<*> -> {
                return null
            }

            is NetworkCallHandler.Error -> {

                val errorMessage = (result.data.toString())
                if (errorMessage.contains(General.BASED_URL)) {
                    errorMessage.replace(General.BASED_URL, " Server ")
                }
                return errorMessage
            }

        }
    }

    suspend fun otpVerifing(
        email: String,
        otp: String
    ): String? {
        _isLoading.emit(true)

        val result = authRepository.verifyingOtp(email, otp)
        when (result) {
            is NetworkCallHandler.Successful<*> -> {
                return null
            }

            is NetworkCallHandler.Error -> {

                val errorMessage = (result.data.toString())
                if (errorMessage.contains(General.BASED_URL)) {
                    errorMessage.replace(General.BASED_URL, " Server ")
                }
                return errorMessage
            }

        }
    }


    suspend fun reseatPassword(
        email: String,
        otp: String,
        newPassword: String

    ): String? {

        val result = authRepository.resetPassword(email, otp, newPassword)
        when (result) {
            is NetworkCallHandler.Successful<*> -> {
                val authData = result.data as AuthDto
                val authDataHolder = AuthModleEntity(
                    id = 0,
                    token = authData.token,
                    RefreshToken = authData.refreshToken
                )
                dao.saveAuthData(
                    authDataHolder
                )
                General.authData.emit(authDataHolder)

                return null
            }

            is NetworkCallHandler.Error -> {

                val errorMessage = (result.data.toString())
                if (errorMessage.contains(General.BASED_URL)) {
                    errorMessage.replace(General.BASED_URL, " Server ")
                }
                return errorMessage
            }

        }
    }

    fun logout() {
        viewModelScope.launch(Dispatchers.IO + _coroutineException) {
            dao.nukeTable()
        }
    }
}