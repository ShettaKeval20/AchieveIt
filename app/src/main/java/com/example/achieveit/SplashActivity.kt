package com.example.achieveit

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatDelegate

class SplashActivity : AppCompatActivity() {

    private lateinit var splash: ImageView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        splash = findViewById(R.id.splash);

        splash.alpha = 0f

        splash.animate().setDuration(1500).alpha(1f).withEndAction {

            val intent = Intent(this, MainActivity::class.java)
            startActivities(arrayOf(intent))
            finish()
        }
    }
}