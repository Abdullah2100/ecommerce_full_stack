package com.example.eccomerce_app.util

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.provider.MediaStore
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorInt
import android.graphics.Canvas
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.example.e_commercompose.R
import com.example.eccomerce_app.data.Room.Model.AuthModelEntity
import kotlinx.coroutines.flow.MutableStateFlow
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory
import java.io.ByteArrayOutputStream
import java.io.File
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Calendar


object General {


    val authData = MutableStateFlow<AuthModelEntity?>(null)
    val currentLocal = MutableStateFlow<String?>(null)

    val BASED_URL = Secrets.getBaseUrl()


    fun encryptionFactory(databaseName: String): SupportFactory {
        val passPhraseBytes = SQLiteDatabase.getBytes(databaseName.toCharArray())
        return SupportFactory(passPhraseBytes)
    }

    val satoshiFamily = FontFamily(
        Font(R.font.satoshi_bold, FontWeight.Bold),
        Font(R.font.satoshi_medium, FontWeight.Medium),
        Font(R.font.satoshi_regular, FontWeight.Normal)
    )

    fun handlingImageForCoil(imageUrl: String?, context: Context): ImageRequest? {
        if (imageUrl == null) return null
        return when (imageUrl.endsWith(".svg")) {
            true -> {
                ImageRequest.Builder(context)
                    .data(imageUrl)
                    .decoderFactory(SvgDecoder.Factory())
                    .build()
            }

            else -> {
                ImageRequest.Builder(context)
                    .data(imageUrl)
                    .build()
            }

        }
    }


    fun convertColorToInt(value: String): Color? {
        return try {
            Color("#${value}".toColorInt())
        } catch (ex: Exception) {
            null
        }
    }


    fun Uri.toCustomFil(context: Context): File? {
        var file: File? = null

        try {
            val resolver = context.contentResolver
            resolver.query(this, null, null, null, null)
                .use { cursor ->
                    if (cursor == null) throw Exception("could not accesses Local Storage")

                    cursor.moveToFirst()
                    val column = arrayOf(MediaStore.Images.Media.DATA)
                    val filePath = cursor.getColumnIndex(column[0])
                    file = File(cursor.getString(filePath))

                }
            return file
        } catch (e: Exception) {
            throw e
        }
    }

    fun Calendar.toLocalDateTime(): LocalDateTime? {

        val tz = this.getTimeZone()
        val zid = if (tz == null) ZoneId.systemDefault() else tz.toZoneId()
        return LocalDateTime.ofInstant(this.toInstant(), zid)
    }

    fun Long.toCalender(): Calendar {
        val calendar = Calendar.getInstance()

        val instant = Instant.ofEpochMilli(this)
        val zonedDateTime = instant.atZone(ZoneId.systemDefault())
        calendar.set(
            zonedDateTime.year,
            zonedDateTime.month.value - 1,
            zonedDateTime.dayOfMonth,
            Calendar.HOUR,
            Calendar.MINUTE,
            Calendar.SECOND
        )
        return calendar

    }

    fun LazyListState.reachedBottom(): Boolean {
        val visibleItemsInfo = layoutInfo.visibleItemsInfo // Get the visible items
        return if (layoutInfo.totalItemsCount == 0) {
            false // Return false if there are no items
        } else {
            val lastVisibleItem = visibleItemsInfo.last() // Get the last visible item
            val viewportHeight =
                layoutInfo.viewportEndOffset + layoutInfo.viewportStartOffset // Calculate the viewport height

            // Check if the last visible item is the last item in the list and fully visible
            // This indicates that the user has scrolled to the bottom
            (lastVisibleItem.index + 1 == layoutInfo.totalItemsCount &&
                    lastVisibleItem.offset + lastVisibleItem.size <= viewportHeight)
        }
    }

    fun drawableToByteArray(drawableResId: Int,context: Context): ByteArray {
        val drawable = ContextCompat.getDrawable(context, drawableResId) ?: return ByteArray(0)
        val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
    }

}
