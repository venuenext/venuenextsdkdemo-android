package com.venuenext.venuenextsdkdemo.settings

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.recyclerview.widget.RecyclerView
import com.venuenext.venuenextsdkdemo.R
import com.venuenext.venuenextsdkdemo.databinding.ItemSettingBinding

typealias OnSettingClickListener = (Int) -> Unit

class SettingsAdapter(
    private val settings: List<Int>,
    private val listener: OnSettingClickListener
) : RecyclerView.Adapter<SettingsAdapter.MenuItemViewHolder>() {

    override fun getItemViewType(position: Int) = R.layout.item_setting

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return MenuItemViewHolder(view)
    }

    override fun getItemCount() = settings.size

    override fun onBindViewHolder(holder: MenuItemViewHolder, position: Int) {
        val setting = settings[position]
        holder.bind(setting, listener)
    }

    class MenuItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(@StringRes setting: Int, listener: OnSettingClickListener) {
            val binding = ItemSettingBinding.bind(itemView)

            binding.settingTextView.text = itemView.resources.getString(setting)
            binding.root.setOnClickListener { listener(setting) }
        }
    }
}
