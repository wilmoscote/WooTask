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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

import com.woo.task.R
import com.woo.task.databinding.ActivityLoginBinding
import com.woo.task.viewmodel.AuthViewModel

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val authViewModel: AuthViewModel by viewModels()
    private lateinit var auth: FirebaseAuth
    private val TAG = "LoginDebug"
    private val GOOGLE_SIGN_IN = 100
    lateinit var account: GoogleSignInAccount
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {

            if(binding.txtEmail.text.toString().isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(binding.txtEmail.text.toString()).matches()){
                binding.txtEmail.error = getString(R.string.email_error)
                binding.txtEmail.requestFocus()
            }else if(binding.txtPass.text.toString().isEmpty()){
                binding.txtPass.error = getString(R.string.pass_error)
                binding.txtPass.requestFocus()
            }else{
                binding.pgBar.visibility = View.VISIBLE
                auth.signInWithEmailAndPassword(binding.txtEmail.text.toString(), binding.txtPass.text.toString())
                    .addOnCompleteListener { task->
                        if (task.isSuccessful) {
                            binding.pgBar.visibility = View.INVISIBLE
                            Log.d(TAG, "signInEmail:success")
                            val user = auth.currentUser
                            startActivity(Intent(this,MainActivity::class.java))
                            finish()
                        }else{
                            binding.pgBar.visibility = View.INVISIBLE
                            Log.w(TAG, "signInEmail:failure", task.exception)
                            Toast.makeText(baseContext, "Authentication failed.",
                                Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }

        binding.btnGoogle.setOnClickListener {
            val googleConf = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.google_web_client_id))
                .requestEmail()
                .build()
            val googleClient = GoogleSignIn.getClient(this,googleConf)
            googleClient.signOut()
            startActivityForResult(googleClient.signInIntent,GOOGLE_SIGN_IN)
        }

        binding.btnNologin.setOnClickListener {
            binding.pgBar.visibility = View.VISIBLE
            auth.signInAnonymously()
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        binding.pgBar.visibility = View.INVISIBLE
                        Log.d(TAG, "signInAnonymously:success")
                        val user = auth.currentUser
                        startActivity(Intent(this,MainActivity::class.java))
                        finish()
                    } else {
                        binding.pgBar.visibility = View.INVISIBLE
                        Log.w(TAG, "signInAnonymously:failure", task.exception)
                        Toast.makeText(baseContext, "Authentication failed.",
                            Toast.LENGTH_SHORT).show()
                    }
                }

        }

        binding.versionInfo.setOnLongClickListener {
            Toast.makeText(this,getString(R.string.love_message),Toast.LENGTH_LONG).show()


            return@setOnLongClickListener true
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == GOOGLE_SIGN_IN){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try{
                account = task.getResult(ApiException::class.java)
                val credential = GoogleAuthProvider.getCredential(account.idToken,null)
                auth.signInWithCredential(credential).addOnCompleteListener {
                    if(it.isSuccessful){
                        val user = auth.currentUser
                        startActivity(Intent(this,MainActivity::class.java))
                        finish()
                    }else{
                        Toast.makeText(this,"Error al iniciar con Google.",Toast.LENGTH_SHORT).show()
                    }
                }
            }catch(e: ApiException){
                Log.d("GOOGLEDEBUG","ERROR: ${e.message}")
            }
        }
    }
}