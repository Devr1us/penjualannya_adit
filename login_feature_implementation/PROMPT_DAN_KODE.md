# Rangkuman Implementasi Fitur Autentikasi (Login, Register & Akun)

Dokumen ini dibuat khusus untuk menambahkan prompt Anda ke dalam proyek tanpa mengubah satupun file kode bawaan yang ada di proyek utama (`app/`). Di sini disajikan kode lengkap **HTML (untuk web demo/simulasi)** dan **Kotlin & XML (untuk aplikasi Android)** sesuai dengan alur sistem yang Anda minta.

---

## 📌 Prompt Permintaan Pengguna

> *"dari video tersebut, buatkan kode kotlin dan html untuk tampilan login jika belum pernah login ada mendaftar, dan jika sudah mendaftar lalu akan diarahkan ke login, jika nanti sudah punya akun dan masuk ke aplikasi, langsung disambut dengan akun yang pernah login tersebut, dan ada opsi untuk login ataupun daftar lagi. itu untuk fitur login ( itu masuk nya di akun ya) berarti nanti setelah apk di run langsung muncul ui untuk login, dan jika nanti ingin logout akun juga ke tampilan ui yang bertuliskan akun. tambahkan prompt ini ke project yang tertera ya, tanpa mengubah satupun file"*

---

## 🌟 Ringkasan Alur Fitur yang Diimplementasikan

Sesuai dengan permintaan Anda, alur kerja sistem autentikasi ini telah dirancang sebagai berikut:
1. **Pertama Kali Dijalankan (Startup):** Aplikasi/Demo Web akan **langsung memunculkan UI Login** jika pengguna belum pernah masuk.
2. **Pendaftaran Akun (Register):** Terdapat opsi *"Belum punya akun? Daftar disini"*. Ketika dipilih, pengguna diarahkan ke halaman Registrasi.
3. **Redirect setelah Daftar:** Setelah menekan tombol daftar dan registrasi berhasil, pengguna **langsung diarahkan kembali ke UI Login** (dengan input username yang terisi otomatis agar praktis).
4. **Penyambutan Sesi Aktif (Persistent Session):** Jika pengguna sudah berhasil login dan kemudian keluar-masuk aplikasi lagi, sistem akan **langsung menyambut pengguna dengan profil akunnya** yang aktif (tidak perlu login ulang).
5. **UI Detail Akun (Profile & Log Out):** Pada UI Akun ini, terdapat detail profil dan dua opsi penting:
   - **Tombol Keluar dari Akun (Logout):** Menghapus sesi login aktif secara permanen dan mengarahkan kembali ke **UI Login**.
   - **Tombol Daftar / Masuk Akun Lain:** Menghapus sesi saat ini dan mengarahkan langsung ke **UI Registrasi** untuk mendaftarkan akun baru.

---

## 📁 Struktur File dalam Folder `login_feature_implementation/`

Semua kode ini telah diletakkan secara terpisah di dalam folder khusus agar aman dan tidak memengaruhi file utama Anda:

```text
login_feature_implementation/
├── login_demo.html                # Simulasi interaktif berbasis HTML/JS/CSS (Premium)
├── panduan_integrasi.md           # Langkah-langkah penyalinan file ke folder utama
├── kotlin/
│   ├── LoginActivity.kt           # Aktivitas Login (Launcher Utama)
│   ├── RegisterActivity.kt        # Aktivitas Registrasi Akun Baru
│   ├── AccountActivity.kt         # Aktivitas Profil Akun, Logout & Tukar Sesi
│   └── SessionManager.kt          # Pengelola persistent session SharedPreferences
└── layouts/
    ├── activity_login.xml         # Layout XML untuk Login
    ├── activity_register.xml      # Layout XML untuk Registrasi
    └── activity_account.xml       # Layout XML untuk Profil Akun
```

---

## 🖥️ 1. Simulasi Interaktif HTML/JS (`login_demo.html`)
Demo HTML premium yang menggunakan **LocalStorage** untuk mensimulasikan database pengguna serta status login persisten di browser secara real-time.

