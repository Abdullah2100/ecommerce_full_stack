package com.example.e_commercompose.viewModel

import android.R
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.e_commercompose.Util.General
import com.example.e_commercompose.data.Room.AuthDao
import com.example.e_commercompose.data.Room.IsPassSetLocationScreen
import com.example.e_commercompose.data.repository.HomeRepository
import com.example.e_commercompose.dto.ModelToDto.toOrderRequestDto
import com.example.e_commercompose.dto.ModelToDto.toSubCategoryUpdateDto
import com.example.e_commercompose.dto.request.AddressRequestDto
import com.example.e_commercompose.dto.request.AddressRequestUpdateDto
import com.example.e_commercompose.dto.request.SubCategoryRequestDto
import com.example.e_commercompose.dto.response.AddressResponseDto
import com.example.e_commercompose.dto.response.BannerResponseDto
import com.example.e_commercompose.dto.response.CategoryReponseDto
import com.example.e_commercompose.dto.response.ProductResponseDto
import com.example.e_commercompose.dto.response.StoreResposeDto
import com.example.e_commercompose.dto.response.SubCategoryResponseDto
import com.example.e_commercompose.dto.response.UserDto
import com.example.e_commercompose.dto.response.VarientResponseDto
import com.example.e_commercompose.model.Address
import com.example.e_commercompose.model.BannerModel
import com.example.e_commercompose.model.CardProductModel
import com.example.e_commercompose.model.CartModel
import com.example.e_commercompose.model.Category
import com.example.e_commercompose.model.DtoToModel.toAddress
import com.example.e_commercompose.model.DtoToModel.toBanner
import com.example.e_commercompose.model.DtoToModel.toCategory
import com.example.e_commercompose.model.DtoToModel.toOrderItem
import com.example.e_commercompose.model.DtoToModel.toProdcut
import com.example.e_commercompose.model.DtoToModel.toStore
import com.example.e_commercompose.model.DtoToModel.toSubCategory
import com.example.e_commercompose.model.DtoToModel.toUser
import com.example.e_commercompose.model.DtoToModel.toVarient
import com.example.e_commercompose.model.MyInfoUpdate
import com.example.e_commercompose.model.ProductModel
import com.example.e_commercompose.model.ProductVarientSelection
import com.example.e_commercompose.model.StoreModel
import com.example.e_commercompose.model.SubCategory
import com.example.e_commercompose.model.SubCategoryUpdate
import com.example.e_commercompose.model.UserModel
import com.example.e_commercompose.model.VarientModel
import com.example.eccomerce_app.dto.response.OrderItemResponseDto
import com.example.eccomerce_app.dto.response.OrderResponseDto
import com.example.eccomerce_app.dto.response.StoreStatusResponseDto
import com.example.eccomerce_app.model.Order
import com.example.eccomerce_app.model.OrderItem
import com.example.hotel_mobile.Modle.NetworkCallHandler
import com.microsoft.signalr.HubConnection
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
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

    private var _varients = MutableStateFlow<MutableList<VarientModel>?>(null)
    var varients = _varients.asStateFlow()

    private var _myInfo = MutableStateFlow<UserModel?>(null)
    var myInfo = _myInfo.asStateFlow()


    private var _banners = MutableStateFlow<List<BannerModel>?>(null);
    var banners = _banners.asStateFlow();

    //this for home only to prevent the size when getting new banner for defferent store
    private var _homeBanners = MutableStateFlow<List<BannerModel>?>(null);
    var homeBanners = _homeBanners.asStateFlow();

    private var _stores = MutableStateFlow<List<StoreModel>?>(null);
    var stores = _stores.asStateFlow();

    private var _storeAddress = MutableStateFlow<List<Address>?>(null);
    var storeAddress = _storeAddress.asStateFlow();

    private var _SubCategories = MutableStateFlow<List<SubCategory>?>(null);
    var subCategories = _SubCategories.asStateFlow();

    private var _products = MutableStateFlow<List<ProductModel>?>(null);
    var products = _products.asStateFlow();

    private var _orderItemForMyStore = MutableStateFlow<List<OrderItem>?>(null);
    var orderItemForMyStore = _orderItemForMyStore.asStateFlow();
    private var _cartImes = MutableStateFlow<CartModel>(
        CartModel(
            0.0,
            0.0,
            0.0,
            UUID.randomUUID(),
            emptyList()
        )
    );
    var cartImes = _cartImes.asStateFlow();


    private var _orders = MutableStateFlow<List<Order>?>(null);
    var orders = _orders.asStateFlow();


    private var _coroutinExption = CoroutineExceptionHandler { _, message ->
        Log.d("ErrorMessageIs", message.message.toString())
    }


    fun addToCart(product: CardProductModel) {
        viewModelScope.launch {
            var cardProducts = mutableListOf<CardProductModel>()

            cardProducts.add(product)
            cardProducts.addAll(_cartImes.value.cartProducts)
            var productLength = cardProducts.size;
            var disntcProduct = cardProducts.distinctBy { it.productvarients }.toList()
            var distenctProductsLength = disntcProduct.size

            if (productLength == distenctProductsLength) {
                var varientPrice = product.price
                product.productvarients.forEach { it ->
                    varientPrice = varientPrice * it.precentage
                }
                var price = ((_cartImes.value.totalPrice ?: 0.0)) + varientPrice;
                var copyCardItme = cartImes.value.copy(
                    totalPrice = price, cartProducts = cardProducts
                )
                _cartImes.emit(copyCardItme)
            }
        }
    }


    fun increaseCardItem(productId: UUID) {
        viewModelScope.launch {
            var copyproduct: CardProductModel? = null;
            var productHolders = _cartImes.value.cartProducts.map { product ->
                if (product.id == productId) {
                    copyproduct = product.copy(quantity = product.quantity + 1)
                    copyproduct
                } else {
                    product
                }
            }
            var varientPrice = copyproduct!!.price


            copyproduct.productvarients.forEach { it ->
                varientPrice = varientPrice * it.precentage
            }
            var price = (_cartImes.value.totalPrice ?: 0.0) + (varientPrice);


            val copyCardItme =
                _cartImes.value.copy(cartProducts = productHolders, totalPrice = price)

            _cartImes.emit(copyCardItme)

        }
    }


    fun decreaseCardItem(productId: UUID) {
        viewModelScope.launch {
            var productList = _cartImes.value.cartProducts;
            var fristProduct = productList.first { it.id == productId }
            var varientPrice = fristProduct!!.price


            fristProduct.productvarients.forEach { it ->
                varientPrice = varientPrice * it.precentage
            }

            var totalPrice =
                (_cartImes.value.totalPrice ?: 0.0) - (varientPrice)

            when (fristProduct.quantity > 1) {
                true -> {
                    productList = _cartImes.value.cartProducts.map { product ->
                        if (product.id == productId) {
                            product.copy(quantity = product.quantity - 1)
                        } else {
                            product
                        }
                    }
                }

                else -> {
                    productList = _cartImes.value.cartProducts.filter { it.id != productId }
                }
            }

            val copyCardItme =
                _cartImes.value.copy(cartProducts = productList, totalPrice = totalPrice)

            _cartImes.emit(copyCardItme)

        }
    }

    fun removeItemFromCard(productId: UUID) {
        viewModelScope.launch {
            var productList = _cartImes.value.cartProducts;
            var fristProduct = productList.first { it.id == productId }
            var varientPrice = fristProduct!!.price


            fristProduct.productvarients.forEach { it ->
                varientPrice = varientPrice * it.precentage
            }

            var totalPrice =
                (_cartImes.value.totalPrice ?: 0.0) - (varientPrice)

            productList = _cartImes.value.cartProducts.filter { it.id != productId }

            val copyCardItme =
                _cartImes.value.copy(cartProducts = productList, totalPrice = totalPrice)

            _cartImes.emit(copyCardItme)

        }
    }


    init {
        initialFun()
    }

    fun initialFun() {
        getMyInfo()
        getCategories(1)
        getStoresBanner()
        getVarients(1)
        getProducts(mutableStateOf(1))
        getMyOrder(mutableStateOf(1))

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
                        var banners = mutableListOf<BannerModel>();
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
                    "storeStatus",
                    { result ->

                        viewModelScope.launch(Dispatchers.IO) {
                            if (result.status == true) {
                                val productNotBelongToStore =
                                    _products.value?.filter { it.store_id != result.storeId }
                                val storeWithoutCurrentId =
                                    _stores.value?.filter { it.id != result.storeId }
                                _products.emit(productNotBelongToStore)
                                _stores.emit(storeWithoutCurrentId)
                            }
                        }
                    },
                    StoreStatusResponseDto::class.java
                )

                _hub.value!!.on(
                    "orderExcptedByAdmin",
                    { response ->
                        val orderItemList = mutableListOf<OrderItem>();

                        response.order_items.forEach { value ->
                            if (value.product.store_id == myInfo.value?.store_id) {
                                orderItemList.add(value.toOrderItem())
                            }
                        }
                        if (_orderItemForMyStore.value != null) {
                            orderItemList.addAll(_orderItemForMyStore.value!!)
                        }
                        viewModelScope.launch(Dispatchers.IO) {
                            _orderItemForMyStore.emit(orderItemList.distinctBy { it.id }.toList())
                        }
                    },
                    OrderResponseDto::class.java
                )

            }

    }


    fun getCategories(pageNumber: Int = 1) {
        if (pageNumber == 1 && _categories.value != null) return;
        viewModelScope.launch(Dispatchers.IO) {
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

                is NetworkCallHandler.Error -> {
                    if (_categories.value == null)
                        _categories.emit(mutableListOf())
                }
            }
        }
    }


    fun getVarients(pageNumber: Int = 1) {
        if (pageNumber == 1 && _varients.value != null) return;
        viewModelScope.launch(Dispatchers.IO) {
            var result = homeRepository.getVarient(pageNumber)
            when (result) {
                is NetworkCallHandler.Successful<*> -> {
                    var varientolder = result.data as List<VarientResponseDto>

                    var mutableVarient = mutableListOf<VarientModel>()

                    if (pageNumber != 1 && _varients.value != null) {
                        mutableVarient.addAll(_varients.value!!.toList())
                    }
                    if (varientolder.isNotEmpty())
                        mutableVarient.addAll(
                            varientolder.map { it.toVarient() }.toList()
                        )

                    if (mutableVarient.isNotEmpty()) {
                        _varients.emit(
                            mutableVarient
                        )
                    } else {
                        if (_varients.value == null)
                            _varients.emit(mutableListOf())
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
                AddressRequestDto(
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

                    if (_myInfo.value?.address != null) {

                        val newAddress = _myInfo.value?.address?.map { it ->
                            if (it.isCurrnt == true) {
                                it.copy(isCurrnt = false)
                            } else it;
                        }
                        locationsCopy.addAll(newAddress ?: emptyList());
                    }
                    locationsCopy.add(address.toAddress())

                    var copyMyInfo = _myInfo.value?.copy(address = locationsCopy.toList())
                    _myInfo.emit(copyMyInfo)

                    dao.savePassingLocation(
                        IsPassSetLocationScreen(
                            0,
                            true
                        )
                    )
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

    suspend fun setCurrentActiveUserAddress(
        addressId: UUID,
    ): String? {
        _isLoading.emit(true)
        var result = homeRepository.setAddressAsCurrent(addressId)
        when (result) {
            is NetworkCallHandler.Successful<*> -> {
                _isLoading.emit(false)


                if (myInfo.value?.address.isNullOrEmpty() == false) {
                    var addresses = _myInfo.value?.address?.map { address ->
                        if (address.id == addressId) {
                            address.copy(isCurrnt = true);
                        } else {
                            address.copy(isCurrnt = false);
                        }
                    }
                    var copyMyAddress = _myInfo.value?.copy(address = addresses);
                    _myInfo.emit(copyMyAddress)
                }

                dao.savePassingLocation(
                    IsPassSetLocationScreen(
                        0,
                        true
                    )
                )

                return null

            }

            is NetworkCallHandler.Error -> {
                var errorMessage = result.data
                return errorMessage.toString()
            }


        }
    }

    suspend fun updateUserAddress(
        addressId: UUID,
        addressTitle: String?,
        longit: Double?,
        latit: Double?
    ): String? {
        _isLoading.emit(true)
        var result = homeRepository.userUpdateAddress(
            AddressRequestUpdateDto(
                id = addressId,
                title = addressTitle,
                latitude = latit,
                longitude = longit
            )
        )
        when (result) {
            is NetworkCallHandler.Successful<*> -> {
                var resultData = result.data as AddressResponseDto

                var address = _myInfo.value?.address?.map {
                    it
                    if (it.id == addressId) {
                        resultData.toAddress()
                    } else {
                        it
                    }
                }
                var copyMyAddress = _myInfo.value?.copy(address = address);
                _myInfo.emit(copyMyAddress)


                return null

            }

            is NetworkCallHandler.Error -> {
                var errorMessage = result.data
                return errorMessage.toString()
            }


        }
    }

    suspend fun deleteUserAddress(
        addressId: UUID,
    ): String? {
        _isLoading.emit(true)
        var result = homeRepository.deleteUserAddress(addressId)
        when (result) {
            is NetworkCallHandler.Successful<*> -> {

                var address = _myInfo.value?.address?.filter { it.id != addressId }

                var copyMyAddress = _myInfo.value?.copy(address = address);
                _myInfo.emit(copyMyAddress)

                return null

            }

            is NetworkCallHandler.Error -> {
                var errorMessage = result.data
                return errorMessage.toString()
            }


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

                    if (_orderItemForMyStore.value == null) {
                        getMyOrderItemBelongToMyStore(myInfo.value!!.store_id, mutableStateOf(1))

                    }
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
                var storesHolder = mutableListOf<StoreModel>()
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

                var storesHolder = mutableListOf<StoreModel>()
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

                    var storesHolder = mutableListOf<StoreModel>()
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

                    var bannersHolder = mutableListOf<BannerModel>()
                    var bannersResponse = data.map { it.toBanner() }.toList()

                    bannersHolder.addAll(bannersResponse)
                    if (_banners.value != null) {
                        bannersHolder.addAll(_banners.value!!)
                    }

                    var distinticBanner = bannersHolder.distinctBy { it.id }.toMutableList()
                    if (distinticBanner.size > 0)
                        _banners.emit(distinticBanner)
                    else
                        _banners.emit(emptyList<BannerModel>())

                }

                is NetworkCallHandler.Error -> {
                    _banners.emit(emptyList<BannerModel>())

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

    private fun getStoresBanner() {
        viewModelScope.launch(Dispatchers.Main + _coroutinExption) {
            var result = homeRepository.getRandomBanner();
            when (result) {
                is NetworkCallHandler.Successful<*> -> {
                    var data = result.data as List<BannerResponseDto>

                    var bannersHolder = mutableListOf<BannerModel>()
                    var bannersResponse = data.map { it.toBanner() }.toList()

                    bannersHolder.addAll(bannersResponse)
                    if (_banners.value != null) {
                        bannersHolder.addAll(_banners.value!!)
                    }

                    var distinticBanner = bannersHolder.distinctBy { it.id }.toMutableList()
                    if (distinticBanner.size > 0)
                        _banners.emit(distinticBanner)
                    else
                        _banners.emit(emptyList<BannerModel>())

                }

                is NetworkCallHandler.Error -> {
                    _banners.emit(emptyList<BannerModel>())

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

                var bannersHolder = mutableListOf<BannerModel>()
                var bannersResponse = data.toBanner()

                bannersHolder.add(bannersResponse)
                if (_banners.value != null) {
                    bannersHolder.addAll(_banners.value!!)
                }

                var distinticBanner = bannersHolder.distinctBy { it.id }.toMutableList()
                if (distinticBanner.size > 0)
                    _banners.emit(distinticBanner)
                else
                    _banners.emit(emptyList<BannerModel>())
                return null;
            }

            is NetworkCallHandler.Error -> {
                _isLoading.emit(false)

                var errorMessage = (result.data.toString().replace("", ""))
                if (errorMessage.contains(General.BASED_URL.substring(8, 20))) {
                    errorMessage.replace(General.BASED_URL, " Server ")
                }
                return errorMessage.replace("\"", "")
            }

            else -> {
                return null;
            }
        }
    }

    suspend fun deleteBanner(
        banner_id: UUID,

        ): String? {
        _isLoading.emit(true)

        var result = homeRepository.deleteBanner(banner_id);
        when (result) {
            is NetworkCallHandler.Successful<*> -> {
                var copyBanner = _banners.value?.filter { it.id != banner_id }
                if (!copyBanner.isNullOrEmpty())
                    _banners.emit(copyBanner)
                else
                    _banners.emit(emptyList())
                return null;
            }

            is NetworkCallHandler.Error -> {
                _isLoading.emit(false)

                var errorMessage = (result.data.toString().replace("", ""))
                if (errorMessage.contains(General.BASED_URL.substring(8, 20))) {
                    errorMessage.replace(General.BASED_URL, " Server ")
                }
                return errorMessage.replace("\"", "")
            }

            else -> {
                return null;
            }
        }
    }

    fun getStoreAddress(store_id: UUID, pageNumber: Int = 1) {
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

                    var distinticSubCategories =
                        subCategoriesolder.distinctBy { it.id }.toMutableList()

                    if (distinticSubCategories.size > 0)
                        _SubCategories.emit(distinticSubCategories)
                    else
                        _SubCategories.emit(emptyList())

                }

                is NetworkCallHandler.Error -> {
                    _isLoading.emit(false)
                    if (_SubCategories.value == null)
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

    fun getProducts(
        pageNumber: MutableState<Int>,
        isLoading: MutableState<Boolean>? = null
    ) {
        viewModelScope.launch(Dispatchers.IO + _coroutinExption) {
            if (isLoading != null) isLoading.value = true;
            delay(500)

            var result = homeRepository.getProduct(pageNumber.value);
            when (result) {
                is NetworkCallHandler.Successful<*> -> {
                    var data = result.data as List<ProductResponseDto>

                    var holder = mutableListOf<ProductModel>()
                    var addressResponse = data.map { it.toProdcut() }.toList()

                    holder.addAll(addressResponse)
                    if (_products.value != null) {
                        holder.addAll(_products.value!!)
                    }

                    var distinticSubCategories = holder.distinctBy { it.id }.toMutableList()

                    if (distinticSubCategories.size > 0)
                        _products.emit(distinticSubCategories)
                    else
                        _products.emit(emptyList())



                    if (isLoading != null) isLoading.value = false;
                    if (data.size == 25)
                        pageNumber.value++
                }

                is NetworkCallHandler.Error -> {
                    if (_products.value == null)
                        _products.emit(emptyList())

                    var errorMessage = (result.data.toString())
                    if (errorMessage.contains(General.BASED_URL)) {
                        errorMessage.replace(General.BASED_URL, " Server ")
                    }
                    Log.d("errorFromGettingStoreData", errorMessage)

                    if (isLoading != null) isLoading.value = false;
                }
            }

        }

    }

    fun getProducts(
        pageNumber: MutableState<Int>,
        store_id: UUID,
        isLoading: MutableState<Boolean>? = null
    ) {
        if (isLoading != null) isLoading.value = true;
        viewModelScope.launch(Dispatchers.Main + _coroutinExption) {
            var result = homeRepository.getProduct(store_id, pageNumber.value);
            when (result) {
                is NetworkCallHandler.Successful<*> -> {
                    var data = result.data as List<ProductResponseDto>

                    var holder = mutableListOf<ProductModel>()
                    var addressResponse = data.map { it.toProdcut() }.toList()

                    holder.addAll(addressResponse)
                    if (_products.value != null) {
                        holder.addAll(_products.value!!)
                    }

                    var distinticSubCategories = holder.distinctBy { it.id }.toMutableList()

                    if (distinticSubCategories.size > 0)
                        _products.emit(distinticSubCategories)
                    else
                        _products.emit(emptyList())
                    if (isLoading != null) isLoading.value = false;
                    if (data.size == 25)
                        pageNumber.value++

                }

                is NetworkCallHandler.Error -> {
                    if (_products.value == null)
                        _products.emit(emptyList())

                    var errorMessage = (result.data.toString())
                    if (errorMessage.contains(General.BASED_URL)) {
                        errorMessage.replace(General.BASED_URL, " Server ")
                    }
                    Log.d("errorFromGettingStoreData", errorMessage)
                    if (isLoading != null) isLoading.value = false;
                }
            }
        }

    }


    fun getProducts(
        pageNumber: MutableState<Int>,
        store_id: UUID,
        subCategory_id: UUID,
        isLoading: MutableState<Boolean>? = null
    ) {
        if (isLoading != null) isLoading.value = true
        viewModelScope.launch(Dispatchers.Main + _coroutinExption) {
            var result = homeRepository.getProduct(
                store_id,
                subCategory_id,
                pageNumber = pageNumber.value
            );
            when (result) {
                is NetworkCallHandler.Successful<*> -> {
                    var data = result.data as List<ProductResponseDto>

                    var holder = mutableListOf<ProductModel>()
                    var productsesponse = data.map { it.toProdcut() }.toList()

                    holder.addAll(productsesponse)
                    if (_products.value != null) {
                        holder.addAll(_products.value!!)
                    }

                    var distinticSubCategories = holder.distinctBy { it.id }.toMutableList()

                    if (distinticSubCategories.size > 0)
                        _products.emit(distinticSubCategories)
                    else if (_products.value == null)
                        _products.emit(emptyList())

                    if (isLoading != null) isLoading.value = false
                    if (data.size == 25)
                        pageNumber.value++

                }

                is NetworkCallHandler.Error -> {
                    if (_products.value == null)
                        _products.emit(emptyList())

                    var errorMessage = (result.data.toString())
                    if (errorMessage.contains(General.BASED_URL)) {
                        errorMessage.replace(General.BASED_URL, " Server ")
                    }
                    Log.d("errorFromGettingStoreData", errorMessage)
                    if (isLoading != null) isLoading.value = false

                }
            }
        }

    }

    suspend fun createProducts(
        name: String,
        description: String,
        thmbnail: File,
        subcategory_id: UUID,
        store_id: UUID,
        price: Double,
        productVarients: List<ProductVarientSelection>,
        images: List<File>
    ): String? {
        var result = homeRepository.createProduct(
            name,
            description,
            thmbnail,
            subcategory_id,
            store_id,
            price,
            productVarients,
            images
        );
        when (result) {
            is NetworkCallHandler.Successful<*> -> {
                var data = result.data as ProductResponseDto

                var holder = mutableListOf<ProductModel>()
                var addressResponse = data.toProdcut()

                holder.add(addressResponse)
                if (_products.value != null) {
                    holder.addAll(_products.value!!)
                }

                if (holder.size > 0)
                    _products.emit(holder)
                else
                    _products.emit(emptyList())
                return null
            }

            is NetworkCallHandler.Error -> {
                _isLoading.emit(false)
                _products.emit(emptyList())

                var errorMessage = (result.data.toString())
                if (errorMessage.contains(General.BASED_URL)) {
                    errorMessage.replace(General.BASED_URL, " Server ")
                }
                Log.d("errorFromGettingStoreData", errorMessage)
                return errorMessage.replace("\"", "")
            }
        }
    }

    suspend fun updateProducts(
        id: UUID,
        name: String?,
        description: String?,
        thmbnail: File?,
        subcategory_id: UUID?,
        store_id: UUID,
        price: Double?,
        productVarients: List<ProductVarientSelection>?,
        images: List<File>?,
        deletedProductVarients: List<ProductVarientSelection>?,
        deletedimages: List<String>?

    ): String? {
        var result = homeRepository.updateProduct(
            id,
            name,
            description,
            thmbnail,
            subcategory_id,
            store_id,
            price,
            productVarients,
            images,
            deletedProductVarients,
            deletedimages
        );
        when (result) {
            is NetworkCallHandler.Successful<*> -> {
                var data = result.data as ProductResponseDto

                var holder = mutableListOf<ProductModel>()
                var addressResponse = data.toProdcut()

                holder.add(addressResponse)
                if (_products.value != null) {
                    holder.addAll(_products.value!!)
                }
                val distnectHolder = holder.distinctBy { it.id }

                if (distnectHolder.size > 0)
                    _products.emit(distnectHolder)

                return null
            }

            is NetworkCallHandler.Error -> {
                _isLoading.emit(false)
                _products.emit(emptyList())

                var errorMessage = (result.data.toString())
                if (errorMessage.contains(General.BASED_URL)) {
                    errorMessage.replace(General.BASED_URL, " Server ")
                }
                Log.d("errorFromGettingStoreData", errorMessage)
                return errorMessage.replace("\"", "")
            }
        }
    }


    suspend fun deleteProduct(
        store_id: UUID,
        product_id: UUID,

        ): String? {
        var result = homeRepository.deleteProduct(
            store_id = store_id,
            product_id = product_id
        );
        when (result) {
            is NetworkCallHandler.Successful<*> -> {
                if (products.value != null) {
                    var copyProduct = _products.value?.filter { it.id != product_id };
                    _products.emit(copyProduct);
                }
                return null
            }

            is NetworkCallHandler.Error -> {

                var errorMessage = (result.data.toString())
                if (errorMessage.contains(General.BASED_URL)) {
                    errorMessage.replace(General.BASED_URL, " Server ")
                }
                Log.d("errorFromGettingStoreData", errorMessage)
                return errorMessage.replace("\"", "")
            }
        }
    }


    fun logout() {
        viewModelScope.launch {
            dao.nukeTable()
            dao.nukePassLocationTable()
        }
    }


    suspend fun submitCartTitems(): String? {
        var result = homeRepository.submitOrder(_cartImes.value.toOrderRequestDto())
        when (result) {
            is NetworkCallHandler.Successful<*> -> {
                var data = result.data as OrderResponseDto
                var orderList = mutableListOf<Order>()
                orderList.add(data.toOrderItem())
                if (!_orders.value.isNullOrEmpty()) {
                    orderList.addAll(_orders.value!!)
                }
                _orders.emit(orderList)
                _cartImes.emit(
                    CartModel(
                        0.0,
                        0.0,
                        0.0,
                        UUID.randomUUID(),
                        emptyList()
                    )
                )
                return null
            }

            is NetworkCallHandler.Error -> {
                var errorMessage = result.data as String
                return errorMessage
            }
        }

    }


    fun getMyOrder(
        pageNumber: MutableState<Int>,
        isLoading: MutableState<Boolean>? = null
    ) {
        if (isLoading != null) isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            if (isLoading != null)
                delay(500)
            var result = homeRepository.getMyOrders(pageNumber.value)
            when (result) {

                is NetworkCallHandler.Successful<*> -> {
                    var data = result.data as List<OrderResponseDto>
                    var orderList = mutableListOf<Order>()
                    orderList.addAll(data.map { it.toOrderItem() })
                    if (!_orders.value.isNullOrEmpty()) {
                        orderList.addAll(_orders.value!!)
                    }
                    val distincetOrder = orderList.distinctBy { it.id }.toList()
                    _orders.emit(distincetOrder)

                    if (isLoading != null) isLoading.value = false
                    if (data.size == 25)
                        pageNumber.value++
                }

                is NetworkCallHandler.Error -> {
                    if (_orders.value == null) {
                        _orders.emit(emptyList())
                    }
                    var errorMessage = result.data as String
                    Log.d("errorFromGettingOrder", errorMessage)
                    if (isLoading != null) isLoading.value = false

                }
            }

        }
    }


    suspend fun deleteOrder(order_id: UUID): String? {
        var result = homeRepository.deleteOrder(order_id)
        when (result) {
            is NetworkCallHandler.Successful<*> -> {
                var orderList = _orders.value?.filter { it.id != order_id };


                _orders.emit(orderList)

                return null
            }

            is NetworkCallHandler.Error -> {
                var errorMessage = result.data as String
                return errorMessage
            }
        }

    }

    fun getMyOrderItemBelongToMyStore(
        store_id: UUID,
        pageNumber: MutableState<Int>,
        isLoading: MutableState<Boolean>? = null
    ) {

        viewModelScope.launch(Dispatchers.IO) {
            if(isLoading!=null)
            {
                isLoading.value=true
                delay(500)
            }
            var result = homeRepository.getMyOrderItemForStoreId(store_id, pageNumber.value)
            when (result) {
                is NetworkCallHandler.Successful<*> -> {
                    var data = result.data as List<OrderItemResponseDto>
                    var orderItemList = mutableListOf<OrderItem>()
                    orderItemList.addAll(data.map { it.toOrderItem() })
                    if (!_orderItemForMyStore.value.isNullOrEmpty()) {
                        orderItemList.addAll(_orderItemForMyStore.value!!)
                    }
                    val distinctOrderItem = orderItemList.distinctBy { it.id }.toList()
                    _orderItemForMyStore.emit(distinctOrderItem)
                    if(isLoading!=null)isLoading.value=false
                    if(data.size==25)pageNumber.value++

                }

                is NetworkCallHandler.Error -> {
                    if(_orderItemForMyStore.value==null)
                    {
                        _orderItemForMyStore.emit(emptyList())
                    }
                    if(isLoading!=null)isLoading.value=false

                    var errorMessage = result.data as String
                    Log.d("errorFromGettingOrder", errorMessage)
                    if (_orderItemForMyStore.value == null) {
                        _orderItemForMyStore.emit(emptyList())
                    }
                }
            }

        }
    }


    suspend fun updateOrderItemStatusFromStore(id: UUID, status: Int): String? {
        var result = homeRepository.updateOrderItemStatus(id, status)
        when (result) {
            is NetworkCallHandler.Successful<*> -> {
                var orderItemStatus = when (status) {
                    0 -> "Excepted"
                    else -> "Cancelled"
                }
                var updateOrderItme = _orderItemForMyStore.value?.map { it ->
                    if (it.id == id) {
                        it.copy(orderItemStatus = orderItemStatus)
                    } else {
                        it
                    }
                }
                _orderItemForMyStore.emit(updateOrderItme);
                return null
            }

            is NetworkCallHandler.Error -> {
                var errorMessage = result.data as String
                Log.d("errorFromGettingOrder", errorMessage)
                if (_orderItemForMyStore.value == null) {
                    _orderItemForMyStore.emit(emptyList())
                }
                return errorMessage;
            }
        }

    }


}
