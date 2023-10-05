package com.example.achieveit

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.transition.TransitionManager
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class ForgotActivity : AppCompatActivity() {

    private var Reset: Button? = null
    private var GoBack: TextView? = null
    private var forgotEmail: EditText? = null
    private var mAuth: FirebaseAuth? = null

    private var emailIconContainer: ViewGroup? = null
    private var emailIcon: ImageView? = null
    private var emailIconText: TextView? = null
    private var progressBar: ProgressBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot)

        mAuth = FirebaseAuth.getInstance()

        forgotEmail = findViewById(R.id.forgot_email)
        forgotEmail!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                checkInputs()
            }

            override fun afterTextChanged(s: Editable) {

            }
        })

        Reset = findViewById(R.id.forgot_reset_button)
        Reset!!.setOnClickListener(View.OnClickListener {
            TransitionManager.beginDelayedTransition(emailIconContainer)
            emailIconText!!.visibility = View.GONE
            TransitionManager.beginDelayedTransition(emailIconContainer)
            emailIcon!!.visibility = View.VISIBLE
            progressBar!!.visibility = View.VISIBLE
            Reset!!.setEnabled(false)
            Reset!!.setTextColor(Color.argb(50, 0, 0, 0))
            mAuth!!.sendPasswordResetEmail(forgotEmail!!.getText().toString())
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val scaleAnimation = ScaleAnimation(
                            1f,
                            0f,
                            1f,
                            0f,
                            (emailIcon!!.width / 2).toFloat(),
                            (emailIcon!!.height / 2).toFloat()
                        )
                        scaleAnimation.duration = 100
                        scaleAnimation.interpolator = AccelerateInterpolator()
                        scaleAnimation.repeatMode = Animation.REVERSE
                        scaleAnimation.repeatCount = 1
                        scaleAnimation.setAnimationListener(object : Animation.AnimationListener {
                            override fun onAnimationStart(animation: Animation) {
                                emailIconText!!.text =
                                    "Recovery email sent successfully ! check your inbox"
                                emailIconText!!.setTextColor(resources.getColor(R.color.successGreen))
                                emailIconText!!.visibility = View.VISIBLE
                                TransitionManager.beginDelayedTransition(
                                    emailIconContainer
                                )
                                emailIcon!!.visibility = View.VISIBLE
                            }

                            override fun onAnimationEnd(animation: Animation) {
                                emailIcon!!.setImageResource(R.drawable.mail_sent)
                            }

                            override fun onAnimationRepeat(animation: Animation) {}
                        })
                        emailIcon!!.startAnimation(scaleAnimation)
                    } else {
                        Reset!!.setEnabled(true)
                        Reset!!.setTextColor(Color.rgb(0, 0, 0))
                        val message = task.exception!!.message
                        emailIconText!!.text = message
                        emailIconText!!.setTextColor(resources.getColor(R.color.colorPrimaryDark))
                        TransitionManager.beginDelayedTransition(emailIconContainer)
                        emailIconText!!.visibility = View.VISIBLE
                    }
                    progressBar!!.visibility = View.GONE
                }
        })

        GoBack = findViewById(R.id.forgot_go_back)
        GoBack!!.setOnClickListener(View.OnClickListener { SendUserToLoginActivity() })

        emailIconContainer = findViewById(R.id.forgot_password_email_icon_container)
        emailIcon = findViewById(R.id.forgot_password_email_icon)
        emailIconText = findViewById(R.id.forgot_password_email_icon_text)
        progressBar = findViewById(R.id.progressBar)
    }


    private fun checkInputs() {
        if (TextUtils.isEmpty(forgotEmail!!.text)) {
            Reset!!.isEnabled = false
            Reset!!.setTextColor(Color.argb(50, 0, 0, 0))
        } else {
            Reset!!.isEnabled = true
            Reset!!.setTextColor(Color.rgb(0, 0, 0))
        }
    }

    private fun SendUserToLoginActivity() {
        val fIntent = Intent(this, LoginActivity::class.java)
        startActivity(fIntent)
    }
}