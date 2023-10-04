package com.example.achieveit

import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class LoginActivity : AppCompatActivity() {
    private lateinit var google: ImageView
//    private lateinit var meta: ImageView

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2

    private var mAuth: FirebaseAuth? = null
    private lateinit var googleSignInClient : GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        tabLayout = findViewById(R.id.tab_layout)
        viewPager = findViewById(R.id.view_pager)
        google = findViewById(R.id.google)
//        meta = findViewById(R.id.meta)

        mAuth = FirebaseAuth.getInstance()

        // Create an adapter for the ViewPager2
        val adapter = TabAdapter(this)

        // Add fragments to the adapter
        adapter.addFragment(LoginFragment(), "Login")
        adapter.addFragment(RegisterFragment(), "Register")

        // Set the adapter to the ViewPager2
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = adapter.getPageTitle(position)
        }.attach()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .requestProfile()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        google.setOnClickListener {
            signInGoogle();
        }
    }

    private fun signInGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result->

        if(result.resultCode == Activity.RESULT_OK){
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleResults(task)
        }
    }

    private fun handleResults(task: Task<GoogleSignInAccount>?) {
        if (task!!.isSuccessful){
            val account : GoogleSignInAccount? = task.result
            if(account!=null){
                updateUI(account)
            }
        }
    }

    private fun updateUI(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        mAuth!!.signInWithCredential(credential).addOnCompleteListener{
            if(it.isSuccessful){
                val mainIntent = Intent(this, MainActivity::class.java)
                mainIntent.putExtra("email", account.email)
                mainIntent.putExtra("name", account.displayName)
                startActivity(mainIntent)
                this.finish()
            }else{
                Toast.makeText(this, "Try again later", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()

        val animationDuration = 2500L // Adjust as needed
        val translateY = -100f // Adjust the distance to move the images

        // Animate Google ImageView
        val googleAnimator = ObjectAnimator.ofFloat(google, "translationY", 0f, translateY)
        googleAnimator.duration = animationDuration
        googleAnimator.interpolator = AccelerateDecelerateInterpolator()

        // Animate Meta ImageView
//        val metaAnimator = ObjectAnimator.ofFloat(meta, "translationY", 0f, translateY)
//        metaAnimator.duration = animationDuration
//        metaAnimator.interpolator = AccelerateDecelerateInterpolator()

        // Start animations
        googleAnimator.start()
//        metaAnimator.start()
    }
}

class TabAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    private val fragments = ArrayList<Fragment>()
    private val fragmentTitles = ArrayList<String>()

    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }

    fun addFragment(fragment: Fragment, title: String) {
        fragments.add(fragment)
        fragmentTitles.add(title)
    }

    fun getPageTitle(position: Int): CharSequence? {
        return fragmentTitles[position]
    }
}