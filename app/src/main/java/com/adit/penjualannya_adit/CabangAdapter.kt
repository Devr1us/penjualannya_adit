package com.adit.penjualannya_adit

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase

class CabangAdapter(
    private val cabangList: MutableList<Cabang>,
    private val onEdit: (Cabang) -> Unit
) : RecyclerView.Adapter<CabangAdapter.CabangViewHolder>() {

    private val db = FirebaseDatabase.getInstance()

    class CabangViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtNamaCabang: TextView = itemView.findViewById(R.id.txtNamaCabang)
        val txtAlamatCabang: TextView = itemView.findViewById(R.id.txtAlamatCabang)
        val txtTeleponCabang: TextView = itemView.findViewById(R.id.txtTeleponCabang)
        val btnEditCabang: Button = itemView.findViewById(R.id.btnEditCabang)
        val btnHapusCabang: Button = itemView.findViewById(R.id.btnHapusCabang)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CabangViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cabang, parent, false)
        return CabangViewHolder(view)
    }

    override fun onBindViewHolder(holder: CabangViewHolder, position: Int) {
        val cabang = cabangList[position]
        holder.txtNamaCabang.text = cabang.nama
        holder.txtAlamatCabang.text = cabang.alamat
        holder.txtTeleponCabang.text = cabang.telepon

        holder.btnEditCabang.setOnClickListener { onEdit(cabang) }

        holder.btnHapusCabang.setOnClickListener {
            AlertDialog.Builder(holder.itemView.context)
                .setTitle("Hapus Cabang")
                .setMessage("Yakin ingin menghapus cabang ini?")
                .setPositiveButton("Hapus") { _, _ ->
                    db.getReference("branches").child(cabang.id).removeValue()
                        .addOnSuccessListener {
                            cabangList.removeAt(position)
                            notifyItemRemoved(position)
                        }
                }
                .setNegativeButton("Batal", null)
                .show()
        }
    }

    override fun getItemCount(): Int = cabangList.size
}
