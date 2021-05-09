package com.GarDi.Models;

public abstract class MaterialHandler {

    private static String[][] materialsAndSorting = {{"plastic", "brännbart"}, {"glass", "glasförpackningar"}, {"plast", "brännbart"}, {"wood", "brännbart"}, {"metal", "deponi"}
    , {"plast", "brännbart"}, {"carton", "pappersförpackningar"}, {"pappersförpackningar", "pappersförpackningar"}, {"combustible", "brännbart"}, {"plastic packaging", "plastförpackningar"}
    , {"tetra", "pappersförpackningar"}, {"phone", "elskrot"}};


    public static String findSortingFromMaterial(String material) {
        for (int i = 0; i < materialsAndSorting.length; i++) {
            if (material.toLowerCase().equals(materialsAndSorting[i][0])) {
                return materialsAndSorting[i][1];
            }
        }
        return "No sorting options found";
    }
}
