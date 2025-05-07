package com.example.eccomerce_app.viewModel

import android.R
import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eccomerce_app.Dto.AuthResultDto
import com.example.eccomerce_app.Util.General
import com.example.eccomerce_app.data.Room.AuthDao
import com.example.eccomerce_app.data.Room.AuthModleEntity
import com.example.eccomerce_app.data.Room.IsPassOnBoardingScreen
import com.example.eccomerce_app.di.viewModelModel
import com.example.eccomerce_app.dto.request.LoginDto
import com.example.eccomerce_app.data.repository.AuthRepository
import com.example.hotel_mobile.Modle.NetworkCallHandler
import io.ktor.client.HttpClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class AuthViewModel(val authRepository: AuthRepository, val dao:AuthDao) : ViewModel() {
   private val _isPassingOnBoardinScreen = MutableStateFlow<Boolean?>(null)
    val isPassingOnBoardinScreen = _isPassingOnBoardinScreen.asStateFlow()

    private  val _isLoadin = MutableStateFlow<Boolean>(false)
    val isLoadin = _isLoadin.asStateFlow()

    init {
        getIfPassingOnBoardingScreen()
    }

    fun getIfPassingOnBoardingScreen(){
        viewModelScope.launch(Dispatchers.IO) {
         val result =   dao.isPassOnBoarding()
            _isPassingOnBoardinScreen.emit(result)
        }
    }

    fun setIsPassOnBoardingScreen(){
        viewModelScope.launch (Dispatchers.IO){
            dao.saveIsPassingOnBoarding(IsPassOnBoardingScreen(0,true))
        }
    }

    private suspend  fun validateLoginInput(
        username: String,
        password: String,
        snackbarHostState: SnackbarHostState
    ): Boolean{

        if(username.trim().isEmpty()){
            snackbarHostState.showSnackbar("username must not be empty")
            return false;
        }
        else if(password.trim().isEmpty()){
            snackbarHostState.showSnackbar("password must not be empty")
            return false;
        }

        return true
    }
    fun loginUser(
        username:String,
        password:String,
        snackBark: SnackbarHostState
    ){
        viewModelScope.launch {
            val isValideToRequest = validateLoginInput(username,password,snackBark);
            if(isValideToRequest){
                _isLoadin.emit(true);

               val result = authRepository.login(LoginDto(username = username,password=password))
                when(result){
                    is NetworkCallHandler.Successful<*> ->{
                        val authData = result.data as AuthResultDto;
                        dao.saveAuthData(
                            AuthModleEntity(
                                id = 0,
                                token = authData.accessToken,
                                refreshToken = authData.refreshToken
                            )
                        )
                    }
                    else ->{
                        var errorMessage = result.toString()
                        if(errorMessage.toString().contains(General.BASED_URL)){
                            errorMessage.toString().replace(General.BASED_URL," Server ")
                        }
                        snackBark.showSnackbar(errorMessage)
                    }
                }
                _isLoadin.emit(false)
            }

        }
    }
}