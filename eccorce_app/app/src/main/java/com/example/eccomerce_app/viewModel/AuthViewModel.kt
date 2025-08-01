package com.example.e_commercompose.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.example.e_commercompose.Util.General
import com.example.e_commercompose.data.Room.AuthDao
import com.example.e_commercompose.data.Room.AuthModleEntity
import com.example.e_commercompose.data.Room.IsPassOnBoardingScreen
import com.example.eccomerce_app.dto.LoginDto
import com.example.e_commercompose.data.repository.AuthRepository
import com.example.e_commercompose.ui.Screens
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
    private val _isLoading = MutableStateFlow<Boolean>(false)
    val isLoading = _isLoading.asStateFlow()

    val errorMesssage = MutableStateFlow<String?>(null)


    private val _cuurentScreen = MutableStateFlow<Int?>(null);
    val currentScreen = _cuurentScreen.asStateFlow();

    suspend fun clearErrorMessage(){
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
        viewModelScope.launch(Dispatchers.IO) {
            val authData = dao.getAuthData()
            val isPassOnBoard = dao.isPassOnBoarding()
            val isLocation = dao.isPassLocationScreen();
            General.authData.emit(authData)
            when (isPassOnBoard) {
                false -> {
                    _cuurentScreen.emit(1)
                }

                else -> {
                    when (authData == null) {
                        true -> {
                            _cuurentScreen.emit(2)
                        }

                        else -> {
                            when (isLocation == false) {
                                true -> {
                                    _cuurentScreen.emit(3)
                                }

                                else -> {
                                    _cuurentScreen.emit(4)
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
       viewModelScope.launch (Dispatchers.IO+_coroutinExption){
           _isLoading.emit(true)
           val token = FirebaseMessaging.getInstance().token.await() ?: ""
           val result = authRepository.signup(
               SignupDto(
                   name = name,
                   password = password,
                   phone = phone,
                   email = email,
                   deviceToken = token
               )
           )
           when (result) {
               is NetworkCallHandler.Successful<*> -> {
                   _isLoading.emit(false)
                   val authData = result.data as AuthResultDto;
                   var authDataHolder = AuthModleEntity(
                       id = 0,
                       token = authData.accessToken,
                       refreshToken = authData.refreshToken
                   )
                   dao.saveAuthData(
                       authDataHolder
                   );

                   General.authData.emit(authDataHolder)
                   nav.navigate(Screens.LocationGraph) {
                       popUpTo(nav.graph.id) {
                           inclusive = true
                       }
                   }
               }

               is NetworkCallHandler.Error -> {
                   _isLoading.emit(false)

                   var errorMessage = (result.data.toString())
                   if (errorMessage.contains(General.BASED_URL)) {
                       errorMessage.replace(General.BASED_URL, " Server ")
                   }
                   errorMesssage.emit(errorMessage)
               }

           }

       }
      }

     fun loginUser(
         username: String,
         password: String,
         nav: NavHostController,
    ) {
        viewModelScope.launch(Dispatchers.IO+_coroutinExption){
            _isLoading.emit(true);
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
                    nav.navigate(Screens.LocationGraph) {
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


    suspend fun getOtp(
        email: String,
    ): String? {
        _isLoading.emit(true);

        val result = authRepository.getOtp(email)
        when (result) {
            is NetworkCallHandler.Successful<*> -> {
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

    suspend fun otpVerifing(
        email: String,
        otp: String
    ): String? {
        _isLoading.emit(true);

        val result = authRepository.verifyingOtp(email,otp)
        when (result) {
            is NetworkCallHandler.Successful<*> -> {
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


    suspend fun reseatPassword(
        email:String,
        otp:String,
        newPassword: String

    ): String? {
        val token = FirebaseMessaging.getInstance().token.await() ?: ""

        val result = authRepository.resetPassword(email,otp,newPassword)
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