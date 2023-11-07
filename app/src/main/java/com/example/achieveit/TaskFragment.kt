package com.example.achieveit

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import android.content.ContentValues
import android.database.sqlite.SQLiteException
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class TaskFragment(val taskTitle: String) : Fragment() {

    private lateinit var dbHelper: TasksDatabaseHelper
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var tasksRecyclerView: RecyclerView
    private lateinit var selectDateButton: TextView
    private lateinit var addButton: TextView
    private lateinit var noDataFoundLayout: LottieAnimationView
    private var dueDate: Calendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_task, container, false)

        dbHelper = TasksDatabaseHelper(requireContext())
        tasksRecyclerView = view.findViewById(R.id.tasksRecyclerView)
        addButton = view.findViewById(R.id.addButton)
        selectDateButton = view.findViewById(R.id.SelectDate)
        noDataFoundLayout = view.findViewById(R.id.noDataFoundLayout)
        setView()
        return view
    }

    override fun onResume() {
        setView()
        super.onResume()
    }

    fun setView() {
        val allTasks = dbHelper.getAllTasks(taskTitle)
        tasksRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        if(taskTitle=="Calender")
        {
            selectDateButton.visibility = View.VISIBLE
            // Format the date as a string in the desired format
            val formattedDate =
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(dueDate.time)
            // Update the due date EditText to display the selected date
            selectDateButton.setText(formattedDate)
            val allTasks = dbHelper.getData(selectDateButton.text.toString())
            if(allTasks.isEmpty()) {
                tasksRecyclerView.visibility = View.GONE
                noDataFoundLayout.visibility = View.VISIBLE
            }
            else
            {
                noDataFoundLayout.visibility = View.GONE
                tasksRecyclerView.visibility = View.VISIBLE
                taskAdapter = TaskAdapter(allTasks, dbHelper, taskTitle) { task ->
                    showOptionsDialog(task)
                }
                tasksRecyclerView.adapter = taskAdapter
            }
        }
        else
        {
            selectDateButton.visibility = View.GONE
            taskAdapter = TaskAdapter(allTasks, dbHelper,taskTitle) { task ->
                showOptionsDialog(task)
            }
            tasksRecyclerView.adapter = taskAdapter
            tasksRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        }

        selectDateButton.setOnClickListener {
            showDatePickerDialog()
        }

        if(allTasks.isEmpty()) {
            tasksRecyclerView.visibility = View.GONE
            noDataFoundLayout.visibility = View.VISIBLE
        }
        else
        {
            noDataFoundLayout.visibility = View.GONE
            tasksRecyclerView.visibility = View.VISIBLE
        }

        if(taskTitle=="")
            addButton.visibility = View.VISIBLE
        else
            addButton.visibility = View.GONE


        addButton.setOnClickListener {
            val customDialogFragment = CustomDialogFragment(this, 0, null)
            customDialogFragment.show(parentFragmentManager, "CustomDialog")
        }
    }

    private fun showOptionsDialog(task: Task) {
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Options")
            .setPositiveButton("Edit") { _, _ ->
                val customDialogFragment = CustomDialogFragment(this, 1, task)
                customDialogFragment.show(parentFragmentManager, "CustomDialog")
            }
            .setNegativeButton("Delete") { _, _ ->
                confirmAndDeleteTask(task as Task)
            }
            .setNeutralButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        dialog.show()
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
                dueDate.set(Calendar.HOUR_OF_DAY, 0)
                // Format the date as a string in the desired format
                val formattedDate =
                    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(dueDate.time)
                // Update the due date EditText to display the selected date
                selectDateButton.setText(formattedDate)
                val allTasks = dbHelper.getData(selectDateButton.text.toString())
                if(allTasks.isEmpty()) {
                    tasksRecyclerView.visibility = View.GONE
                    noDataFoundLayout.visibility = View.VISIBLE
                }
                else
                {
                    noDataFoundLayout.visibility = View.GONE
                    tasksRecyclerView.visibility = View.VISIBLE
                    taskAdapter = TaskAdapter(allTasks, dbHelper, taskTitle) { task ->
                        showOptionsDialog(task)
                    }
                    tasksRecyclerView.adapter = taskAdapter
                }


            },
            year, month, day
        )
        datePickerDialog.show()
    }

    private fun confirmAndDeleteTask(task: Task) {
        val confirmDialog = AlertDialog.Builder(requireContext())
            .setTitle("Confirm Delete?")
            .setMessage("Are you sure you want to delete this task?")
            .setPositiveButton("Yes") { _, _ ->
                val deletedRows = dbHelper.deleteTask(task)
                val allTasks = dbHelper.getAllTasks(taskTitle)
                taskAdapter.updateTasks(allTasks)
                setView()
//
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        confirmDialog.show()
    }

    // Define your database helper class
    class TasksDatabaseHelper(context: Context) :
        SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

        override fun onCreate(db: SQLiteDatabase) {
            db.execSQL(SQL_CREATE_ENTRIES)
        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            //db.execSQL(SQL_DELETE_ENTRIES)
            onCreate(db)
        }

        @SuppressLint("Range")
        fun getData(timeInMillis: String)
                : ArrayList<Task> {
            val taskList = ArrayList<Task>()
            val selectQuery = "SELECT * FROM $TABLE_NAME where $COLUMN_DUE_DATE = '$timeInMillis'"
            val db = this.readableDatabase
            Log.d("database", "database open")

            return try {
                db.rawQuery(selectQuery, null).use { cursor ->
                    while (cursor.moveToNext()) {
                        val taskName = cursor.getString(cursor.getColumnIndex(TasksDatabaseHelper.COLUMN_TASK_NAME))
                        val description =
                            cursor.getString(cursor.getColumnIndex(TasksDatabaseHelper.COLUMN_DESCRIPTION))
                        val isActive = cursor.getInt(cursor.getColumnIndex(TasksDatabaseHelper.COLUMN_IS_ACTIVE)) == 1
                        val isCompleted = cursor.getInt(cursor.getColumnIndex(TasksDatabaseHelper.COLUMN_IS_COMPLETED)) == 1
                        val hasTimer = cursor.getInt(cursor.getColumnIndex(TasksDatabaseHelper.COLUMN_HAS_TIMER)) == 1
                        val showPieChart =
                            cursor.getInt(cursor.getColumnIndex(TasksDatabaseHelper.COLUMN_SHOW_PIE_CHART)) == 1
                        val note = cursor.getString(cursor.getColumnIndex(TasksDatabaseHelper.COLUMN_NOTE))
                        val date = cursor.getString(cursor.getColumnIndex(COLUMN_DUE_DATE))
                        val id = cursor.getInt(cursor.getColumnIndex(TasksDatabaseHelper.COLUMN_ID))
                        val task = Task(
                            taskName, description, isActive,isCompleted, hasTimer, showPieChart, note,
                            id.toString(), date
                        )
                        taskList.add(task)
                    }
                }
                taskList
            } catch (e: SQLiteException) {
                // Handle the exception here
                e.printStackTrace()
                ArrayList()
            } finally {
                db.close()
            }
        }

        fun insertTask(task: Task): Long {
            val db = writableDatabase
            val values = ContentValues().apply {
                put(COLUMN_TASK_NAME, task.taskName)
                put(COLUMN_DESCRIPTION, task.description)
                put(COLUMN_IS_ACTIVE, if (task.isActive) 1 else 0)
                put(COLUMN_IS_COMPLETED, if (task.isCompleted) 1 else 0)
                put(COLUMN_DUE_DATE, task.date)
                put(COLUMN_HAS_TIMER, if (task.hasTimer) 1 else 0)
                put(COLUMN_SHOW_PIE_CHART, if (task.showPieChart) 1 else 0)
                put(COLUMN_NOTE, task.note)
            }
            val result = db.insert(TABLE_NAME, null, values)
            db.close()
            return result
        }

        fun updateTask(task: Task, id: String): Long {
            val db = writableDatabase
            val values = ContentValues().apply {
                put(COLUMN_TASK_NAME, task.taskName)
                put(COLUMN_DESCRIPTION, task.description)
                put(COLUMN_IS_ACTIVE, if (task.isActive) 1 else 0)
                put(COLUMN_IS_COMPLETED, if (task.isCompleted) 1 else 0)
                put(COLUMN_DUE_DATE, task.date)
                put(COLUMN_HAS_TIMER, if (task.hasTimer) 1 else 0)
                put(COLUMN_SHOW_PIE_CHART, if (task.showPieChart) 1 else 0)
                put(COLUMN_NOTE, task.note)
            }
            val result = db.update(TABLE_NAME, values, "$COLUMN_ID = ?", arrayOf(id))
            db.close()
            return result.toLong()
        }


        @SuppressLint("Range")
        fun getAllTasks(taskTitle: String): ArrayList<Task> {
            val taskList = ArrayList<Task>()
            val selectQuery = when (taskTitle) {
                "Active" -> "SELECT * FROM $TABLE_NAME where $COLUMN_IS_ACTIVE = 1"
                "Upcoming" -> "SELECT * FROM $TABLE_NAME where $COLUMN_IS_ACTIVE = 0 AND $COLUMN_IS_COMPLETED = 0"
                "Completed" -> "SELECT * FROM $TABLE_NAME where $COLUMN_IS_COMPLETED = 1"
                else -> "SELECT * FROM $TABLE_NAME"
            }
            val db = this.readableDatabase
            Log.d("database", "database open")

            return try {
                db.rawQuery(selectQuery, null).use { cursor ->
                    while (cursor.moveToNext()) {
                        val taskName = cursor.getString(cursor.getColumnIndex(COLUMN_TASK_NAME))
                        val description =
                            cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION))
                        val isActive = cursor.getInt(cursor.getColumnIndex(COLUMN_IS_ACTIVE)) == 1
                        val isCompleted = cursor.getInt(cursor.getColumnIndex(COLUMN_IS_COMPLETED)) == 1
                        val hasTimer = cursor.getInt(cursor.getColumnIndex(COLUMN_HAS_TIMER)) == 1
                        val showPieChart =
                            cursor.getInt(cursor.getColumnIndex(COLUMN_SHOW_PIE_CHART)) == 1
                        val note = cursor.getString(cursor.getColumnIndex(COLUMN_NOTE))
                        val date = cursor.getString(cursor.getColumnIndex(COLUMN_DUE_DATE))
                        val id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID))
                        val task = Task(
                            taskName, description, isActive,isCompleted, hasTimer, showPieChart, note,
                            id.toString(), date
                        )
                        taskList.add(task)
                    }
                }
                taskList
            } catch (e: SQLiteException) {
                // Handle the exception here
                e.printStackTrace()
                ArrayList()
            } finally {
                db.close()
            }
        }

