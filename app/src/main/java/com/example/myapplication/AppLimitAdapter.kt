package com.example.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AppLimitAdapter(
    private val appLimits: MutableList<AppLimit>,
    private val onItemClick: (AppLimit, Int) -> Unit
) : RecyclerView.Adapter<AppLimitAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvAppName: TextView = itemView.findViewById(R.id.tvAppName)
        val tvTime: TextView = itemView.findViewById(R.id.tvTime)

        init {
            itemView.setOnClickListener {
                onItemClick(appLimits[adapterPosition], adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_limit, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = appLimits[position]
        holder.tvAppName.text = item.appName
        holder.tvTime.text = "${item.limitMinutes} minutes"
    }

    override fun getItemCount(): Int = appLimits.size
}