```html
<!DOCTYPE html>
<html lang="id">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Demo Autentikasi Penjualan Adit</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Outfit:wght@300;400;500;600;700;800&display=swap" rel="stylesheet">
    <style>
        :root {
            --primary: #9C27B0;
            --primary-light: #CE93D8;
            --secondary: #00BFA5;
            --secondary-dark: #008786;
            --background: #0f0c1b;
            --card-bg: rgba(255, 255, 255, 0.08);
            --card-border: rgba(255, 255, 255, 0.15);
            --text-main: #ffffff;
            --text-muted: #b0aec4;
            --danger: #ff334b;
        }
        * {
            margin: 0; padding: 0; box-sizing: border-box;
            font-family: 'Outfit', sans-serif;
            transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
        }
        body {
            background: radial-gradient(circle at 10% 20%, rgb(26, 20, 48) 0%, rgb(9, 7, 18) 90.2%);
            color: var(--text-main); min-height: 100vh;
            display: flex; justify-content: center; align-items: center; padding: 20px; overflow-x: hidden;
        }
        .blob-container {
            position: absolute; width: 100%; height: 100%; overflow: hidden; top: 0; left: 0; z-index: 1; pointer-events: none;
        }
        .blob { position: absolute; border-radius: 50%; filter: blur(80px); opacity: 0.2; }
        .blob-1 { width: 400px; height: 400px; background: var(--primary); top: -100px; left: -100px; animation: float 15s ease-in-out infinite alternate; }
        .blob-2 { width: 350px; height: 350px; background: var(--secondary); bottom: -50px; right: -50px; animation: float 12s ease-in-out infinite alternate-reverse; }
        @keyframes float { 0% { transform: translateY(0) scale(1); } 100% { transform: translateY(50px) scale(1.1); } }
        .card-container {
            position: relative; z-index: 10; width: 100%; max-width: 460px;
            background: var(--card-bg); backdrop-filter: blur(16px); -webkit-backdrop-filter: blur(16px);
            border: 1px solid var(--card-border); border-radius: 24px; padding: 40px;
            box-shadow: 0 20px 40px rgba(0, 0, 0, 0.4); overflow: hidden;
        }
        .header { text-align: center; margin-bottom: 30px; }
        .logo-icon {
            display: inline-flex; justify-content: center; align-items: center;
            width: 70px; height: 70px; background: linear-gradient(135deg, var(--primary), var(--secondary));
            border-radius: 20px; margin-bottom: 20px; box-shadow: 0 8px 24px rgba(156, 39, 176, 0.3);
            font-size: 32px; animation: pulse 3s infinite;
        }
        @keyframes pulse { 0% { transform: scale(1); } 50% { transform: scale(1.05); box-shadow: 0 8px 30px rgba(0, 191, 165, 0.4); } 100% { transform: scale(1); } }
        h2 { font-size: 28px; font-weight: 700; background: linear-gradient(135deg, #ffffff 60%, #ce93d8 100%); -webkit-background-clip: text; -webkit-text-fill-color: transparent; margin-bottom: 8px; }
        .subtitle { font-size: 14px; color: var(--text-muted); }
        .form-group { position: relative; margin-bottom: 22px; }
        label { display: block; font-size: 13px; font-weight: 600; margin-bottom: 8px; color: var(--primary-light); letter-spacing: 0.5px; text-transform: uppercase; }
        .input-wrapper { position: relative; }
        .input-icon { position: absolute; left: 16px; top: 50%; transform: translateY(-50%); font-size: 16px; color: var(--text-muted); }
        input { width: 100%; height: 52px; background: rgba(255, 255, 255, 0.05); border: 1.5px solid rgba(255, 255, 255, 0.1); border-radius: 12px; padding: 0 16px 0 45px; color: #ffffff; font-size: 15px; outline: none; }
        input:focus { background: rgba(255, 255, 255, 0.08); border-color: var(--secondary); box-shadow: 0 0 12px rgba(0, 191, 165, 0.2); }
        .btn { width: 100%; height: 52px; border: none; border-radius: 12px; font-size: 16px; font-weight: 600; color: #ffffff; cursor: pointer; display: flex; justify-content: center; align-items: center; box-shadow: 0 4px 15px rgba(0, 0, 0, 0.2); }
        .btn-primary { background: linear-gradient(135deg, var(--primary), #8e24aa); }
        .btn-primary:hover { transform: translateY(-2px); box-shadow: 0 6px 20px rgba(156, 39, 176, 0.4); filter: brightness(1.1); }
        .btn-secondary { background: linear-gradient(135deg, var(--secondary), var(--secondary-dark)); }
        .btn-secondary:hover { transform: translateY(-2px); box-shadow: 0 6px 20px rgba(0, 191, 165, 0.4); filter: brightness(1.1); }
        .btn-danger { background: linear-gradient(135deg, var(--danger), #e53935); }
        .btn-danger:hover { transform: translateY(-2px); box-shadow: 0 6px 20px rgba(255, 51, 75, 0.4); filter: brightness(1.1); }
        .switch-prompt { text-align: center; margin-top: 24px; font-size: 14px; color: var(--text-muted); }
        .switch-link { color: var(--secondary); font-weight: 600; text-decoration: none; cursor: pointer; }
        .switch-link:hover { text-decoration: underline; color: var(--primary-light); }
        .profile-info { background: rgba(255, 255, 255, 0.03); border: 1px solid rgba(255, 255, 255, 0.05); border-radius: 16px; padding: 20px; margin-bottom: 24px; }
        .profile-row { display: flex; justify-content: space-between; align-items: center; padding: 12px 0; border-bottom: 1px solid rgba(255, 255, 255, 0.06); }
        .profile-row:last-child { border-bottom: none; }
        .profile-label { font-size: 13px; color: var(--text-muted); font-weight: 500; }
        .profile-value { font-size: 15px; color: #ffffff; font-weight: 600; }
        .toast { position: fixed; top: 24px; right: 24px; background: rgba(255, 255, 255, 0.1); backdrop-filter: blur(12px); border: 1px solid rgba(255, 255, 255, 0.2); border-left: 5px solid var(--secondary); color: #ffffff; padding: 16px 24px; border-radius: 12px; box-shadow: 0 10px 30px rgba(0,0,0,0.3); transform: translateX(120%); z-index: 1000; font-weight: 500; }
        .toast.show { transform: translateX(0); }
        .hidden { display: none !important; }
    </style>
</head>
<body>
    <div class="blob-container"><div class="blob blob-1"></div><div class="blob blob-2"></div></div>
    <div class="card-container">
        <div id="toast" class="toast">Notifikasi!</div>

        <!-- ================= LOGIN VIEW ================= -->
        <div id="loginView">
            <div class="header">
                <div class="logo-icon">🛍️</div>
                <h2>Sistem Penjualan</h2>
                <p class="subtitle">Silakan login untuk melanjutkan ke aplikasi</p>
            </div>
            <form id="loginForm" onsubmit="event.preventDefault(); handleLogin();">
                <div class="form-group">
                    <label for="loginUsername">Username</label>
                    <div class="input-wrapper">
                        <span class="input-icon">👤</span>
                        <input type="text" id="loginUsername" placeholder="Masukkan username" required>
                    </div>
                </div>
                <div class="form-group">
                    <label for="loginPassword">Password</label>
                    <div class="input-wrapper">
                        <span class="input-icon">🔑</span>
                        <input type="password" id="loginPassword" placeholder="Masukkan password" required>
                    </div>
                </div>
                <button type="submit" class="btn btn-primary" style="margin-top: 10px;">Masuk</button>
            </form>
            <div class="switch-prompt">
                Belum punya akun? <span class="switch-link" onclick="switchView('register')">Daftar sekarang</span>
            </div>
        </div>

        <!-- ================= REGISTER VIEW ================= -->
        <div id="registerView" class="hidden">
            <div class="header">
                <div class="logo-icon">✨</div>
                <h2>Buat Akun Baru</h2>
                <p class="subtitle">Daftarkan akun untuk mengelola sistem penjualan</p>
            </div>
            <form id="registerForm" onsubmit="event.preventDefault(); handleRegister();">
                <div class="form-group">
                    <label for="regNama">Nama Lengkap</label>
                    <div class="input-wrapper">
                        <span class="input-icon">📛</span>
                        <input type="text" id="regNama" placeholder="Nama Lengkap Anda" required>
                    </div>
                </div>
                <div class="form-group">
                    <label for="regUsername">Username</label>
                    <div class="input-wrapper">
                        <span class="input-icon">👤</span>
                        <input type="text" id="regUsername" placeholder="Pilih username unik" required>
                    </div>
                </div>
                <div class="form-group">
                    <label for="regEmail">Alamat Email</label>
                    <div class="input-wrapper">
                        <span class="input-icon">📧</span>
                        <input type="email" id="regEmail" placeholder="nama@email.com" required>
                    </div>
                </div>
                <div class="form-group">
                    <label for="regPassword">Password</label>
                    <div class="input-wrapper">
                        <span class="input-icon">🔑</span>
                        <input type="password" id="regPassword" placeholder="Minimal 6 karakter" required>
                    </div>
                </div>
                <button type="submit" class="btn btn-primary" style="margin-top: 10px;">Daftar Baru</button>
            </form>
            <div class="switch-prompt">
                Sudah punya akun? <span class="switch-link" onclick="switchView('login')">Login disini</span>
            </div>
        </div>

        <!-- ================= ACCOUNT VIEW ================= -->
        <div id="accountView" class="hidden">
            <div class="header">
                <div class="logo-icon">🌟</div>
                <h2 id="welcomeGreeting">Selamat Datang!</h2>
                <p class="subtitle">Kelola informasi akun Anda di bawah ini</p>
            </div>
            <div class="profile-info">
                <div class="profile-row">
                    <span class="profile-label">Nama Lengkap</span>
                    <span class="profile-value" id="profileName">Adit</span>
                </div>
                <div class="profile-row">
                    <span class="profile-label">Username</span>
                    <span class="profile-value" id="profileUsername">@adit_admin</span>
                </div>
                <div class="profile-row">
                    <span class="profile-label">Alamat Email</span>
                    <span class="profile-value" id="profileEmail">adit@email.com</span>
                </div>
                <div class="profile-row">
                    <span class="profile-label">Status Sesi</span>
                    <span class="profile-value" style="color: var(--secondary);">Aktif (Persistent)</span>
                </div>
            </div>
            <div style="display: flex; flex-direction: column; gap: 12px;">
                <button onclick="switchAccount()" class="btn btn-secondary">Daftar / Masuk Akun Lain</button>
                <button onclick="handleLogout()" class="btn btn-danger">Keluar dari Akun</button>
            </div>
        </div>
    </div>

    <script>
        if (!localStorage.getItem('database_users')) {
            const defaultUsers = {
                'adit': { nama: 'Adit Firmansyah', username: 'adit', email: 'adit@email.com', password: 'password123' }
            };
            localStorage.setItem('database_users', JSON.stringify(defaultUsers));
        }

        window.onload = function() { checkSession(); };

        function showToast(message, type = 'success') {
            const toast = document.getElementById('toast');
            toast.innerText = message;
            toast.style.borderLeftColor = type === 'success' ? 'var(--secondary)' : 'var(--danger)';
            toast.classList.add('show');
            setTimeout(() => { toast.classList.remove('show'); }, 3000);
        }

        function checkSession() {
            const loggedInUser = localStorage.getItem('logged_in_user');
            if (loggedInUser) {
                const users = JSON.parse(localStorage.getItem('database_users'));
                const userData = users[loggedInUser];
                if (userData) {
                    document.getElementById('welcomeGreeting').innerText = `Selamat Datang Kembali,\n${userData.nama}!`;
                    document.getElementById('profileName').innerText = userData.nama;
                    document.getElementById('profileUsername').innerText = `@${userData.username}`;
                    document.getElementById('profileEmail').innerText = userData.email;
                    switchView('account');
                    showToast(`Sesi Aktif: Selamat datang kembali, ${userData.nama}!`);
                }
            } else {
                switchView('login');
            }
        }

        function switchView(viewName) {
            document.getElementById('loginView').classList.add('hidden');
            document.getElementById('registerView').classList.add('hidden');
            document.getElementById('accountView').classList.add('hidden');
            if (viewName === 'login') document.getElementById('loginView').classList.remove('hidden');
            else if (viewName === 'register') document.getElementById('registerView').classList.remove('hidden');
            else if (viewName === 'account') document.getElementById('accountView').classList.remove('hidden');
        }

        function handleLogin() {
            const usernameInput = document.getElementById('loginUsername').value.trim();
            const passwordInput = document.getElementById('loginPassword').value.trim();
            const users = JSON.parse(localStorage.getItem('database_users'));
            if (users[usernameInput]) {
                if (users[usernameInput].password === passwordInput) {
                    localStorage.setItem('logged_in_user', usernameInput);
                    checkSession();
                } else {
                    showToast('Password salah!', 'danger');
                }
            } else {
                showToast('Username tidak terdaftar! Silakan mendaftar dahulu.', 'danger');
            }
        }

        function handleRegister() {
            const nama = document.getElementById('regNama').value.trim();
            const username = document.getElementById('regUsername').value.trim().toLowerCase();
            const email = document.getElementById('regEmail').value.trim();
            const password = document.getElementById('regPassword').value.trim();
            if (password.length < 6) {
                showToast('Password minimal harus 6 karakter!', 'danger'); return;
            }
            const users = JSON.parse(localStorage.getItem('database_users'));
            if (users[username]) {
                showToast(`Username '${username}' sudah digunakan!`, 'danger');
            } else {
                users[username] = { nama, username, email, password };
                localStorage.setItem('database_users', JSON.stringify(users));
                showToast('Registrasi Berhasil! Silakan masuk ke akun Anda.', 'success');
                document.getElementById('registerForm').reset();
                switchView('login');
                document.getElementById('loginUsername').value = username;
                document.getElementById('loginPassword').focus();
            }
        }

        function handleLogout() {
            localStorage.removeItem('logged_in_user');
            showToast('Berhasil keluar dari akun.', 'success');
            document.getElementById('loginForm').reset();
            switchView('login');
        }

        function switchAccount() {
            localStorage.removeItem('logged_in_user');
            showToast('Menyiapkan pendaftaran akun baru...', 'success');
            document.getElementById('registerForm').reset();
            switchView('register');
        }
    </script>
</body>
</html>
```

