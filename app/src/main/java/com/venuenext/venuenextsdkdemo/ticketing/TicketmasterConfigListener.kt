package com.venuenext.venuenextsdkdemo.ticketing

import com.ticketmaster.presencesdk.PresenceSDK
import com.ticketmaster.presencesdk.login.PresenceSdkConfigListener
import com.venuenext.vnwallet.BuildConfig
import com.venuenext.vnwallet.R

internal typealias LaunchPresenceSDKListener = (Boolean) -> Unit

/**
 * Configures the Ticketmaster Presence SDK
 */
class TicketmasterConfigListener(
    private val presenceSDK: PresenceSDK,
    private val listener: LaunchPresenceSDKListener
) : PresenceSdkConfigListener {

    fun configurePresenceSDK() {
        presenceSDK.apply {
            registerConfigListener(this@TicketmasterConfigListener)
            setConfig(
                BuildConfig.ticketmasterSdkKey,
                BuildConfig.ticketmasterDisplayName,
                true
            )

            // Configure your branding color for the SDK
            setBrandingColor(R.color.colorAccent)
        }
    }

    override fun onPresenceSdkConfigSuccessful() = listener(true)

    override fun onPresenceSdkConfigFailed(p0: String?) = listener(false)
}
