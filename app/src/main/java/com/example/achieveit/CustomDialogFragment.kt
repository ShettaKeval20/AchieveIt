package com.example.achieveit

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CustomDialogFragment : DialogFragment() {

    private lateinit var dbHelper: TaskFragment.TasksDatabaseHelper // Import the DBHelper from TaskFragment
    private var dueDate: Calendar = Calendar.getInstance()
    private lateinit var editTextDueDate: EditText

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
        val toggleButtonActive = view.findViewById<Switch>(R.id.toggleButtonActive)
        val toggleButtonTimer = view.findViewById<Switch>(R.id.toggleButtonTimer)
        val toggleButtonPieChart = view.findViewById<Switch>(R.id.toggleButtonPieChart)
        val saveButton = view.findViewById<Button>(R.id.saveButton)
        val cancelButton = view.findViewById<Button>(R.id.cancelButton)
        editTextDueDate = view.findViewById(R.id.editDate)

        editTextDueDate.setOnClickListener {
            showDatePickerDialog()
        }

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
                val task = Task(taskName, description, isActive,  hasTimer, showPieChart, note)
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

    private fun showDatePickerDialog() {
        val year = dueDate.get(Calendar.YEAR)
        val month = dueDate.get(Calendar.MONTH)
        val day = dueDate.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                // Update the selected due date
                dueDate.set(year, month, dayOfMonth)
                // Format the date as a string in the desired format
                val formattedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(dueDate.time)
                // Update the due date EditText to display the selected date
                editTextDueDate.setText(formattedDate)
            },
            year, month, day
        )
        datePickerDialog.show()
    }
}
