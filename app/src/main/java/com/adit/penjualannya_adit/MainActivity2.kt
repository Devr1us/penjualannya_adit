package com.adit.penjualannya_adit

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.adit.penjualannya_adit.kategori.DataKategoriActivity
import com.google.firebase.database.FirebaseDatabase

class MainActivity2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        // --- TES KONEKSI FIREBASE ---
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("tes_koneksi")
        myRef.setValue("Berhasil Terhubung ke Firebase dari MainActivity2!")
            .addOnSuccessListener {
                Toast.makeText(this, "Koneksi Firebase Berhasil!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Koneksi Firebase Gagal: ${e.message}", Toast.LENGTH_LONG).show()
            }
        // ----------------------------

        findViewById<CardView>(R.id.cardKategori).setOnClickListener {
            startActivity(Intent(this, DataKategoriActivity::class.java))
        }
    }
}
