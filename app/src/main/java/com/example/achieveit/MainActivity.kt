package com.example.achieveit

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.achieveit.databinding.ActivityMainBinding
import android.widget.Button
import android.widget.RelativeLayout
import android.view.View

class MainActivity : AppCompatActivity(), AddTaskLayout.OnFragmentInteractionListener {

    private lateinit var binding: ActivityMainBinding

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(HomeFragment())



        binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> replaceFragment(HomeFragment())
                R.id.task -> replaceFragment(TaskFragment())
                R.id.notifications -> replaceFragment(NotificationFragment())
                else -> {
                }
            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, fragment)
        fragmentTransaction.commit()
    }

    override fun onCloseFragment() {
        val fragment = supportFragmentManager.findFragmentById(R.id.frameLayout)
        if (fragment is AddTaskLayout) {
            supportFragmentManager.beginTransaction().remove(fragment).commit()
        }
    }
}
