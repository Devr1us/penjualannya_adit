package com.adit.penjualannya_adit

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class OrderProductAdapter(
    private val productList: MutableList<OrderProduct>,
    private val onChanged: () -> Unit
) : RecyclerView.Adapter<OrderProductAdapter.OrderViewHolder>() {

    class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtNama: TextView = itemView.findViewById(R.id.txtNamaOrder)
        val txtHarga: TextView = itemView.findViewById(R.id.txtHargaOrder)
        val txtStok: TextView = itemView.findViewById(R.id.txtStokOrder)
        val txtJumlah: TextView = itemView.findViewById(R.id.txtJumlahOrder)
        val btnMinus: TextView = itemView.findViewById(R.id.btnMinus)
        val btnPlus: TextView = itemView.findViewById(R.id.btnPlus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order_product, parent, false)

        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val product = productList[position]

        holder.txtNama.text = product.nama
        holder.txtHarga.text = "Rp ${product.harga}"
        holder.txtStok.text = "Stok: ${product.stok}"
        holder.txtJumlah.text = product.jumlahPesan.toString()

        holder.btnPlus.setOnClickListener {
            if (product.jumlahPesan < product.stok) {
                product.jumlahPesan++
                notifyItemChanged(holder.adapterPosition)
                onChanged()
            }
        }

        holder.btnMinus.setOnClickListener {
            if (product.jumlahPesan > 0) {
                product.jumlahPesan--
                notifyItemChanged(holder.adapterPosition)
                onChanged()
            }
        }
    }

    override fun getItemCount(): Int = productList.size
}
