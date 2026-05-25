package com.adit.penjualannya_adit

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    private lateinit var edtNama: TextInputEditText
    private lateinit var edtEmail: TextInputEditText
    private lateinit var edtPassword: TextInputEditText
    private lateinit var edtConfirmPassword: TextInputEditText
    private lateinit var btnDaftar: MaterialButton
    private lateinit var tvGoToLogin: TextView // SUDAH DIPERBAIKI: Menggunakan TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

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
    }

    private fun setupClickListener() {
        btnDaftar.setOnClickListener {
            registerUser()
        }

        tvGoToLogin.setOnClickListener {
            // Kembali ke halaman Login
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun registerUser() {
        val nama = edtNama.text.toString().trim()
        val email = edtEmail.text.toString().trim()
        val password = edtPassword.text.toString().trim()
        val confirmPassword = edtConfirmPassword.text.toString().trim()

        // Validasi input
        if (nama.isEmpty()) {
            edtNama.error = getString(R.string.error_field_required)
            return
        }
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edtEmail.error = getString(R.string.error_invalid_email)
            return
        }
        if (password.length < 6) {
            edtPassword.error = getString(R.string.error_short_pass)
            return
        }
        if (password != confirmPassword) {
            edtConfirmPassword.error = getString(R.string.error_pass_mismatch)
            return
        }

        // Proses registrasi ke Firebase
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val uid = result.user?.uid ?: return@addOnSuccessListener

                val userData = mapOf(
                    "uid" to uid,
                    "nama" to nama,
                    "email" to email,
                    "createdAt" to System.currentTimeMillis()
                )

                database.reference.child("users").child(uid).setValue(userData)
                    .addOnSuccessListener {
                        Toast.makeText(this, R.string.register_success, Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, MainActivity2::class.java))
                        finish()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Gagal menyimpan data: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener { e ->
                val errorMsg = when (e) {
                    is FirebaseAuthUserCollisionException -> "Email sudah terdaftar!"
                    else -> e.localizedMessage ?: "Registrasi Gagal"
                }
                Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show()
            }
    }
}
