package com.venuenext.venuenextsdkdemo.settings

import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.ticketmaster.presencesdk.PresenceSDK
import com.venuenext.venuenextsdkdemo.R
import com.venuenext.venuenextsdkdemo.databinding.FragmentSettingsBinding
import com.venuenext.vncore.VenueNext
import com.venuenext.vnorderui.VNOrderUI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val SPACE_HEIGHT = 18
private const val TAG = "SettingsFragment"

class SettingsFragment : Fragment() {
    private val presenceSDK: PresenceSDK by lazy { PresenceSDK.getPresenceSDK(activity!!.application) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentSettingsBinding.inflate(inflater)

        val viewModel =
            ViewModelProviders.of(this@SettingsFragment).get(SettingsViewModel::class.java)

        binding.version.text =
            getString(R.string.settings_app_version, getString(R.string.app_version))

        val adapter = SettingsSectionAdapter(viewModel.settingsSections, ::onItemClicked)
        binding.recyclerView.apply {
            addItemDecoration(MarginItemDecoration(SPACE_HEIGHT))
            setHasFixedSize(true)
            this.adapter = adapter
        }

        return binding.root
    }

    private fun onItemClicked(@StringRes id: Int) {

        when(id) {
            R.string.settings_show_awarded_or_transferred_experience_receipt -> {
                showTextInputDialogForUUIDCapture(
                    "User Item UUID"
                ) {
                    VNOrderUI.showAwardedOrTransferredExperience(
                        requireView().findNavController(),
                        it
                    )
                }
            }
            R.string.settings_show_purchased_experience_receipt -> {
                showTextInputDialogForUUIDCapture(
                    "Order UUID"
                ) {
                    VNOrderUI.showPurchasedExperienceReceipt(
                        requireView().findNavController(),
                        it

                    )
                }
            }
            R.string.new_jwt_or_org -> {
                showDropdownSelectionDialogWithThreeEditTexts(
                    getString(R.string.choose_org),
                    resources.getStringArray(R.array.orgs_array),
                    getString(R.string.enter_jwt),
                    getString(R.string.enter_key),
                    getString(R.string.enter_secret)
                ) { orgName, jwt, sdkKey, sdkSecret ->
                    reInitSDK(orgName, jwt, sdkKey, sdkSecret)
                }
            }
        }

        val navId = when (id) {
            // Revenue Center Flows
            R.string.settings_display_all_rvc -> R.id.action_all_stands_flow
            R.string.settings_display_f_and_b_rvc -> R.id.action_to_food_bev_flow
            R.string.settings_display_merchandise_rvc -> R.id.action_to_merchandise_flow
            R.string.settings_display_marketplace_experiences -> R.id.action_to_experience_flow

            // Display Orders
            R.string.settings_launch_order_history -> R.id.action_to_my_orders_flow

            // Display Wallet
            R.string.settings_launch_wallet -> {
                if (presenceSDK.isLoggedIn) {
                    R.id.action_to_wallet_flow
                } else {
                    // Wallet should not be shown if you are logged out of your ticketing provider
                    Snackbar.make(view!!, R.string.settings_wallet_not_ready, Snackbar.LENGTH_SHORT)
                        .show()
                    null
                }
            }

            // Demo Specific Settings
            R.string.settings_logout_tm -> {
                if (presenceSDK.isLoggedIn) {
                    presenceSDK.logOut()
                    Toast.makeText(requireActivity(), R.string.tm_logging_out, Toast.LENGTH_SHORT)
                        .show()
                } else
                    Snackbar.make(view!!, R.string.tm_not_logged_in, Snackbar.LENGTH_SHORT).show()
                null
            }
            R.string.settings_launch_tm -> R.id.action_to_ticketing_flow

            // Direct Launch Flows
            R.string.settings_launch_to_food_menu -> R.id.action_start_to_stand_menu
            R.string.settings_launch_to_experience_menu, R.string.settings_launch_to_experience_menu_event_selected -> R.id.action_start_to_experience_menu
            R.string.settings_launch_to_experience_detail -> R.id.action_start_to_experience_detail
            else -> null
        }

        val bundle = when (id) {
            // Direct Launch Flows
            R.string.settings_launch_to_food_menu -> bundleOf(
                "toMenuId" to "<YourFoodMenuUUID>"
            )
            R.string.settings_launch_to_experience_menu -> bundleOf(
                "toMenuId" to "<YourExperienceMenuUUID>"
            )
            R.string.settings_launch_to_experience_menu_event_selected -> bundleOf(
                "toMenuId" to "<YourExperienceMenuUUID>",
                "defaultEventId" to "<YourExperienceEventUUID>"
            )
            R.string.settings_launch_to_experience_detail -> bundleOf(
                "menu_id" to "<YourExperienceMenuUUID>",
                "event_id" to "<YourEventUUID>",
                "item_id" to "<YourItemUUID>"
            )
            else -> null
        }

        navId?.let {
            // Change this to false if you don't want a back button to be displayed in these views
            bundle?.putBoolean("showBackButton", true)

            findNavController().navigate(
                R.id.action_to_vn_activity_flow,
                bundleOf(
                    "navId" to navId,
                    "inboundBundle" to bundle
                )
            )
        }
    }

