package com.example.achieveit

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TaskAdapter(private val taskList: ArrayList<Task>) :
    RecyclerView.Adapter<TaskAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.taskitems, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val task = taskList[position]
        holder.taskName.text = task.taskName
        holder.description.text = task.description
        holder.active.text = "Active: ${task.isActive}"
        holder.timer.text = "Timer: ${task.hasTimer}"
        holder.pieChart.text = "Explode Piechart: ${task.showPieChart}"
        holder.note.text = task.note
        // bind other data to UI elements
    }

    override fun getItemCount(): Int {
        return taskList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val taskName: TextView = itemView.findViewById(R.id.taskNameTextView)
        val description: TextView = itemView.findViewById(R.id.descriptionTextView)
        val active: TextView = itemView.findViewById(R.id.activeTextView)
        val timer: TextView = itemView.findViewById(R.id.timerTextView)
        val pieChart: TextView = itemView.findViewById(R.id.pieChartTextView)
        val note: TextView = itemView.findViewById(R.id.noteTextView)
        // define other UI elements here
    }
}
