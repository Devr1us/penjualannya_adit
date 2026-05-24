package com.adit.penjualannya_adit.model

import android.os.Parcel
import android.os.Parcelable

data class ModelCartItem(
    var produk: ModelProdukTK? = null,
    var jumlah: Int = 0
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readParcelable(ModelProdukTK::class.java.classLoader),
        parcel.readInt()
    )
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(produk, flags); parcel.writeInt(jumlah)
    }
    override fun describeContents() = 0
    companion object CREATOR : Parcelable.Creator<ModelCartItem> {
        override fun createFromParcel(parcel: Parcel) = ModelCartItem(parcel)
        override fun newArray(size: Int) = arrayOfNulls<ModelCartItem>(size)
    }
}
