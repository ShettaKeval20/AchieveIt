package com.example.achieveit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ToggleButton
import android.widget.Toast
import androidx.fragment.app.DialogFragment

class CustomDialogFragment : DialogFragment() {

    private lateinit var dbHelper: TaskFragment.TasksDatabaseHelper // Import the DBHelper from TaskFragment

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.customdialog_layout, container, false)

        dbHelper = TaskFragment.TasksDatabaseHelper(requireContext()) // Initialize the DBHelper

        // Find your views
        val editTextTaskName = view.findViewById<EditText>(R.id.editTextTaskName)
        val editTextDescription = view.findViewById<EditText>(R.id.editTextDescription)
        val editTextNote = view.findViewById<EditText>(R.id.editTextNote)
        val toggleButtonActive = view.findViewById<ToggleButton>(R.id.toggleButtonActive)
        val toggleButtonTimer = view.findViewById<ToggleButton>(R.id.toggleButtonTimer)
        val toggleButtonPieChart = view.findViewById<ToggleButton>(R.id.toggleButtonPieChart)
        val saveButton = view.findViewById<Button>(R.id.saveButton)
        val cancelButton = view.findViewById<Button>(R.id.cancelButton)

        // Set click listener for save button
        saveButton.setOnClickListener {
            val taskName = editTextTaskName.text.toString()
            val description = editTextDescription.text.toString()
            val note = editTextNote.text.toString()

            if (taskName.isEmpty() || description.isEmpty() || note.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill the details", Toast.LENGTH_SHORT).show()
            } else {
                val isActive = toggleButtonActive.isChecked
                val hasTimer = toggleButtonTimer.isChecked
                val showPieChart = toggleButtonPieChart.isChecked

                // Add your data to the database
                val task = Task(taskName, description, isActive, hasTimer, showPieChart, note)
                val isInserted = dbHelper.insertTask(task)
                if (isInserted != -1L) {
                    Toast.makeText(requireContext(), "Data saved successfully", Toast.LENGTH_SHORT).show()
                }

                // Close the dialog
                dialog?.dismiss()
            }
        }

        // Set click listener for cancel button
        cancelButton.setOnClickListener {
            // Close the dialog without adding anything
            dialog?.dismiss()
        }

        return view
    }
}