---

## ☕ 2. Kode Kotlin Aplikasi Android (`app/`)

### A. `SessionManager.kt`
Menggunakan `SharedPreferences` untuk menyimpan data pengguna yang sedang login secara lokal agar status login bersifat persisten (tetap tersimpan saat aplikasi ditutup dan dibuka kembali).

```kotlin
package com.adit.penjualannya_adit

import android.content.Context
import android.content.SharedPreferences

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

    fun createLoginSession(username: String, name: String, email: String) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true)
        editor.putString(KEY_USERNAME, username)
        editor.putString(KEY_NAME, name)
        editor.putString(KEY_EMAIL, email)
        editor.apply()
    }

    fun isLoggedIn(): Boolean {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun getName(): String? {
        return pref.getString(KEY_NAME, null)
    }

    fun getUsername(): String? {
        return pref.getString(KEY_USERNAME, null)
    }

    fun getEmail(): String? {
        return pref.getString(KEY_EMAIL, null)
    }

    fun logoutUser() {
        editor.clear()
        editor.apply()
    }
}
```

### B. `LoginActivity.kt`
Aktivitas pertama kali muncul saat aplikasi dibuka. Melakukan pemeriksaan sesi via `SessionManager`. Jika sesi sudah ada, langsung menyambut pengguna dan masuk ke dashboard. Jika belum, menyajikan UI input login serta integrasi ke Firebase Realtime Database.

