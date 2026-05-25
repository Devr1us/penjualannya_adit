package com.adit.penjualannya_adit

data class OrderProduct(
    val id: String = "",
    val nama: String = "",
    val harga: Long = 0,
    val stok: Long = 0,
    var jumlahPesan: Int = 0
)
