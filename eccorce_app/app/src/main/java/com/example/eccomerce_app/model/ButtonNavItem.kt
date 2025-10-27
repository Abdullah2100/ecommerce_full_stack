package com.example.e_commercompose.model

import android.annotation.SuppressLint
import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector


data class ButtonNavItem(
    @SuppressLint("SupportAnnotationUsage") @StringRes val name: Int,
    val imageId: ImageVector,
    val index: Int
)
