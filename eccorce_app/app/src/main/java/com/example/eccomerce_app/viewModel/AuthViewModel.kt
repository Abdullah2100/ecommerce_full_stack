package com.example.eccomerce_app.viewModel

import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.example.eccomerce_app.Dto.AuthResultDto
import com.example.eccomerce_app.Util.General
import com.example.eccomerce_app.data.Room.AuthDao
import com.example.eccomerce_app.data.Room.AuthModleEntity
import com.example.eccomerce_app.data.Room.IsPassOnBoardingScreen
import com.example.eccomerce_app.dto.request.LoginDto
import com.example.eccomerce_app.data.repository.AuthRepository
import com.example.eccomerce_app.dto.request.SignupDto
import com.example.eccomerce_app.dto.response.ErrorResponse
import com.example.eccomerce_app.ui.Screens
import com.example.hotel_mobile.Modle.NetworkCallHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
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


    fun signUpUser(
        email: String,
        name: String,
        phone: String,
        password: String,
        snackBark: SnackbarHostState,
        nav: NavHostController

        ) {
        viewModelScope.launch {
            _isLoadin.emit(true);

            val result = authRepository.signup(
                SignupDto(
                    name = name,
                    password = password,
                    phone = phone,
                    email = email
                )
            )
            when (result) {
                is NetworkCallHandler.Successful<*> -> {
                    val authData = result.data as AuthResultDto;
                    dao.saveAuthData(
                        AuthModleEntity(
                            id = 0,
                            token = authData.accessToken,
                            refreshToken = authData.refreshToken
                        )
                    )
                    nav.navigate(Screens.LocationGraph){
                        popUpTo(nav.graph.id){
                            inclusive=true
                        }
                    }
                }
                is NetworkCallHandler.Error->{
                    var errorMessage = (result.data.toString())
                    if (errorMessage.contains(General.BASED_URL)) {
                        errorMessage.replace(General.BASED_URL, " Server ")
                    }
                    snackBark.showSnackbar(errorMessage.replace("\"",""))
                }
                else -> {
                    var errorMessage = (result.toString())
                    if (errorMessage.contains(General.BASED_URL)) {
                        errorMessage.replace(General.BASED_URL, " Server ")
                    }
                    snackBark.showSnackbar(errorMessage.replace("\"",""))
                }
            }
            _isLoadin.emit(false)

        }
    }

    fun loginUser(
        username: String,
        password: String,
        snackBark: SnackbarHostState,
        nav: NavHostController,
        ) {
        viewModelScope.launch {
            _isLoadin.emit(true);

            val result = authRepository.login(LoginDto(username = username, password = password))
            when (result) {
                is NetworkCallHandler.Successful<*> -> {
                    val authData = result.data as AuthResultDto;
                    dao.saveAuthData(
                        AuthModleEntity(
                            id = 0,
                            token = authData.accessToken,
                            refreshToken = authData.refreshToken
                        )
                    )
                    nav.navigate(Screens.LocationGraph){
                        popUpTo(nav.graph.id){
                            inclusive=true
                        }
                    }
                }
            is NetworkCallHandler.Error->{
                var errorMessage = (result.data.toString())
                if (errorMessage.contains(General.BASED_URL)) {
                    errorMessage.replace(General.BASED_URL, " Server ")
                }
                snackBark.showSnackbar(errorMessage.replace("\"",""))
            }
                else -> {
                    var errorMessage = result.toString()
                    if (errorMessage.toString().contains(General.BASED_URL)) {
                        errorMessage.toString().replace(General.BASED_URL, " Server ")
                    }
                    snackBark.showSnackbar(errorMessage.replace("\"",""))
                }
            }
            _isLoadin.emit(false)

        }
    }

}