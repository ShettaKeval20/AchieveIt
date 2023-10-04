package com.example.achieveit

import android.annotation.SuppressLint
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
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class RegisterFragment : Fragment() {
    private lateinit var rEmail: EditText
    private lateinit var rPassword: EditText
    private lateinit var rcPassword: EditText
    private lateinit var rbutton: Button

    private var loadingBar: ProgressDialog? = null

    private var mAuth: FirebaseAuth? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_register, container, false)

        mAuth = FirebaseAuth.getInstance()

        rEmail = view.findViewById(R.id.rEmail);
        rEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                checkInputs()
            }

            override fun afterTextChanged(s: Editable) {

            }
        })

        rPassword = view.findViewById(R.id.rPassword);
        rPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                checkInputs()
            }

            override fun afterTextChanged(s: Editable) {

            }
        })

        rcPassword = view.findViewById(R.id.rcPassword);
        rcPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                checkInputs()
            }

            override fun afterTextChanged(s: Editable) {

            }
        })

        rbutton = view.findViewById(R.id.rbutton);
        rbutton.setOnClickListener(View.OnClickListener {
            checkEmailAndPassword()
        })

        loadingBar = ProgressDialog(requireContext())

        return view
    }

    private fun checkEmailAndPassword() {

        mAuth!!.createUserWithEmailAndPassword(
            rEmail.getText().toString(),
            rPassword.getText().toString()
        ).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val mainIntent = Intent(requireContext(), LoginActivity::class.java)
                startActivity(mainIntent)
                requireActivity().finish()

                Toast.makeText(
                    requireContext(),
                    "Registration succesful please Login...",
                    Toast.LENGTH_SHORT
                ).show()

                loadingBar!!.dismiss()
            } else {
                rbutton.setEnabled(true)
                rbutton.setTextColor(Color.rgb(0, 0, 0))
                val message = task.exception!!.message
                Toast.makeText(
                    requireContext(),
                    "Error occurred: $message",
                    Toast.LENGTH_SHORT
                ).show()
                loadingBar!!.dismiss()
            }
        }
    }

    private fun checkInputs() {

        if (!TextUtils.isEmpty(rEmail.getText().toString())) {
            if (!TextUtils.isEmpty(rPassword.getText()) && rPassword.length() >= 8) {
                if (!TextUtils.isEmpty(rcPassword.getText().toString())) {
                    rbutton.setEnabled(true)
                    rbutton.setTextColor(Color.rgb(0, 0, 0))
                } else {
                    rbutton.setEnabled(false)
                    rbutton.setTextColor(Color.argb(50, 0, 0, 0))
                }
            } else {
                rbutton.setEnabled(false)
                rbutton.setTextColor(Color.argb(50, 0, 0, 0))
            }
        } else {
            rbutton.setEnabled(false)
            rbutton.setTextColor(Color.argb(50, 0, 0, 0))
        }
    }
}
