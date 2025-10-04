package com.example.e_commerc_delivery_man

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.serialization.json.JsonObject
import org.json.JSONObject

//class MyFirebaseMessagingService
//    : FirebaseMessagingService()
//{
//
//    // Assuming you have access to userId
////    private val userId:String= "userId"
//
//    override fun onNewToken(token: String) {
//        super.onNewToken(token)
//
//        Log.d("FCM", "Token: $token")
//    }
//
//    override fun onMessageReceived(remoteMessage: RemoteMessage) {
//        // This method will be called everytime FCM service sends message.
//        super.onMessageReceived(remoteMessage)
//        remoteMessage?.notification?.let {
//            showNotification(it.title, it.body)
//
//        };
//    }
//    private fun showNotification(title: String?, body: String?) {
//        val channelId = "default_channel"
//        val channelName = "Default Channel"
//
//        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
//            notificationManager.createNotificationChannel(channel)
//        }
//
//        val notification = NotificationCompat.Builder(this, channelId)
//            .setContentTitle(title)
//            .setContentText(body)
//            .setSmallIcon(R.drawable.ic_launcher_foreground)
//            .setPriority(NotificationCompat.PRIORITY_HIGH)
//            .build()
//
//        notificationManager.notify(0, notification)
//    }
//
//
//
//    }