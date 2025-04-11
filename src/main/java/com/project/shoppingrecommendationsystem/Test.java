package com.project.shoppingrecommendationsystem;

import com.project.shoppingrecommendationsystem.models.crawler.FPTShopCrawler;

public class Test {
    public static void main(String[] args) {
        FPTShopCrawler crawler = new FPTShopCrawler();
        crawler.crawlLaptops();
    }
}