    private fun showTextInputDialogForUUIDCapture(title: String, action: (String) -> Unit) {
        val layout = LinearLayout(context).apply { orientation = LinearLayout.VERTICAL }
        val editText = EditText(context)
        layout.addView(editText)
        AlertDialog.Builder(context!!)
            .setTitle(title)
            .setView(layout)
            .setPositiveButton(R.string.ok) { d, _ ->
                try {
                    action(editText.text.toString())
                } catch (e: Exception) {
                    val error = e.message ?: "Unknown Error"
                    showMessage(error)
                }
                d.dismiss()
            }
            .show()
    }

    private fun showDropdownSelectionDialogWithThreeEditTexts(
        title: String,
        options: Array<String>,
        hint1: String,
        hint2: String,
        hint3: String,
        action: (dropDownSelection: String, response1: String, response2: String, response3: String) -> Unit
    ) {
        val layout = LinearLayout(requireContext()).apply { orientation = LinearLayout.VERTICAL }
        val spinner = Spinner(requireContext())
        val adapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_list_item_1, options)
        spinner.adapter = adapter

        val editText1 = EditText(requireContext())
        val editText2 = EditText(requireContext())
        val editText3 = EditText(requireContext())

        editText1.hint = hint1
        editText2.hint = hint2
        editText3.hint = hint3

        layout.addView(spinner)
        layout.addView(editText1)
        layout.addView(editText2)
        layout.addView(editText3)

        AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setView(layout)
            .setPositiveButton(R.string.ok) { d, _ ->
                try {
                    action(
                        (spinner.selectedItem as? String) ?: "",
                        editText1.text.toString(),
                        editText2.text.toString(),
                        editText3.text.toString()
                    )
                }
                catch (e: Exception) {
                    val error = e.message ?: "Unknown Error"
                    showMessage(error)
                }
                d.dismiss()
            }
            .show()
    }

    private fun showMessage(msg: String) {
        Log.i(TAG, msg)
        AlertDialog.Builder(context!!)
            .setTitle("Result")
            .setMessage(msg)
            .setPositiveButton(R.string.ok) { _, _ -> }
            .show()
    }

    private fun reInitSDK(orgName: String, jwt: String, key: String, secret: String) {
        val token = if (jwt.isNotBlank()) jwt else null
        val configFileName = getConfigFileName(orgName)

        val configStream = resources.openRawResource(
            resources.getIdentifier(configFileName, "raw", activity?.packageName)
        )
        var configString = ""
        try {
            configStream.bufferedReader().use { configString = it.readText() }
        }
        catch (e: Exception) {
            Log.e(TAG, "Error reading the VN SDK config file", e)
        }

        VenueNext.initialize(
            sdkKey = key,
            sdkSecret = secret,
            context = requireContext(),
            jwt = token,
            configJsonString = configString,
            onSuccess = this::completeInitialize,
            onError = this::completeInitializeAfterFailure
        )
    }

    private fun completeInitialize() {
        Snackbar.make(requireView(), getString(R.string.test_init_success), Snackbar.LENGTH_LONG).show()
    }

    private fun completeInitializeAfterFailure(e: Throwable) {
        Snackbar.make(requireView(), getString(R.string.test_init_failure), Snackbar.LENGTH_LONG).show()
    }

    private fun getConfigFileName(orgName: String): String = when (orgName) {
        requireContext().getString(R.string.org_1) -> requireContext().getString(R.string.config_file_1)
        requireContext().getString(R.string.org_2) -> requireContext().getString(R.string.config_file_2)
        else -> ""
    }

    class MarginItemDecoration(private val spaceHeight: Int) : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            with(outRect) {
                if (parent.getChildAdapterPosition(view) == 0) {
                    top = spaceHeight
                }
                left = 0
                right = 0
                bottom = spaceHeight
            }
        }
    }
}
