package com.venuenext.venuenextsdkdemo

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.venuenext.vncore.VenueNext

class FCMService : FirebaseMessagingService() {

    override fun onNewToken(s: String?) {
        super.onNewToken(s)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        super.onMessageReceived(remoteMessage)

        VenueNext.handleRemoteMessage(this, remoteMessage)
    }
}
