package com.adit.penjualannya_adit

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class AkunActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    private lateinit var txtNama: TextView
    private lateinit var txtEmail: TextView
    private lateinit var txtUid: TextView
    private lateinit var btnLogout: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        // Proteksi: Jika user belum login, lempar ke halaman Login
        if (auth.currentUser == null) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        setContentView(R.layout.activity_akun)

        initView()
        setupClickListener()

        val user = auth.currentUser
        txtEmail.text = getString(R.string.profile_email_format, user?.email ?: "-")
        txtUid.text = getString(R.string.profile_uid_format, user?.uid ?: "-")

        loadUserData(user?.uid)
    }

    private fun initView() {
        txtNama = findViewById(R.id.txtNama)
        txtEmail = findViewById(R.id.txtEmail)
        txtUid = findViewById(R.id.txtUid)
        btnLogout = findViewById(R.id.btnLogout)
    }

    private fun setupClickListener() {
        btnLogout.setOnClickListener {
            auth.signOut()
            Toast.makeText(this, "Berhasil Keluar", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun loadUserData(uid: String?) {
        if (uid == null) return

        database.reference.child("users").child(uid)
            .get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val nama = snapshot.child("nama").value?.toString() ?: "User"
                    txtNama.text = getString(R.string.profile_name_format, nama)
                } else {
                    txtNama.text = getString(R.string.profile_name_format, getString(R.string.profile_not_found))
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, R.string.error_load_profile, Toast.LENGTH_SHORT).show()
            }
    }
}