```kotlin
package com.adit.penjualannya_adit

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class LoginActivity : AppCompatActivity() {

    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var tvRegisterLink: TextView
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        sessionManager = SessionManager(this)

        // Cek Sesi: Jika pengguna sudah login, langsung sambut dan arahkan ke dashboard
        if (sessionManager.isLoggedIn()) {
            Toast.makeText(this, "Selamat datang kembali, ${sessionManager.getName()}!", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, MainActivity2::class.java))
            finish()
            return
        }

        etUsername = findViewById(R.id.etUsername)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        tvRegisterLink = findViewById(R.id.tvRegisterLink)

        btnLogin.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (username.isEmpty()) {
                etUsername.error = "Username tidak boleh kosong!"
                etUsername.requestFocus()
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                etPassword.error = "Password tidak boleh kosong!"
                etPassword.requestFocus()
                return@setOnClickListener
            }

            prosesLogin(username, password)
        }

        tvRegisterLink.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun prosesLogin(usernameInput: String, passwordInput: String) {
        val database = FirebaseDatabase.getInstance()
        val usersRef = database.getReference("users")

        usersRef.child(usernameInput).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val dbPassword = snapshot.child("password").getValue(String::class.java)
                    val dbName = snapshot.child("nama").getValue(String::class.java) ?: usernameInput
                    val dbEmail = snapshot.child("email").getValue(String::class.java) ?: ""

                    if (dbPassword == passwordInput) {
                        sessionManager.createLoginSession(usernameInput, dbName, dbEmail)
                        Toast.makeText(this@LoginActivity, "Login Berhasil! Selamat Datang $dbName", Toast.LENGTH_SHORT).show()
                        
                        startActivity(Intent(this@LoginActivity, MainActivity2::class.java))
                        finish()
                    } else {
                        Toast.makeText(this@LoginActivity, "Password salah!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@LoginActivity, "Username tidak terdaftar! Silakan mendaftar dahulu.", Toast.LENGTH_LONG).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@LoginActivity, "Koneksi bermasalah: ${error.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}
```

