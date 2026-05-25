package com.adit.penjualannya_adit

import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class CabangActivity : AppCompatActivity() {

    private lateinit var db: FirebaseDatabase
    private lateinit var refCabang: DatabaseReference

    private lateinit var edtNamaCabang: EditText
    private lateinit var edtAlamatCabang: EditText
    private lateinit var edtTeleponCabang: EditText
    private lateinit var btnSimpanCabang: Button
    private lateinit var recyclerCabang: RecyclerView

    private val cabangList = mutableListOf<Cabang>()
    private lateinit var cabangAdapter: CabangAdapter
    private var selectedCabangId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cabang)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        db = FirebaseDatabase.getInstance()
        refCabang = db.getReference("branches")

        edtNamaCabang = findViewById(R.id.edtNamaCabang)
        edtAlamatCabang = findViewById(R.id.edtAlamatCabang)
        edtTeleponCabang = findViewById(R.id.edtTeleponCabang)
        btnSimpanCabang = findViewById(R.id.btnSimpanCabang)
        recyclerCabang = findViewById(R.id.recyclerCabang)

        cabangAdapter = CabangAdapter(cabangList) { cabang ->
            selectedCabangId = cabang.id
            edtNamaCabang.setText(cabang.nama)
            edtAlamatCabang.setText(cabang.alamat)
            edtTeleponCabang.setText(cabang.telepon)
            btnSimpanCabang.text = "Update Cabang"
        }

        recyclerCabang.layoutManager = LinearLayoutManager(this)
        recyclerCabang.adapter = cabangAdapter

        btnSimpanCabang.setOnClickListener { simpanCabang() }
        tampilkanCabang()

        try { findViewById<ImageView>(R.id.ivBack)?.setOnClickListener { finish() } } catch (_: Exception) {}
    }

    private fun simpanCabang() {
        val nama = edtNamaCabang.text.toString().trim()
        val alamat = edtAlamatCabang.text.toString().trim()
        val telepon = edtTeleponCabang.text.toString().trim()

        if (nama.isEmpty() || alamat.isEmpty() || telepon.isEmpty()) {
            Toast.makeText(this, "Semua data cabang wajib diisi", Toast.LENGTH_SHORT).show()
            return
        }

        val dataCabang = hashMapOf<String, Any>(
            "nama" to nama, "alamat" to alamat, "telepon" to telepon,
            "updatedAt" to System.currentTimeMillis()
        )

        val id = selectedCabangId
        if (id == null) {
            dataCabang["createdAt"] = System.currentTimeMillis()
            val newKey = refCabang.push().key ?: return
            refCabang.child(newKey).setValue(dataCabang).addOnSuccessListener {
                Toast.makeText(this, "Cabang berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                resetForm()
            }.addOnFailureListener {
                Toast.makeText(this, "Gagal menambahkan cabang", Toast.LENGTH_SHORT).show()
            }
        } else {
            refCabang.child(id).updateChildren(dataCabang).addOnSuccessListener {
                Toast.makeText(this, "Cabang berhasil diperbarui", Toast.LENGTH_SHORT).show()
                resetForm()
            }.addOnFailureListener {
                Toast.makeText(this, "Gagal memperbarui cabang", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun tampilkanCabang() {
        refCabang.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                cabangList.clear()
                for (snap in snapshot.children) {
                    val cabang = Cabang(
                        id = snap.key ?: "",
                        nama = snap.child("nama").getValue(String::class.java) ?: "-",
                        alamat = snap.child("alamat").getValue(String::class.java) ?: "-",
                        telepon = snap.child("telepon").getValue(String::class.java) ?: "-"
                    )
                    cabangList.add(cabang)
                }
                cabangAdapter.notifyDataSetChanged()
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@CabangActivity, "Gagal mengambil data cabang", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun resetForm() {
        selectedCabangId = null
        edtNamaCabang.text.clear(); edtAlamatCabang.text.clear(); edtTeleponCabang.text.clear()
        btnSimpanCabang.text = "Simpan Cabang"
    }
}
