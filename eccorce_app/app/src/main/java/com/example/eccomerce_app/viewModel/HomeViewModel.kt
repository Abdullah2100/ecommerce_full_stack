package com.example.eccomerce_app.viewModel

import android.util.Log
import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.example.eccomerce_app.Util.General
import com.example.eccomerce_app.data.Room.AuthDao
import com.example.eccomerce_app.data.Room.IsPassSetLocationScreen
import com.example.eccomerce_app.data.repository.HomeRepository
import com.example.eccomerce_app.dto.ModelToDto.toLocationRequestDto
import com.example.eccomerce_app.dto.request.LocationRequestDto
import com.example.eccomerce_app.dto.request.SubCategoryRequestDto
import com.example.eccomerce_app.dto.response.AddressResponseDto
import com.example.eccomerce_app.dto.response.CategoryReponseDto
import com.example.eccomerce_app.dto.response.StoreResposeDto
import com.example.eccomerce_app.dto.response.SubCategoryResponseDto
import com.example.eccomerce_app.dto.response.UserDto
import com.example.eccomerce_app.model.Address
import com.example.eccomerce_app.model.Category
import com.example.eccomerce_app.model.DtoToModel.toAddress
import com.example.eccomerce_app.model.DtoToModel.toCategory
import com.example.eccomerce_app.model.DtoToModel.toStore
import com.example.eccomerce_app.model.DtoToModel.toSubCategory
import com.example.eccomerce_app.model.DtoToModel.toUser
import com.example.eccomerce_app.model.MyInfoUpdate
import com.example.eccomerce_app.model.Store
import com.example.eccomerce_app.model.SubCategory
import com.example.eccomerce_app.model.User
import com.example.eccomerce_app.ui.Screens
import com.example.hotel_mobile.Modle.NetworkCallHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.util.UUID


class HomeViewModel(val homeRepository: HomeRepository, val dao: AuthDao) : ViewModel() {


    private val _isLoading = MutableStateFlow<Boolean>(false)
    val isLoading = _isLoading.asStateFlow()

    private var _categories = MutableStateFlow<MutableList<Category>?>(null)
    var categories = _categories.asStateFlow()



    private var _myInfo = MutableStateFlow<User?>(null)
    var myInfo = _myInfo.asStateFlow()


//    private var _myStore = MutableStateFlow<Store?>(null)
//    var myStore = _myStore.asStateFlow()
//

    private var _myInfoUpdate = MutableStateFlow<MyInfoUpdate>(MyInfoUpdate())
    var myInfoUpdate = _myInfoUpdate.asStateFlow()


 /*   fun updateAddressObj(longit: Double? = null, latit: Double? = null, title: String? = null) {
        var locationCopy: Address? = null;
        if (longit != null) {
            locationCopy = _location.value.copy(longitude = longit);

        }

        if (latit != null) {
            locationCopy = _location.value.copy(latitude = latit);
        }

        if (title != null) {
            locationCopy = _location.value.copy(title = title);
        }

        if (locationCopy != null) {
            viewModelScope.launch {
                _location.emit(locationCopy)
            }
        }

    }
fun getUserLocations() {
        if (_locations.value != null) return;
        viewModelScope.launch(Dispatchers.Main) {
            var result = homeRepository.getUserAddress()
            when (result) {
                is NetworkCallHandler.Successful<*> -> {
                    var address = result.data as List<AddressResponseDto>
                    if (address.size > 0) {
                        _locations.emit(
                            address.map { it.toAddress() }.toList()
                        )
                    } else {
                        if (_locations.value.isNullOrEmpty())
                            _locations.emit(emptyList())
                    }
                }

                else -> {
                    if (_locations.value.isNullOrEmpty())
                        _locations.emit(emptyList())
                }
            }
        }
    }
*/

    fun getCategory(pageNumber: Int = 1) {
        if (pageNumber == 1 && _categories.value != null) return;
        viewModelScope.launch(Dispatchers.IO) {
            var result = homeRepository.getCategory(pageNumber)
            when (result) {
                is NetworkCallHandler.Successful<*> -> {
                    dao.savePassingLocation(IsPassSetLocationScreen(0,true))
                    var categoriesHolder = result.data as List<CategoryReponseDto>

                    var mutableCategories = mutableListOf<Category>()

                    if (pageNumber != 1 && _categories.value != null) {
                        mutableCategories.addAll(_categories.value!!.toList())
                    }
                    if (categoriesHolder.isNotEmpty())
                        mutableCategories.addAll(
                            categoriesHolder.map { it.toCategory() }.toList()
                        )

                    if (mutableCategories.isNotEmpty()) {
                        _categories.emit(
                            mutableCategories
                        )
                    } else {
                        if (_categories.value == null)
                            _categories.emit(mutableListOf())
                    }
                }

                else -> {}
            }
        }
    }


   suspend fun addUserAddress(
       longit: Double? = null,
       latit: Double? = null,
       title: String? = null,
       ):String? {


            _isLoading.emit(true)
       delay(100)
            var result = homeRepository
                .userAddNewAddress(LocationRequestDto(longitude = longit?:5.5,
                    latitude = latit?:5.5,
                    title = title?:"home"))
            when (result) {
                is NetworkCallHandler.Successful<*> -> {
                    var address = result.data as AddressResponseDto?
                    if (address != null) {
                        var locationsCopy = mutableListOf<Address>()
                        locationsCopy.add(address.toAddress())
                        if (myInfo.value?.address != null) {
                            locationsCopy.addAll(myInfo.value!!.address!!)
                        }
                        var copyMyInfo = _myInfo.value?.copy(address = locationsCopy.toList())
                        _myInfo.emit(copyMyInfo)
                    } else {

                    }
                    _isLoading.emit(false)

                    return null;
                }
                is NetworkCallHandler.Error -> {
                    _isLoading.emit(false)
                    return  result.data.toString().replace("\"","")
                }
            }
       _isLoading.emit(false)


   }

