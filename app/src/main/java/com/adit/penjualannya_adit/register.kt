package com.adit.penjualannya_adit

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var googleSignInClient: GoogleSignInClient

    private lateinit var edtNama: TextInputEditText
    private lateinit var edtEmail: TextInputEditText
    private lateinit var edtPassword: TextInputEditText
    private lateinit var edtConfirmPassword: TextInputEditText
    private lateinit var btnDaftar: MaterialButton
    private lateinit var tvGoToLogin: MaterialButton

    private val googleLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Toast.makeText(this, "Google sign in gagal", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        initView()
        setupClickListener()
    }

    private fun initView() {
        edtNama = findViewById(R.id.edtNama)
        edtEmail = findViewById(R.id.edtEmail)
        edtPassword = findViewById(R.id.edtPassword)
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword)
        btnDaftar = findViewById(R.id.btnDaftar)
        tvGoToLogin = findViewById(R.id.tvGoToLogin)
        val btnGoogle = findViewById<SignInButton>(R.id.btnGoogle)

        btnGoogle.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            googleLauncher.launch(signInIntent)
        }
    }

    private fun setupClickListener() {
        btnDaftar.setOnClickListener { registerUser() }
        tvGoToLogin.setOnClickListener { finish() }
    }

    private fun registerUser() {
        val nama = edtNama.text.toString().trim()
        val email = edtEmail.text.toString().trim()
        val password = edtPassword.text.toString().trim()
        val confirm = edtConfirmPassword.text.toString().trim()

        if (nama.isEmpty()) { edtNama.error = getString(R.string.error_field_required); return }
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) { 
            edtEmail.error = getString(R.string.error_invalid_email); return 
        }
        if (password.length < 6) { 
            edtPassword.error = "Password minimal 6 karakter"; return 
        }
        if (password != confirm) { 
            edtConfirmPassword.error = getString(R.string.error_pass_mismatch); return 
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val uid = result.user?.uid ?: return@addOnSuccessListener
                val userData = mapOf("nama" to nama, "email" to email, "uid" to uid)
                
                database.reference.child("users").child(uid).setValue(userData)
                    .addOnSuccessListener {
                        Toast.makeText(this, R.string.register_success, Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, MainActivity2::class.java))
                        finish()
                    }
            }
            .addOnFailureListener { e ->
                val errorMsg = when (e) {
                    is FirebaseAuthUserCollisionException -> "Email sudah terdaftar!"
                    is FirebaseAuthWeakPasswordException -> "Password terlalu lemah!"
                    else -> e.localizedMessage ?: "Registrasi Gagal"
                }
                Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show()
            }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential).addOnSuccessListener {
            startActivity(Intent(this, MainActivity2::class.java))
            finish()
        }
    }
}
