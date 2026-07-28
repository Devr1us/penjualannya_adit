package com.adit.penjualannya_adit

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.View
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
import java.text.SimpleDateFormat
import java.util.*

class StrukActivity : AppCompatActivity() {

    private lateinit var tvIdTransaksi: TextView
    private lateinit var tvTanggal: TextView
    private lateinit var tvKasir: TextView
    private lateinit var tvStatus: TextView
    private lateinit var tvMetodeBayar: TextView
    private lateinit var llOrderItems: LinearLayout
    private lateinit var tvSubtotal: TextView
    private lateinit var tvTotal: TextView
    private lateinit var tvJumlahItem: TextView
    private lateinit var layoutDiskon: LinearLayout
    private lateinit var tvDiskon: TextView

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
        tvTanggal     = findViewById(R.id.tvTanggal)
        tvKasir       = findViewById(R.id.tvKasir)
        tvStatus      = findViewById(R.id.tvStatus)
        tvMetodeBayar = findViewById(R.id.tvMetodeBayar)
        llOrderItems  = findViewById(R.id.llOrderItems)
        tvSubtotal    = findViewById(R.id.tvSubtotal)
        tvTotal       = findViewById(R.id.tvTotal)
        tvJumlahItem  = findViewById(R.id.tvJumlahItem)
        layoutDiskon  = findViewById(R.id.layoutDiskon)
        tvDiskon      = findViewById(R.id.tvDiskon)

        btnKirimWa    = findViewById(R.id.btnKirimWa)
        btnBagikanLain = findViewById(R.id.btnBagikanLain)
        btnSelesai    = findViewById(R.id.btnSelesai)

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

        // Info transaksi — tampilkan ID singkat agar mudah dibaca
        val shortId = t.idTransaksi?.takeLast(8)?.uppercase() ?: "-"
        tvIdTransaksi.text = shortId

        // Format tanggal lebih ramah
        tvTanggal.text = formatTanggalRamah(t.tanggal)

        // Kasir
        tvKasir.text = t.namaPegawai ?: "Kasir"

        // Metode Pembayaran
        tvMetodeBayar.text = t.metodePembayaran ?: "Tunai"

        // Status dengan warna
        val status = t.status ?: "Selesai"
        tvStatus.text = status
        if (status == "Selesai") {
            tvStatus.setTextColor(android.graphics.Color.parseColor("#388E3C"))
        } else {
            tvStatus.setTextColor(android.graphics.Color.parseColor("#F57C00"))
        }

        // ═══ Order Items - format tabel rapi ═══
        llOrderItems.removeAllViews()
        var totalQty = 0

