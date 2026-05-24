package com.adit.penjualannya_adit.model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Produk(
    var id: String = "",
    var namaProduk: String = "",
    var hargaJual: Double = 0.0,
    var kategoriNama: String = "",
    var cabangNama: String = "",
    var status: String = "Aktif",
    var stok: Int = 0,
    var stokTakTerbatas: Boolean = false,
    var fotoUrl: String = "",
    var barcode: String = ""
)