### C. `RegisterActivity.kt`
Mengurus pendaftaran data akun baru ke Firebase Realtime Database. Validasi format karakter username agar ramah Firebase Key, serta menutup halaman jika pendaftaran berhasil (otomatis terarah kembali ke halaman `LoginActivity`).

```kotlin
package com.adit.penjualannya_adit

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {

    private lateinit var etNama: EditText
    private lateinit var etUsername: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnRegister: Button
    private lateinit var tvLoginLink: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        etNama = findViewById(R.id.etNama)
        etUsername = findViewById(R.id.etUsername)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnRegister = findViewById(R.id.btnRegister)
        tvLoginLink = findViewById(R.id.tvLoginLink)

        btnRegister.setOnClickListener {
            val nama = etNama.text.toString().trim()
            val username = etUsername.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (nama.isEmpty()) {
                etNama.error = "Nama Lengkap tidak boleh kosong!"
                etNama.requestFocus()
                return@setOnClickListener
            }

            if (username.isEmpty()) {
                etUsername.error = "Username tidak boleh kosong!"
                etUsername.requestFocus()
                return@setOnClickListener
            }

            if (username.contains(".") || username.contains("#") || username.contains("$") || username.contains("[") || username.contains("]")) {
                etUsername.error = "Username tidak boleh mengandung karakter . # $ [ ]"
                etUsername.requestFocus()
                return@setOnClickListener
            }

            if (email.isEmpty()) {
                etEmail.error = "Email tidak boleh kosong!"
                etEmail.requestFocus()
                return@setOnClickListener
            }

            if (password.length < 6) {
                etPassword.error = "Password minimal harus 6 karakter!"
                etPassword.requestFocus()
                return@setOnClickListener
            }

            prosesRegistrasi(nama, username, email, password)
        }

        tvLoginLink.setOnClickListener {
            finish()
        }
    }

    private fun prosesRegistrasi(nama: String, username: String, email: String, sandi: String) {
        val database = FirebaseDatabase.getInstance()
        val usersRef = database.getReference("users")

        val userMap = HashMap<String, String>()
        userMap["nama"] = nama
        userMap["username"] = username
        userMap["email"] = email
        userMap["password"] = sandi

        usersRef.child(username).get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                Toast.makeText(this, "Username '$username' sudah terdaftar! Gunakan username lain.", Toast.LENGTH_LONG).show()
            } else {
                usersRef.child(username).setValue(userMap)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Registrasi Berhasil! Silakan Login.", Toast.LENGTH_LONG).show()
                        finish()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Registrasi Gagal: ${e.message}", Toast.LENGTH_LONG).show()
                    }
            }
        }.addOnFailureListener { e ->
            Toast.makeText(this, "Koneksi database bermasalah: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}
```

