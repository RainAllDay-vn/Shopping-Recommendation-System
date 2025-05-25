package com.project.shoppingrecommendationsystem.models.database;

import com.project.shoppingrecommendationsystem.models.Product;
import com.project.shoppingrecommendationsystem.models.crawlers.Crawler;
import com.project.shoppingrecommendationsystem.models.crawlers.cellphones.CellphoneSLaptopCrawler;
import com.project.shoppingrecommendationsystem.models.crawlers.cellphones.CellphoneSSmartPhoneCrawler;

import java.util.*;

public class CellphoneSDatabase implements ProductDatabase {
    private final List<Crawler> crawlers;
    private final List<Product> storeProducts;

    public CellphoneSDatabase() {
        crawlers = new ArrayList<>();
        crawlers.add(new CellphoneSLaptopCrawler());
        crawlers.add(new CellphoneSSmartPhoneCrawler());
        storeProducts = new ArrayList<>();
        crawlers.stream()
                .map(Crawler::getAllProducts)
                .flatMap(Collection::stream)
                .forEach(storeProducts::add);
        Collections.shuffle(storeProducts);
    }

    @Override
    public void crawl() {
        crawl(Integer.MAX_VALUE);
    }

    @Override
    public void crawl(int limit) {
        storeProducts.clear();
        crawlers.forEach(crawler -> crawler.crawl(limit));
        crawlers.stream()
                .map(Crawler::getAllProducts)
                .flatMap(Collection::stream)
                .forEach(storeProducts::add);
    }

    @Override
    public void crawl(Crawler crawler) {
        storeProducts.clear();
        crawler.crawl();
        storeProducts.addAll(crawler.getAllProducts());
    }

    @Override
    public List<Product> findAllProducts() {
        return storeProducts;
    }

    @Override
    public Optional<Product> findProductById(int id) {
        for (Product product : findAllProducts()) {
            if (product.getId() == id) {
                return Optional.of(product);
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Product> findProducts(List<String[]> query, int limit, int offset) {
        return storeProducts.stream()
                .filter(product -> product.match(query))
                .skip(offset)
                .limit(limit)
                .toList();
    }
}
