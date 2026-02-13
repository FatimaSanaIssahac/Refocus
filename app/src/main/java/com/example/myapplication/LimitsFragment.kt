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

        adapter = AppLimitAdapter(appLimits) { selectedItem, position ->
            showEditDeleteDialog(selectedItem, position)
        }

        recyclerView.adapter = adapter

        // Load saved limits from SharedPreferences
        appLimits.addAll(LimitsStorage.getLimits(requireContext()))
        adapter.notifyDataSetChanged()

        return view
    }

    fun addNewLimit(limit: AppLimit) {
        appLimits.add(limit)
        adapter.notifyItemInserted(appLimits.size - 1)
    }

    fun getLimits(): List<AppLimit> {
        return appLimits
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
                    // Persist changes
                    LimitsStorage.saveLimits(requireContext(), appLimits)
                }
            }
        }

        builder.show()
    }

    private fun showEditTimeDialog(item: AppLimit, position: Int) {

        val input = EditText(requireContext())
        input.inputType = InputType.TYPE_CLASS_NUMBER
        input.setText(item.limitMinutes.toString())

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Edit time for ${item.appName}")
        builder.setView(input)

        builder.setPositiveButton("Update") { _, _ ->
            val newTimeText = input.text.toString()

            if (newTimeText.isNotEmpty()) {

                val newMinutes = newTimeText.toInt()

                // Replace entire object (since properties are val)
                appLimits[position] = AppLimit(
                    appName = item.appName,
                    packageName = item.packageName,
                    limitMinutes = newMinutes
                )

                adapter.notifyItemChanged(position)
                // Persist changes
                LimitsStorage.saveLimits(requireContext(), appLimits)
            }
        }

        builder.setNegativeButton("Cancel", null)

        builder.show()
    }
}
