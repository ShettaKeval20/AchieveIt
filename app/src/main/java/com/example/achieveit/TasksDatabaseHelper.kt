package com.example.achieveit

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.database.sqlite.SQLiteException
import android.util.Log

class TasksDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "Tasks.db"
        const val TABLE_NAME = "Tasks"
        const val COLUMN_TASK_NAME = "task_name"
        const val COLUMN_DESCRIPTION = "description"
        const val COLUMN_IS_ACTIVE = "is_active"
        const val COLUMN_HAS_TIMER = "has_timer"
        const val COLUMN_SHOW_PIE_CHART = "show_pie_chart"
        const val COLUMN_NOTE = "note"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = "CREATE TABLE $TABLE_NAME (" +
                "$COLUMN_TASK_NAME TEXT," +
                "$COLUMN_DESCRIPTION TEXT," +
                "$COLUMN_IS_ACTIVE INTEGER," +
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
            }
            db.insert(TABLE_NAME, null, values)
        } catch (e: SQLiteException) {
            // Handle the exception here
            e.printStackTrace()
      }
//        finally {
////            db.close()
////        }
    }
}

