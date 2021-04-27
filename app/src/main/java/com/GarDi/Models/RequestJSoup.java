package com.GarDi.Models;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;

public class RequestJSoup {

    public static String getSearchResultFromGoogle(String searchCode) throws IOException {
        Document document = Jsoup.parse(new URL("https://www.google.com/search?q=" + searchCode + "&num=1"), 5000);
        Elements fuelTypesAndPrice = document.select("#rso");
        return fuelTypesAndPrice.select("#rso > div:nth-child(1) > div:nth-child(1) > div > div > div.yuRUbf > a > h3").text();
    }
}
