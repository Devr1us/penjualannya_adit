package com.adit.penjualannya_adit

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Kode untuk mengetes koneksi Firebase
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("tes_koneksi")

        myRef.setValue("Berhasil Terhubung ke Firebase!")
            .addOnSuccessListener {
                // Jika data berhasil terkirim ke Firebase
                Toast.makeText(this, "Koneksi Firebase Berhasil!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                // Jika terjadi error (misal: belum setting rules database atau jaringan bermasalah)
                Toast.makeText(this, "Koneksi Gagal: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }
}
