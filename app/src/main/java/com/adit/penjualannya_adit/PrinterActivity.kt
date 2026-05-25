package com.adit.penjualannya_adit

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class PrinterActivity : AppCompatActivity() {

    private lateinit var tvStatusPrinter: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_printer)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        tvStatusPrinter = findViewById(R.id.tvStatusPrinter)
        findViewById<ImageView>(R.id.ivBack).setOnClickListener { finish() }

        // Cek status Bluetooth & printer
        checkBluetoothStatus()

        // Tombol refresh status
        findViewById<com.google.android.material.button.MaterialButton>(R.id.btnCariPrinter).setOnClickListener {
            checkBluetoothStatus()
        }
    }

    override fun onResume() {
        super.onResume()
        checkBluetoothStatus()
    }

    private fun checkBluetoothStatus() {
        // Cek izin Bluetooth
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN),
                200
            )
            updateStatusUI(connected = false)
            return
        }

        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager
        val bluetoothAdapter = bluetoothManager?.adapter

        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled) {
            updateStatusUI(connected = false)
            return
        }

        // Cek apakah ada perangkat Bluetooth yang terhubung (paired & connected)
        val pairedDevices = bluetoothAdapter.bondedDevices
        val printerConnected = pairedDevices?.any { device ->
            // Deteksi printer: biasanya mengandung kata "printer", "POS", "thermal", "RPP", "PT", "BT"
            val name = device.name?.uppercase() ?: ""
            name.contains("PRINTER") || name.contains("POS") || name.contains("THERMAL") ||
            name.contains("RPP") || name.contains("PT-") || name.contains("BT") ||
            name.contains("EPSON") || name.contains("STAR") || name.contains("RONGTA") ||
            // Atau cek apakah sudah paired (dianggap terhubung jika ada paired device)
            device.bondState == android.bluetooth.BluetoothDevice.BOND_BONDED
        } ?: false

        updateStatusUI(connected = printerConnected && pairedDevices?.isNotEmpty() == true)
    }

    private fun updateStatusUI(connected: Boolean) {
        if (connected) {
            tvStatusPrinter.text = "Printer sudah terhubung dengan perangkat"
            tvStatusPrinter.setTextColor(android.graphics.Color.parseColor("#16A34A"))
        } else {
            tvStatusPrinter.text = "Printer Tidak Terhubung"
            tvStatusPrinter.setTextColor(android.graphics.Color.parseColor("#EF4444"))
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 200) {
            checkBluetoothStatus()
        }
    }
}
