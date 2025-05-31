package com.example.e_commercompose.viewModel

import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.example.e_commercompose.Dto.AuthResultDto
import com.example.e_commercompose.Util.General
import com.example.e_commercompose.data.Room.AuthDao
import com.example.e_commercompose.data.Room.AuthModleEntity
import com.example.e_commercompose.data.Room.IsPassOnBoardingScreen
import com.example.e_commercompose.dto.request.LoginDto
import com.example.e_commercompose.data.repository.AuthRepository
import com.example.e_commercompose.dto.request.SignupDto
import com.example.e_commercompose.ui.Screens
import com.example.hotel_mobile.Modle.NetworkCallHandler
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.serialization.json.Json


class AuthViewModel(val authRepository: AuthRepository, val dao: AuthDao) : ViewModel() {
    private val _isLoadin = MutableStateFlow<Boolean>(false)
    val isLoadin = _isLoadin.asStateFlow()



    private  val _cuurentScreen = MutableStateFlow<Int?>(null);
    val currentScreen = _cuurentScreen.asStateFlow();

    init {
        getStartedScreen()
    }

    fun getStartedScreen() {
        viewModelScope.launch(Dispatchers.IO) {
            val authData = dao.getAuthData()
            val isPassOnBoard = dao.isPassOnBoarding()
            val isLocation = dao.isPassLocationScreen();
            General.authData.emit(authData)
          when(isPassOnBoard){
              false->{
                  _cuurentScreen.emit(1)
              }
              else->{
                  when(authData==null){
                      true->{
                          _cuurentScreen.emit(2)
                      }
                      else->{
                          when(isLocation==false){
                              true->{
                                  _cuurentScreen.emit(3)
                              }
                              else->{
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


suspend    fun signUpUser(
        email: String,
        name: String,
        phone: String,
        password: String,

        ): String? {

             val token = FirebaseMessaging.getInstance().token.await()?:""
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
                    val authData = result.data as AuthResultDto;
                    var authDataHolder =   AuthModleEntity(
                        id = 0,
                        token = authData.accessToken,
                        refreshToken = authData.refreshToken
                    )
                    dao.saveAuthData(
                        authDataHolder
                    );

                    General.authData.emit(authDataHolder)
return null;
                }
                is NetworkCallHandler.Error->{
                    _isLoadin.emit(false)

                    var errorMessage = (result.data.toString())
                    if (errorMessage.contains(General.BASED_URL)) {
                        errorMessage.replace(General.BASED_URL, " Server ")
                    }
                    return errorMessage
            }

        }
    }

suspend    fun loginUser(
        username: String,
        password: String,
        ): String? {
            _isLoadin.emit(true);
    val token = FirebaseMessaging.getInstance().token.await()?:""

    val result = authRepository.login(LoginDto(
        username = username,
        password = password,
        deviceToken = token))
            when (result) {
                is NetworkCallHandler.Successful<*> -> {
                    val authData = result.data as AuthResultDto;
                    var authDataHolder =   AuthModleEntity(
                        id = 0,
                        token = authData.accessToken,
                        refreshToken = authData.refreshToken
                    )
                    dao.saveAuthData(
                        authDataHolder
                    )
                    General.authData.emit(authDataHolder)

                    return  null;
                }
            is NetworkCallHandler.Error->{

                var errorMessage = (result.data.toString())
                if (errorMessage.contains(General.BASED_URL)) {
                    errorMessage.replace(General.BASED_URL, " Server ")
                }
                return errorMessage
            }

        }
    }

}