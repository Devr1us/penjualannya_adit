package com.adit.penjualannya_adit

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.adit.penjualannya_adit.kategori.DataKategoriActivity
import com.google.firebase.database.FirebaseDatabase
import java.util.*

class MainActivity2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main2)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Greeting
        val tvGreeting = findViewById<TextView>(R.id.tvGreeting)
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val greeting = when (hour) {
            in 0..11 -> "Selamat Pagi"
            in 12..14 -> "Selamat Siang"
            in 15..18 -> "Selamat Sore"
            else -> "Selamat Malam"
        }
        tvGreeting.text = "$greeting, Adit"

        // Firebase tes koneksi
        FirebaseDatabase.getInstance().getReference("tes_koneksi").setValue("Berhasil")

        // --- Tombol quick action ---
        // Transaksi
        findViewById<LinearLayout>(R.id.btnTransaksi).setOnClickListener {
            startActivity(Intent(this, DataTransaksiActivity::class.java))
        }
        // Laporan
        findViewById<LinearLayout>(R.id.btnLaporan).setOnClickListener {
            startActivity(Intent(this, LaporanActivity::class.java))
        }

        // --- Card menu utama ---
        // Akun
        findViewById<CardView>(R.id.cardAkun).setOnClickListener {
            startActivity(Intent(this, AccountActivity::class.java))
        }
        // Produk
        findViewById<CardView>(R.id.cardProduk).setOnClickListener {
            startActivity(Intent(this, TambahProduk::class.java))
        }
        // Kategori
        findViewById<CardView>(R.id.cardKategori).setOnClickListener {
            startActivity(Intent(this, DataKategoriActivity::class.java))
        }
        // Pegawai
        findViewById<CardView>(R.id.cardPegawai).setOnClickListener {
            startActivity(Intent(this, PegawaiActivity::class.java))
        }
        // Cabang
        findViewById<CardView>(R.id.cardCabang).setOnClickListener {
            startActivity(Intent(this, CabangActivity::class.java))
        }
        // Printer
        findViewById<CardView>(R.id.cardPrinter).setOnClickListener {
            startActivity(Intent(this, PrinterActivity::class.java))
        }
    }
}
