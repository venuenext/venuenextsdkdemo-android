package com.venuenext.venuenextsdkdemo.settings

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import com.ticketmaster.presencesdk.PresenceSDK
import com.venuenext.venuenextsdkdemo.R
import com.venuenext.vncore.VenueNext
import com.venuenext.vncore.protocol.TicketingInterface
import com.venuenext.vncore.protocol.WalletInterface
import java.lang.Exception

private const val TAG = "VNFlowActivity"
private const val ARG_NAV_ID = "navId"

/**
 * This is a basic example of SDK flows outside of a single Activity app.
 *
 * Note: The SDK is not initialized here and does not need to be.
 */
class VenueNextFlowActivity : AppCompatActivity(), TicketingInterface, WalletInterface {

    private lateinit var view: View
    private val presenceSDK: PresenceSDK by lazy { PresenceSDK.getPresenceSDK(application) }
    private val navController by lazy { Navigation.findNavController(view) }
    override val virtualCurrencyName by lazy { getString(R.string.virtual_currency_name) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_venuenext_flow)

        view = findViewById<View>(R.id.fragment_main)

        val bundle = savedInstanceState ?: intent?.extras
        val navId = requireNotNull(bundle?.getInt(ARG_NAV_ID)) { "Missing required nav ID" }

        // Receive callbacks for wallet and ticketing.
        VenueNext.ticketingInterface = this
        VenueNext.walletInterface = this

        try {
            // Pop the loading fragment for this example
            if (navId != 0) {
                navController.popBackStack()

                // Show the back button in these flows
                navController.navigate(navId, bundleOf("showBackButton" to true))
            }
        } catch (e: Exception) {
            Log.w(TAG, "Navigation configuration error", e)
        }

    }

    override fun startLoginFlow() {
        if (!presenceSDK.isLoggedIn) {
            navController.navigate(R.id.action_to_ticketing_flow)
        }
    }

    override fun showWallet() {
        // Show the login instead of Wallet if the user is not logged in
        val destination = if (presenceSDK.isLoggedIn)
            R.id.action_to_wallet_flow
        else R.id.action_to_ticketing_flow

        navController.navigate(destination)
    }
}
