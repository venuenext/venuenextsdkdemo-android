package com.venuenext.venuenextsdkdemo

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.venuenext.vncore.VenueNext

class FCMService : FirebaseMessagingService() {
    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
    }

    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)

        VenueNext.handleRemoteMessage(this, p0)
    }
}