//        private fun formatDate(date: Date): String {
//            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
//            return dateFormat.format(date)
//        }
//
//        private fun parseDate(dateStr: String): Calendar {
//            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
//            val calendar = Calendar.getInstance()
//
//            try {
//                val date = dateFormat.parse(dateStr)
//                if (date != null) {
//                    calendar.time = date
//                }
//            } catch (e: ParseException) {
//                // Handle parse errors if necessary
//            }
//
//            return calendar
//        }

        fun deleteTask(task: Task): Any {
            val db = this.writableDatabase
            return try {
                val whereClause = "$COLUMN_ID = ?"
                val whereArgs = arrayOf(task.id)
                val deletedRows = db.delete(TABLE_NAME, whereClause, whereArgs)
                deletedRows // Returns the number of rows deleted
            } catch (e: SQLiteException) {
                // Handle the exception here or return an error code
                e.printStackTrace()
                -1 // Return -1 to indicate an error
            } finally {
                db.close()
            }
        }


        companion object {
            const val DATABASE_VERSION = 1
            const val DATABASE_NAME = "Tasks.db"
            const val TABLE_NAME = "Tasks"
            const val COLUMN_ID = "taskID"
            const val COLUMN_TASK_NAME = "taskName"
            const val COLUMN_DESCRIPTION = "description"
            const val COLUMN_IS_ACTIVE = "active"
            const val COLUMN_IS_COMPLETED = "completed"
            const val COLUMN_DUE_DATE = "dueDate"
            const val COLUMN_HAS_TIMER = "timer"
            const val COLUMN_SHOW_PIE_CHART = "pieChart"
            const val COLUMN_NOTE = "note"
            private const val SQL_CREATE_ENTRIES =
                "CREATE TABLE $TABLE_NAME ($COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,$COLUMN_TASK_NAME TEXT, $COLUMN_DESCRIPTION TEXT, $COLUMN_IS_ACTIVE INTEGER,$COLUMN_IS_COMPLETED INTEGER, $COLUMN_DUE_DATE TEXT, $COLUMN_HAS_TIMER INTEGER, $COLUMN_SHOW_PIE_CHART INTEGER, $COLUMN_NOTE TEXT)"
            //private const val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS $TABLE_NAME"
        }
    }
}
