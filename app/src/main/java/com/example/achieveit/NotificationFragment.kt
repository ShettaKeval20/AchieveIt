package com.example.achieveit

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.FragmentTransaction
import com.faskn.lib.ClickablePieChart
import com.faskn.lib.Slice
import com.faskn.lib.buildChart
import com.faskn.lib.legend.LegendAdapter
import com.faskn.lib.legend.LegendItemViewHolder
import kotlin.random.Random

class NotificationFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_notification, container, false)


        val childFragment: Fragment = TaskFragment("Reports")
        val transaction: FragmentTransaction = childFragmentManager.beginTransaction()
        transaction.replace(R.id.child_fragment_container, childFragment).commit()
        return view
    }


}