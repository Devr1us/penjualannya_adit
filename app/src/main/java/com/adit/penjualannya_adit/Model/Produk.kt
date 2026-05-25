package com.adit.penjualannya_adit.Model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Produk(
    var id: String = "",
    var namaProduk: String = "",
    var hargaJual: Double = 0.0,
    var kategoriId: String = "",
    var kategoriNama: String = "", // Sesuai dengan yang disimpan di TambahProduk
    var cabangId: String = "",
    var cabangNama: String = "",   // Sesuai dengan yang disimpan di TambahProduk
    var status: String = "Aktif",
    var stok: Int = 0,
    var stokTakTerbatas: Boolean = false,
    var fotoUrl: String = "",
    var barcode: String = ""
)
