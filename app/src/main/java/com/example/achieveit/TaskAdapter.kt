package com.example.achieveit

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.media.RingtoneManager
import android.os.Build
import android.provider.CalendarContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone


class TaskAdapter(
    private val taskList: ArrayList<Task>,
    private val dbHelper: TaskFragment.TasksDatabaseHelper,
    private val taskTitle: String,
    private val onItemClick: (Task) -> Unit
) :
    RecyclerView.Adapter<TaskAdapter.ViewHolder>() {

    var context: Context? = null
    val callbackId = 42
    var reqCode = 1
    lateinit var intent : Intent
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.taskitems, parent, false)
        return ViewHolder(view)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        context = recyclerView.getContext()
        intent = Intent(context, MainActivity::class.java)
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

        val dueDate: Calendar = Calendar.getInstance()
        /*dueDate.timeInMillis = task.date.toLong()
        val formattedDate =
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(dueDate.time)
        // Update the due date EditText to display the selected date*/
        holder.date.text = task.date

        holder.reminderTextView.setOnClickListener {
           //checkPermission(task,callbackId, android.Manifest.permission.READ_CALENDAR, android.Manifest.permission.WRITE_CALENDAR)
            val intent = Intent(Intent.ACTION_EDIT)
            intent.type = "vnd.android.cursor.item/event"
            intent.putExtra("title", task.taskName)
            intent.putExtra("description", task.description)
            context?.startActivity(intent)
        }

        if (task.isCompleted)
        {
            holder.completeTextView.visibility = View.GONE
            holder.reminderTextView.visibility = GONE
        }
        else
        {
            holder.completeTextView.visibility = View.VISIBLE
            holder.reminderTextView.visibility = VISIBLE

        }

        if(taskTitle=="")
        {
            holder.completeTextView.visibility = View.VISIBLE
            if (task.isCompleted)
                holder.completeTextView.visibility = View.GONE
            else
                holder.completeTextView.visibility = View.VISIBLE
        }
        else
            holder.completeTextView.visibility = View.GONE

        holder.notificationTextView.setOnClickListener {
            context?.let { it1 ->
                showNotification(
                    it1,
                    task.taskName,
                    task.description,intent,reqCode)
            }
        }

        holder.completeTextView.setOnClickListener {
            task.isCompleted=true
            dbHelper.updateTask(task,task.id)
            notifyDataSetChanged()
        }

        holder.itemView.setOnClickListener {
            onItemClick(task)
        }

        if(taskTitle=="Reports"){
            holder.active.visibility = GONE
            holder.timer.visibility = GONE
            holder.pieChart.visibility = GONE
            holder.note.visibility = GONE
            holder.date.visibility = GONE
        }
    }

    fun updateTasks(newTaskList: List<Task>) {
        taskList.clear()
        taskList.addAll(newTaskList)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return taskList.size
    }

    fun showNotification(
        context: Context,
        title: String?,
        message: String?,
        intent: Intent?,
        reqCode: Int
    ) {
        val pendingIntent =
            PendingIntent.getActivity(context, reqCode, intent,
                PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)
        val CHANNEL_ID = "channel_name" // The id of the channel.
        val notificationBuilder: NotificationCompat.Builder =
            NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentIntent(pendingIntent)
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = "Channel Name" // The user-visible name of the channel.
            val importance = NotificationManager.IMPORTANCE_HIGH
            val mChannel = NotificationChannel(CHANNEL_ID, name, importance)
            notificationManager.createNotificationChannel(mChannel)
        }
        notificationManager.notify(reqCode, notificationBuilder.build()) // 0 is the request code, it should be unique id
        Log.d("showNotification", "showNotification: $reqCode")
    }

    private fun checkPermission(task: Task, callbackId: Int, vararg permissionsId: String) {
        var permissions = true
        for (p in permissionsId) {
            permissions =
                permissions && ContextCompat.checkSelfPermission(context!!, p) == PERMISSION_GRANTED
        }
        if (!permissions) ActivityCompat.requestPermissions(context as Activity, permissionsId, callbackId)
        else
            addEventToCalendar(task.date, "12:00", 5, task.taskName, task.description)

    }

    fun addEventToCalendar(date: String, time: String, notifyTime: Int = 0, title: String, note: String) {
        // Make sure to which calender you want to add event
        val calID: Long = 1 // primary calendar
        val beginTime = Calendar.getInstance()
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH)
        beginTime.time = simpleDateFormat.parse("$date $time")
        val startMillis = beginTime.timeInMillis
        val endMillis = startMillis + (30 * 60 * 1000) //in this snippet the endtime is 30 minutes after the start time

        val cr =    context?.contentResolver
        val values = ContentValues()
        values.put(CalendarContract.Events.DTSTART, startMillis)
        values.put(CalendarContract.Events.DTEND, endMillis)
        values.put(CalendarContract.Events.TITLE, title)
        values.put(CalendarContract.Events.DESCRIPTION, note)
        values.put(CalendarContract.Events.CALENDAR_ID, calID)
        values.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().id)
        values.put(CalendarContract.Events.HAS_ALARM, 1)
        val uri = cr?.insert(CalendarContract.Events.CONTENT_URI, values) //insert the event into the user's calendar here

        // get the event ID that is the last element in the Uri
        val eventID = uri!!.lastPathSegment!!.toLong()
        setReminderForEvent(eventID, notifyTime)
    }

    private fun setReminderForEvent(eventID: Long, notifyTime: Int) {
        val otherValues = ContentValues().apply {
            put(CalendarContract.Reminders.MINUTES, notifyTime)
            put(CalendarContract.Reminders.MINUTES, 0)
            put(CalendarContract.Reminders.EVENT_ID, eventID)
            put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT)
        }
        context?.contentResolver?.insert(CalendarContract.Reminders.CONTENT_URI, otherValues)
        // outputs a URI
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val taskName: TextView = itemView.findViewById(R.id.taskNameTextView)
        val description: TextView = itemView.findViewById(R.id.descriptionTextView)
        val active: TextView = itemView.findViewById(R.id.activeTextView)
        val completeTextView: TextView = itemView.findViewById(R.id.completeTextView)
        val timer: TextView = itemView.findViewById(R.id.timerTextView)
        val pieChart: TextView = itemView.findViewById(R.id.pieChartTextView)
        val note: TextView = itemView.findViewById(R.id.noteTextView)
        val date: TextView = itemView.findViewById(R.id.dateTextView)
        val reminderTextView: TextView = itemView.findViewById(R.id.reminderTextView)
        val notificationTextView: TextView = itemView.findViewById(R.id.notificationTextView)
        // define other UI elements here
    }
}
