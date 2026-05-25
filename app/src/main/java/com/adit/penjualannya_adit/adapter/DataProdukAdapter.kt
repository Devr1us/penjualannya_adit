package com.adit.penjualannya_adit.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.adit.penjualannya_adit.Model.Produk
import com.adit.penjualannya_adit.R
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip

class DataProdukAdapter(
    private var listProduk: MutableList<Produk>,
    private val onItemClick: (Produk) -> Unit
) : RecyclerView.Adapter<DataProdukAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgProduk: ImageView = itemView.findViewById(R.id.img_produk)
        val tvNama: TextView = itemView.findViewById(R.id.tv_nama_produk)
        val tvHarga: TextView = itemView.findViewById(R.id.tv_harga_produk)
        val tvStok: TextView = itemView.findViewById(R.id.tv_stok_produk)
        val tvKategori: TextView = itemView.findViewById(R.id.tv_kategori_produk)
        val chipStatus: Chip = itemView.findViewById(R.id.chip_status)

        fun bind(produk: Produk) {
            tvNama.text = produk.namaProduk
            tvHarga.text = "Rp ${String.format("%,.0f", produk.hargaJual)}"
            tvStok.text = if (produk.stokTakTerbatas) "Stok: ∞ (Tak Terbatas)" else "Stok: ${produk.stok} pcs"
            tvKategori.text = "Kategori: ${produk.kategoriNama.ifEmpty { "-" }}"
            chipStatus.text = produk.status

            if (!produk.fotoUrl.isNullOrEmpty()) {
                Glide.with(itemView.context)
                    .load(produk.fotoUrl)
                    .placeholder(R.color.background_gray)
                    .error(R.color.background_gray)
                    .centerCrop()
                    .into(imgProduk)
            }

            itemView.setOnClickListener { onItemClick(produk) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_data_produk, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listProduk[position])
    }

    override fun getItemCount(): Int = listProduk.size

    fun updateData(newList: List<Produk>) {
        listProduk.clear()
        listProduk.addAll(newList)
        notifyDataSetChanged()
    }
}
