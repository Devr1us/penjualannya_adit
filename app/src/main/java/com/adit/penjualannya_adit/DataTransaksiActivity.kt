package com.adit.penjualannya_adit

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.adit.penjualannya_adit.adapter.CartAdapter
import com.adit.penjualannya_adit.adapter.ProdukTransaksiAdapter
import com.adit.penjualannya_adit.Model.ModelCartItem
import com.adit.penjualannya_adit.Model.ModelKategoriTK
import com.adit.penjualannya_adit.Model.Produk
import com.adit.penjualannya_adit.Model.ModelTransaksi
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.firebase.database.*
import androidx.core.view.GravityCompat
import java.text.SimpleDateFormat
import java.util.*

class DataTransaksiActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var svProduk: SearchView
    private lateinit var cgKategori: ChipGroup
    private lateinit var rvProdukSelection: RecyclerView
    private lateinit var rvCart: RecyclerView
    private lateinit var tvSubtotal: TextView
    private lateinit var tvDiskon: TextView
    private lateinit var tvTotal: TextView
    private lateinit var tvCountItem: TextView
    private lateinit var tvBadgeCart: TextView
    private lateinit var btnBayar: Button
    private lateinit var btnHapus: Button
    private lateinit var btnCetakStruk: Button
    private lateinit var spinnerPembayaran: Spinner

    private lateinit var adapterProduk: ProdukTransaksiAdapter
    private lateinit var adapterCart: CartAdapter

    private val listProduk = mutableListOf<Produk>()
    private val listKategori = mutableListOf<ModelKategoriTK>()
    private val listCart = mutableListOf<ModelCartItem>()

    private val db = FirebaseDatabase.getInstance()

    // Filter berdasarkan namaKategori (bukan idKategori) agar cocok dengan field di Produk
    private var selectedKategoriNama: String = "Semua"
    private var lastTransactionId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_transaksi)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initView()
        setupRecyclerViews()
        loadKategori()
        loadProduk()
        setupSearch()
        setupActions()
    }

    private fun initView() {
        drawerLayout     = findViewById(R.id.drawerLayout)
        svProduk          = findViewById(R.id.svProduk)
        cgKategori        = findViewById(R.id.cgKategori)
        rvProdukSelection = findViewById(R.id.rvProdukSelection)
        rvCart            = findViewById(R.id.rvCart)
        tvSubtotal        = findViewById(R.id.tvSubtotal)
        tvDiskon          = findViewById(R.id.tvDiskon)
        tvTotal           = findViewById(R.id.tvTotal)
        tvCountItem       = findViewById(R.id.tvCountItem)
        tvBadgeCart       = findViewById(R.id.tvBadgeCart)
        btnBayar          = findViewById(R.id.btnBayar)
        btnHapus          = findViewById(R.id.btnHapus)
        btnCetakStruk     = findViewById(R.id.btnCetakStruk)
        spinnerPembayaran = findViewById(R.id.spinnerPembayaran)
        findViewById<ImageView>(R.id.ivBack).setOnClickListener { finish() }
        findViewById<ImageView>(R.id.ivHistory).setOnClickListener {
            startActivity(Intent(this, LaporanActivity::class.java))
        }

        findViewById<ImageView>(R.id.ivCart).setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.END)
        }

        btnCetakStruk.isEnabled = false
    }

    private fun setupRecyclerViews() {
        adapterProduk = ProdukTransaksiAdapter(emptyList()) { produk -> addToCart(produk) }
        rvProdukSelection.layoutManager = GridLayoutManager(this, 2)
        rvProdukSelection.adapter = adapterProduk

        adapterCart = CartAdapter(listCart) { updateSummary() }
        rvCart.layoutManager = LinearLayoutManager(this)
        rvCart.adapter = adapterCart
    }

    private fun loadKategori() {
        db.getReference("kategori").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listKategori.clear()
                cgKategori.removeAllViews()

                // Chip "Semua"
                val chipSemua = Chip(this@DataTransaksiActivity)
                styleKategoriChip(chipSemua)
                chipSemua.text = "Semua"
                chipSemua.isChecked = true
                chipSemua.setOnClickListener {
                    selectedKategoriNama = "Semua"
                    filterProduk()
                }
                cgKategori.addView(chipSemua)

                for (snap in snapshot.children) {
                    val kategori = snap.getValue(ModelKategoriTK::class.java)
                    if (kategori != null && kategori.statusKategori == "Aktif") {
                        listKategori.add(kategori)
                        val chip = Chip(this@DataTransaksiActivity)
                        styleKategoriChip(chip)
                        chip.text = kategori.namaKategori
                        chip.setOnClickListener {
                            // Filter pakai namaKategori agar cocok dengan field kategoriNama di Produk
                            selectedKategoriNama = kategori.namaKategori ?: "Semua"
                            filterProduk()
                        }
                        cgKategori.addView(chip)
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun styleKategoriChip(chip: Chip) {
        chip.isCheckable = true
        chip.isClickable = true
        chip.checkedIcon = null
        chip.isCheckedIconVisible = false
        chip.chipCornerRadius = 999f
        chip.chipBackgroundColor = getColorStateList(R.color.transaksi_chip_bg)
        chip.setTextColor(getColorStateList(R.color.transaksi_chip_text))
        chip.setEnsureMinTouchTargetSize(false)
        chip.textSize = 12f
        chip.setPadding(14, 6, 14, 6)
    }

    private fun loadProduk() {
        db.getReference("produk").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listProduk.clear()
                for (snap in snapshot.children) {
                    val produk = snap.getValue(Produk::class.java)
                    if (produk != null && produk.status == "Aktif") {
                        produk.id = snap.key ?: ""
                        // Jika stok produk 0 dan bukan unlimited, set default stok 100
                        if (!produk.stokTakTerbatas && produk.stok == 0) {
                            produk.stok = 100
                            db.getReference("produk").child(produk.id).child("stok").setValue(100)
                        }
                        listProduk.add(produk)
                    }
                }
                filterProduk()
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun setupSearch() {
        svProduk.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = false
            override fun onQueryTextChange(newText: String?): Boolean {
                filterProduk()
                return true
            }
        })
    }

    private fun filterProduk() {
        val query = svProduk.query.toString().lowercase().trim()
        val filtered = listProduk.filter { produk ->
            // Cocokkan berdasarkan kategoriNama (bukan kategoriId)
            val matchKategori = selectedKategoriNama == "Semua" ||
                produk.kategoriNama.equals(selectedKategoriNama, ignoreCase = true)
            val matchSearch = query.isEmpty() ||
                produk.namaProduk.lowercase().contains(query) ||
                produk.barcode.lowercase().contains(query)
            matchKategori && matchSearch
        }
        adapterProduk.updateData(filtered)
    }

    private fun addToCart(produk: Produk) {
        val existing = listCart.find { it.produk?.id == produk.id }
        if (existing != null) {
            existing.jumlah++
        } else {
            listCart.add(ModelCartItem(produk, 1))
        }
        adapterCart.notifyDataSetChanged()
        updateSummary()
    }

    private fun updateSummary() {
        var subtotal = 0
        var count = 0
        for (item in listCart) {
            subtotal += (item.produk?.hargaJual?.toInt() ?: 0) * item.jumlah
            count += item.jumlah
        }
        tvSubtotal.text  = "Rp %,d".format(subtotal)
        tvTotal.text     = "Rp %,d".format(subtotal)
        tvCountItem.text = "$count item"
        tvDiskon.text    = "- Rp 0"

        // Badge di ikon keranjang
        tvBadgeCart.isVisible = count > 0
        tvBadgeCart.text = count.toString()

        // Biar drawer tetap "hidup" dan informatif
        if (listCart.isEmpty()) {
            btnCetakStruk.isEnabled = lastTransactionId != null
        }
    }

    private fun setupActions() {
        btnHapus.setOnClickListener {
            listCart.clear()
            adapterCart.notifyDataSetChanged()
            updateSummary()
        }
        btnBayar.setOnClickListener {
            if (listCart.isEmpty()) {
                Toast.makeText(this, "Keranjang kosong", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            saveTransaction()
        }

        btnCetakStruk.setOnClickListener {
            val id = lastTransactionId
            if (id.isNullOrBlank()) {
                Toast.makeText(this, "Belum ada transaksi untuk dicetak", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val intent = Intent(this, StrukActivity::class.java)
            intent.putExtra("ID_TRANSAKSI", id)
            startActivity(intent)
        }
    }

    private fun saveTransaction() {
        val ref = db.getReference("transaksi")
        val id = ref.push().key ?: return
        var subtotal = 0
        for (item in listCart) subtotal += (item.produk?.hargaJual?.toInt() ?: 0) * item.jumlah

        val date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        val metodePembayaran = spinnerPembayaran.selectedItem.toString()

        val transaksi = ModelTransaksi(
            idTransaksi       = id,
            listProduk        = listCart.toList(),
            subtotal          = subtotal,
            totalHarga        = subtotal,
            diskon            = 0,
            tanggal           = date,
            namaPegawai       = "Kasir",
            metodePembayaran  = metodePembayaran
        )

        ref.child(id).setValue(transaksi).addOnSuccessListener {
            updateStock()
            Toast.makeText(this, "Transaksi Berhasil", Toast.LENGTH_LONG).show()

            lastTransactionId = id
            btnCetakStruk.isEnabled = true

            val intent = Intent(this, StrukActivity::class.java)
            intent.putExtra("ID_TRANSAKSI", id)
            startActivity(intent)

            listCart.clear()
            adapterCart.notifyDataSetChanged()
            updateSummary()
        }.addOnFailureListener {
            Toast.makeText(this, "Transaksi Gagal", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateStock() {
        for (item in listCart) {
            val produk = item.produk ?: continue
            if (produk.stokTakTerbatas) continue
            val newStok = produk.stok - item.jumlah
            db.getReference("produk").child(produk.id).child("stok").setValue(newStok)
        }
    }

    override fun onBackPressed() {
        if (this::drawerLayout.isInitialized && drawerLayout.isDrawerOpen(GravityCompat.END)) {
            drawerLayout.closeDrawer(GravityCompat.END)
        } else {
            super.onBackPressed()
        }
    }
}
