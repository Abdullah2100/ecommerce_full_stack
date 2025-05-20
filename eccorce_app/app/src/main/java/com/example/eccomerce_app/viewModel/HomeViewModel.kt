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
import com.example.eccomerce_app.dto.ModelToDto.toSubCategoryUpdateDto
import com.example.eccomerce_app.dto.request.LocationRequestDto
import com.example.eccomerce_app.dto.request.SubCategoryRequestDto
import com.example.eccomerce_app.dto.response.AddressResponseDto
import com.example.eccomerce_app.dto.response.BannerResponseDto
import com.example.eccomerce_app.dto.response.CategoryReponseDto
import com.example.eccomerce_app.dto.response.StoreResposeDto
import com.example.eccomerce_app.dto.response.SubCategoryResponseDto
import com.example.eccomerce_app.dto.response.UserDto
import com.example.eccomerce_app.model.Address
import com.example.eccomerce_app.model.Banner
import com.example.eccomerce_app.model.Category
import com.example.eccomerce_app.model.DtoToModel.toAddress
import com.example.eccomerce_app.model.DtoToModel.toBanner
import com.example.eccomerce_app.model.DtoToModel.toCategory
import com.example.eccomerce_app.model.DtoToModel.toStore
import com.example.eccomerce_app.model.DtoToModel.toSubCategory
import com.example.eccomerce_app.model.DtoToModel.toUser
import com.example.eccomerce_app.model.MyInfoUpdate
import com.example.eccomerce_app.model.Store
import com.example.eccomerce_app.model.SubCategory
import com.example.eccomerce_app.model.SubCategoryUpdate
import com.example.eccomerce_app.model.User
import com.example.eccomerce_app.ui.Screens
import com.example.hotel_mobile.Modle.NetworkCallHandler
import com.microsoft.signalr.HubConnection
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.util.UUID


