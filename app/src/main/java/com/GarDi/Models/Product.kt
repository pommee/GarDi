package com.GarDi.Models

import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
class Product(
    barcode: String,
    name: String,
    materialList: List<String>?
) {

    var barcode: String? = null
    var productName: String? = null
    var materials: List<String>? = null

    fun Product() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    fun Product(barcode: String?, productName: String?, materials: List<String>) {
        this.barcode = barcode
        this.productName = productName
        this.materials = materials
    }



}