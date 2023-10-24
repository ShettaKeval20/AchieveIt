package com.example.achieveit

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class AddTaskLayout : Fragment() {

    interface OnFragmentInteractionListener {
        fun onCloseFragment()
    }

    private var listener: OnFragmentInteractionListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.customdialog_layout, container, false)


        return view
    }

    fun setOnFragmentInteractionListener(listener: OnFragmentInteractionListener) {
        this.listener = listener
    }
}
