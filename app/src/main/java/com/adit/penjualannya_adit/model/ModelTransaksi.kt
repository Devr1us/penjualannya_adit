package com.adit.penjualannya_adit.model

import android.os.Parcel
import android.os.Parcelable

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
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.createTypedArrayList(ModelCartItem.CREATOR),
        parcel.readInt(), parcel.readInt(), parcel.readInt(),
        parcel.readString(), parcel.readString(), parcel.readString(),
        parcel.readString(), parcel.readString()
    )
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(idTransaksi); parcel.writeTypedList(listProduk)
        parcel.writeInt(totalHarga); parcel.writeInt(diskon); parcel.writeInt(subtotal)
        parcel.writeString(idPegawai); parcel.writeString(namaPegawai); parcel.writeString(tanggal)
        parcel.writeString(metodePembayaran); parcel.writeString(status)
    }
    override fun describeContents() = 0
    companion object CREATOR : Parcelable.Creator<ModelTransaksi> {
        override fun createFromParcel(parcel: Parcel) = ModelTransaksi(parcel)
        override fun newArray(size: Int) = arrayOfNulls<ModelTransaksi>(size)
    }
}
