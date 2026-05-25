# Panduan Integrasi Fitur Login & Akun

Dokumen ini menjelaskan langkah demi langkah untuk mengintegrasikan kode Kotlin dan layout XML yang berada di folder `login_feature_implementation/` ke dalam proyek Android utama Anda (`app/`).

---

## Langkah 1: Memindahkan File Sumber (Source Files)

### 1. Salin File Kotlin ke Folder Package
Pindahkan/salin seluruh file Kotlin dari `login_feature_implementation/kotlin/` ke folder package utama aplikasi Anda:
👉 `app/src/main/java/com/adit/penjualannya_adit/`

File yang dipindahkan:
- `SessionManager.kt`
- `LoginActivity.kt`
- `RegisterActivity.kt`
- `AccountActivity.kt`

### 2. Salin Layout XML ke Folder Layout Resources
Pindahkan/salin seluruh file layout XML dari `login_feature_implementation/layouts/` ke folder layout proyek Anda:
👉 `app/src/main/res/layout/`

File yang dipindahkan:
- `activity_login.xml`
- `activity_register.xml`
- `activity_account.xml`

---

## Langkah 2: Daftarkan Activity Baru di `AndroidManifest.xml`

Buka file [AndroidManifest.xml](file:///e:/penjualannya_adit/penjualannya_adit-master/app/src/main/AndroidManifest.xml) dan daftarkan ketiga activity baru Anda di dalam tag `<application>`. 

Untuk membuat halaman **Login** muncul pertama kali saat aplikasi dijalankan, pindahkan elemen `<intent-filter>` (Launcher) dari `MainActivity2` ke `LoginActivity`.

Lakukan perubahan sebagai berikut:

```xml
        <!-- Halaman Login sebagai Launcher Utama -->
        <activity
            android:name=".LoginActivity"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>

        <!-- Halaman Registrasi Akun Baru -->
        <activity
            android:name=".RegisterActivity"
            android:exported="false" />

        <!-- Halaman Detail Profil Akun & Logout -->
        <activity
            android:name=".AccountActivity"
            android:exported="false" />

        <!-- MainActivity2 (Dashboard Utama) -->
        <activity
            android:name=".MainActivity2"
            android:exported="false" />
```

---

## Langkah 3: Sambungkan Tombol Akun di `MainActivity2.kt`

Buka file [MainActivity2.kt](file:///e:/penjualannya_adit/penjualannya_adit-master/app/src/main/java/com/adit/penjualannya_adit/MainActivity2.kt) untuk menghubungkan tombol **Akun** (cardAkun) dan menyambut pengguna dengan nama yang mereka gunakan saat mendaftar secara dinamis.

### 1. Deklarasikan SessionManager
Di dalam `onCreate`, panggil `SessionManager` untuk mendapatkan sesi yang sedang aktif:

```kotlin
        val sessionManager = SessionManager(this)
```

### 2. Update Sapaan Nama Pengguna secara Dinamis
Ganti teks sapaan statis `tvGreeting` agar mengambil nama asli dari sesi login:

```kotlin
        val tvGreeting = findViewById<TextView>(R.id.tvGreeting)
        if (sessionManager.isLoggedIn()) {
            tvGreeting.text = "Selamat Morning, ${sessionManager.getName()}"
        }
```

### 3. Arahkan Klik Card Akun ke AccountActivity
Tambahkan event listener untuk mendeteksi ketukan pada Card Akun di dalam `onCreate`:

```kotlin
        findViewById<androidx.cardview.widget.CardView>(R.id.cardAkun).setOnClickListener {
            startActivity(Intent(this, AccountActivity::class.java))
        }
```

---

## Langkah 4: Jalankan dan Verifikasi Proyek

1. **Jalankan Aplikasi:** Saat pertama kali aplikasi di-run, Anda akan langsung disambut oleh halaman **Login**.
2. **Daftarkan Akun Baru:** Ketuk teks *"Belum punya akun? Daftar disini"*, isi seluruh kolom pendaftaran, lalu tekan tombol **Daftar Baru**. Aplikasi otomatis mengarahkan Anda kembali ke halaman Login.
3. **Proses Login:** Masukkan username dan password yang baru saja Anda daftarkan. Data Anda akan diverifikasi ke Firebase Database. Begitu sukses, Anda langsung diarahkan ke Dashboard utama (`MainActivity2`).
4. **Validasi Persistensi Sesi:** Tutup paksa (*force close*) aplikasi Anda, lalu jalankan kembali. Aplikasi akan langsung membuka Dashboard utama tanpa meminta login ulang karena sesi tersimpan dengan aman di SharedPreferences.
5. **Aksi Keluar Akun:** Di Dashboard utama, ketuk tombol **Akun**. Anda akan disambut secara personal dan melihat info profil Anda. Ketuk **Keluar dari Akun** untuk menghapus sesi dan kembali ke halaman utama Login.
