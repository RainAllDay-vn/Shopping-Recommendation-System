package com.project.shoppingrecommendationsystem;

import com.project.shoppingrecommendationsystem.models.ProductDatabase;
import com.project.shoppingrecommendationsystem.models.crawler.FPTShopCrawler;

public class CrawlOnly {
    public static void main(String[] args) {
        ProductDatabase database = ProductDatabase.getInstance();
        database.crawl(new FPTShopCrawler());
    }
}
