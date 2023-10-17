package com.example.achieveit

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import java.util.Calendar

class HomeFragment : Fragment() {

        private lateinit var wishView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view =  inflater.inflate(R.layout.fragment_home, container, false)

        wishView = view.findViewById(R.id.wishView)

        val currentTime = Calendar.getInstance()
        val currentHour = currentTime.get(Calendar.HOUR_OF_DAY)

        val greeting = getGreeting(currentHour)

        wishView.text = greeting

        return view
    }

        private fun getGreeting(hour: Int): String {
        return when (hour) {
            in 6..11 -> "Good Morning"
            in 12..16 -> "Good Afternoon"
            in 17..21 -> "Good Evening"
            else -> "Good Night"
        }
    }
}