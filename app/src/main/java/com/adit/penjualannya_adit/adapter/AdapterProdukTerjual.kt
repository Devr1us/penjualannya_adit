package com.adit.penjualannya_adit.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.adit.penjualannya_adit.R
import com.adit.penjualannya_adit.Model.ItemProdukTerjual
import android.widget.TextView

class AdapterProdukTerjual(private val listProduk: List<ItemProdukTerjual>) : 
    RecyclerView.Adapter<AdapterProdukTerjual.ViewHolder>() {

    inner class ViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {
        val tvNamaProduk: TextView = itemView.findViewById(R.id.tvNamaProduk)
        val tvJumlah: TextView = itemView.findViewById(R.id.tvJumlah)
        val tvHargaTotal: TextView = itemView.findViewById(R.id.tvHargaTotal)

        fun bind(item: ItemProdukTerjual) {
            tvNamaProduk.text = item.namaProduk
            tvJumlah.text = "${item.jumlah}x"
            tvHargaTotal.text = "Rp %,d".format(item.totalHarga).replace(',', '.')
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_produk_terjual, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listProduk[position])
    }

    override fun getItemCount(): Int = listProduk.size
}
