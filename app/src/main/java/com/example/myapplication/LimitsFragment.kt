package com.example.myapplication

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class LimitsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AppLimitAdapter
    private val appLimits = mutableListOf<AppLimit>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.fragment_limits, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewLimits)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // ✅ THIS is where you add the new adapter version
        adapter = AppLimitAdapter(appLimits) { selectedItem, position ->
            showEditDeleteDialog(selectedItem, position)
        }

        recyclerView.adapter = adapter

        return view
    }

    fun addNewLimit(limit: AppLimit) {
        appLimits.add(limit)
        adapter.notifyItemInserted(appLimits.size - 1)
    }

    // =========================
    // Edit / Delete Dialog
    // =========================

    private fun showEditDeleteDialog(item: AppLimit, position: Int) {

        val options = arrayOf("Edit Time", "Delete")

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(item.appName)

        builder.setItems(options) { _, which ->

            when (which) {
                0 -> showEditTimeDialog(item, position)
                1 -> {
                    appLimits.removeAt(position)
                    adapter.notifyItemRemoved(position)
                }
            }
        }

        builder.show()
    }

    private fun showEditTimeDialog(item: AppLimit, position: Int) {

        val input = EditText(requireContext())
        input.inputType = InputType.TYPE_CLASS_NUMBER
        input.setText(item.time.replace(" minutes", ""))

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Edit time for ${item.appName}")
        builder.setView(input)

        builder.setPositiveButton("Update") { _, _ ->
            val newTime = input.text.toString()
            if (newTime.isNotEmpty()) {
                item.time = "$newTime minutes"
                adapter.notifyItemChanged(position)
            }
        }

        builder.setNegativeButton("Cancel", null)

        builder.show()
    }
}