class HomeViewModel(
    val homeRepository: HomeRepository,
    val dao: AuthDao,
    var webSocket: HubConnection?
) : ViewModel() {


    private val _hub = MutableStateFlow<HubConnection?>(null)

    private val _isLoading = MutableStateFlow<Boolean>(false)
    val isLoading = _isLoading.asStateFlow()

    private var _categories = MutableStateFlow<MutableList<Category>?>(null)
    var categories = _categories.asStateFlow()


    private var _myInfo = MutableStateFlow<User?>(null)
    var myInfo = _myInfo.asStateFlow()


    private var _banners = MutableStateFlow<List<Banner>?>(null);
    var banners = _banners.asStateFlow();

    //this for home only to prevent the size when getting new banner for defferent store
    private var _homeBanners = MutableStateFlow<List<Banner>?>(null);
    var homeBanners = _homeBanners.asStateFlow();

    private var _stores = MutableStateFlow<List<Store>?>(null);
    var stores = _stores.asStateFlow();

    private var _storeAddress = MutableStateFlow<List<Address>?>(null);
    var storeAddress = _storeAddress.asStateFlow();

    private var _SubCategories = MutableStateFlow<List<SubCategory>?>(null);
    var subCategories = _SubCategories.asStateFlow();


    private var _coroutinExption = CoroutineExceptionHandler { _, message ->
        Log.d("ErrorMessageIs", message.message.toString())
    }


    init {
        if (webSocket != null) {
            connection()
        }
    }

    override fun onCleared() {
        viewModelScope.launch(Dispatchers.IO) {
            if (_hub.value != null)
                _hub.value!!.stop()
        }
        super.onCleared()
    }

    fun connection() {

        if (webSocket != null)
            viewModelScope.launch(Dispatchers.Main + _coroutinExption) {
                _hub.emit(webSocket)
                _hub.value!!.start().blockingAwait()
                _hub.value!!.send("streamBanners") // Start streaming
                _hub.value!!.on(
                    "createdBanner",
                    { result ->
                        var banners = mutableListOf<Banner>();
                        if (_banners.value == null) {
                            banners.add(result.toBanner())
                        } else {
                            banners.add(result.toBanner())
                            banners.addAll(_banners.value!!)
                        }
                        viewModelScope.launch(Dispatchers.IO) {
                            Log.d("bannerCreationData", banners.toString())

                            _banners.emit(banners)
                        }
                    },
                    BannerResponseDto::class.java
                )

                _hub.value!!.on(
                    "Banners",
                    { resultHolder ->

                        var result = resultHolder


                        var copyBanner = result.map { it.toBanner() }.toList();

                        viewModelScope.launch(Dispatchers.IO) {

                            Log.d("bannerCreationData", banners.toString())
                            _homeBanners.emit(copyBanner)
                        }
                    },
                    Array<BannerResponseDto>::class.java
                )
            }

    }


    fun getCategories(pageNumber: Int = 1) {
        if (pageNumber == 1 && _categories.value != null) return;
        viewModelScope.launch(Dispatchers.IO) {
            var result = homeRepository.getCategory(pageNumber)
            when (result) {
                is NetworkCallHandler.Successful<*> -> {
                    dao.savePassingLocation(IsPassSetLocationScreen(0, true))
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
    ): String? {


        _isLoading.emit(true)
        delay(100)
        var result = homeRepository
            .userAddNewAddress(
                LocationRequestDto(
                    longitude = longit ?: 5.5,
                    latitude = latit ?: 5.5,
                    title = title ?: "home"
                )
            )
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
                return result.data.toString().replace("\"", "")
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
                    Log.d("errorFromNetowrk", resultError)
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
    ): String? {
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
                var storesHolder = mutableListOf<Store>()
                if (_stores.value != null) {
                    storesHolder.add(data.toStore())
                    storesHolder.addAll(_stores.value!!.toList())
                } else {
                    storesHolder.add(data.toStore())
                }
                var distinticStore = storesHolder.distinctBy { it.id }.toMutableList()
                _stores.emit(distinticStore)
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
    ): String? {
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

                var storesHolder = mutableListOf<Store>()
                if (_stores.value != null) {
                    storesHolder.add(data.toStore())
                    storesHolder.addAll(_stores.value!!.toList())
                } else {
                    storesHolder.add(data.toStore())
                }
                var distinticStore = storesHolder.distinctBy { it.id }.toMutableList()
                _stores.emit(distinticStore)

                return null;
            }

            is NetworkCallHandler.Error -> {
                _isLoading.emit(false)

                var errorMessage = (result.data.toString())
                if (errorMessage.contains(General.BASED_URL)) {
                    errorMessage.replace(General.BASED_URL, " Server ")
                }
                return errorMessage.replace("\"", "")
            }

            else -> {
                return null;
            }
        }
    }

    suspend fun createSubCategory(
        name: String,
        categoryId: UUID
    ): String? {
        _isLoading.emit(true)

        var result = homeRepository.createSubCategory(
            SubCategoryRequestDto(
                name = name,
                cateogy_id = categoryId
            )
        );
        when (result) {
            is NetworkCallHandler.Successful<*> -> {
                var data = result.data as SubCategoryResponseDto
                var listSubCategory = mutableListOf<SubCategory>()

                listSubCategory.add(data.toSubCategory())
                if (_SubCategories.value != null) {
                    listSubCategory.addAll(_SubCategories.value!!);
                }

                _SubCategories.emit(listSubCategory)

                return null;
            }

            is NetworkCallHandler.Error -> {
                _isLoading.emit(false)

                var errorMessage = (result.data.toString())
                if (errorMessage.contains(General.BASED_URL)) {
                    errorMessage.replace(General.BASED_URL, " Server ")
                }
                return errorMessage.replace("\"", "")
            }

            else -> {
                return null;
            }
        }
    }

    suspend fun updateSubCategory(
        data: SubCategoryUpdate
    ): String? {
        _isLoading.emit(true)

        var result = homeRepository.updateSubCategory(
            data.toSubCategoryUpdateDto()
        );
        when (result) {
            is NetworkCallHandler.Successful<*> -> {
                var data = result.data as SubCategoryResponseDto
                var listSubCategory = mutableListOf<SubCategory>()

                if (_SubCategories.value != null) {
                    listSubCategory.add(data.toSubCategory())
                    listSubCategory.addAll(_SubCategories.value!!);
                } else {
                    listSubCategory.add(data.toSubCategory())
                }
                var distincetSubCategory = listSubCategory.distinctBy { it.id }.toList()

                _SubCategories.emit(distincetSubCategory)
                return null;
            }

            is NetworkCallHandler.Error -> {
                _isLoading.emit(false)

                var errorMessage = (result.data.toString())
                if (errorMessage.contains(General.BASED_URL)) {
                    errorMessage.replace(General.BASED_URL, " Server ")
                }
                return errorMessage.replace("\"", "")
            }

            else -> {
                return null;
            }
        }
    }

    fun getStoreInfoByStoreId(store_id: UUID) {
        getStoreData(store_id)
        getStoreBanner(store_id)
        getStoreAddress(store_id, 1)
        getStoreSubCategories(store_id, 1)
    }

    fun getStoreData(store_id: UUID) {
        viewModelScope.launch(Dispatchers.IO + _coroutinExption) {
            var result = homeRepository.getStoreById(store_id);
            when (result) {
                is NetworkCallHandler.Successful<*> -> {
                    var data = result.data as StoreResposeDto

                    var storesHolder = mutableListOf<Store>()
                    storesHolder.add(data.toStore())

                    if (_stores.value != null) {
                        storesHolder.addAll(_stores.value!!.toList())
                    }

                    var distinticStore = storesHolder.distinctBy { it.id }.toMutableList()
                    _stores.emit(distinticStore)

                }

                is NetworkCallHandler.Error -> {
                    _isLoading.emit(false)

                    var errorMessage = (result.data.toString())
                    if (errorMessage.contains(General.BASED_URL)) {
                        errorMessage.replace(General.BASED_URL, " Server ")
                    }
                    Log.d("errorFromGettingStoreData", errorMessage)
                }
            }
        }
    }

    private fun getStoreBanner(store_id: UUID, pageNumber: Int = 1) {
        viewModelScope.launch(Dispatchers.Main + _coroutinExption) {
            var result = homeRepository.getBannerByStoreId(store_id, pageNumber);
            when (result) {
                is NetworkCallHandler.Successful<*> -> {
                    var data = result.data as List<BannerResponseDto>

                    var bannersHolder = mutableListOf<Banner>()
                    var bannersResponse = data.map { it.toBanner() }.toList()

                    bannersHolder.addAll(bannersResponse)
                    if (_banners.value != null) {
                        bannersHolder.addAll(_banners.value!!)
                    }

                    var distinticBanner = bannersHolder.distinctBy { it.id }.toMutableList()
                    if (distinticBanner.size > 0)
                        _banners.emit(distinticBanner)
                    else
                        _banners.emit(emptyList<Banner>())

                }

                is NetworkCallHandler.Error -> {
                    _banners.emit(emptyList<Banner>())

                    _isLoading.emit(false)

                    var errorMessage = (result.data.toString())
                    if (errorMessage.contains(General.BASED_URL)) {
                        errorMessage.replace(General.BASED_URL, " Server ")
                    }
                    Log.d("errorFromGettingStoreData", errorMessage)
                }
            }
        }

    }

    suspend fun createBanner(
        end_date: String,
        image: File,

    ): String? {
        _isLoading.emit(true)

        var result = homeRepository.createBanner(
            end_date,
            image
        );
        when (result) {
            is NetworkCallHandler.Successful<*> -> {
                var data = result.data as BannerResponseDto

                var bannersHolder = mutableListOf<Banner>()
                var bannersResponse =data.toBanner()

                bannersHolder.add(bannersResponse)
                if (_banners.value != null) {
                    bannersHolder.addAll(_banners.value!!)
                }

                var distinticBanner = bannersHolder.distinctBy { it.id }.toMutableList()
                if (distinticBanner.size > 0)
                    _banners.emit(distinticBanner)
                else
                    _banners.emit(emptyList<Banner>())
                return null;
            }

            is NetworkCallHandler.Error -> {
                _isLoading.emit(false)

                var errorMessage = (result.data.toString().replace("",""))
                if (errorMessage.contains(General.BASED_URL.substring(8,20))) {
                    errorMessage.replace(General.BASED_URL, " Server ")
                }
                return errorMessage.replace("\"","")
            }

            else -> {
                return null;
            }
        }
    }


    private fun getStoreAddress(store_id: UUID, pageNumber: Int = 1) {
        viewModelScope.launch(Dispatchers.Main + _coroutinExption) {
            var result = homeRepository.getStoreAddress(store_id, pageNumber);
            when (result) {
                is NetworkCallHandler.Successful<*> -> {
                    var data = result.data as List<AddressResponseDto>

                    var addressHolder = mutableListOf<Address>()
                    var addressResponse = data.map { it.toAddress() }.toList()

                    addressHolder.addAll(addressResponse)
                    if (_storeAddress.value != null) {
                        addressHolder.addAll(_storeAddress.value!!)
                    }

                    var distinticBanner = addressHolder.distinctBy { it.id }.toMutableList()

                    if (distinticBanner.size > 0)
                        _storeAddress.emit(distinticBanner)
                    else
                        _storeAddress.emit(emptyList())

                }

                is NetworkCallHandler.Error -> {
                    _isLoading.emit(false)
                    _storeAddress.emit(emptyList())

                    var errorMessage = (result.data.toString())
                    if (errorMessage.contains(General.BASED_URL)) {
                        errorMessage.replace(General.BASED_URL, " Server ")
                    }
                    Log.d("errorFromGettingStoreData", errorMessage)
                }
            }
        }

    }

    private fun getStoreSubCategories(store_id: UUID, pageNumber: Int = 1) {
        viewModelScope.launch(Dispatchers.Main + _coroutinExption) {
            var result = homeRepository.getStoreSubCategory(store_id, pageNumber);
            when (result) {
                is NetworkCallHandler.Successful<*> -> {
                    var data = result.data as List<SubCategoryResponseDto>

                    var subCategoriesolder = mutableListOf<SubCategory>()
                    var addressResponse = data.map { it.toSubCategory() }.toList()

                    subCategoriesolder.addAll(addressResponse)
                    if (_SubCategories.value != null) {
                        subCategoriesolder.addAll(_SubCategories.value!!)
                    }

                    var distinticSubCategories = subCategoriesolder.distinctBy { it.id }.toMutableList()

                    if(distinticSubCategories.size>0)
                    _SubCategories.emit(distinticSubCategories)
                    else
                        _SubCategories.emit(emptyList())

                }

                is NetworkCallHandler.Error -> {
                    _isLoading.emit(false)
                    _SubCategories.emit(emptyList())

                    var errorMessage = (result.data.toString())
                    if (errorMessage.contains(General.BASED_URL)) {
                        errorMessage.replace(General.BASED_URL, " Server ")
                    }
                    Log.d("errorFromGettingStoreData", errorMessage)
                }
            }
        }

    }

}
