package com.example.e_commerc_delivery_man.Util

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.core.graphics.toColorInt
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.example.e_commerc_delivery_man.R
import com.example.e_commerc_delivery_man.data.Room.AuthModleEntity
import com.example.e_commerc_delivery_man.util.Secrets
import kotlinx.coroutines.flow.MutableStateFlow
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory
import java.io.File
import java.nio.ByteBuffer
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Calendar


object General {


    var authData = MutableStateFlow<AuthModleEntity?>(null);

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

    fun handlingImageForCoil(imageUrl:String?,context: Context):ImageRequest?{
        if(imageUrl==null)return null
       return  when (imageUrl.endsWith(".svg")){
            true->{
                ImageRequest.Builder(context)
                    .data(imageUrl)
                    .decoderFactory(SvgDecoder.Factory())
                    .build()
        }
            else->{
                ImageRequest.Builder(context)
                    .data(imageUrl)
                    .build()
            }

        }
    }


    fun isValideMony(number:String): Boolean{
        var reges= Regex("/^(?:100(?:\\.0(?:0)?)?|\\d{1,2}(?:\\.\\d{1,2})?)\$")
        return reges.containsMatchIn(number)
    }

    fun Uri.toCustomFil(context: Context): File? {
        var file: File? = null;

        try {
            val resolver = context.contentResolver;
            resolver.query(this, null, null, null, null)
                .use {
                        cursor->
                    if(cursor==null) throw Exception("could not accesss Local Storage")

                    cursor.moveToFirst()
                    val column = arrayOf(MediaStore.Images.Media.DATA)
                    val filePath = cursor.getColumnIndex(column[0])
                    file = File(cursor.getString(filePath))

                }
            return  file;
        } catch (e: Exception) {
            throw e;
        }
    }
    fun Calendar.toLocalDateTime(): LocalDateTime? {

        val tz = this.getTimeZone()
        val zid = if (tz == null) ZoneId.systemDefault() else tz.toZoneId()
        return LocalDateTime.ofInstant(this.toInstant(), zid)
    }

    fun convertColorToInt(value: String): Color?{
        try{
            return Color("#${value}".toColorInt())
        }catch(ex: Exception)
        {
            return null;
        }
    }
    fun LazyListState.reachedBottom(): Boolean {
        val visibleItemsInfo = layoutInfo.visibleItemsInfo // Get the visible items
        return if (layoutInfo.totalItemsCount == 0) {
            false // Return false if there are no items
        } else {
            val lastVisibleItem = visibleItemsInfo.last() // Get the last visible item
            val viewportHeight =
                layoutInfo.viewportEndOffset +
                        layoutInfo.viewportStartOffset // Calculate the viewport height

            // Check if the last visible item is the last item in the list and fully visible
            // This indicates that the user has scrolled to the bottom
            (lastVisibleItem.index + 1 == layoutInfo.totalItemsCount &&
                    lastVisibleItem.offset + lastVisibleItem.size <= viewportHeight)
        }
    }

    fun String.removeTheSingle():String{
        return this.replace("\'","");
    }

    fun ByteBuffer.toByteArray(): ByteArray{
        rewind()
        return ByteArray(remaining()).also{
            get(it)}

    }
}
