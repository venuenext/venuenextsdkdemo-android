package com.venuenext.venuenextsdkdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.venuenext.vnanalytics.firebase.FirebaseAnalytics
import com.venuenext.vncore.OrderNotificationListener
import com.venuenext.vncore.VenueNext
import com.venuenext.vnorder.VenueNextOrders
import com.venuenext.vnorder.orders.model.PaymentProcessableFragment
import com.venuenext.vnorderui.orders.MyOrdersFragment
import com.venuenext.vnorderui.orders.OrderSummaryFragment
import com.venuenext.vnorderui.stands.StandMenuFragment
import com.venuenext.vnorderui.stands.StandsFragment
import com.venuenext.vnorderui.ui.OrderCancelDialogFragment
import com.venuenext.vnpayment.braintree.ui.BraintreePaymentProcessableFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import java.lang.Exception

class MainActivity : AppCompatActivity(), OrderNotificationListener {

    lateinit var view: View

    val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        view = findViewById<View>(R.id.fragment_main)

        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->

            val navOptions = NavOptions.Builder().setLaunchSingleTop(true).build()

            when (menuItem.itemId) {
                R.id.navigation_menus -> {

                    VenueNextOrders.orderUUID = null
                    val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment_main)
                    val currentHostedFragment = navHostFragment?.childFragmentManager?.fragments?.get(0)

                    if (!VenueNextOrders.currentCart?.cartEntries.isNullOrEmpty()) {
                        if (currentHostedFragment != null && currentHostedFragment is StandMenuFragment) {
                            val orderCancelFragment = OrderCancelDialogFragment()
                            orderCancelFragment.show(navHostFragment.childFragmentManager, orderCancelFragment.tag)
                        } else if (currentHostedFragment!= null && (currentHostedFragment is OrderSummaryFragment || currentHostedFragment is PaymentProcessableFragment)){
                            VenueNextOrders.currentCart = null
                            Navigation.findNavController(view).navigate(com.venuenext.vnorderui.R.id.standsFragment, null, navOptions)
                        } else {
                            Navigation.findNavController(view).navigate(com.venuenext.vnorderui.R.id.standMenuFragment, null, navOptions)
                        }
                    } else {
                        if (currentHostedFragment != null && currentHostedFragment !is StandsFragment) {
                            Navigation.findNavController(view).navigate(com.venuenext.vnorderui.R.id.standsFragment, null, navOptions)
                        } else {
                            return@setOnNavigationItemSelectedListener false
                        }
                    }

                    return@setOnNavigationItemSelectedListener true
                }
                R.id.navigation_myorders -> {
                    val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment_main)
                    val currentHostedFragment = navHostFragment?.childFragmentManager?.fragments?.get(0)

                    if (currentHostedFragment != null && currentHostedFragment !is MyOrdersFragment) {
                        Navigation.findNavController(view).navigate(com.venuenext.vnorderui.R.id.myOrdersFragment, null, navOptions)
                    } else {
                        return@setOnNavigationItemSelectedListener false
                    }

                    return@setOnNavigationItemSelectedListener true
                }
                /* R.id.navigation_inbox -> {
                    Navigation.findNavController(view).navigate(com.venuenext.vnorderui.R.id.inboxFragment)
                    return@setOnNavigationItemSelectedListener true
                } */
                R.id.navigation_settings -> {
                    Navigation.findNavController(view).navigate(com.venuenext.vnorderui.R.id.settingsFragment)
                    return@setOnNavigationItemSelectedListener true
                }
            }
            false
        }

        GlobalScope.async {
            try {
                val newRelicSdkKey = BuildConfig.newRelicSdkKey
                val sdkKey = BuildConfig.sdkKey
                val sdkSecret = BuildConfig.sdkSecret

                VenueNext.notificationTitle = "Intel Demo"
                VenueNext.notificationSmallIcon = R.mipmap.ic_launcher
                VenueNext.notificationLargeIcon = R.mipmap.ic_launcher

                VenueNext.newRelicSdkKey = newRelicSdkKey

                VenueNext.configureAnalytics(FirebaseAnalytics(this@MainActivity))
                VenueNextOrders.configurePaymentProcessing(BraintreePaymentProcessableFragment(), true)

                VenueNext.initialize(sdkKey, sdkSecret, this@MainActivity).await()
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    completeInitialize()
                }
            }

            withContext(Dispatchers.Main) {
                completeInitialize()
            }
        }
    }

    override fun onResume() {
        super.onResume()

        VenueNext.subscribeOrderNotification(this)
        VenueNext.checkIsConnected(this)
    }

    override fun onPause() {
        super.onPause()

        VenueNext.unsubscribeOrderNotification(this)
    }

    override fun onOrderNotification(orderUUID: String?) {
        if (orderUUID.isNullOrEmpty()) {
            return
        }

        VenueNextOrders.orderUUID = orderUUID

        Navigation.findNavController(view).navigate(com.venuenext.vnorderui.R.id.orderSummaryFragment)
    }

    private fun completeInitialize() {
        registerFCM()

        bottomNavigationView.visibility = View.VISIBLE

        Navigation.findNavController(view).navigate(R.id.action_main_to_stands)
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
}
