package com.GarDi

import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.GarDi.Models.Singleton
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

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

    private var materials : ArrayList<String> ? = null

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
                if (materials!!.isNotEmpty()) { // If materials have been checked

                    storeProductToFirebase()
                    Toast.makeText(
                        applicationContext,
                        "Information should now be saved in firebase database",
                        Toast.LENGTH_SHORT
                    ).show()

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

        val db = Firebase.firestore
         //val db = Firebase.firestore
         val product = hashMapOf(
             "barcode" to scannedText!!.text,
             "productName" to productName!!.text,
             "materials" to materials
         )

         db.collection("Products").add(product)
             .addOnSuccessListener { documentReference ->
                 Log.d(
                     "MyTag",
                     "DocumentSnapshot added with ID: ${documentReference.id}"
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
        materials = ArrayList()
        if (plasticBox!!.isChecked) {
            materials!!.add("plastic")
        }
        if (metalBox!!.isChecked) {
            materials!!.add("metal")
        }
        if (cartonBox!!.isChecked) {
            materials!!.add("carton")
        }
        if (glassBox!!.isChecked) {
            materials!!.add("glass")
        }
        if (paperPackagingBox!!.isChecked) {
            materials!!.add("paper packaging")
        }
        if (combustibleBox!!.isChecked) {
            materials!!.add("combustible")
        }
        if (plasticPackagingBox!!.isChecked) {
            materials!!.add("plastic packaging")
        }
    }
}