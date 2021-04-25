package com.GarDi

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.GarDi.Models.Singleton

class BarcodeScanned : AppCompatActivity() {
    private var scannedText: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_barcode_scanned)
        initID()
        scannedText!!.text = Singleton.getInstance().scannedText
    }

    private fun initID() {
        scannedText = findViewById(R.id.scannedText)
    }
}