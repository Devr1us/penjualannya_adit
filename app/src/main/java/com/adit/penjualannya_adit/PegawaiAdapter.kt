package com.adit.penjualannya_adit

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase

class PegawaiAdapter(
    private val pegawaiList: MutableList<Pegawai>,
    private val onEdit: (Pegawai) -> Unit
) : RecyclerView.Adapter<PegawaiAdapter.PegawaiViewHolder>() {

    private val db = FirebaseDatabase.getInstance()

    class PegawaiViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtIdPegawai: TextView = itemView.findViewById(R.id.txtIdPegawai)
        val txtNamaPegawai: TextView = itemView.findViewById(R.id.txtNamaPegawai)
        val txtJabatanPegawai: TextView = itemView.findViewById(R.id.txtJabatanPegawai)
        val txtCabangPegawai: TextView = itemView.findViewById(R.id.txtCabangPegawai)
        val txtStatusPegawai: TextView = itemView.findViewById(R.id.txtStatusPegawai)
        val btnEditPegawai: Button = itemView.findViewById(R.id.btnEditPegawai)
        val btnHapusPegawai: Button = itemView.findViewById(R.id.btnHapusPegawai)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PegawaiViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pegawai, parent, false)
        return PegawaiViewHolder(view)
    }

    override fun onBindViewHolder(holder: PegawaiViewHolder, position: Int) {
        val pegawai = pegawaiList[position]
        holder.txtIdPegawai.text = pegawai.id
        holder.txtNamaPegawai.text = pegawai.nama
        holder.txtJabatanPegawai.text = pegawai.jabatan
        holder.txtCabangPegawai.text = "Cabang: ${pegawai.cabangNama}"
        holder.txtStatusPegawai.text = if (pegawai.aktif) "Status: Aktif" else "Status: Tidak Aktif"

        holder.btnEditPegawai.setOnClickListener { onEdit(pegawai) }

        holder.btnHapusPegawai.setOnClickListener {
            AlertDialog.Builder(holder.itemView.context)
                .setTitle("Hapus Pegawai")
                .setMessage("Yakin ingin menghapus pegawai?")
                .setPositiveButton("Hapus") { _, _ ->
                    db.getReference("employees").child(pegawai.id).removeValue()
                        .addOnSuccessListener {
                            pegawaiList.removeAt(position)
                            notifyItemRemoved(position)
                        }
                }
                .setNegativeButton("Batal", null)
                .show()
        }
    }

    override fun getItemCount(): Int = pegawaiList.size
}
