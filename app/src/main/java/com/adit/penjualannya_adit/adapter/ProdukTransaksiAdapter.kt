package com.adit.penjualannya_adit.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.adit.penjualannya_adit.R
import com.adit.penjualannya_adit.Model.Produk

class ProdukTransaksiAdapter(
    private var list: List<Produk>,
    private val onAdd: (Produk) -> Unit
) : RecyclerView.Adapter<ProdukTransaksiAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNama: TextView = view.findViewById(R.id.txtNamaOrder)
        val tvHarga: TextView = view.findViewById(R.id.txtHargaOrder)
        val tvStok: TextView = view.findViewById(R.id.txtStokOrder)
        val btnTambah: TextView = view.findViewById(R.id.btnPlus)
        val btnMinus: TextView = view.findViewById(R.id.btnMinus)
        val tvJumlah: TextView = view.findViewById(R.id.txtJumlahOrder)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_order_product, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.tvNama.text = item.namaProduk
        holder.tvHarga.text = "Rp %,d".format(item.hargaJual.toInt())
        holder.tvStok.text = if (item.stokTakTerbatas) "Stok: ∞" else "Stok: ${item.stok}"
        
        holder.btnMinus.visibility = View.GONE
        holder.tvJumlah.visibility = View.GONE
        
        holder.btnTambah.setOnClickListener { onAdd(item) }
    }

    override fun getItemCount(): Int = list.size

    fun updateData(newList: List<Produk>) {
        list = newList
        notifyDataSetChanged()
    }
}
