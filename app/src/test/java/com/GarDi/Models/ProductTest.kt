package com.GarDi.Models

import junit.framework.TestCase
import org.junit.Test

class ProductTest : TestCase() {
    var barcode: String = "123456789"
    var productName: String = "Coca Cola"
    var materialList: List<String> = listOf("Plast", "Papper", "Metall")
    var product: Product = Product(barcode, productName, materialList)


    @Test
    fun testGetBarcode() {
        val returnedValue: String? = product.barcode
        assertEquals(barcode, returnedValue)
    }

    @Test
    fun testSetBarcode() {
        val newValue = "987654321"
        product.barcode = newValue
        assertEquals(newValue, product.barcode)
    }

    @Test
    fun testGetProductName() {
        val returnedValue: String? = product.productName
        assertEquals(productName, returnedValue)
    }

    @Test
    fun testSetProductName() {
        val newValue = "Pepsi Max"
        product.productName = newValue
        assertEquals(newValue, product.productName)
    }

    @Test
    fun testGetMaterialList() {
        val returnedValue: List<String>? = product.materialList
        assertEquals(materialList, returnedValue)
    }

    @Test
    fun testSetMaterialList() {
        val newValue = listOf("Hund", "Katt", "Elefant")
        product.materialList = newValue
        assertEquals(newValue, product.materialList)
    }
}