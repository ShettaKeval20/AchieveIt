package com.example.achieveit

import android.icu.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Date
import java.util.Locale


data class Task(
    var taskName: String,
    var description: String,
    var isActive: Boolean,
    var dueDate: String,
    var hasTimer: Boolean,
    var showPieChart: Boolean,
    var note: String
)

