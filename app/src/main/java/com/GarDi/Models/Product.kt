package com.GarDi.Models


class Product {
    var barcode: String? = null
    var productName: String? = null
    var materialList: List<String>? = null

    constructor(
        barcode: String?,
        productName: String?,
        materialList: List<String>?
    ) {
        this.barcode = barcode
        this.productName = productName
        this.materialList = materialList
    }

    constructor() {}

}
