package com.adit.penjualannya_adit

data class Pegawai(
    val id: String = "",
    val nama: String = "",
    val email: String = "",
    val telepon: String = "",
    val jabatan: String = "",
    val cabangId: String = "",
    val cabangNama: String = "",
    val aktif: Boolean = true
)
