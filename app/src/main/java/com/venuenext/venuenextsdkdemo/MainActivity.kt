package com.venuenext.venuenextsdkdemo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.iid.FirebaseInstanceId
import com.ticketmaster.presencesdk.PresenceSDK
import com.venuenext.vnanalytics.firebase.FirebaseAnalytics
import com.venuenext.vncore.VenueNext
import com.venuenext.vncore.protocol.TicketingInterface
import com.venuenext.vncore.protocol.WalletInterface
import com.venuenext.vncoreui.LifecycleCoroutineScope
import com.venuenext.vnlocalytics.localytics.LocalyticsAnalytics
import com.venuenext.vnorderui.VNOrderUI
import com.venuenext.vnpayment.VNPayment
import com.venuenext.vnpayment.braintree.ui.BraintreePaymentProcessableFragment
import com.venuenext.vnwalletui.QrConfig
import com.venuenext.vnwalletui.VNWalletUI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener,
    TicketingInterface,
    WalletInterface {

    private lateinit var view: View
    private lateinit var bottomNavigationView: BottomNavigationView

    private val coroutineScope = LifecycleCoroutineScope(this)

    private val presenceSDK: PresenceSDK by lazy { PresenceSDK.getPresenceSDK(application) }
    private val navController by lazy { Navigation.findNavController(view) }
    override val virtualCurrencyName by lazy { getString(R.string.virtual_currency_name) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        view = findViewById(R.id.fragment_main)

        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView.setOnNavigationItemSelectedListener(this)

        // Setting a reselected listener will disallow selection of the same tab
//        bottomNavigationView.setOnNavigationItemReselectedListener { /* Disallow reselection */ }

        coroutineScope.launch(Dispatchers.IO) {
            try {
                val sdkKey = getString(R.string.vn_sdk_key)
                val sdkSecret = getString(R.string.vn_sdk_secret)

                VenueNext.configureAnalytics(FirebaseAnalytics(this@MainActivity))
                VenueNext.configureAnalytics(LocalyticsAnalytics(this@MainActivity, application!!))

                VNPayment.configurePaymentProcessing(BraintreePaymentProcessableFragment(), true)

                VenueNext.ticketingInterface = this@MainActivity
                VenueNext.walletInterface = this@MainActivity

                VenueNext.initialize(sdkKey, sdkSecret, this@MainActivity).await()

                // Initialize top level module objects to handle deep links
                VenueNext.registerDeepLinkable(VNOrderUI, VNWalletUI)

                // Call configure to set virtual currency toggle visibility in the wallet UI
                VNWalletUI.configure(
                    isVirtualCurrencyToggleVisible = true,
                    qrConfig = QrConfig.VC_AND_SCANNER,
                    actionBarTitle = getString(R.string.wallet_title)
                )

                withContext(Dispatchers.Main) {
                    completeInitialize()
                }

            } catch (e: Exception) {
                Log.e(TAG, "Initialization failed", e)
                finish()
            }

        }
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.navigation_food_and_bev_rvc -> {
                navController.navigate(R.id.action_to_food_bev_flow)
                true
            }
            // This flow is not currently supported by the SDK
//            R.id.navigation_merchandise_rvc -> {
//                navController.navigate(R.id.action_to_merchandise_flow)
//                true
//            }
            R.id.navigation_marketplace_rvc -> {
                navController.navigate(R.id.action_to_experience_flow)
                true
            }
            R.id.navigation_order_history -> {
                navController.navigate(R.id.action_to_my_orders_flow)
                true
            }
            R.id.navigation_wallet -> {
                showWallet()
                true
            }
            R.id.navigation_more -> {
                Snackbar.make(view, R.string.nav_more_message, Snackbar.LENGTH_SHORT).show()
                false
            }
            else -> false
        }
    }

    override fun onResume() {
        super.onResume()

        VenueNext.checkIsConnected(this)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        VenueNext.analytics?.forEach {
            if (it is LocalyticsAnalytics) it.permissionHelper
                .checkLocationPermissionRequest(requestCode, permissions, grantResults)
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent()
    }

    private fun completeInitialize() {
        registerFCM()

        bottomNavigationView.visibility = View.VISIBLE
        navController.navigate(R.id.start_main)

        // Handle deep linking after the app has loaded
        handleIntent()
    }

    private fun handleIntent() {
        if (intent?.action == Intent.ACTION_VIEW) {
            intent.data?.let {
                if (VenueNext.canHandleDeepLink(it)) {

                    VenueNext.handleDeepLink(view, it)

                    // Clear the arguments so the deep link isn't triggered again
                    intent = null
                }
            }
        }
    }

    private fun registerFCM() {
        // Get token
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(TAG, "getInstanceId failed", task.exception)
                    return@OnCompleteListener
                }

                // Get new Instance ID token
                val token = task.result?.token

                // Register device
                VenueNext.registerDevice(this@MainActivity, token!!)
            })
    }

    override fun startLoginFlow() {
        if (!presenceSDK.isLoggedIn) {
            navController.navigate(R.id.action_to_ticketing_flow)
        } else {
            Log.w(TAG, "Login requested but user is already logged in")
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
