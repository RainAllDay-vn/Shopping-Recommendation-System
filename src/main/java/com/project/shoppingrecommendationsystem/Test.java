package com.project.shoppingrecommendationsystem;

import com.project.shoppingrecommendationsystem.models.crawlers.Crawler;
import com.project.shoppingrecommendationsystem.models.crawlers.cellphones.CellphoneSSmartPhoneCrawler;
import com.project.shoppingrecommendationsystem.models.database.LaptopDatabase;

import java.io.IOException;

public class Test {
    public static void main(String[] args) throws IOException {
        LaptopDatabase database = LaptopDatabase.getInstance();
        database.crawl(10000);

    }
}
