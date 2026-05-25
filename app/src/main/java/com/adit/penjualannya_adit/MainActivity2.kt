package com.adit.penjualannya_adit

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.adit.penjualannya_adit.kategori.DataKategoriActivity
import com.adit.penjualannya_adit.Model.ModelTransaksi
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.text.SimpleDateFormat
import java.util.*

class MainActivity2 : AppCompatActivity() {

    private lateinit var tvEstimasi: TextView
    private lateinit var tvDate: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main2)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Greeting dengan username dari SessionManager
        val tvGreeting = findViewById<TextView>(R.id.tvGreeting)
        tvDate = findViewById(R.id.tvDate)
        updateTanggalHariIni()

        val sessionManager = SessionManager(this)
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val greeting = when (hour) {
            in 0..11 -> "Selamat Pagi"
            in 12..14 -> "Selamat Siang"
            in 15..18 -> "Selamat Sore"
            else -> "Selamat Malam"
        }
        // Gunakan nama lengkap jika ada, fallback ke username
        val displayName = sessionManager.getName()?.takeIf { it.isNotBlank() }
            ?: sessionManager.getUsername()?.takeIf { it.isNotBlank() }
            ?: "Pengguna"
        tvGreeting.text = "$greeting, $displayName"

        // Inisialisasi TextView estimasi
        tvEstimasi = findViewById(R.id.tvEstimasi)
        // Default awal supaya tidak menampilkan angka dummy dari XML saat data Firebase belum masuk
        tvEstimasi.text = formatRupiah(0)

        // Load estimasi pendapatan hari ini
        loadEstimasiPendapatan()

        // Firebase tes koneksi
        FirebaseDatabase.getInstance().getReference("tes_koneksi").setValue("Berhasil")

        // --- Dark / Light Mode Toggle ---
        val ivDarkMode = findViewById<ImageView>(R.id.ivDarkMode)
        val currentNightMode = AppCompatDelegate.getDefaultNightMode()
        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val isDark = prefs.getBoolean("is_dark_mode", currentNightMode == AppCompatDelegate.MODE_NIGHT_YES)
        updateDarkModeIcon(ivDarkMode, isDark)

        ivDarkMode.setOnClickListener {
            val nowDark = prefs.getBoolean("is_dark_mode", false)
            val newDark = !nowDark
            prefs.edit().putBoolean("is_dark_mode", newDark).apply()
            AppCompatDelegate.setDefaultNightMode(
                if (newDark) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )
        }

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
        // Produk → ke halaman daftar produk dulu, dari sana ada FAB untuk tambah
        findViewById<CardView>(R.id.cardProduk).setOnClickListener {
            startActivity(Intent(this, DataProdukActivity::class.java))
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

    override fun onResume() {
        super.onResume()
        // Pastikan tanggal selalu up-to-date saat kembali ke halaman Home
        updateTanggalHariIni()
    }

    private fun updateDarkModeIcon(iv: ImageView, isDark: Boolean) {
        iv.setImageResource(if (isDark) android.R.drawable.ic_menu_day else android.R.drawable.ic_menu_day)
        iv.alpha = if (isDark) 0.6f else 1.0f
    }

    private fun updateTanggalHariIni() {
        // Format Indonesia: 25 Mei 2026
        val localeId = Locale("id", "ID")
        val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", localeId)
        tvDate.text = LocalDate.now().format(formatter)
    }

    private fun loadEstimasiPendapatan() {
        // Reset dulu agar tidak ada angka "nyangkut" sebelum data selesai dihitung
        tvEstimasi.text = formatRupiah(0)

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

                tvEstimasi.text = formatRupiah(totalHariIni)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun formatRupiah(value: Int): String {
        // Format ribuan pakai titik (contoh: 969.000)
        return "Rp %,d".format(value).replace(',', '.')
    }
}
