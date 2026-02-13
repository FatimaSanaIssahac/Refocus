package com.example.myapplication

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.ItemInstalledAppBinding

class InstalledAppAdapter(
    private val list: List<InstalledApp>,
    private val onClick: (InstalledApp) -> Unit
) : RecyclerView.Adapter<InstalledAppAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemInstalledAppBinding)
        : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemInstalledAppBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val app = list[position]
        holder.binding.tvAppName.text = app.appName
        holder.binding.ivIcon.setImageDrawable(app.icon)

        holder.binding.root.setOnClickListener {
            onClick(app)
        }
    }

    override fun getItemCount() = list.size
}
