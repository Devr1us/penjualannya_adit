package com.adit.penjualannya_adit.model

import android.os.Parcel
import android.os.Parcelable

data class ModelKategoriTK(
    var idKategori: String? = null,
    var namaKategori: String? = null,
    var statusKategori: String? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(parcel.readString(), parcel.readString(), parcel.readString())
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(idKategori); parcel.writeString(namaKategori); parcel.writeString(statusKategori)
    }
    override fun describeContents() = 0
    companion object CREATOR : Parcelable.Creator<ModelKategoriTK> {
        override fun createFromParcel(parcel: Parcel) = ModelKategoriTK(parcel)
        override fun newArray(size: Int) = arrayOfNulls<ModelKategoriTK>(size)
    }
}
