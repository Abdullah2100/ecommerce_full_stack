package com.example.e_commercompose.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.e_commercompose.data.repository.VariantRepository
import com.example.eccomerce_app.dto.VarientDto
import com.example.e_commercompose.model.DtoToModel.toVarient
import com.example.e_commercompose.model.VarirntModel
import com.example.hotel_mobile.Modle.NetworkCallHandler
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class VariantViewModel(val variantRepository: VariantRepository) : ViewModel() {


    private val _variants = MutableStateFlow<MutableList<VarirntModel>?>(null)
    val variants = _variants.asStateFlow()

    private val _coroutineException = CoroutineExceptionHandler { _, message ->
        Log.d("ErrorMessageIs", message.message.toString())
    }

    fun getVariants(pageNumber: Int = 1) {
        if (pageNumber == 1 && _variants.value != null) return
        viewModelScope.launch(Dispatchers.IO + _coroutineException) {
            val result = variantRepository.getVariant(pageNumber)
            when (result) {
                is NetworkCallHandler.Successful<*> -> {
                    val variantsHolder = result.data as List<VarientDto>

                    val mutableVariant = mutableListOf<VarirntModel>()

                    if (pageNumber != 1 && _variants.value != null) {
                        mutableVariant.addAll(_variants.value!!.toList())
                    }
                    if (variantsHolder.isNotEmpty()) mutableVariant.addAll(
                        variantsHolder.map { it.toVarient() }.toList()
                    )

                    if (mutableVariant.isNotEmpty()) {
                        _variants.emit(
                            mutableVariant
                        )
                    } else {
                        if (_variants.value == null) _variants.emit(mutableListOf())
                    }
                }

                else -> {}
            }
        }
    }

    init {
        getVariants(1)
    }
}