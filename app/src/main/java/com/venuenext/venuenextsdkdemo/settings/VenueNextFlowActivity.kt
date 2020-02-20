package com.venuenext.venuenextsdkdemo.settings

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import com.ticketmaster.presencesdk.PresenceSDK
import com.venuenext.venuenextsdkdemo.MainActivity
import com.venuenext.venuenextsdkdemo.R
import com.venuenext.vncore.VenueNext
import com.venuenext.vncore.protocol.TicketingInterface
import com.venuenext.vncore.protocol.WalletInterface
import androidx.activity.addCallback
import java.lang.Exception

private const val TAG = "VNFlowActivity"
private const val ARG_NAV_ID = "navId"
private const val ARG_INBOUND_BUNDLE = "inboundBundle"

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
        val inboundBundle = bundle?.getBundle(ARG_INBOUND_BUNDLE)

        // Receive callbacks for wallet and ticketing.
        VenueNext.ticketingInterface = this
        VenueNext.walletInterface = this

        onBackPressedDispatcher.addCallback(this) { handleBackPress() }

        try {
            // Pop the loading fragment for this example
            if (navId != 0) {
                navController.popBackStack()

                val navBundle = inboundBundle ?: bundleOf("showBackButton" to true)
                // Show the back button in these flows
                navController.navigate(navId, navBundle)
            } else {
                // When back-navigating, this handles the scenario where the hosting Activity is
                // re-launched. In our scenario, we start the MainActivity again, which drops the
                // user back onto the StandsFragment. Apply your own flow/business logic here.
                startActivity(Intent(this, MainActivity::class.java))
            }
        } catch (e: Exception) {
            Log.w(TAG, "Navigation configuration error", e)
        }

    }

    override fun onResume() {

        // This fixes Localytics throwing an exception if there are no extras in the intent onResume
        if (intent == null) {
            intent = Intent()
        }
        intent.putExtras(intent.extras ?: Bundle())

        super.onResume()
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

    private fun handleBackPress() {
        // Here you can perform any actions that you would normally perform when overriding
        // onBackPressed in the Activity that hosts VenueNext content.
        // Get the nav host fragment for this activity
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment_main)!!
        val count = navHostFragment.childFragmentManager.backStackEntryCount
        if (count == 0) {
            finish()
        }
        else {
            navController.popBackStack()
        }
    }
}