### D. `AccountActivity.kt`
Ini adalah UI Akun (halaman profil). Halaman ini menyambut pengguna dengan nama asli mereka, lalu menyediakan dua tombol aksi:
1. **Daftar / Masuk Akun Lain (Tukar Akun):** Melakukan logout otomatis, lalu mengarahkan langsung ke halaman pendaftaran akun baru (`RegisterActivity`).
2. **Keluar dari Akun (Logout):** Menghapus sesi lokal SharedPreferences dan mengarahkan kembali pengguna ke halaman awal Login (`LoginActivity`) dengan membersihkan tumpukan aktivitas (*clear task activity stack*) agar pengguna tidak dapat kembali ke menu utama dengan menekan tombol kembali Android.

```kotlin
package com.adit.penjualannya_adit

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AccountActivity : AppCompatActivity() {

    private lateinit var tvWelcomeTitle: TextView
    private lateinit var tvNamaProfil: TextView
    private lateinit var tvUsernameProfil: TextView
    private lateinit var tvEmailProfil: TextView
    private lateinit var btnLogout: Button
    private lateinit var btnDaftarLagi: Button
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)

        sessionManager = SessionManager(this)

        if (!sessionManager.isLoggedIn()) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        tvWelcomeTitle = findViewById(R.id.tvWelcomeTitle)
        tvNamaProfil = findViewById(R.id.tvNamaProfil)
        tvUsernameProfil = findViewById(R.id.tvUsernameProfil)
        tvEmailProfil = findViewById(R.id.tvEmailProfil)
        btnLogout = findViewById(R.id.btnLogout)
        btnDaftarLagi = findViewById(R.id.btnDaftarLagi)

        val currentName = sessionManager.getName() ?: "Pengguna"
        val currentUsername = sessionManager.getUsername() ?: "-"
        val currentEmail = sessionManager.getEmail() ?: "-"

        tvWelcomeTitle.text = "Selamat Datang Kembali,\n$currentName!"
        tvNamaProfil.text = currentName
        tvUsernameProfil.text = "@$currentUsername"
        tvEmailProfil.text = currentEmail

        // Opsi 1: Tombol Keluar Akun
        btnLogout.setOnClickListener {
            sessionManager.logoutUser()
            Toast.makeText(this, "Anda berhasil keluar dari akun.", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        // Opsi 2: Tombol Daftar / Masuk Akun Lain
        btnDaftarLagi.setOnClickListener {
            sessionManager.logoutUser()
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}
```

