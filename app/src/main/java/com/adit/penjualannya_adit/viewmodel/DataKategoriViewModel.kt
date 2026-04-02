package com.adit.penjualannya_adit.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.adit.penjualannya_adit.Model.ModelKategori
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DataKategoriViewModel : ViewModel() {
    private val database = FirebaseDatabase.getInstance()
    private val myRef = database.getReference("kategori")

    val kategoriList = MutableLiveData<List<ModelKategori>>()
    private val originalKategoriList = ArrayList<ModelKategori>()

    val isLoading = MutableLiveData<Boolean>()
    val isSearchEmpty = MutableLiveData<Boolean>()

    private var valueEventListener: ValueEventListener? = null

    init {
        getData()
    }

    fun getData() {
        isLoading.postValue(true)

        valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                isLoading.postValue(false)
                if (snapshot.exists()) {
                    val list = ArrayList<ModelKategori>()
                    for (dataSnapshot in snapshot.children) {
                        val kategori = dataSnapshot.getValue(ModelKategori::class.java)
                        if (kategori != null) {
                            list.add(kategori)
                        }
                    }
                    originalKategoriList.clear()
                    originalKategoriList.addAll(list)
                    isSearchEmpty.postValue(false)
                    kategoriList.postValue(ArrayList(originalKategoriList))
                } else {
                    originalKategoriList.clear()
                    isSearchEmpty.postValue(true)
                    kategoriList.postValue(emptyList())
                }
            }

            override fun onCancelled(error: DatabaseError) {
                isLoading.postValue(false)
                Log.e("DataKategoriViewModel", "onCancelled: ${error.message}", error.toException())
            }
        }
        valueEventListener?.let { myRef.addValueEventListener(it) }
    }

    fun searchKategori(keyword: String) {
        if (keyword.isEmpty()) {
            isSearchEmpty.postValue(originalKategoriList.isEmpty())
            kategoriList.postValue(ArrayList(originalKategoriList))
            return
        }
        val filtered = originalKategoriList.filter { kategori ->
            kategori.namaKategori.contains(keyword, ignoreCase = true)
        }
        isSearchEmpty.postValue(filtered.isEmpty())
        kategoriList.postValue(filtered)
    }

    override fun onCleared() {
        super.onCleared()
        valueEventListener?.let { myRef.removeEventListener(it) }
    }
}