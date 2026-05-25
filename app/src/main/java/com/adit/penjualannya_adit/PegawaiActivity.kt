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
import kotlin.random.Random

class PegawaiActivity : AppCompatActivity() {

    private lateinit var db: FirebaseDatabase
    private lateinit var refPegawai: DatabaseReference

    private lateinit var edtNamaPegawai: EditText
    private lateinit var edtEmailPegawai: EditText
    private lateinit var edtTeleponPegawai: EditText
    private lateinit var edtJabatanPegawai: EditText
    private lateinit var spinnerCabang: Spinner
    private lateinit var switchStatusPegawai: Switch
    private lateinit var btnSimpanPegawai: Button
    private lateinit var recyclerPegawai: RecyclerView

    private val pegawaiList = mutableListOf<Pegawai>()
    private lateinit var pegawaiAdapter: PegawaiAdapter

    private val cabangNamaList = mutableListOf<String>()
    private val cabangIdList = mutableListOf<String>()
    private var selectedPegawaiId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pegawai)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        db = FirebaseDatabase.getInstance()
        refPegawai = db.getReference("employees")

        edtNamaPegawai = findViewById(R.id.edtNamaPegawai)
        edtEmailPegawai = findViewById(R.id.edtEmailPegawai)
        edtTeleponPegawai = findViewById(R.id.edtTeleponPegawai)
        edtJabatanPegawai = findViewById(R.id.edtJabatanPegawai)
        spinnerCabang = findViewById(R.id.spinnerCabang)
        switchStatusPegawai = findViewById(R.id.switchStatusPegawai)
        btnSimpanPegawai = findViewById(R.id.btnSimpanPegawai)
        recyclerPegawai = findViewById(R.id.recyclerPegawai)

        pegawaiAdapter = PegawaiAdapter(pegawaiList) { pegawai ->
            selectedPegawaiId = pegawai.id
            edtNamaPegawai.setText(pegawai.nama)
            edtEmailPegawai.setText(pegawai.email)
            edtTeleponPegawai.setText(pegawai.telepon)
            edtJabatanPegawai.setText(pegawai.jabatan)
            switchStatusPegawai.isChecked = pegawai.aktif
            val index = cabangNamaList.indexOf(pegawai.cabangNama)
            if (index >= 0) spinnerCabang.setSelection(index)
            btnSimpanPegawai.text = "Update Pegawai"
        }

        recyclerPegawai.layoutManager = LinearLayoutManager(this)
        recyclerPegawai.adapter = pegawaiAdapter

        loadCabang()
        tampilkanPegawai()

        btnSimpanPegawai.setOnClickListener { simpanPegawai() }

        // Tombol back jika ada
        try { findViewById<ImageView>(R.id.ivBack)?.setOnClickListener { finish() } } catch (_: Exception) {}
    }

    private fun loadCabang() {
        db.getReference("branches").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                cabangNamaList.clear(); cabangIdList.clear()
                for (snap in snapshot.children) {
                    cabangIdList.add(snap.key ?: "")
                    cabangNamaList.add(snap.child("nama").getValue(String::class.java) ?: "-")
                }
                val adapter = ArrayAdapter(this@PegawaiActivity, android.R.layout.simple_spinner_item, cabangNamaList)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerCabang.adapter = adapter
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun simpanPegawai() {
        val nama = edtNamaPegawai.text.toString().trim()
        val email = edtEmailPegawai.text.toString().trim()
        val telepon = edtTeleponPegawai.text.toString().trim()
        val jabatan = edtJabatanPegawai.text.toString().trim()
        val aktif = switchStatusPegawai.isChecked

        if (nama.isEmpty() || email.isEmpty() || telepon.isEmpty() || jabatan.isEmpty()) {
            Toast.makeText(this, "Semua data wajib diisi", Toast.LENGTH_SHORT).show()
            return
        }

        val cabangIndex = spinnerCabang.selectedItemPosition
        val cabangId = if (cabangIdList.isNotEmpty()) cabangIdList[cabangIndex] else ""
        val cabangNama = if (cabangNamaList.isNotEmpty()) cabangNamaList[cabangIndex] else ""
        val idPegawai = selectedPegawaiId ?: refPegawai.push().key ?: "PG${Random.nextInt(10000, 99999)}"

        val pegawai = hashMapOf(
            "nama" to nama, "email" to email, "telepon" to telepon,
            "jabatan" to jabatan, "cabangId" to cabangId, "cabangNama" to cabangNama,
            "aktif" to aktif, "updatedAt" to System.currentTimeMillis()
        )

        refPegawai.child(idPegawai).setValue(pegawai).addOnSuccessListener {
            Toast.makeText(this, "Pegawai berhasil disimpan", Toast.LENGTH_SHORT).show()
            resetForm()
        }.addOnFailureListener {
            Toast.makeText(this, "Gagal menyimpan pegawai", Toast.LENGTH_SHORT).show()
        }
    }

    private fun tampilkanPegawai() {
        refPegawai.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                pegawaiList.clear()
                for (snap in snapshot.children) {
                    val pegawai = Pegawai(
                        id = snap.key ?: "",
                        nama = snap.child("nama").getValue(String::class.java) ?: "-",
                        email = snap.child("email").getValue(String::class.java) ?: "-",
                        telepon = snap.child("telepon").getValue(String::class.java) ?: "-",
                        jabatan = snap.child("jabatan").getValue(String::class.java) ?: "-",
                        cabangId = snap.child("cabangId").getValue(String::class.java) ?: "-",
                        cabangNama = snap.child("cabangNama").getValue(String::class.java) ?: "-",
                        aktif = snap.child("aktif").getValue(Boolean::class.java) ?: true
                    )
                    pegawaiList.add(pegawai)
                }
                pegawaiAdapter.notifyDataSetChanged()
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun resetForm() {
        selectedPegawaiId = null
        edtNamaPegawai.text.clear(); edtEmailPegawai.text.clear()
        edtTeleponPegawai.text.clear(); edtJabatanPegawai.text.clear()
        switchStatusPegawai.isChecked = true
        btnSimpanPegawai.text = "Simpan Pegawai"
    }
}
