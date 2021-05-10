package com.GarDi.Models;

import android.graphics.Bitmap;

import java.util.ArrayList;

public class Singleton {

    private static Singleton instance = null;
    String itemName, scannedText, itemImage, materialOfProduct;
    Bitmap barcode;
    Product product;

    public Singleton() {
    }

    public static Singleton getInstance() {
        if (instance == null) {
            instance = new Singleton();
        }
        return (instance);
    }

    public String getItemImage() {
        return itemImage;
    }

    public void setItemImage(String itemImage) {
        this.itemImage = itemImage;
    }

    public static void setInstance(Singleton instance) {
        Singleton.instance = instance;
    }

    public String getScannedText() {
        return scannedText;
    }

    public void setScannedText(String scannedText) {
        this.scannedText = scannedText;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public Bitmap getBarcode() {
        return barcode;
    }

    public void setBarcode(Bitmap barcode) {
        this.barcode = barcode;
    }

    public String getMaterialOfProduct() {
        return materialOfProduct;
    }

    public void setMaterialOfProduct(String materialOfProduct) {
        this.materialOfProduct = materialOfProduct;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}