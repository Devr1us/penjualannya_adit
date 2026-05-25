package com.adit.penjualannya_adit

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase

/**
 * RegisterActivity menangani proses pendaftaran pengguna baru.
 * Pengguna menginputkan Nama Lengkap, Username, Email, dan Password.
 * Data disimpan secara terstruktur ke dalam Firebase Realtime Database.
 */
class RegisterActivity : AppCompatActivity() {

    private lateinit var etNama: EditText
    private lateinit var etUsername: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnRegister: Button
    private lateinit var tvLoginLink: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Inisialisasi UI View
        etNama = findViewById(R.id.etNama)
        etUsername = findViewById(R.id.etUsername)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnRegister = findViewById(R.id.btnRegister)
        tvLoginLink = findViewById(R.id.tvLoginLink)

        // Aksi tombol Daftar
        btnRegister.setOnClickListener {
            val nama = etNama.text.toString().trim()
            val username = etUsername.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            // Validasi Input
            if (nama.isEmpty()) {
                etNama.error = "Nama Lengkap tidak boleh kosong!"
                etNama.requestFocus()
                return@setOnClickListener
            }

            if (username.isEmpty()) {
                etUsername.error = "Username tidak boleh kosong!"
                etUsername.requestFocus()
                return@setOnClickListener
            }

            // Memastikan username tidak mengandung karakter ilegal untuk Firebase key (seperti '.', '#', '$', '[', ']')
            if (username.contains(".") || username.contains("#") || username.contains("$") || username.contains("[") || username.contains("]")) {
                etUsername.error = "Username tidak boleh mengandung karakter . # $ [ ]"
                etUsername.requestFocus()
                return@setOnClickListener
            }

            if (email.isEmpty()) {
                etEmail.error = "Email tidak boleh kosong!"
                etEmail.requestFocus()
                return@setOnClickListener
            }

            if (password.length < 6) {
                etPassword.error = "Password minimal harus 6 karakter!"
                etPassword.requestFocus()
                return@setOnClickListener
            }

            prosesRegistrasi(nama, username, email, password)
        }

        // Aksi kembali ke Halaman Login jika sudah terdaftar
        tvLoginLink.setOnClickListener {
            finish() // Menutup RegisterActivity untuk kembali ke LoginActivity
        }
    }

    /**
     * Memproses penyimpanan akun baru ke Firebase Realtime Database
     */
    private fun prosesRegistrasi(nama: String, username: String, email: String, sandi: String) {
        val database = FirebaseDatabase.getInstance()
        val usersRef = database.getReference("users")

        // Buat objek data pengguna untuk disimpan
        val userMap = HashMap<String, String>()
        userMap["nama"] = nama
        userMap["username"] = username
        userMap["email"] = email
        userMap["password"] = sandi

        // Cek dulu apakah username sudah terdaftar
        usersRef.child(username).get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                Toast.makeText(this, "Username '$username' sudah terdaftar! Gunakan username lain.", Toast.LENGTH_LONG).show()
            } else {
                // Simpan data pendaftaran
                usersRef.child(username).setValue(userMap)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Registrasi Berhasil! Silakan Login.", Toast.LENGTH_LONG).show()
                        // Tutup activity dan kembali ke LoginActivity
                        finish()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Registrasi Gagal: ${e.message}", Toast.LENGTH_LONG).show()
                    }
            }
        }.addOnFailureListener { e ->
            Toast.makeText(this, "Koneksi database bermasalah: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}
