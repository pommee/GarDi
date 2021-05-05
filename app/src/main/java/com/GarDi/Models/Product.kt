package com.GarDi.Models

class Product {

    var barcode: String? = null
    var productName: String? = null
    var materials: ArrayList<String>? = null

    fun Product() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    fun Product(barcode: String?, productName: String?, materials: ArrayList<String>) {
        this.barcode = barcode
        this.productName = productName
        this.materials = materials
    }



}