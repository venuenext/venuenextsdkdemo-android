package com.venuenext.venuenextsdkdemo.settings

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.venuenext.venuenextsdkdemo.R
import com.venuenext.venuenextsdkdemo.databinding.ItemSettingSectionBinding


class SettingsSectionAdapter(
    private val settingsSections: List<SettingsSection>,
    private val listener: OnSettingClickListener
) : RecyclerView.Adapter<SettingsSectionAdapter.SectionViewHolder>() {

    override fun getItemCount(): Int = settingsSections.size
    override fun getItemViewType(position: Int) = R.layout.item_setting_section

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SectionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return SectionViewHolder(view)
    }

    override fun onBindViewHolder(holder: SectionViewHolder, position: Int) {
        val settingsSection = settingsSections[position]
        val binding = ItemSettingSectionBinding.bind(holder.itemView)

        val sectionName = settingsSection.sectionName
        if (sectionName.isBlank()) {
            binding.sectionTextView.visibility = View.GONE
        } else {
            binding.sectionTextView.text = sectionName
        }

        val adapter = SettingsAdapter(settingsSection.settingsIds, listener)
        binding.settingsRecyclerView.adapter = adapter
    }

    class SectionViewHolder(view: View) : RecyclerView.ViewHolder(view)
}
