package com.woo.task.view.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

import com.woo.task.R
import com.woo.task.databinding.ActivityLoginBinding
import com.woo.task.viewmodel.AuthViewModel
import kotlinx.coroutines.*

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val authViewModel: AuthViewModel by viewModels()
    private lateinit var auth: FirebaseAuth
    private val TAG = "LoginDebug"
    private val GOOGLE_SIGN_IN = 100
    lateinit var account: GoogleSignInAccount
    private val firebaseAnalytics = FirebaseAnalytics.getInstance(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            if (binding.txtEmail.text.toString().isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(
                    binding.txtEmail.text.toString()
                ).matches()
            ) {
                binding.txtEmail.error = getString(R.string.email_error)
                binding.txtEmail.requestFocus()
            } else if (binding.txtPass.text.toString().isEmpty()) {
                binding.txtPass.error = getString(R.string.pass_error)
                binding.txtPass.requestFocus()
            } else {
                binding.pgBar.visibility = View.VISIBLE
                CoroutineScope(Dispatchers.IO).launch {
                    auth.signInWithEmailAndPassword(
                        binding.txtEmail.text.toString(),
                        binding.txtPass.text.toString()
                    )
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val bundle = Bundle()
                                bundle.putString(FirebaseAnalytics.Param.METHOD, "LOGIN_NORMAL")
                                firebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, bundle)
                                binding.pgBar.visibility = View.INVISIBLE
                                Log.d(TAG, "signInEmail:success")
                                //val user = auth.currentUser
                                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                                overridePendingTransition(
                                    R.anim.slide_in_right,
                                    R.anim.slide_out_left
                                )
                                finish()
                            } else {
                                binding.pgBar.visibility = View.INVISIBLE
                                Log.w(TAG, "signInEmail:failure", task.exception)
                                Toast.makeText(
                                    this@LoginActivity,
                                    getString(R.string.error_login_pass),
                                    Toast.LENGTH_SHORT
                                ).show()
                                binding.txForgot.visibility = View.VISIBLE
                            }
                        }
                }
            }
        }

        binding.btnRegister.setOnClickListener {
            startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        binding.txForgot.setOnClickListener {

            MaterialAlertDialogBuilder(this)
                .setTitle(resources.getString(R.string.forgot_title))
                .setMessage(getString(R.string.forgot_message, binding.txtEmail.text.toString()))
                .setPositiveButton(resources.getString(R.string.dialog_confirm)) { _, _ ->
                    binding.pgBar.visibility = View.VISIBLE
                    CoroutineScope(Dispatchers.Main).launch {
                        Firebase.auth.sendPasswordResetEmail(binding.txtEmail.text.toString())
                            .addOnCompleteListener { task ->
                                binding.pgBar.visibility = View.INVISIBLE
                                if (task.isSuccessful) {
                                    Log.d(TAG, "Email sent.")
                                    Toast.makeText(
                                        this@LoginActivity,
                                        getString(R.string.forgot_email_sent),
                                        Toast.LENGTH_LONG
                                    ).show()
                                } else {
                                    Log.d(TAG, "Email sent ERROR.")
                                    Toast.makeText(
                                        this@LoginActivity,
                                        getString(R.string.forgot_email_error),
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                    }
                }
                .setNegativeButton(resources.getString(R.string.dialog_cancel)) { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
            Log.d(TAG, "Recuperar pass")
        }

        binding.btnGoogle.setOnClickListener {
            binding.pgBar.visibility = View.VISIBLE
            CoroutineScope(Dispatchers.IO).launch {
                val googleConf = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.google_web_client_id))
                    .requestEmail()
                    .build()
                val googleClient = GoogleSignIn.getClient(this@LoginActivity, googleConf)
                googleClient.signOut()
                startActivityForResult(googleClient.signInIntent, GOOGLE_SIGN_IN)
            }
        }

        binding.btnNologin.setOnClickListener {
            MaterialAlertDialogBuilder(this)
                .setTitle(resources.getString(R.string.nologin_title))
                .setMessage(getString(R.string.nologin_message))
                .setPositiveButton(resources.getString(R.string.dialog_confirm)) { _, _ ->
                    binding.pgBar.visibility = View.VISIBLE
                    CoroutineScope(Dispatchers.IO).launch {
                        auth.signInAnonymously()
                            .addOnCompleteListener(this@LoginActivity) { task ->
                                if (task.isSuccessful) {
                                    val bundle = Bundle()
                                    bundle.putString(FirebaseAnalytics.Param.METHOD, "LOGIN_NOSESSION")
                                    firebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, bundle)
                                    binding.pgBar.visibility = View.INVISIBLE
                                    Log.d(TAG, "signInAnonymously:success")
                                    //val user = auth.currentUser
                                    startActivity(
                                        Intent(
                                            this@LoginActivity,
                                            MainActivity::class.java
                                        )
                                    )
                                    overridePendingTransition(
                                        R.anim.slide_in_right,
                                        R.anim.slide_out_left
                                    )
                                    finish()
                                } else {
                                    binding.pgBar.visibility = View.INVISIBLE
                                    Log.w(TAG, "signInAnonymously:failure", task.exception)
                                    Toast.makeText(
                                        baseContext, "Authentication failed.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                    }

                }
                .setNegativeButton(resources.getString(R.string.dialog_cancel)) { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }

        binding.versionInfo.setOnLongClickListener {
            Toast.makeText(this, getString(R.string.love_message), Toast.LENGTH_LONG).show()
            return@setOnLongClickListener true
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GOOGLE_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    account = task.getResult(ApiException::class.java)
                    val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                    auth.signInWithCredential(credential).addOnCompleteListener {
                        binding.pgBar.visibility = View.INVISIBLE
                        if (it.isSuccessful) {
                            val bundle = Bundle()
                            bundle.putString(FirebaseAnalytics.Param.METHOD, "LOGIN_GOOGLE")
                            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, bundle)
                            //val user = auth.currentUser
                            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                            finish()
                        } else {
                            Toast.makeText(
                                this@LoginActivity,
                                getString(R.string.login_google_error),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } catch (e: ApiException) {
                    binding.pgBar.visibility = View.INVISIBLE
                    Log.d("GOOGLEDEBUG", "ERROR: ${e.message}")
                }
            }
        }
    }
}