    fun setCurrentActiveUserAddress(
        addressId: UUID,
        snackBark: SnackbarHostState,
        nav: NavHostController,
        isFromLocationHome: Boolean? = false,
    ) {
        viewModelScope.launch(Dispatchers.Main) {
            _isLoading.emit(true)
            delay(6000)
            var result = homeRepository.setAddressAsCurrent(addressId)
            when (result) {
                is NetworkCallHandler.Successful<*> -> {
                    _isLoading.emit(false)

                    snackBark.showSnackbar("the address set as current active address")
                    when (isFromLocationHome) {
                        true -> {
                            dao.savePassingLocation(
                                IsPassSetLocationScreen(
                                    0,
                                    true
                                )
                            )
                            nav.navigate(Screens.HomeGraph) {
                                popUpTo(nav.graph.id) {
                                    inclusive = true
                                }
                            }
                        }

                        else -> {
                            nav.popBackStack()
                        }
                    }
                }

                else -> {
                    _isLoading.emit(false)
                }
            }
            _isLoading.emit(false)
        }
    }


    fun getMyInfo() {
        if (_myInfo.value != null) return;
        viewModelScope.launch(Dispatchers.Main) {
            var result = homeRepository.getMyInfo();
            when (result) {
                is NetworkCallHandler.Successful<*> -> {
                    var data = result.data as UserDto
                    _myInfo.emit(data.toUser())
                }
                is NetworkCallHandler.Error -> {
                    var resultError = result.data as String
                    Log.d("errorFromNetowrk",resultError)
                }

                else -> {

                }
            }
        }
    }


    suspend fun updateMyInfo(
        userData: MyInfoUpdate

    ): String? {
        var result = homeRepository.UpdateMyInfo(
            userData
        );
        when (result) {
            is NetworkCallHandler.Successful<*> -> {
                var data = result.data as UserDto
                _myInfo.emit(data.toUser())
                return null;
            }

            is NetworkCallHandler.Error -> {

                var errorMessage = (result.data.toString())
                if (errorMessage.contains(General.BASED_URL)) {
                    errorMessage.replace(General.BASED_URL, " Server ")
                }
                return errorMessage.replace("\"", "").toString()

            }

        }
    }




    suspend fun createStore(
        name: String,
        wallpaper_image: File,
        small_image: File,
        longitude: Double,
        latitude: Double,
    ):String? {
        _isLoading.emit(true)

        var result = homeRepository.createStore(
            name,
            wallpaper_image,
            small_image,
            longitude,
            latitude
        );
        when (result) {
            is NetworkCallHandler.Successful<*> -> {
                var data = result.data as StoreResposeDto
                var copyMyInfo = _myInfo.value!!.copy(store = data.toStore())
                _myInfo.emit(copyMyInfo)
                return null;
            }

            is NetworkCallHandler.Error -> {
                _isLoading.emit(false)

                var errorMessage = (result.data.toString())
                if (errorMessage.contains(General.BASED_URL)) {
                    errorMessage.replace(General.BASED_URL, " Server ")
                }
                return errorMessage
            }

            else -> {
                return null;
            }
        }
    }

    suspend fun updateStore(
        name: String,
        wallpaper_image: File?,
        small_image: File?,
        longitude: Double,
        latitude: Double,
    ):String? {
        _isLoading.emit(true)

        var result = homeRepository.updateStore(
            name,
            wallpaper_image,
            small_image,
            longitude,
            latitude
        );
        when (result) {
            is NetworkCallHandler.Successful<*> -> {
                var data = result.data as StoreResposeDto
                var copyMyInfo = _myInfo.value!!.copy(store = data.toStore())
                _myInfo.emit(copyMyInfo)
                return null;
            }

            is NetworkCallHandler.Error -> {
                _isLoading.emit(false)

                var errorMessage = (result.data.toString())
                if (errorMessage.contains(General.BASED_URL)) {
                    errorMessage.replace(General.BASED_URL, " Server ")
                }
                return errorMessage.replace("\"","")
            }

            else -> {
                return null;
            }
        }
    }

    suspend fun createSubCategory(
        name: String,
        categoryId: UUID
    ):String? {
        _isLoading.emit(true)

        var result = homeRepository.createSubCategory(
            SubCategoryRequestDto(
               name= name,
                cateogy_id = categoryId
            )
        );
        when (result) {
            is NetworkCallHandler.Successful<*> -> {
                var data = result.data as SubCategoryResponseDto
                var listSubCategory = mutableListOf<SubCategory>()
                if(_myInfo.value?.store?.subcategory!=null) {
                    listSubCategory.addAll(_myInfo.value!!.store!!.subcategory!!)
                    listSubCategory.add(data.toSubCategory())
                }else{
                    listSubCategory.add(data.toSubCategory())
                }
                var storeCopy = _myInfo?.value?.store?.copy(subcategory = listSubCategory)
                var copyStore = _myInfo.value?.copy(store = storeCopy)
                _myInfo.emit(copyStore)
                return null;
            }

            is NetworkCallHandler.Error -> {
                _isLoading.emit(false)

                var errorMessage = (result.data.toString())
                if (errorMessage.contains(General.BASED_URL)) {
                    errorMessage.replace(General.BASED_URL, " Server ")
                }
                return errorMessage.replace("\"","")
            }

            else -> {
                return null;
            }
        }
    }



}