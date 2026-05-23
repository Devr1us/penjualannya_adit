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
import com.google.firebase.auth.*
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
                Toast.makeText(this, "Google Sign In Gagal", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        // Konfigurasi Google Sign In
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

        // VALIDASI INPUT
        if (nama.isEmpty()) { edtNama.error = "Nama wajib diisi"; return }
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) { 
            edtEmail.error = "Email tidak valid!"; return 
        }
        if (password.length < 6) { 
            edtPassword.error = "Password minimal 6 karakter!"; return 
        }
        if (password != confirm) { 
            edtConfirmPassword.error = "Password tidak cocok!"; return 
        }

        // PROSES KE FIREBASE AUTH
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val uid = result.user?.uid ?: return@addOnSuccessListener
                val userData = mapOf("nama" to nama, "email" to email, "uid" to uid)
                
                // Simpan profil ke Database
                database.reference.child("users").child(uid).setValue(userData)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Registrasi Berhasil!", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, MainActivity2::class.java))
                        finish()
                    }
            }
            .addOnFailureListener { e ->
                // Mengubah kode angka menjadi pesan bahasa manusia
                val errorMsg = when (e) {
                    is FirebaseAuthUserCollisionException -> "Email sudah digunakan akun lain!"
                    is FirebaseAuthWeakPasswordException -> "Password terlalu lemah!"
                    is FirebaseAuthInvalidCredentialsException -> "Format email salah!"
                    else -> "Registrasi Gagal: Cek koneksi internet Anda"
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
