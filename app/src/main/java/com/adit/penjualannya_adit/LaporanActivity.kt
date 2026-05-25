package com.adit.penjualannya_adit

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.adit.penjualannya_adit.Model.ModelTransaksi
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

class LaporanActivity : AppCompatActivity() {

    private lateinit var tvPendapatanHariIni: TextView
    private lateinit var tvTransaksiHariIni: TextView
    private lateinit var tvPendapatanMingguIni: TextView
    private lateinit var tvTransaksiMingguIni: TextView
    private lateinit var tvPendapatanBulanIni: TextView
    private lateinit var tvTransaksiBulanIni: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_laporan)
        
        findViewById<ImageView>(R.id.ivBack).setOnClickListener { finish() }
        
        tvPendapatanHariIni = findViewById(R.id.tvPendapatanHariIni)
        tvTransaksiHariIni = findViewById(R.id.tvTransaksiHariIni)
        tvPendapatanMingguIni = findViewById(R.id.tvPendapatanMingguIni)
        tvTransaksiMingguIni = findViewById(R.id.tvTransaksiMingguIni)
        tvPendapatanBulanIni = findViewById(R.id.tvPendapatanBulanIni)
        tvTransaksiBulanIni = findViewById(R.id.tvTransaksiBulanIni)

        loadDataFirebase()
    }

    private fun loadDataFirebase() {
        val ref = FirebaseDatabase.getInstance().getReference("transaksi")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var totalHariIni = 0
                var countHariIni = 0
                var totalMingguIni = 0
                var countMingguIni = 0
                var totalBulanIni = 0
                var countBulanIni = 0

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
                            val isSameWeek = isSameYear && cal.get(Calendar.WEEK_OF_YEAR) == now.get(Calendar.WEEK_OF_YEAR)
                            val isSameDay = isSameYear && cal.get(Calendar.DAY_OF_YEAR) == now.get(Calendar.DAY_OF_YEAR)

                            val subtotal = transaksi.subtotal
                            
                            if (isSameMonth) {
                                totalBulanIni += subtotal
                                countBulanIni++
                            }
                            if (isSameWeek) {
                                totalMingguIni += subtotal
                                countMingguIni++
                            }
                            if (isSameDay) {
                                totalHariIni += subtotal
                                countHariIni++
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
                
                tvPendapatanHariIni.text = "Rp %,d".format(totalHariIni).replace(',', '.')
                tvTransaksiHariIni.text = "$countHariIni transaksi"
                
                tvPendapatanMingguIni.text = "Rp %,d".format(totalMingguIni).replace(',', '.')
                tvTransaksiMingguIni.text = "$countMingguIni transaksi"
                
                tvPendapatanBulanIni.text = "Rp %,d".format(totalBulanIni).replace(',', '.')
                tvTransaksiBulanIni.text = "$countBulanIni transaksi"
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}
