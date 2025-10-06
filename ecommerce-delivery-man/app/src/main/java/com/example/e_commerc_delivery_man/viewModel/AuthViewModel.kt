package com.example.e_commerc_delivery_man.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.e_commerc_delivery_man.Util.General
import com.example.e_commerc_delivery_man.Util.General.removeTheSingle
import com.example.e_commerc_delivery_man.data.Room.AuthModleEntity
import com.example.e_commerc_delivery_man.data.Room.IAuthDao
import com.example.e_commerc_delivery_man.data.Room.ILocationDao
import com.example.e_commerc_delivery_man.dto.AuthDto
import com.example.e_commerc_delivery_man.data.repository.AuthRepository
import com.example.e_commerc_delivery_man.dto.AuthResultDto
import com.example.hotel_mobile.Modle.NetworkCallHandler
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


class AuthViewModel(
    val authRepository: AuthRepository,
    val authDao: IAuthDao,
    val locationDao: ILocationDao
) : ViewModel() {
    private val _isLoading = MutableStateFlow<Boolean>(false)
    val isLoading = _isLoading.asStateFlow()


    private val _currentScreen = MutableStateFlow<Int?>(null);
    val currentScreen = _currentScreen.asStateFlow();

    val _coroutinExption = CoroutineExceptionHandler { _, message ->
        Log.d("ErrorMessageIs", message.message.toString())
        viewModelScope.launch(Dispatchers.Main) {
            _isLoading.emit(false)
        }
    }

    init {
        getStartedScreen()
    }


    fun getStartedScreen() {
        viewModelScope.launch(Dispatchers.Default + _coroutinExption) {
            val authData = authDao.getAuthData()
            val isPassLocationScreen = locationDao.isPassLocationScreen()

            when (authData == null) {
                true -> {
                    _currentScreen.emit(0)
                }

                else->{
                    when(!isPassLocationScreen)
                    {
                        true -> {
                            _currentScreen.emit(1)
                        }
                        else -> {
                            General.authData.emit(authData)
                            _currentScreen.emit(2)
                        }
                    }
                }

            }

        }
    }

    suspend fun generateTokenNotification(): Pair<String?, String?> {
        return try {
            val token = FirebaseMessaging.getInstance().token.await()
            Pair(token, null)
        } catch (e: Exception) {
            Pair(null, "Network should be connecting for some functionality")
        }
    }

    suspend fun loginUser(
        username: String,
        password: String,
        token: String
    ): String? {
        val result = authRepository.login(
            AuthDto(
                Username = username,
                Password = password,
                DeviceToken = token
            )
        )
        return when (result) {
            is NetworkCallHandler.Successful<*> -> {
                _isLoading.emit(false);

                val authData = result.data as AuthResultDto;
                val authDataHolder = AuthModleEntity(
                    id = 0,
                    token = authData.accessToken,
                    refreshToken = authData.refreshToken
                )
                authDao.saveAuthData(
                    authDataHolder
                )
                General.authData.emit(authDataHolder)
                null
            }

            is NetworkCallHandler.Error -> {
                _isLoading.emit(false);

                val errorMessage = (result.data.toString())
                if (errorMessage.contains(General.BASED_URL)) {
                    errorMessage.replace(General.BASED_URL, " Server ")
                }
                errorMessage.removeTheSingle()
            }
        }
    }


    fun logout() {
        viewModelScope.launch(Dispatchers.IO + _coroutinExption) {
            authDao.nukeTable()
        }
    }
}