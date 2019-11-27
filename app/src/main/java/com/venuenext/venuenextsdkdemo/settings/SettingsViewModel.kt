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
                listOf(R.string.settings_logout_tm)
            )

            return@lazy mutableListOf(menusSection, ordersSection, walletSection, demoSection)
        }
    }
}
