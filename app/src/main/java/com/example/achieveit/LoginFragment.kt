package com.example.achieveit

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class LoginFragment : Fragment() {

    private lateinit var eLogin: EditText
    private lateinit var ePassword: EditText
    private lateinit var forgot: TextView
    private lateinit var loginButton: Button

    private var loadingBar: ProgressDialog? = null

    private var mAuth: FirebaseAuth? = null
    private var UsersRef: DatabaseReference? = null

    private val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+.[a-z]+"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        mAuth = FirebaseAuth.getInstance()
        UsersRef = FirebaseDatabase.getInstance().reference.child("Users")

        eLogin = view.findViewById(R.id.eLogin)
        eLogin.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                checkInputs()
            }

            override fun afterTextChanged(s: Editable) {

            }
        })

        ePassword = view.findViewById(R.id.ePassword)
        ePassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                checkInputs()
            }

            override fun afterTextChanged(s: Editable) {

            }
        })

        loginButton = view.findViewById(R.id.loginButton)
        loginButton.setOnClickListener(View.OnClickListener {
            checkEmailAndPassword()
        })

        forgot = view.findViewById(R.id.forgot)
        forgot.setOnClickListener {
            val intent = Intent(requireContext(), ForgotActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

        loadingBar = ProgressDialog(requireContext())

        return view
    }

    private fun checkEmailAndPassword() {

        loginButton.setEnabled(false)
        loginButton.setTextColor(Color.argb(50, 0, 0, 0))

        mAuth?.signInWithEmailAndPassword(
            eLogin.getText().toString(),
            ePassword.getText().toString()
        )?.addOnCompleteListener(OnCompleteListener<AuthResult?> { task ->
            if (task.isSuccessful) {
                SendUserToMainActivity()

                Toast.makeText(
                    requireContext(),
                    "Welcome...",
                    Toast.LENGTH_SHORT
                ).show()

            } else {
                loginButton.setEnabled(true)
                loginButton.setTextColor(Color.rgb(0, 0, 0))
                val message = task.exception!!.message
                Toast.makeText(
                    requireContext(),
                    "Email or password are incorrect",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun checkInputs() {
        if (!TextUtils.isEmpty(eLogin.getText().toString())) {
            if (!TextUtils.isEmpty(ePassword.getText().toString())) {
                loginButton.setEnabled(true)
                loginButton.setTextColor(Color.rgb(0, 0, 0))
            } else {
                loginButton.setEnabled(false)
                loginButton.setTextColor(Color.argb(50, 0, 0, 0))
            }
        } else {
            loginButton.setEnabled(false)
            loginButton.setTextColor(Color.argb(50, 0, 0, 0))
        }
    }

    private fun SendUserToMainActivity() {
        val mainIntent = Intent(requireContext(), MainActivity::class.java)
        startActivity(mainIntent)
        requireActivity().finish()
    }
}
