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
import com.adit.penjualannya_adit.Model.ModelTransaksi
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import java.text.SimpleDateFormat
import java.util.*

class MainActivity2 : AppCompatActivity() {
    
    private lateinit var tvEstimasi: TextView
    
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
        
        // Inisialisasi TextView estimasi
        tvEstimasi = findViewById(R.id.tvEstimasi)
        
        // Load estimasi pendapatan hari ini
        loadEstimasiPendapatan()

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

    private fun loadEstimasiPendapatan() {
        val ref = FirebaseDatabase.getInstance().getReference("transaksi")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var totalHariIni = 0
                val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                val now = Calendar.getInstance()
                
                for (snap in snapshot.children) {
                    val transaksi = snap.getValue(ModelTransaksi::class.java)
                    if (transaksi != null && transaksi.status == "Selesai") {
                        try {
                            val dateStr = transaksi.tanggal ?: continue
                            val date = sdf.parse(dateStr) ?: continue
                            val cal = Calendar.getInstance()
                            cal.time = date
                            
                            val isSameYear = cal.get(Calendar.YEAR) == now.get(Calendar.YEAR)
                            val isSameMonth = isSameYear && cal.get(Calendar.MONTH) == now.get(Calendar.MONTH)
                            val isSameDay = isSameMonth && cal.get(Calendar.DAY_OF_MONTH) == now.get(Calendar.DAY_OF_MONTH)

                            if (isSameDay) {
                                totalHariIni += transaksi.subtotal
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
                
                tvEstimasi.text = "Rp %,d".format(totalHariIni).replace(',', '.')
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}
