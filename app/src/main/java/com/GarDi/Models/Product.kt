package com.GarDi.Models


class Product {
    var barcode: String? = null
    var productName: String? = null
    var materialList: List<String>? = null
    var timesSearched: Int = 1

    constructor(
        barcode: String?,
        productName: String?,
        materialList: List<String>?,
        timesSearched: Int = 1
    ) {
        this.barcode = barcode
        this.productName = productName
        this.materialList = materialList
        this.timesSearched = timesSearched
    }

    constructor()

}
