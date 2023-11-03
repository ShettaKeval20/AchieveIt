package com.example.achieveit

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.database.sqlite.SQLiteException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class TasksDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "Tasks.db"
        const val TABLE_NAME = "Tasks"
        const val COLUMN_TASK_NAME = "task_name"
        const val COLUMN_DESCRIPTION = "description"
        const val COLUMN_IS_ACTIVE = "is_active"
        const val COLUMN_DATE = "Date"
        const val COLUMN_HAS_TIMER = "has_timer"
        const val COLUMN_SHOW_PIE_CHART = "show_pie_chart"
        const val COLUMN_NOTE = "note"

        private const val SQL_CREATE_ENTRIES = "CREATE TABLE $TABLE_NAME ($COLUMN_TASK_NAME TEXT, $COLUMN_DESCRIPTION TEXT, $COLUMN_IS_ACTIVE INTEGER,  $COLUMN_HAS_TIMER INTEGER, $COLUMN_SHOW_PIE_CHART INTEGER, $COLUMN_DATE DATE)"
        private const val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS $TABLE_NAME"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = "CREATE TABLE $TABLE_NAME (" +
                "$COLUMN_TASK_NAME TEXT," +
                "$COLUMN_DESCRIPTION TEXT," +
                "$COLUMN_IS_ACTIVE INTEGER," +
                "$COLUMN_DATE DATE, " +
                "$COLUMN_HAS_TIMER INTEGER," +
                "$COLUMN_SHOW_PIE_CHART INTEGER," +
                "$COLUMN_NOTE TEXT" +
                ")"
        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun addTask(task: Task) {
        val db = this.writableDatabase
        try {
            val values = ContentValues().apply {
                put(COLUMN_TASK_NAME, task.taskName)
                put(COLUMN_DESCRIPTION, task.description)
                put(COLUMN_IS_ACTIVE, if (task.isActive) 1 else 0)
                put(COLUMN_HAS_TIMER, if (task.hasTimer) 1 else 0)
                put(COLUMN_SHOW_PIE_CHART, if (task.showPieChart) 1 else 0)
                put(COLUMN_NOTE, task.note)
                put(COLUMN_DATE, formatDate(task.Date))
            }
            db.insert(TABLE_NAME, null, values)
        } catch (e: SQLiteException) {
            // Handle the exception here
            e.printStackTrace()
        } finally {
            db.close()
        }





        //        delete Task
        fun deleteTask(task: Task): Int {
            val db = this.writableDatabase
            return try {
                val whereClause = "$COLUMN_TASK_NAME = ?"
                val whereArgs = arrayOf(task.taskName)
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
//        finally {
////            db.close()
////        }
    }



    private fun dateToCalendarFormat(dueDate: Calendar): String {
        val day = dueDate.get(Calendar.DAY_OF_MONTH)
        val month = dueDate.get(Calendar.MONTH) + 1 // Months are 0-based, so add 1
        val year = dueDate.get(Calendar.YEAR)

        return String.format(Locale.getDefault(), "%02d/%02d/%04d", month, day, year)
    }

    private fun formatDate(dueDate: Date): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(dueDate.time)
    }

    private fun parseDate(dateStr: String): Date {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        try {
            return dateFormat.parse(dateStr) ?: Date()
        } catch (e: ParseException) {
            e.printStackTrace()
            return Date()
        }
    }


}

