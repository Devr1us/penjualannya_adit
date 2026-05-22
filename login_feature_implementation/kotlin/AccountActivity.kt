package com.adit.penjualannya_adit

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

/**
 * AccountActivity adalah tampilan profil akun pengguna.
 * Menampilkan detail akun yang sedang aktif/login, dan menyediakan opsi
 * untuk Logout (Keluar) kembali ke halaman Login, serta mendaftarkan akun baru.
 */
class AccountActivity : AppCompatActivity() {

    private lateinit var tvWelcomeTitle: TextView
    private lateinit var tvNamaProfil: TextView
    private lateinit var tvUsernameProfil: TextView
    private lateinit var tvEmailProfil: TextView
    private lateinit var btnLogout: Button
    private lateinit var btnDaftarLagi: Button
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)

        sessionManager = SessionManager(this)

        // Jika belum login (kondisi tidak terduga), arahkan langsung ke LoginActivity
        if (!sessionManager.isLoggedIn()) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        // Inisialisasi UI View
        tvWelcomeTitle = findViewById(R.id.tvWelcomeTitle)
        tvNamaProfil = findViewById(R.id.tvNamaProfil)
        tvUsernameProfil = findViewById(R.id.tvUsernameProfil)
        tvEmailProfil = findViewById(R.id.tvEmailProfil)
        btnLogout = findViewById(R.id.btnLogout)
        btnDaftarLagi = findViewById(R.id.btnDaftarLagi)

        // 1. Tampilkan sapaan nama pengguna yang sedang aktif saat ini
        val currentName = sessionManager.getName() ?: "Pengguna"
        val currentUsername = sessionManager.getUsername() ?: "-"
        val currentEmail = sessionManager.getEmail() ?: "-"

        tvWelcomeTitle.text = "Selamat Datang Kembali,\n$currentName!"
        tvNamaProfil.text = currentName
        tvUsernameProfil.text = "@$currentUsername"
        tvEmailProfil.text = currentEmail

        // 2. Tombol Keluar Akun (Logout)
        btnLogout.setOnClickListener {
            // Hapus sesi lokal di SharedPreferences
            sessionManager.logoutUser()
            Toast.makeText(this, "Anda berhasil keluar dari akun.", Toast.LENGTH_SHORT).show()

            // Alihkan kembali ke halaman LoginActivity
            val intent = Intent(this, LoginActivity::class.java)
            // Hapus stack activity sebelumnya agar pengguna tidak bisa menekan tombol back untuk masuk lagi
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        // 3. Tombol opsi mendaftar / masuk dengan akun lain
        btnDaftarLagi.setOnClickListener {
            // Log out akun saat ini terlebih dahulu demi keamanan data sesi
            sessionManager.logoutUser()

            // Alihkan langsung ke RegisterActivity untuk mendaftarkan akun baru
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}
