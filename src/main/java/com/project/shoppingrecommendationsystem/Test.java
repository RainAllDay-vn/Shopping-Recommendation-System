package com.project.shoppingrecommendationsystem;

import com.project.shoppingrecommendationsystem.models.ProductDatabase;
import com.project.shoppingrecommendationsystem.models.crawlers.Crawler;
import com.project.shoppingrecommendationsystem.models.crawlers.cellphones.CellphoneSLaptopCrawler;
import com.project.shoppingrecommendationsystem.models.crawlers.cellphones.CellphoneSSmartPhoneCrawler;
import com.project.shoppingrecommendationsystem.models.crawlers.fptshop.FPTShopLaptopCrawler;
import com.project.shoppingrecommendationsystem.models.crawlers.tgdd.TGDDLaptopCrawler;

import java.io.IOException;

public class Test {
    public static void main(String[] args) throws IOException {
        ProductDatabase database = ProductDatabase.getInstance();
//        database.crawl(10);
//        Crawler crawler = new CellphoneSSmartPhoneCrawler();
//        crawler.crawl(10);
    }
}
