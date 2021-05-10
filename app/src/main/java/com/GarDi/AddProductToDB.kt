package com.GarDi

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.GarDi.Models.Product
import com.GarDi.Models.Singleton
import com.google.firebase.firestore.FirebaseFirestore

//import com.google.firebase.ktx.Firebase


class AddProductToDB : AppCompatActivity() {
    private var barcodeImage: ImageView? = null
    private var scannedText: TextView? = null
    private var productName: TextView? = null
    private var plasticBox: CheckBox? = null
    private var metalBox: CheckBox? = null
    private var cartonBox: CheckBox? = null
    private var glassBox: CheckBox? = null
    private var paperPackagingBox: CheckBox? = null
    private var combustibleBox: CheckBox? = null
    private var plasticPackagingBox: CheckBox? = null

    private var materialList: List<String>? = null

    private var addButton: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_product_db)
        initID()
        scannedText!!.text = Singleton.getInstance().scannedText
        barcodeImage!!.setImageBitmap(Singleton.getInstance().barcode)
        addButton!!.setOnClickListener {

            if (productName!!.text.length in 3..39) {   // If productName is correct
                getSelectedMaterials()
                if (materialList!!.isNotEmpty()) { // If materials have been checked
                    storeProductToFirebase()
                    Toast.makeText(
                        applicationContext,
                        "Information should now be saved in firebase database",
                        Toast.LENGTH_SHORT
                    ).show()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(
                        applicationContext,
                        "Select atleast 1 material before adding!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(
                    applicationContext,
                    "Please enter a name of the product, between 2-40 characters.",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }
    }

    private fun storeProductToFirebase() {
        val db = FirebaseFirestore.getInstance()

        val product =
            Product(scannedText!!.text.toString(), productName!!.text.toString(), materialList)
        Log.d("MyTag", product.barcode + " " + product.productName + " " + product.materialList.toString())
        db.collection("Products").document(scannedText!!.text.toString()).set(product)
            .addOnSuccessListener { documentReference ->
                Log.d(
                    "MyTag",
                    "DocumentSnapshot added with ID: " + scannedText!!.text.toString()
                )
            }
            .addOnFailureListener { e ->
                Log.w("MyTag", "Error adding document", e)
            }
    }

    private fun initID() {
        scannedText = findViewById(R.id.scannedText)
        productName = findViewById(R.id.productName)
        barcodeImage = findViewById(R.id.barcodeImage)
        addButton = findViewById(R.id.addButton)
        plasticBox = findViewById(R.id.plastic)
        metalBox = findViewById(R.id.metal)
        cartonBox = findViewById(R.id.carton)
        glassBox = findViewById(R.id.glass)
        paperPackagingBox = findViewById(R.id.paperPackaging)
        combustibleBox = findViewById(R.id.combustible)
        plasticPackagingBox = findViewById(R.id.plasticPackaging)
    }

    private fun getSelectedMaterials() {
        val myList = ArrayList<String>()
        if (plasticBox!!.isChecked) {
            myList.add("plastic")
        }
        if (metalBox!!.isChecked) {
            myList.add("metal")
        }
        if (cartonBox!!.isChecked) {
            myList.add("carton")
        }
        if (glassBox!!.isChecked) {
            myList.add("glass")
        }
        if (paperPackagingBox!!.isChecked) {
            myList.add("paper packaging")
        }
        if (combustibleBox!!.isChecked) {
            myList.add("combustible")
        }
        if (plasticPackagingBox!!.isChecked) {
            myList.add("plastic packaging")
        }
        materialList = myList.toList()
    }
}