---

## 🎨 3. Layout XML Android (`layouts/`)

### A. `activity_login.xml`
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:padding="24dp"
    android:gravity="center"
    android:background="#FFFFFF">

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Silakan Masuk"
        android:textSize="28sp"
        android:textStyle="bold"
        android:textColor="#333333" />

    <EditText
        android:id="@+id/etUsername"
        android:layout_width="match_parent"
        android:layout_height="50dp"
        android:layout_marginTop="32dp"
        android:hint="Username"
        android:paddingLeft="16dp"
        android:background="@android:drawable/editbox_background_normal"/>

    <EditText
        android:id="@+id/etPassword"
        android:layout_width="match_parent"
        android:layout_height="50dp"
        android:layout_marginTop="16dp"
        android:hint="Password"
        android:inputType="textPassword"
        android:paddingLeft="16dp"
        android:background="@android:drawable/editbox_background_normal"/>

    <Button
        android:id="@+id/btnLogin"
        android:layout_width="match_parent"
        android:layout_height="50dp"
        android:layout_marginTop="24dp"
        android:text="MASUK"
        android:textStyle="bold"
        android:backgroundTint="#9C27B0"
        android:textColor="#FFFFFF"/>

    <TextView
        android:id="@+id/tvRegisterLink"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginTop="24dp"
        android:text="Belum punya akun? Daftar sekarang"
        android:textColor="#00BFA5"
        android:textStyle="bold" />
</LinearLayout>
```

### B. `activity_register.xml`
```xml
<?xml version="1.0" encoding="utf-8"?>
<ScrollView xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:fillViewport="true">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="vertical"
        android:padding="24dp"
        android:gravity="center"
        android:background="#FFFFFF">

        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="Daftar Baru"
            android:textSize="28sp"
            android:textStyle="bold"
            android:textColor="#333333" />

        <EditText
            android:id="@+id/etNama"
            android:layout_width="match_parent"
            android:layout_height="50dp"
            android:layout_marginTop="32dp"
            android:hint="Nama Lengkap"
            android:paddingLeft="16dp"
            android:background="@android:drawable/editbox_background_normal"/>

        <EditText
            android:id="@+id/etUsername"
            android:layout_width="match_parent"
            android:layout_height="50dp"
            android:layout_marginTop="16dp"
            android:hint="Username unik"
            android:paddingLeft="16dp"
            android:background="@android:drawable/editbox_background_normal"/>

        <EditText
            android:id="@+id/etEmail"
            android:layout_width="match_parent"
            android:layout_height="50dp"
            android:layout_marginTop="16dp"
            android:hint="Alamat Email"
            android:inputType="textEmailAddress"
            android:paddingLeft="16dp"
            android:background="@android:drawable/editbox_background_normal"/>

        <EditText
            android:id="@+id/etPassword"
            android:layout_width="match_parent"
            android:layout_height="50dp"
            android:layout_marginTop="16dp"
            android:hint="Password (min 6 karakter)"
            android:inputType="textPassword"
            android:paddingLeft="16dp"
            android:background="@android:drawable/editbox_background_normal"/>

        <Button
            android:id="@+id/btnRegister"
            android:layout_width="match_parent"
            android:layout_height="50dp"
            android:layout_marginTop="24dp"
            android:text="DAFTAR BARU"
            android:textStyle="bold"
            android:backgroundTint="#9C27B0"
            android:textColor="#FFFFFF"/>

        <TextView
            android:id="@+id/tvLoginLink"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_marginTop="24dp"
            android:text="Sudah punya akun? Login disini"
            android:textColor="#00BFA5"
            android:textStyle="bold" />
    </LinearLayout>
