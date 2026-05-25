package com.adit.penjualannya_adit

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.adit.penjualannya_adit.Model.Produk
import com.adit.penjualannya_adit.R
import com.adit.penjualannya_adit.adapter.ProdukAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.*

class DataProdukActivity : AppCompatActivity() {

    private lateinit var rvProduk: RecyclerView
    private lateinit var svProduk: SearchView
    private lateinit var fabTambah: FloatingActionButton

    private lateinit var listProduk: ArrayList<Produk>
    private lateinit var adapter: ProdukAdapter

    private val db = FirebaseDatabase.getInstance()
    private val ref = db.getReference("produk")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_produk)

        initView()
        setupRecycler()
        loadData()
        setupSearch()
        setupAction()
    }

    private fun initView() {
        rvProduk = findViewById(R.id.rvProduk)
        svProduk = findViewById(R.id.svProduk)
        fabTambah = findViewById(R.id.fabTambah)
        findViewById<ImageView>(R.id.ivBack).setOnClickListener { finish() }
    }

    private fun setupRecycler() {
        listProduk = ArrayList<Produk>()
        // ProdukAdapter membutuhkan 2 parameter: list dan lambda klik
        adapter = ProdukAdapter(listProduk) { produk ->
            val intent = Intent(this, TambahProduk::class.java)
            intent.putExtra("id", produk.id)
            intent.putExtra("namaProduk", produk.namaProduk)
            intent.putExtra("hargaJual", produk.hargaJual.toString())
            intent.putExtra("kategoriNama", produk.kategoriNama)
            intent.putExtra("cabangNama", produk.cabangNama)
            intent.putExtra("stok", produk.stok.toString())
            intent.putExtra("stokTakTerbatas", produk.stokTakTerbatas)
            intent.putExtra("barcode", produk.barcode)
            startActivity(intent)
        }

        rvProduk.layoutManager = LinearLayoutManager(this)
        rvProduk.adapter = adapter
    }

    private fun loadData() {
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listProduk.clear()
                for (snap in snapshot.children) {
                    val data = snap.getValue(Produk::class.java)
                    if (data != null) {
                        data.id = snap.key ?: ""
                        listProduk.add(data)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun setupSearch() {
        svProduk.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false
            override fun onQueryTextChange(newText: String?): Boolean {
                filterList(newText)
                return true
            }
        })
    }

    private fun filterList(query: String?) {
        val filtered = ArrayList<Produk>()
        if (query.isNullOrEmpty()) {
            filtered.addAll(listProduk)
        } else {
            val lowerCaseQuery = query.lowercase()
            for (item in listProduk) {
                if (item.namaProduk.lowercase().contains(lowerCaseQuery) ||
                    item.barcode.lowercase().contains(lowerCaseQuery)
                ) {
                    filtered.add(item)
                }
            }
        }
        adapter.updateData(filtered)
    }

    private fun setupAction() {
        fabTambah.setOnClickListener {
            // Berpindah ke TambahProduk
            startActivity(Intent(this, TambahProduk::class.java))
        }
    }
}
