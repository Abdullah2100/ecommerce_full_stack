package com.example.eccomerce_app.viewModel

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.example.eccomerce_app.data.Room.AuthDao
import com.example.eccomerce_app.data.Room.IsPassSetLocationScreen
import com.example.eccomerce_app.data.repository.HomeRepository
import com.example.eccomerce_app.dto.ModelToDto.toLocationRequestDto
import com.example.eccomerce_app.dto.response.AddressResponseDto
import com.example.eccomerce_app.dto.response.CategoryReponseDto
import com.example.eccomerce_app.model.Address
import com.example.eccomerce_app.model.Category
import com.example.eccomerce_app.model.DtoToModel.toAddress
import com.example.eccomerce_app.model.DtoToModel.toCategory
import com.example.eccomerce_app.ui.Screens
import com.example.hotel_mobile.Modle.NetworkCallHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.concurrent.timerTask


class HomeViewModel(val homeRepository: HomeRepository, val dao: AuthDao) : ViewModel() {


    private val _isLoading = MutableStateFlow<Boolean>(false)
    val isLoading = _isLoading.asStateFlow()

    private var _categories = MutableStateFlow<MutableList<Category>?>(null)
    var categories = _categories.asStateFlow()


    private var _locations = MutableStateFlow<List<Address>?>(null)
    var locations = _locations.asStateFlow()


    private var _location = MutableStateFlow<Address>(Address(null, 0.0, 0.0, "", false))
    var location = _location.asStateFlow()


    private var _currentLocationId = MutableStateFlow<UUID?>(null)
    var savedCurrentLocationToLocal = _currentLocationId.asStateFlow()


    init {
        getSavedCurrentLocation()
    }

    fun updateLocationObj(longit: Double? = null, latit: Double? = null, title: String? = null) {
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
        viewModelScope.launch {
            var result = homeRepository.getUserLocations()
            when (result) {
                is NetworkCallHandler.Successful<*> -> {
                    var address = result.data as List<AddressResponseDto>
                    if (address.size > 0) {
                        _locations.emit(
                            address.map { it.toAddress() }.toList()
                        )
                    } else {
                        _locations.emit(emptyList())
                    }
                }

                else -> {}
            }
        }
    }

    fun getCategory(pageNumber: Int = 1) {
        if (pageNumber == 1 && _categories.value != null) return;
        viewModelScope.launch {
            var result = homeRepository.getCategory(pageNumber)
            when (result) {
                is NetworkCallHandler.Successful<*> -> {
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


    fun addUserLocation() {
        viewModelScope.launch {

            _isLoading.emit(true)
            var result = homeRepository.userAddNewLocation(_location.value.toLocationRequestDto())
            when (result) {
                is NetworkCallHandler.Successful<*> -> {
                    _location.emit(Address(null, 0.0, 0.0, "", false))
                    var address = result.data as AddressResponseDto?
                    if (address != null) {
                        var locationsCopy = mutableListOf<Address>()
                        locationsCopy.add(address.toAddress())
                        if (_locations.value != null) {
                            locationsCopy.addAll(_locations.value!!.toList())
                        }
                        _locations.emit(locationsCopy)
                    } else {
                        _locations.emit(emptyList())
                    }
                }

                else -> {}
            }
            _isLoading.emit(false)
        }
    }

    fun setCurrentActiveUserAddress(
        addressId: UUID,
        snackBark: SnackbarHostState,
        nav: NavHostController,
        isFromLocationHome: Boolean? = false,
    ) {
        viewModelScope.launch {

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
                                    addressId.toString(),
                                    true
                                )
                            )
                            _currentLocationId.emit(addressId)
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

    fun getSavedCurrentLocation() {
        viewModelScope.launch(Dispatchers.IO) {
            getUserLocations()
            var result = dao.getSavedLocation()
            if (result != null)
                _currentLocationId.emit(UUID.fromString(result.locationId))
        }
    }

}