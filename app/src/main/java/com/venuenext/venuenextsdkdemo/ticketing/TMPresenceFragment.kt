package com.venuenext.venuenextsdkdemo.ticketing

import android.app.Application
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.ticketmaster.presencesdk.PresenceSDK
import com.ticketmaster.presencesdk.login.PresenceLoginListener
import com.ticketmaster.presencesdk.login.TMLoginApi
import com.ticketmaster.presencesdk.login.UserInfoManager
import com.venuenext.venuenextsdkdemo.R
import com.venuenext.venuenextsdkdemo.databinding.FragmentPresenceBinding
import com.venuenext.vncore.VenueNext
import com.venuenext.vncoreui.LifecycleCoroutineScope
import com.venuenext.vnticket.VNTicket
import com.venuenext.vnticket.model.TicketingLoginData
import com.venuenext.vnticket.protocol.LoginResultListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val TAG = "TMPresenceFragment"

// This sample is used to demonstrate logging into the VenueNext Wallet from Ticketmaster. This is
// is not intended to utilize TM functionality.
class TMPresenceFragment : Fragment(), PresenceLoginListener,
    PresenceSDK.MemberInfoCompletionCallback, View.OnClickListener, LoginResultListener {

    private lateinit var binding: FragmentPresenceBinding
    private var ticketmasterConfigListener: TicketmasterConfigListener? = null

    private val coroutineScope = LifecycleCoroutineScope(this)
    private val presenceSDK by lazy { PresenceSDK.getPresenceSDK(application) }
    private val application by lazy { context?.applicationContext as Application }

    private var memberInfoDidLoad = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentPresenceBinding.inflate(inflater)
        binding.contentPresenceBackImageview.setOnClickListener(this)
        binding.contentPresenceLogoutButton.setOnClickListener(this)
        binding.contentPresenceLogoutButton.isVisible = presenceSDK.isLoggedIn

        ticketmasterConfigListener =
            TicketmasterConfigListener(presenceSDK, ::shouldLaunchPresenceSDK)
        ticketmasterConfigListener?.configurePresenceSDK()

        return binding.root
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            binding.contentPresenceBackImageview.id -> findNavController().navigateUp()
            binding.contentPresenceLogoutButton.id -> {
                presenceSDK.logOut()

                showView(true)
            }
        }
    }

    // Note: This is called from a background thread
    override fun onLoginSuccessful(backendName: TMLoginApi.BackendName?, p1: String?) {
        coroutineScope.launch(Dispatchers.Main) {
            presenceSDK.getMemberInfo(backendName, this@TMPresenceFragment)
        }
    }

    override fun onLogoutAllSuccessful() {
        VNTicket.logout(::vnSdkLogoutSuccess, ::vnSdkLogoutFailure)
    }

    private fun shouldLaunchPresenceSDK(shouldLaunch: Boolean) {
        if (activity == null) return

        if (shouldLaunch) {
            presenceSDK.start(activity as AppCompatActivity, R.id.fragment_presence_container, this)
        } else {
            // Configuration failed
            Snackbar.make(binding.root, getText(R.string.tm_config_failure), Snackbar.LENGTH_LONG)
                .show()

            showView(false)
        }
    }

    // Note: This is a callback from a background thread
    private fun vnSdkLogoutSuccess() {
        // Hide UI, back press, etc
        coroutineScope.launch(Dispatchers.Main) {
            Log.i(TAG, "VenueNext logout success")
            presenceSDK.stop()

            // Refresh to the login state
            showView(false)
            ticketmasterConfigListener?.configurePresenceSDK()
        }
    }

    // Note: This is a callback from a background thread
    private fun vnSdkLogoutFailure(e: Exception) {
        coroutineScope.launch(Dispatchers.Main) {
            // Hide UI, back press, etc
            Log.e(TAG, "Logout failed", e)
            Snackbar.make(binding.root, getText(R.string.sdk_logout_failure), Snackbar.LENGTH_LONG)
                .show()
        }
    }

    override fun onMemberInfoLoaded(info: UserInfoManager.MemberInfo?, p1: String?) {

        memberInfoDidLoad = true

        info?.let {

            val memberId = it.memberId
            val email = it.email
            val firstName = it.firstName
            val lastName = it.lastName

            // Register the login listener
            VNTicket.registerLoginResultListener(this)

            val ticketingLoginData = TicketingLoginData(email, memberId, firstName, lastName)

            // Kick off the (asynchronous) SDK login handler
            VNTicket.onLoginSuccess(ticketingLoginData)

            // We recommend showing a progress bar or other loading UI here
            showView(true)

        }
    }

    // Called asynchronously. Ticketing data can be safely ignored. Wallet is ready to be shown!
    override fun onLoginSuccess(ticketingLoginData: TicketingLoginData) {
        VNTicket.unregisterLoginResultListener(this)

        // This demo app does not support full TM functionality, so we are popping this fragment
        findNavController().navigateUp()
        VenueNext.walletInterface?.showWallet()

        showView(false)
    }

    // Unhide loading UI and show an error here
    override fun onLoginFailure() {
        activity?.let {
            binding.progressBar.isVisible = false

            Snackbar.make(binding.root, getText(R.string.sdk_login_failure), Snackbar.LENGTH_LONG)
                .show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        if (!memberInfoDidLoad) {
            VNTicket.onLoginFailure()
        }

        ticketmasterConfigListener = null
    }

    private fun showView(isLoading: Boolean) {
        binding.fragmentPresenceContainer.isVisible = !isLoading
        binding.progressBar.isVisible = isLoading

        binding.contentPresenceLogoutButton.isVisible = presenceSDK.isLoggedIn && !isLoading
    }

    // Unused for this example
    override fun onLoginMethodUsed(p0: TMLoginApi.BackendName?, p1: TMLoginApi.LoginMethod?) {}
    override fun onLoginWindowDidDisplay(p0: TMLoginApi.BackendName?) {}
    override fun onLoginForgotPasswordClicked(p0: TMLoginApi.BackendName?) {}
    override fun onLoginFailed(p0: TMLoginApi.BackendName?, p1: String?) {}
    override fun onRefreshTokenFailed(p0: TMLoginApi.BackendName?) {}
    override fun onCacheCleared() {}
    override fun onLoginCancelled(p0: TMLoginApi.BackendName?) {}
    override fun onLogoutSuccessful(p0: TMLoginApi.BackendName?) {}
    override fun onMemberUpdated(p0: TMLoginApi.BackendName?, p1: UserInfoManager.MemberInfo?) {}
    override fun onTokenRefreshed(p0: TMLoginApi.BackendName?, p1: String?) {}

}
