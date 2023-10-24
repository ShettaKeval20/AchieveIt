package com.example.achieveit

import android.annotation.SuppressLint
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import android.content.ContentValues
import android.database.sqlite.SQLiteException
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class TaskFragment : Fragment() {

    private lateinit var dbHelper: TasksDatabaseHelper
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var tasksRecyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_task, container, false)

        dbHelper = TasksDatabaseHelper(requireContext())
        tasksRecyclerView = view.findViewById(R.id.tasksRecyclerView)

        // Fetch all tasks from the database
        val allTasks = dbHelper.getAllTasks()

        // Initialize and set up your RecyclerView Adapter
        taskAdapter = TaskAdapter(allTasks)
        tasksRecyclerView.adapter = taskAdapter
        tasksRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        val addButton = view.findViewById<Button>(R.id.addButton)
        addButton.setOnClickListener {
            val customDialogFragment = CustomDialogFragment()
            customDialogFragment.show(parentFragmentManager, "CustomDialog")
        }

        return view
    }

    override fun onResume() {
        super.onResume()
    }
    // Define your database helper class
    class TasksDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

        override fun onCreate(db: SQLiteDatabase) {
            db.execSQL(SQL_CREATE_ENTRIES)
        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            //db.execSQL(SQL_DELETE_ENTRIES)
            onCreate(db)
        }

        fun insertTask(task: Task): Long {
            val db = writableDatabase
            val values = ContentValues().apply {
                put(COLUMN_TASK_NAME, task.taskName)
                put(COLUMN_DESCRIPTION, task.description)
                put(COLUMN_IS_ACTIVE, if (task.isActive) 1 else 0)
                put(COLUMN_HAS_TIMER, if (task.hasTimer) 1 else 0)
                put(COLUMN_SHOW_PIE_CHART, if (task.showPieChart) 1 else 0)
                put(COLUMN_NOTE, task.note)
            }
            val result = db.insert(TABLE_NAME, null, values)
            db.close()
            return result
        }

        @SuppressLint("Range")
        fun getAllTasks(): ArrayList<Task> {
            val taskList = ArrayList<Task>()
            val selectQuery = "SELECT * FROM $TABLE_NAME"
            val db = this.readableDatabase
            Log.d("database", "database open")

            return try {
                db.rawQuery(selectQuery, null).use { cursor ->
                    while (cursor.moveToNext()) {
                        val taskName = cursor.getString(cursor.getColumnIndex(COLUMN_TASK_NAME))
                        val description = cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION))
                        val isActive = cursor.getInt(cursor.getColumnIndex(COLUMN_IS_ACTIVE)) == 1
                        val hasTimer = cursor.getInt(cursor.getColumnIndex(COLUMN_HAS_TIMER)) == 1
                        val showPieChart = cursor.getInt(cursor.getColumnIndex(COLUMN_SHOW_PIE_CHART)) == 1
                        val note = cursor.getString(cursor.getColumnIndex(COLUMN_NOTE))

                        val task = Task(taskName, description, isActive, hasTimer, showPieChart, note)
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


        companion object {
            const val DATABASE_VERSION = 1
            const val DATABASE_NAME = "Tasks.db"
            const val TABLE_NAME = "Tasks"
            const val COLUMN_TASK_NAME = "taskName"
            const val COLUMN_DESCRIPTION = "description"
            const val COLUMN_IS_ACTIVE = "active"
            const val COLUMN_HAS_TIMER = "timer"
            const val COLUMN_SHOW_PIE_CHART = "pieChart"
            const val COLUMN_NOTE = "note"
            private const val SQL_CREATE_ENTRIES = "CREATE TABLE $TABLE_NAME ($COLUMN_TASK_NAME TEXT, $COLUMN_DESCRIPTION TEXT, $COLUMN_IS_ACTIVE INTEGER, $COLUMN_HAS_TIMER INTEGER, $COLUMN_SHOW_PIE_CHART INTEGER, $COLUMN_NOTE TEXT)"
            //private const val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS $TABLE_NAME"
        }
    }
}
