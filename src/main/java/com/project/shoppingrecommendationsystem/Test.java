package com.project.shoppingrecommendationsystem;

import com.project.shoppingrecommendationsystem.models.Laptop;
import com.project.shoppingrecommendationsystem.models.ProductDatabase;
import com.project.shoppingrecommendationsystem.models.crawler.LaptopCrawler;
import com.project.shoppingrecommendationsystem.models.crawler.TGDDCrawler;

import java.io.IOException;

public class Test {
    public static void main(String[] args) throws IOException {
        ProductDatabase database = ProductDatabase.getInstance();
        database.crawl();
    }
}
