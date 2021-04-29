package com.GarDi

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.GarDi.Models.Singleton

class BarcodeScanned : AppCompatActivity() {
    private var scannedText: TextView? = null
    private var itemName: TextView? = null
    private var material: TextView? = null
    private var barcodeImage: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_barcode_scanned)
        initID()
        scannedText!!.text = Singleton.getInstance().scannedText
        itemName!!.text = Singleton.getInstance().itemName
        barcodeImage!!.setImageBitmap(Singleton.getInstance().barcode)
        material!!.text = Singleton.getInstance().materialOfProduct;
    }

    private fun initID() {
        scannedText = findViewById(R.id.scannedText)
        itemName = findViewById(R.id.itemName)
        barcodeImage = findViewById(R.id.barcodeImage)
        material = findViewById(R.id.material)
    }
}