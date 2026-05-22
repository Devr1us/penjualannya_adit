package com.adit.penjualannya_adit

import android.content.Context
import android.content.SharedPreferences

/**
 * SessionManager untuk mengelola sesi login pengguna secara lokal menggunakan SharedPreferences.
 * File ini diletakkan di folder login_feature_implementation/kotlin/
 */
class SessionManager(private val context: Context) {

    private val pref: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = pref.edit()

    companion object {
        private const val PREF_NAME = "UserSessionPref"
        private const val KEY_IS_LOGGED_IN = "isLoggedIn"
        private const val KEY_USERNAME = "username"
        private const val KEY_NAME = "name"
        private const val KEY_EMAIL = "email"
    }

    /**
     * Membuat sesi login pengguna baru
     */
    fun createLoginSession(username: String, name: String, email: String) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true)
        editor.putString(KEY_USERNAME, username)
        editor.putString(KEY_NAME, name)
        editor.putString(KEY_EMAIL, email)
        editor.apply()
    }

    /**
     * Memeriksa apakah pengguna sudah login atau belum
     */
    fun isLoggedIn(): Boolean {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    /**
     * Mendapatkan nama pengguna yang sedang aktif
     */
    fun getName(): String? {
        return pref.getString(KEY_NAME, null)
    }

    /**
     * Mendapatkan username pengguna yang sedang aktif
     */
    fun getUsername(): String? {
        return pref.getString(KEY_USERNAME, null)
    }

    /**
     * Mendapatkan email pengguna yang sedang aktif
     */
    fun getEmail(): String? {
        return pref.getString(KEY_EMAIL, null)
    }

    /**
     * Menghapus sesi login pengguna (Logout)
     */
    fun logoutUser() {
        editor.clear()
        editor.apply()
    }
}