        t.listProduk?.forEachIndexed { index, item ->
            val harga = item.produk?.hargaJual?.toInt() ?: 0
            val qty = item.jumlah
            val sub = harga * qty
            totalQty += qty

            // Buat baris item: [Nama Produk] [Qty] [Subtotal]
            val rowLayout = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    bottomMargin = dpToPx(6)
                }
            }

            // Nama produk (weight 4)
            val tvNama = TextView(this).apply {
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 4f)
                text = item.produk?.namaProduk ?: "Produk"
                setTextColor(android.graphics.Color.parseColor("#333333"))
                textSize = 12f
                maxLines = 2
            }

            // Qty (weight 1, center)
            val tvQty = TextView(this).apply {
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                text = "x$qty"
                setTextColor(android.graphics.Color.parseColor("#777777"))
                textSize = 12f
                gravity = Gravity.CENTER
            }

            // Subtotal (weight 3, end-aligned)
            val tvSub = TextView(this).apply {
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 3f)
                text = formatRupiah(sub)
                setTextColor(android.graphics.Color.parseColor("#333333"))
                textSize = 12f
                gravity = Gravity.END
            }

            rowLayout.addView(tvNama)
            rowLayout.addView(tvQty)
            rowLayout.addView(tvSub)
            llOrderItems.addView(rowLayout)

            // Baris harga satuan (di bawah nama produk, abu-abu kecil)
            if (qty > 1) {
                val tvHargaSatuan = TextView(this).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        bottomMargin = dpToPx(6)
                    }
                    text = "  @ ${formatRupiah(harga)}"
                    setTextColor(android.graphics.Color.parseColor("#AAAAAA"))
                    textSize = 11f
                }
                llOrderItems.addView(tvHargaSatuan)
            }
        }

        // ═══ Totals ═══
        tvSubtotal.text = formatRupiah(t.subtotal)

        // Diskon
        if (t.diskon > 0) {
            layoutDiskon.visibility = View.VISIBLE
            tvDiskon.text = "- ${formatRupiah(t.diskon)}"
        } else {
            layoutDiskon.visibility = View.GONE
        }

        tvTotal.text = formatRupiah(t.totalHarga)
        tvJumlahItem.text = "Total: $totalQty item"
    }

    /**
     * Format tanggal dari "yyyy-MM-dd HH:mm:ss" menjadi format ramah Indonesia
     * Contoh: "28 Jul 2026, 13:45"
     */
    private fun formatTanggalRamah(tanggal: String?): String {
        if (tanggal.isNullOrBlank()) return "-"
        return try {
            val sdfInput = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val date = sdfInput.parse(tanggal) ?: return tanggal
            val localeId = Locale("id", "ID")
            val sdfOutput = SimpleDateFormat("dd MMM yyyy, HH:mm", localeId)
            sdfOutput.format(date)
        } catch (e: Exception) {
            tanggal
        }
    }

    private fun formatRupiah(value: Int): String {
        return "Rp %,d".format(value).replace(',', '.')
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }

    // ═══════════════════════════════════════════
    // Share text — format teks untuk dibagikan
    // ═══════════════════════════════════════════

    private fun generateStrukText(): String {
        if (modelTransaksi == null) return ""
        val t = modelTransaksi!!
        val shortId = t.idTransaksi?.takeLast(8)?.uppercase() ?: "-"
        val tanggalFormatted = formatTanggalRamah(t.tanggal)
        val w = LINE_WIDTH // 32 karakter

        val sb = StringBuilder()
        sb.append("=".repeat(w) + "\n")
        sb.append("          RIUSSHOP\n")
        sb.append("=".repeat(w) + "\n\n")

        sb.append(padLine("No. Transaksi", shortId) + "\n")
        sb.append(padLine("Tanggal", tanggalFormatted) + "\n")
        sb.append(padLine("Kasir", t.namaPegawai ?: "Kasir") + "\n")
        sb.append(padLine("Pembayaran", t.metodePembayaran ?: "Tunai") + "\n")
        sb.append(padLine("Status", t.status ?: "Selesai") + "\n\n")

        sb.append("-".repeat(w) + "\n")
        sb.append(padLine("ITEM", "HARGA") + "\n")
        sb.append("-".repeat(w) + "\n")

        var totalQty = 0
        var no = 1
        t.listProduk?.forEach { item ->
            val nama = item.produk?.namaProduk ?: "Produk"
            val harga = item.produk?.hargaJual?.toInt() ?: 0
            val qty = item.jumlah
            val sub = harga * qty
            totalQty += qty

            val prefix = "$no. "
            val maxNama = w - prefix.length
            sb.append("$prefix${truncate(nama, maxNama)}\n")

            val detailLeft = "   $qty x ${formatRupiah(harga)}"
            val detailRight = formatRupiah(sub)
            sb.append(padLine(detailLeft, detailRight) + "\n")
            no++
        }

        sb.append("-".repeat(w) + "\n")
        sb.append(padLine("Subtotal", formatRupiah(t.subtotal)) + "\n")
        if (t.diskon > 0) {
            sb.append(padLine("Diskon", "- ${formatRupiah(t.diskon)}") + "\n")
        }
        sb.append("=".repeat(w) + "\n")
        sb.append(padLine("*TOTAL*", "*${formatRupiah(t.totalHarga)}*") + "\n")
        sb.append(padLine("", "($totalQty item)") + "\n")
        sb.append("=".repeat(w) + "\n\n")
        sb.append("    Terima kasih telah\n")
        sb.append("  berbelanja di RiusShop!\n")
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

    // ═══════════════════════════════════════════
    // Bluetooth Printing — format ESC/POS
    // ═══════════════════════════════════════════

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

    // ═══════════════════════════════════════════
    // Helper — format teks rata 32 karakter
    // ═══════════════════════════════════════════

    companion object {
        /** Lebar kertas thermal dalam jumlah karakter */
        private const val LINE_WIDTH = 32
    }

    /**
     * Buat satu baris dengan label rata kiri dan nilai rata kanan,
     * total panjang tepat [LINE_WIDTH] karakter.
     * Contoh: "Subtotal         Rp 35.000"
     */
    private fun padLine(label: String, value: String): String {
        val spaceCount = LINE_WIDTH - label.length - value.length
        return if (spaceCount > 0) {
            label + " ".repeat(spaceCount) + value
        } else {
            // Jika terlalu panjang, potong label
            val maxLabel = LINE_WIDTH - value.length - 1
            label.take(maxLabel) + " " + value
        }
    }

    /**
     * Potong teks agar tidak melebihi [maxLen] karakter.
     * Jika dipotong, tambahkan ".." di akhir.
     */
    private fun truncate(text: String, maxLen: Int): String {
        return if (text.length <= maxLen) text
        else text.take(maxLen - 2) + ".."
    }

    /**
     * Buat garis separator penuh 32 karakter.
     */
    private fun line(char: Char = '-'): String {
        return char.toString().repeat(LINE_WIDTH)
    }

    private fun printReceipt() {
        try {
            val connection = BluetoothPrintersConnections.selectFirstPaired()
            if (connection != null) {
                val printer = EscPosPrinter(connection, 203, 48f, LINE_WIDTH)

                val t = modelTransaksi ?: return
                val shortId = t.idTransaksi?.takeLast(8)?.uppercase() ?: "-"
                val tanggalFormatted = formatTanggalRamah(t.tanggal)

                // ══════════ Bangun baris item ══════════
                val itemLines = StringBuilder()
                var totalQty = 0
                var no = 1

                t.listProduk?.forEach { item ->
                    val nama = item.produk?.namaProduk ?: "Produk"
                    val harga = item.produk?.hargaJual?.toInt() ?: 0
                    val qty = item.jumlah
                    val sub = harga * qty
                    totalQty += qty

                    // Baris 1: "1. Nama Produk" (potong jika panjang)
                    val prefix = "$no. "
                    val maxNama = LINE_WIDTH - prefix.length
                    itemLines.append("[L]$prefix${truncate(nama, maxNama)}\n")

                    // Baris 2: "   2 x Rp 15.000      Rp 30.000"
                    val detailLeft = "   $qty x ${formatRupiah(harga)}"
                    val detailRight = formatRupiah(sub)
                    itemLines.append("[L]${padLine(detailLeft, detailRight)}\n")

                    no++
                }

                // ══════════ Bangun struk lengkap ══════════
                val sb = StringBuilder()

                // --- Header toko ---
                sb.append("[C]<font size='big'><b>RIUSSHOP</b></font>\n")
                sb.append("[C]Jl. Contoh No. 123\n")
                sb.append("[C]Telp: 08xx-xxxx-xxxx\n")
                sb.append("[L]${line('=')}\n")

                // --- Info transaksi (kolom rata) ---
                sb.append("[L]${padLine("No. Transaksi", shortId)}\n")
                sb.append("[L]${padLine("Tanggal", tanggalFormatted)}\n")
                sb.append("[L]${padLine("Kasir", t.namaPegawai ?: "Kasir")}\n")
                sb.append("[L]${padLine("Pembayaran", t.metodePembayaran ?: "Tunai")}\n")
                sb.append("[L]${padLine("Status", t.status ?: "Selesai")}\n")
                sb.append("[L]${line('-')}\n")

                // --- Header kolom item ---
                sb.append("[L]${padLine("ITEM", "HARGA")}\n")
                sb.append("[L]${line('-')}\n")

                // --- Daftar item ---
                sb.append(itemLines)
                sb.append("[L]${line('-')}\n")

                // --- Totals ---
                sb.append("[L]${padLine("Subtotal", formatRupiah(t.subtotal))}\n")
                if (t.diskon > 0) {
                    sb.append("[L]${padLine("Diskon", "- ${formatRupiah(t.diskon)}")}\n")
                }
                sb.append("[L]${line('=')}\n")
                sb.append("[L]<b>${padLine("TOTAL", formatRupiah(t.totalHarga))}</b>\n")
                sb.append("[L]${padLine("", "($totalQty item)")}\n")
                sb.append("[L]${line('=')}\n")

                // --- Footer ---
                sb.append("[C]\n")
                sb.append("[C]Terima Kasih!\n")
                sb.append("[C]Barang yang sudah dibeli\n")
                sb.append("[C]tidak dapat ditukar/dikembalikan\n")
                sb.append("[L]\n")

                printer.printFormattedText(sb.toString())
                Toast.makeText(this, "Mencetak Struk...", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Printer Bluetooth tidak ditemukan. Pastikan printer sudah terpasang dan terhubung.", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Gagal mencetak: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
