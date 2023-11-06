package com.example.achieveit

data class Task(
    var taskName: String,
    var description: String,
    var isActive: Boolean,
    var isCompleted: Boolean,
    var hasTimer: Boolean,
    var showPieChart: Boolean,
    var note: String,
    var id:String,
    var date: String
)

