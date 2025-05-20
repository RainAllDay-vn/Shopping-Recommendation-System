package com.project.shoppingrecommendationsystem;

import com.project.shoppingrecommendationsystem.models.Laptop;
import com.project.shoppingrecommendationsystem.models.ProductDatabase;
import com.project.shoppingrecommendationsystem.models.crawler.*;

import java.io.IOException;
import java.util.Arrays;

public class Test {
    public static void main(String[] args) throws IOException {
        Crawler crawler = new CellphoneSLaptopCrawler();
        crawler.crawl(1);
        for (String[] descriptions: crawler.getAll().getFirst().getDescription()) {
            System.out.println(Arrays.toString(descriptions));
        }
    }
}
