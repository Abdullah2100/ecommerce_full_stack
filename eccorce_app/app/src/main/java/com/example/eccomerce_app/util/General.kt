package com.example.eccomerce_app.Util

import com.example.eccomerce_app.R
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.example.eccomerce_app.data.Room.AuthDao
import com.example.eccomerce_app.data.Room.AuthModleEntity
import com.example.eccomerce_app.util.Secrets
import com.example.eccomerce_app.Dto.AuthResultDto
import kotlinx.coroutines.flow.MutableStateFlow
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory
import java.time.LocalDateTime
import java.util.Calendar
import java.util.Date


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




}
