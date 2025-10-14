package com.example.eccomerce_app.viewModel

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eccomerce_app.util.General
import com.example.eccomerce_app.dto.ProductDto
import com.example.e_commercompose.model.DtoToModel.toProdcut
import com.example.e_commercompose.model.ProductModel
import com.example.e_commercompose.model.ProductVarientSelection
import com.example.eccomerce_app.dto.StoreStatusDto
import com.example.eccomerce_app.data.NetworkCallHandler
import com.example.eccomerce_app.data.repository.ProductRepository
import com.microsoft.signalr.HubConnection
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.util.UUID


class ProductViewModel(
    val productRepository: ProductRepository,
//    val webSocket: HubConnection?
) : ViewModel() {


//     val _hub = MutableStateFlow<HubConnection?>(null)

     val _products = MutableStateFlow<List<ProductModel>?>(null)
    val products = _products.asStateFlow()


     val _coroutineException = CoroutineExceptionHandler { _, message ->
        Log.d("ErrorMessageIs", message.message.toString())
    }

/*
    fun connection() {

        if (webSocket != null) {
            viewModelScope.launch(Dispatchers.IO + _coroutineException) {

                _hub.emit(webSocket)
                _hub.value?.on(
                    "storeStatus",
                    { result ->

                        viewModelScope.launch(Dispatchers.IO + _coroutineException) {
                            if (result.Status) {
                                val productNotBelongToStore =
                                    _products.value?.filter { it.storeId != result.StoreId }
                                _products.emit(productNotBelongToStore)
                            }
                        }
                    },
                    StoreStatusDto::class.java
                )

            }

        }
    }


    init {

        connection()
    }

    override fun onCleared() {
        viewModelScope.launch(Dispatchers.IO + _coroutineException) {
            if (_hub.value != null)
                _hub.value!!.stop()
        }
        super.onCleared()
    }
*/
    fun getProducts(
        pageNumber: MutableState<Int>,
        isLoading: MutableState<Boolean>? = null
    ) {
        viewModelScope.launch(Dispatchers.IO + _coroutineException) {
            if (isLoading != null) isLoading.value = true
            delay(500)

            val result = productRepository.getProduct(pageNumber.value)
            when (result) {
                is NetworkCallHandler.Successful<*> -> {
                    val data = result.data as List<ProductDto>

                    val holder = mutableListOf<ProductModel>()
                    val addressResponse = data.map { it.toProdcut() }.toList()

                    holder.addAll(addressResponse)
                    if (_products.value != null) {
                        holder.addAll(_products.value!!)
                    }

                    val distinctSubCategories = holder.distinctBy { it.id }.toMutableList()

                    if (distinctSubCategories.isNotEmpty())
                        _products.emit(distinctSubCategories)
                    else if (_products.value == null)
                        _products.emit(emptyList())



                    if (isLoading != null) isLoading.value = false
                    if (data.size == 25)
                        pageNumber.value++
                }

                is NetworkCallHandler.Error -> {
                    if (_products.value == null)
                        _products.emit(emptyList())

                    val errorMessage = (result.data.toString())
                    if (errorMessage.contains(General.BASED_URL)) {
                        errorMessage.replace(General.BASED_URL, " Server ")
                    }
                    Log.d("errorFromGettingStoreData", errorMessage)

                    if (isLoading != null) isLoading.value = false
                }
            }

        }

    }

    fun getProducts(
        pageNumber: MutableState<Int>,
        storeId: UUID,
        isLoading: MutableState<Boolean>? = null
    ) {
        if (isLoading != null) isLoading.value = true
        viewModelScope.launch(Dispatchers.Main + _coroutineException) {
            val result = productRepository.getProduct(storeId, pageNumber.value)
            when (result) {
                is NetworkCallHandler.Successful<*> -> {
                    val data = result.data as List<ProductDto>

                    val holder = mutableListOf<ProductModel>()
                    val addressResponse = data.map { it.toProdcut() }.toList()

                    holder.addAll(addressResponse)
                    if (_products.value != null) {
                        holder.addAll(_products.value!!)
                    }

                    val distinctSubCategories = holder.distinctBy { it.id }.toMutableList()

                    if (distinctSubCategories.isNotEmpty())
                        _products.emit(distinctSubCategories)
                    else if (_products.value == null)
                        _products.emit(emptyList())
                    if (isLoading != null) isLoading.value = false
                    if (data.size == 25)
                        pageNumber.value++

                }

                is NetworkCallHandler.Error -> {
                    if (_products.value == null)
                        _products.emit(emptyList())

                    val errorMessage = (result.data.toString())
                    if (errorMessage.contains(General.BASED_URL)) {
                        errorMessage.replace(General.BASED_URL, " Server ")
                    }
                    Log.d("errorFromGettingStoreData", errorMessage)
                    if (isLoading != null) isLoading.value = false
                }
            }
        }

    }

    fun getProductsByCategoryID(
        pageNumber: MutableState<Int>,
        categoryId: UUID,
        isLoading: MutableState<Boolean>? = null
    ) {
        if (isLoading != null) isLoading.value = true
        viewModelScope.launch(Dispatchers.Main + _coroutineException) {
            val result = productRepository
                .getProductByCategoryId(
                    categoryId,
                    pageNumber.value
                )
            when (result) {
                is NetworkCallHandler.Successful<*> -> {
                    val data = result.data as List<ProductDto>

                    val holder = mutableListOf<ProductModel>()
                    val addressResponse = data.map { it.toProdcut() }.toList()

                    holder.addAll(addressResponse)
                    if (_products.value != null) {
                        holder.addAll(_products.value!!)
                    }

                    val distinctSubCategories = holder.distinctBy { it.id }.toMutableList()

                    if (distinctSubCategories.isNotEmpty())
                        _products.emit(distinctSubCategories)
                    else if (_products.value == null)
                        _products.emit(emptyList())
                    if (isLoading != null) isLoading.value = false
                    if (data.size == 25)
                        pageNumber.value++

                }

                is NetworkCallHandler.Error -> {
                    if (_products.value == null)
                        _products.emit(emptyList())

                    val errorMessage = (result.data.toString())
                    if (errorMessage.contains(General.BASED_URL)) {
                        errorMessage.replace(General.BASED_URL, " Server ")
                    }
                    Log.d("errorFromGettingStoreData", errorMessage)
                    if (isLoading != null) isLoading.value = false
                }
            }
        }

    }


    fun getProducts(
        pageNumber: MutableState<Int>,
        storeId: UUID,
        subcategoryId: UUID,
        isLoading: MutableState<Boolean>? = null
    ) {
        if (isLoading != null) isLoading.value = true
        viewModelScope.launch(Dispatchers.Main + _coroutineException) {
            val result = productRepository.getProduct(
                storeId,
                subcategoryId,
                pageNumber = pageNumber.value
            )
            when (result) {
                is NetworkCallHandler.Successful<*> -> {
                    val data = result.data as List<ProductDto>

                    val holder = mutableListOf<ProductModel>()
                    val productsesponse = data.map { it.toProdcut() }.toList()

                    holder.addAll(productsesponse)
                    if (_products.value != null) {
                        holder.addAll(_products.value!!)
                    }

                    val distinticSubCategories = holder.distinctBy { it.id }.toMutableList()

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

                    val errorMessage = (result.data.toString())
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
        subcategoryId: UUID,
        store_id: UUID,
        price: Double,
        productVariants: List<ProductVarientSelection>,
        images: List<File>
    ): String? {
        val result = productRepository.createProduct(
            name,
            description,
            thmbnail,
            subcategoryId,
            store_id,
            price,
            productVariants,
            images
        )
        when (result) {
            is NetworkCallHandler.Successful<*> -> {
                val data = result.data as ProductDto

                val holder = mutableListOf<ProductModel>()
                val addressResponse = data.toProdcut()

                holder.add(addressResponse)
                if (_products.value != null) {
                    holder.addAll(_products.value!!)
                }

                if (holder.isNotEmpty())
                    _products.emit(holder)
                else
                    _products.emit(emptyList())
                return null
            }

            is NetworkCallHandler.Error -> {
                _products.emit(emptyList())

                val errorMessage = (result.data.toString())
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
        subcategoryId: UUID?,
        storeId: UUID,
        price: Double?,
        productVariants: List<ProductVarientSelection>?,
        images: List<File>?,
        deletedProductVarients: List<ProductVarientSelection>?,
        deletedImages: List<String>?

    ): String? {
        val result = productRepository.updateProduct(
            id,
            name,
            description,
            thmbnail,
            subcategoryId,
            storeId,
            price,
            productVariants,
            images,
            deletedProductVarients,
            deletedImages
        )
        when (result) {
            is NetworkCallHandler.Successful<*> -> {
                val data = result.data as ProductDto

                val holder = mutableListOf<ProductModel>()
                val addressResponse = data.toProdcut()

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
                _products.emit(emptyList())

                val errorMessage = (result.data.toString())
                if (errorMessage.contains(General.BASED_URL)) {
                    errorMessage.replace(General.BASED_URL, " Server ")
                }
                Log.d("errorFromGettingStoreData", errorMessage)
                return errorMessage.replace("\"", "")
            }
        }
    }


    suspend fun deleteProduct(storeId: UUID, productId: UUID): String? {
        val result = productRepository.deleteProduct(
            storeId = storeId,
            productId = productId
        )
        when (result) {
            is NetworkCallHandler.Successful<*> -> {
                if (products.value != null) {
                    val copyProduct = _products.value?.filter { it.id != productId }
                    _products.emit(copyProduct)
                }
                return null
            }

            is NetworkCallHandler.Error -> {

                val errorMessage = (result.data.toString())
                if (errorMessage.contains(General.BASED_URL)) {
                    errorMessage.replace(General.BASED_URL, " Server ")
                }
                Log.d("errorFromGettingStoreData", errorMessage)
                return errorMessage.replace("\"", "")
            }
        }
    }


}

