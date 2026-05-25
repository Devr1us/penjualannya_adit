package com.adit.penjualannya_adit.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.adit.penjualannya_adit.R
import com.adit.penjualannya_adit.Model.Produk

class ProdukTransaksiAdapter(
    private var list: List<Produk>,
    private val onAdd: (Produk) -> Unit
) : RecyclerView.Adapter<ProdukTransaksiAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNama: TextView    = view.findViewById(R.id.txtNamaOrder)
        val tvHarga: TextView   = view.findViewById(R.id.txtHargaOrder)
        val tvStok: TextView    = view.findViewById(R.id.txtStokOrder)
        val btnTambah: TextView = view.findViewById(R.id.btnPlus)
        val btnMinus: TextView  = view.findViewById(R.id.btnMinus)
        val tvJumlah: TextView  = view.findViewById(R.id.txtJumlahOrder)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order_product, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = holder.tvNama.context  // just for reference; use item below
        val produk = list[position]

        holder.tvNama.text  = produk.namaProduk
        holder.tvHarga.text = "Rp %,d".format(produk.hargaJual.toInt())
        holder.tvStok.text  = if (produk.stokTakTerbatas) "Stok: ∞" else "Stok: ${produk.stok}"

        // Sembunyikan minus dan jumlah di tampilan daftar produk (hanya tombol tambah yang perlu)
        holder.btnMinus.visibility  = View.GONE
        holder.tvJumlah.visibility  = View.GONE

        // Klik tombol + ATAU seluruh card untuk menambah ke keranjang
        holder.btnTambah.setOnClickListener { onAdd(produk) }
        holder.itemView.setOnClickListener  { onAdd(produk) }
    }

    override fun getItemCount(): Int = list.size

    fun updateData(newList: List<Produk>) {
        list = newList
        notifyDataSetChanged()
    }
}
