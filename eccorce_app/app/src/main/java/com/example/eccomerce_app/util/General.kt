package com.example.eccomerce_app.Util

import android.content.Context
import android.net.Uri
import com.example.eccomerce_app.R
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.example.eccomerce_app.data.Room.AuthDao
import com.example.eccomerce_app.data.Room.AuthModleEntity
import com.example.eccomerce_app.util.Secrets
import com.example.eccomerce_app.Dto.AuthResultDto
import kotlinx.coroutines.flow.MutableStateFlow
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory
import java.io.File
import java.time.LocalDateTime
import java.util.Calendar
import java.util.Date
import android.provider.MediaStore


object General {

    fun getCalener(): Calendar {
        return Calendar.getInstance();
    }


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

    fun handlingImageForCoil(imageUrl:String,context: Context):ImageRequest{
       return when (imageUrl.endsWith(".svg")){
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

}
