package com.adit.penjualannya_adit.Model

data class ModelTransaksi(
    var idTransaksi: String? = null,
    var listProduk: List<ModelCartItem>? = null,
    var totalHarga: Int = 0,
    var diskon: Int = 0,
    var subtotal: Int = 0,
    var idPegawai: String? = null,
    var namaPegawai: String? = null,
    var tanggal: String? = null,
    var metodePembayaran: String? = "Tunai",
    var status: String? = "Selesai"
)
