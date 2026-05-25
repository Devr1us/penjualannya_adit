package com.adit.penjualannya_adit.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.adit.penjualannya_adit.R
import com.adit.penjualannya_adit.Model.Produk
import com.google.android.material.button.MaterialButton

class ProdukTransaksiAdapter(
    private var list: List<Produk>,
    private val onAdd: (Produk) -> Unit
) : RecyclerView.Adapter<ProdukTransaksiAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivProduk: ImageView = view.findViewById(R.id.ivProduk)
        val tvNama: TextView = view.findViewById(R.id.tvNamaProduk)
        val tvHarga: TextView = view.findViewById(R.id.tvHargaProduk)
        val tvStok: TextView = view.findViewById(R.id.tvStokProduk)
        val btnTambah: MaterialButton = view.findViewById(R.id.btnTambahProduk)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaksi_produk, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val produk = list[position]

        holder.tvNama.text = produk.namaProduk
        holder.tvHarga.text = "Rp %,d".format(produk.hargaJual.toInt())
        holder.tvStok.text = if (produk.stokTakTerbatas) "Stok: ∞" else "Stok: ${produk.stok}"

        val bisaTambah = produk.stokTakTerbatas || produk.stok > 0
        holder.btnTambah.isEnabled = bisaTambah
        holder.btnTambah.text = if (bisaTambah) "+ Tambah" else "Habis"

        // Klik tombol atau card untuk menambah ke keranjang
        holder.btnTambah.setOnClickListener {
            if (bisaTambah) onAdd(produk)
        }
        holder.itemView.setOnClickListener {
            if (bisaTambah) onAdd(produk)
        }
    }

    override fun getItemCount(): Int = list.size

    fun updateData(newList: List<Produk>) {
        list = newList
        notifyDataSetChanged()
    }
}
