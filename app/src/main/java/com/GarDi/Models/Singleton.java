package com.GarDi.Models;

import java.util.ArrayList;

public class Singleton {

    private static Singleton instance = null;
    String scannedText;

    public Singleton() {
    }

    public static Singleton getInstance() {
        if (instance == null) {
            instance = new Singleton();
        }
        return (instance);
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
}