package com.project.shoppingrecommendationsystem.models;

import com.project.shoppingrecommendationsystem.models.crawler.CellphoneSCrawler;
import com.project.shoppingrecommendationsystem.models.crawler.Crawler;
import com.project.shoppingrecommendationsystem.models.crawler.FPTShopCrawler;
import com.project.shoppingrecommendationsystem.models.crawler.TGDDCrawler;

import java.util.*;

public class ProductDatabase {
    private final List<Crawler> crawlers;
    private final List<Laptop> laptops;

    public ProductDatabase() {
        crawlers = new ArrayList<>();
        crawlers.add(new CellphoneSCrawler());
        crawlers.add(new FPTShopCrawler());
        crawlers.add(new TGDDCrawler());
        laptops = new ArrayList<>();
        crawlers.stream()
                .map(Crawler::getLaptops)
                .flatMap(Collection::stream)
                .forEach(laptops::add);
    }

    public void crawl (int limit) {
        laptops.clear();
        crawlers.forEach(crawler -> crawler.crawlLaptops(limit));
        crawlers.stream()
                .map(Crawler::getLaptops)
                .flatMap(Collection::stream)
                .forEach(laptops::add);
    }

    public List<Laptop> findAllLaptops() {
        return laptops;
    }

    public Optional<Laptop> findLaptopById(int id) {
        for (Laptop laptop : laptops) {
            if (laptop.getId() == id) {
                return Optional.of(laptop);
            }
        }
        return Optional.empty();
    }

    public List<Laptop> findLaptop (String[][] query) {
        return laptops.stream()
                .filter(laptop -> laptop.match(query))
                .toList();
    }
}
