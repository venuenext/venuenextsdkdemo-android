package com.venuenext.venuenextsdkdemo.settings

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.venuenext.venuenextsdkdemo.R
import com.venuenext.venuenextsdkdemo.databinding.FragmentSettingsBinding

private const val SPACE_HEIGHT = 18

class SettingsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentSettingsBinding.inflate(inflater)

        val viewModel =
            ViewModelProviders.of(this@SettingsFragment).get(SettingsViewModel::class.java)

        val adapter = SettingsSectionAdapter(viewModel.settingsSections, ::onItemClicked)
        binding.recyclerView.apply {
            addItemDecoration(MarginItemDecoration(SPACE_HEIGHT))
            setHasFixedSize(true)
            this.adapter = adapter
        }

        return binding.root
    }

    private fun onItemClicked(@StringRes id: Int) {
        val navId = when (id) {
            // Revenue Center Flows
            R.string.settings_display_all_rvc -> R.id.action_all_stands_flow
            R.string.settings_display_f_and_b_rvc -> R.id.action_to_food_bev_flow
            R.string.settings_display_merchandise_rvc -> R.id.action_to_merchandise_flow
            R.string.settings_display_marketplace_experiences -> R.id.action_to_experience_flow

            // Display Orders
            R.string.settings_launch_order_history -> R.id.action_to_my_orders_flow

            // Display Wallet
            R.string.settings_launch_wallet -> R.id.action_to_wallet_flow

            // Demo Specific Settings
            R.string.settings_launch_tm -> R.id.action_to_ticketing_flow
            else -> null
        }

        navId?.let {
            findNavController().navigate(
                R.id.action_to_vn_activity_flow,
                bundleOf("navId" to navId)
            )
        }
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
