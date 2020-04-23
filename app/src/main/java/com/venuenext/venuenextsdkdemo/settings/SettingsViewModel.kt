package com.venuenext.venuenextsdkdemo.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.venuenext.venuenextsdkdemo.R

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    val settingsSections: List<SettingsSection> by lazy {
        with(application.resources) {
            val menusSection = SettingsSection(
                getString(R.string.settings_section_menus),
                listOf(
                    R.string.settings_display_f_and_b_rvc,
                    R.string.settings_display_merchandise_rvc,
                    R.string.settings_display_marketplace_experiences,
                    R.string.settings_display_all_rvc
                )
            )

            val ordersSection = SettingsSection(
                getString(R.string.settings_section_orders),
                listOf(R.string.settings_launch_order_history)
            )

            val walletSection = SettingsSection(
                getString(R.string.settings_section_wallet),
                listOf(R.string.settings_launch_wallet)
            )

            val demoSection = SettingsSection(
                getString(R.string.settings_section_demo),
                listOf(
                    R.string.settings_logout_tm,
                    R.string.new_jwt_or_org
                )
            )
            
            val directLaunchSection = SettingsSection(
                getString(R.string.settings_section_direct_launch),
                listOf(
                    R.string.settings_launch_to_food_menu,
                    R.string.settings_launch_to_experience_menu,
                    R.string.settings_launch_to_experience_menu_event_selected,
                    R.string.settings_launch_to_experience_detail,
                    R.string.settings_show_purchased_experience_receipt,
                    R.string.settings_show_awarded_or_transferred_experience_receipt
                )
            )

            return@lazy mutableListOf(
                menusSection,
                ordersSection,
                walletSection,
                demoSection,
                directLaunchSection
            )
        }
    }
}
