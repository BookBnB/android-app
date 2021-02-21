package com.example.bookbnb.network

import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdReceiver
import com.google.firebase.iid.internal.FirebaseInstanceIdInternal
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import java.io.IOException


class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        // Send token to server
    }

    fun enableFCM() {
        // Enable FCM via enable Auto-init service which generate new token and receive in FCMService
        FirebaseMessaging.getInstance().isAutoInitEnabled = true
    }

    fun disableFCM() {
        // Disable auto init
        FirebaseMessaging.getInstance().isAutoInitEnabled = false
        Thread(Runnable {
            try {
                FirebaseMessaging.getInstance().deleteToken()
                //FirebaseInstallations.getInstance().delete()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }).start()
    }
}