package com.project.shoppingrecommendationsystem;

import com.project.shoppingrecommendationsystem.models.Crawler;
import com.project.shoppingrecommendationsystem.models.FPTShopCrawler;

import java.net.URL;

public class CrawlerTest {
    public static void main(String[] args) throws InterruptedException {
        Crawler crawler = new FPTShopCrawler();
        crawler.crawl();
    }
}
