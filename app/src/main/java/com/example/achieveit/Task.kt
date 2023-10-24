package com.example.achieveit

data class Task(
    var taskName: String,
    var description: String,
    var isActive: Boolean,
    var hasTimer: Boolean,
    var showPieChart: Boolean,
    var note: String
)

