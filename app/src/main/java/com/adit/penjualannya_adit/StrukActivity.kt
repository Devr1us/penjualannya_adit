package com.adit.penjualannya_adit

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.adit.penjualannya_adit.Model.ModelTransaksi
import com.dantsu.escposprinter.EscPosPrinter
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections
import com.google.android.material.button.MaterialButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class StrukActivity : AppCompatActivity() {

    private lateinit var tvIdTransaksi: TextView
    private lateinit var tvTanggal: TextView
    private lateinit var tvKasir: TextView
    private lateinit var tvStatus: TextView
    private lateinit var llOrderItems: LinearLayout
    private lateinit var tvSubtotal: TextView
    private lateinit var tvTotal: TextView

    // btnKirimWa sekarang = Cetak Sekarang
    private lateinit var btnKirimWa: MaterialButton
    // btnBagikanLain sekarang = Bagikan (ke semua sosmed)
    private lateinit var btnBagikanLain: MaterialButton
    private lateinit var btnSelesai: TextView

    private val db = FirebaseDatabase.getInstance()
    private var transactionId: String? = null
    private var modelTransaksi: ModelTransaksi? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_struk)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initView()
        transactionId = intent.getStringExtra("ID_TRANSAKSI")

        if (transactionId != null) {
            loadTransaksi()
        } else {
            Toast.makeText(this, "ID Transaksi tidak ditemukan", Toast.LENGTH_SHORT).show()
        }
    }

    private fun initView() {
        tvIdTransaksi = findViewById(R.id.tvIdTransaksi)
        tvTanggal = findViewById(R.id.tvTanggal)
        tvKasir = findViewById(R.id.tvKasir)
        tvStatus = findViewById(R.id.tvStatus)
        llOrderItems = findViewById(R.id.llOrderItems)
        tvSubtotal = findViewById(R.id.tvSubtotal)
        tvTotal = findViewById(R.id.tvTotal)

        btnKirimWa = findViewById(R.id.btnKirimWa)
        btnBagikanLain = findViewById(R.id.btnBagikanLain)
        btnSelesai = findViewById(R.id.btnSelesai)

        findViewById<ImageView>(R.id.ivBack).setOnClickListener { finish() }

        btnSelesai.setOnClickListener {
            finish()
        }

        // Cetak Sekarang - langsung cetak via Bluetooth
        btnKirimWa.setOnClickListener {
            checkBluetoothPermissionAndPrint()
        }

        // Bagikan - ke semua sosial media / aplikasi
        btnBagikanLain.setOnClickListener {
            shareText()
        }
    }

    private fun loadTransaksi() {
        db.getReference("transaksi").child(transactionId!!)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    modelTransaksi = snapshot.getValue(ModelTransaksi::class.java)
                    if (modelTransaksi != null) {
                        displayStruk()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@StrukActivity, "Gagal memuat data", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun displayStruk() {
        val t = modelTransaksi!!
        tvIdTransaksi.text = "• ID Transaksi: ${t.idTransaksi}"
        tvTanggal.text = "• Tanggal: ${t.tanggal}"
        tvKasir.text = "• Kasir: ${t.namaPegawai}"
        tvStatus.text = "• Status: ${t.status} (${t.metodePembayaran})"

        llOrderItems.removeAllViews()
        var idx = 1
        t.listProduk?.forEach { item ->
            val tv = TextView(this)
            val harga = item.produk?.hargaJual?.toInt() ?: 0
            val sub = harga * item.jumlah
            tv.text = "$idx. ${item.produk?.namaProduk} (x${item.jumlah}) - Rp %,d".format(sub)
            tv.setTextColor(android.graphics.Color.parseColor("#333333"))
            tv.textSize = 14f
            tv.setPadding(0, 0, 0, 8)
            llOrderItems.addView(tv)
            idx++
        }

        tvSubtotal.text = "Subtotal: Rp %,d".format(t.subtotal)
        tvTotal.text = "*Total: Rp %,d*".format(t.totalHarga)
    }

    private fun generateStrukText(): String {
        if (modelTransaksi == null) return ""
        val t = modelTransaksi!!
        val sb = java.lang.StringBuilder()
        sb.append("Halo sobat RiusShop 👋\n\n")
        sb.append("Berikut rincian pesanan Anda di *RiusShop*:\n\n")
        sb.append("• ID Transaksi: ${t.idTransaksi}\n")
        sb.append("• Tanggal: ${t.tanggal}\n")
        sb.append("• Kasir: ${t.namaPegawai}\n")
        sb.append("• Status: ${t.status} (${t.metodePembayaran})\n\n")
        sb.append("*Detail Pesanan:*\n")

        var idx = 1
        t.listProduk?.forEach { item ->
            val harga = item.produk?.hargaJual?.toInt() ?: 0
            val sub = harga * item.jumlah
            sb.append("$idx. ${item.produk?.namaProduk} (x${item.jumlah}) - Rp %,d\n".format(sub))
            idx++
        }

        sb.append("\nSubtotal: Rp %,d\n".format(t.subtotal))
        sb.append("*Total: Rp %,d*\n\n".format(t.totalHarga))
        sb.append("Terima kasih telah berbelanja di *RiusShop* 🙏")
        return sb.toString()
    }

    private fun shareText() {
        val text = generateStrukText()
        if (text.isEmpty()) return
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, text)
        startActivity(Intent.createChooser(intent, "Bagikan Struk"))
    }

    private fun checkBluetoothPermissionAndPrint() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN),
                100
            )
        } else {
            printReceipt()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            printReceipt()
        } else {
            Toast.makeText(this, "Izin Bluetooth ditolak", Toast.LENGTH_SHORT).show()
        }
    }

    private fun printReceipt() {
        try {
            val connection = BluetoothPrintersConnections.selectFirstPaired()
            if (connection != null) {
                val printer = EscPosPrinter(connection, 203, 48f, 32)

                val t = modelTransaksi ?: return
                var itemsPrint = ""
                t.listProduk?.forEach { item ->
                    val harga = item.produk?.hargaJual?.toInt() ?: 0
                    val sub = harga * item.jumlah
                    itemsPrint += "[L]${item.produk?.namaProduk} x${item.jumlah} [R]Rp %,d\n".format(sub)
                }

                val printText = "[C]<font size='big'>RiusShop</font>\n" +
                        "[C]================================\n" +
                        "[L]ID   : ${t.idTransaksi}\n" +
                        "[L]Tgl  : ${t.tanggal}\n" +
                        "[L]Kasir: ${t.namaPegawai}\n" +
                        "[L]Bayar: ${t.metodePembayaran}\n" +
                        "[C]--------------------------------\n" +
                        itemsPrint +
                        "[C]--------------------------------\n" +
                        "[L]<b>Subtotal</b>[R]Rp %,d\n".format(t.subtotal) +
                        "[L]<b>Total</b>[R]Rp %,d\n".format(t.totalHarga) +
                        "[C]================================\n" +
                        "[C]Terima Kasih!\n"

                printer.printFormattedText(printText)
                Toast.makeText(this, "Mencetak Struk...", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Printer Bluetooth tidak ditemukan. Pastikan printer sudah terpasang dan terhubung.", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Gagal mencetak: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