</ScrollView>
```

### C. `activity_account.xml`
```xml
<?xml version="1.0" encoding="utf-8"?>
<ScrollView xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="#F5F5F5"
    android:fillViewport="true">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:gravity="center_horizontal"
        android:orientation="vertical"
        android:padding="24dp">

        <TextView
            android:id="@+id/tvWelcomeTitle"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginTop="32dp"
            android:text="Selamat Datang Kembali,\nAdit!"
            android:textColor="#333333"
            android:textSize="26sp"
            android:textStyle="bold"
            android:textAlignment="center" />

        <androidx.cardview.widget.CardView
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginTop="28dp"
            app:cardCornerRadius="16dp"
            app:cardElevation="4dp"
            app:cardBackgroundColor="#FFFFFF">

            <LinearLayout
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:gravity="center_horizontal"
                android:orientation="vertical"
                android:padding="24dp">

                <ImageView
                    android:layout_width="90dp"
                    android:layout_height="90dp"
                    android:src="@android:drawable/ic_menu_myplaces"
                    android:contentDescription="Avatar Pengguna" />

                <TextView
                    android:id="@+id/tvNamaProfil"
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:layout_marginTop="16dp"
                    android:text="Adit Firmansyah"
                    android:textColor="#333333"
                    android:textSize="20sp"
                    android:textStyle="bold" />

                <TextView
                    android:id="@+id/tvUsernameProfil"
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:layout_marginTop="4dp"
                    android:text="\@adit_admin"
                    android:textColor="#888888"
                    android:textSize="15sp" />

                <View
                    android:layout_width="match_parent"
                    android:layout_height="1dp"
                    android:layout_marginTop="20dp"
                    android:layout_marginBottom="20dp"
                    android:background="#E0E0E0" />

                <LinearLayout
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:orientation="horizontal"
                    android:gravity="center_vertical">

                    <TextView
                        android:layout_width="wrap_content"
                        android:layout_height="wrap_content"
                        android:text="Alamat Email:"
                        android:textColor="#555555"
                        android:textSize="14sp"
                        android:textStyle="bold" />

                    <TextView
                        android:id="@+id/tvEmailProfil"
                        android:layout_width="0dp"
                        android:layout_height="wrap_content"
                        android:layout_weight="1"
                        android:text="adit@email.com"
                        android:textColor="#333333"
                        android:textSize="14sp"
                        android:textAlignment="end" />
                </LinearLayout>

            </LinearLayout>
        </androidx.cardview.widget.CardView>

        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginTop="24dp"
            android:orientation="vertical">

            <Button
                android:id="@+id/btnDaftarLagi"
                android:layout_width="match_parent"
                android:layout_height="50dp"
                android:backgroundTint="#018786"
                android:text="Daftar / Masuk Akun Lain"
                android:textColor="#FFFFFF"
                android:textSize="15sp"
                android:textStyle="bold"
                app:cornerRadius="8dp" />

            <Button
                android:id="@+id/btnLogout"
                android:layout_width="match_parent"
                android:layout_height="50dp"
                android:layout_marginTop="12dp"
                android:layout_marginBottom="32dp"
                android:backgroundTint="#D32F2F"
                android:text="Keluar dari Akun"
                android:textColor="#FFFFFF"
                android:textSize="15sp"
                android:textStyle="bold"
                app:cornerRadius="8dp" />

        </LinearLayout>
    </LinearLayout>
</ScrollView>
```
