package com.GarDi

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.GarDi.Models.Singleton

class BarcodeScanned : AppCompatActivity() {
    private var scannedText: TextView? = null
    private var itemName: TextView? = null
    private var material: TextView? = null
    private var barcodeImage: ImageView? = null
    private var okButton: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_barcode_scanned)
        initID()
        scannedText!!.text = Singleton.getInstance().scannedText
        itemName!!.text = Singleton.getInstance().itemName
        barcodeImage!!.setImageBitmap(Singleton.getInstance().barcode)
        material!!.text = Singleton.getInstance().materialOfProduct;
        okButton!!.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        if (Singleton.getInstance().materialOfProduct.equals("No materials found")) {
            promptForAdding()
        }
    }

    private fun initID() {
        scannedText = findViewById(R.id.scannedText)
        itemName = findViewById(R.id.itemName)
        barcodeImage = findViewById(R.id.barcodeImage)
        material = findViewById(R.id.material)
        okButton = findViewById(R.id.okButton)
    }

    private fun promptForAdding() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(
            "Do you wish to add the barcode: " + Singleton.getInstance().scannedText + " to the database?").setCancelable(false)
            .setPositiveButton(
                "Yes") { dialog, which ->
                val intent = Intent(applicationContext, AddProductToDB::class.java)
                startActivity(intent) }
            .setNegativeButton(
                "No") { dialog, which -> dialog.cancel() }

        val alert = builder.create()
        alert.setTitle("Product not found!")
        alert.show()
    }
}