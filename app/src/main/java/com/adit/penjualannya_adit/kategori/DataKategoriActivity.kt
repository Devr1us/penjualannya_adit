package com.adit.penjualannya_adit.kategori

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.adit.penjualannya_adit.Model.ModelKategori
import com.adit.penjualannya_adit.R
import com.adit.penjualannya_adit.adapter.KategoriAdapter
import com.adit.penjualannya_adit.viewmodel.DataKategoriViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class DataKategoriActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var btnTambah: FloatingActionButton
    private lateinit var btnBack: ImageView
    private lateinit var etSearch: EditText

    private lateinit var adapter: KategoriAdapter
    private lateinit var viewModel: DataKategoriViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_kategori)

        initView()
        setupRecyclerView()
        setupViewModel()
        setupSearch()
        setupClickListener()
    }

    private fun initView() {
        recyclerView = findViewById(R.id.recyclerView)
        btnTambah    = findViewById(R.id.btnTambah)
        btnBack      = findViewById(R.id.btnBack)
        etSearch     = findViewById(R.id.etSearch)
    }

    private fun setupRecyclerView() {
        adapter = KategoriAdapter(
            listKategori    = mutableListOf(),
            onItemClick     = { kategori -> bukaEditKategori(kategori) },
            onItemLongClick = { kategori -> showDeleteDialog(kategori) }
        )
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter       = adapter
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[DataKategoriViewModel::class.java]

        // Observe data kategori
        viewModel.kategoriList.observe(this) { list ->
            adapter.updateData(list ?: emptyList())
        }

        // Observe loading
        viewModel.isLoading.observe(this) { isLoading ->
            // bisa tambahkan ProgressBar di sini jika ada
        }

        // Observe search empty
        viewModel.isSearchEmpty.observe(this) { isEmpty ->
            if (isEmpty) {
                Toast.makeText(this, "Data tidak ditemukan", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupSearch() {
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.searchKategori(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun setupClickListener() {
        btnBack.setOnClickListener { finish() }

        btnTambah.setOnClickListener {
            startActivity(Intent(this, ModKategoriActivity::class.java))
        }
    }

    private fun bukaEditKategori(kategori: ModelKategori) {
        val intent = Intent(this, ModKategoriActivity::class.java).apply {
            putExtra("id",           kategori.id)
            putExtra("namaKategori", kategori.namaKategori)
            putExtra("status",       kategori.status)
        }
        startActivity(intent)
    }

    private fun showDeleteDialog(kategori: ModelKategori) {
        AlertDialog.Builder(this)
            .setTitle("Hapus Kategori")
            .setMessage("Apakah kamu yakin ingin menghapus '${kategori.namaKategori}'?")
            .setPositiveButton("Hapus") { _, _ -> hapusKategori(kategori) }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun hapusKategori(kategori: ModelKategori) {
        // Hapus langsung via Firebase, ViewModel akan auto-update via listener
        viewModel.kategoriList.value
            ?.let {
                Toast.makeText(this, "Menghapus ${kategori.namaKategori}...", Toast.LENGTH_SHORT).show()
            }
        com.google.firebase.database.FirebaseDatabase.getInstance(
            "https://aplikasipertama-2cbc4b5e-default-rtdb.asia-southeast1.firebasedatabase.app"
        ).getReference("kategori").child(kategori.id).removeValue()
            .addOnSuccessListener {
                Toast.makeText(this, "Kategori berhasil dihapus", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Gagal menghapus: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}