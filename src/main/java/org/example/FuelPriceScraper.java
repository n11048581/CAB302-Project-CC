package org.example;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class FuelPriceScraper {
    public static void main(String[] args) {

        // URL of page to be scraped
        String url = "https://fuelprice.io/wa/perth/";

        try {
            // Fetch HTML content from URL
            Document document = Jsoup.connect(url)
                    .userAgent("Mozilla")
                    .get();

            // Select all <span> elements with the class "cheapest_price"
            Elements priceElements = document.select("span.cheapest_price");

            // Print each match
            for (Element element : priceElements) {
                System.out.println("Price: " + element.text());
            }
        } catch (IOException e) {
            // Exception handling
            System.err.println("Error fetching or parsing the page: " + e.getMessage());
        }
    }
}
