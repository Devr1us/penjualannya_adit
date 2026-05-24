package com.adit.penjualannya_adit

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

/**
 * LoginActivity menangani proses autentikasi pengguna.
 * Jika pengguna sudah pernah masuk sebelumnya, otomatis akan dialihkan ke dashboard utama (MainActivity2).
 * Jika belum, pengguna wajib memasukkan username dan password yang sudah terdaftar di Firebase Realtime Database.
 */
class LoginActivity : AppCompatActivity() {

    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var tvRegisterLink: TextView
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        sessionManager = SessionManager(this)

        // 1. Cek Sesi: Jika pengguna sudah login, langsung sambut dengan dashboard utama
        if (sessionManager.isLoggedIn()) {
            Toast.makeText(this, "Selamat datang kembali, ${sessionManager.getName()}!", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, MainActivity2::class.java))
            finish()
            return
        }

        // Inisialisasi UI View
        etUsername = findViewById(R.id.etUsername)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        tvRegisterLink = findViewById(R.id.tvRegisterLink)

        // Aksi tombol Login
        btnLogin.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (username.isEmpty()) {
                etUsername.error = "Username tidak boleh kosong!"
                etUsername.requestFocus()
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                etPassword.error = "Password tidak boleh kosong!"
                etPassword.requestFocus()
                return@setOnClickListener
            }

            prosesLogin(username, password)
        }

        // Aksi tautan ke Halaman Registrasi
        tvRegisterLink.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    /**
     * Memproses pencocokan kredensial ke Firebase Realtime Database
     */
    private fun prosesLogin(usernameInput: String, passwordInput: String) {
        // Menggunakan instance Firebase Realtime Database
        val database = FirebaseDatabase.getInstance()
        val usersRef = database.getReference("users")

        // Cari data berdasarkan username yang menjadi key/child
        usersRef.child(usernameInput).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    // Ambil password dari database
                    val dbPassword = snapshot.child("password").getValue(String::class.java)
                    val dbName = snapshot.child("nama").getValue(String::class.java) ?: usernameInput
                    val dbEmail = snapshot.child("email").getValue(String::class.java) ?: ""

                    // Bandingkan password
                    if (dbPassword == passwordInput) {
                        // Simpan sesi login lokal
                        sessionManager.createLoginSession(usernameInput, dbName, dbEmail)

                        Toast.makeText(this@LoginActivity, "Login Berhasil! Selamat Datang $dbName", Toast.LENGTH_SHORT).show()

                        // Arahkan ke dashboard utama
                        val intent = Intent(this@LoginActivity, MainActivity2::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@LoginActivity, "Password salah!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@LoginActivity, "Username tidak terdaftar! Silakan mendaftar terlebih dahulu.", Toast.LENGTH_LONG).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@LoginActivity, "Terjadi kesalahan koneksi: ${error.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}
