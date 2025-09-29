package com.example.eccomerce_app.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.e_commercompose.model.Category
import com.example.e_commercompose.model.DtoToModel.toCategory
import com.example.eccomerce_app.dto.CategoryDto
import com.example.eccomerce_app.data.NetworkCallHandler
import com.example.eccomerce_app.data.repository.CategoryRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CategoryViewModel(val categoryRepository: CategoryRepository) : ViewModel() {
     val _categories = MutableStateFlow<MutableList<Category>?>(null)
    val categories = _categories.asStateFlow()

     val _coroutineException = CoroutineExceptionHandler { _, message ->
        Log.d("ErrorMessageIs", message.message.toString())
    }


    fun getCategories(pageNumber: Int = 1) {
        Log.d("http://", "is category not null ${pageNumber == 1 && _categories.value != null}")
        if (pageNumber == 1 && !_categories.value.isNullOrEmpty()) return
        viewModelScope.launch(Dispatchers.IO + _coroutineException) {
            val result = categoryRepository.getCategory(pageNumber)
            when (result) {
                is NetworkCallHandler.Successful<*> -> {
                    val categoriesHolder = result.data as List<CategoryDto>

                    val mutableCategories = mutableListOf<Category>()

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
                    }

                }

                is NetworkCallHandler.Error -> {
                    if (_categories.value == null)
                        _categories.emit(mutableListOf())
                }
            }
        }
    }


    init {
        getCategories(1)
    }

}