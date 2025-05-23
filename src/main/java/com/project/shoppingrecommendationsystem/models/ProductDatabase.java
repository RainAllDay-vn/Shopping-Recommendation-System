package com.project.shoppingrecommendationsystem.models;

import com.project.shoppingrecommendationsystem.models.crawlers.Crawler;
import com.project.shoppingrecommendationsystem.models.crawlers.cellphones.CellphoneSLaptopCrawler;
import com.project.shoppingrecommendationsystem.models.crawlers.cellphones.CellphoneSSmartPhoneCrawler;
import com.project.shoppingrecommendationsystem.models.crawlers.fptshop.FPTShopLaptopCrawler;
import com.project.shoppingrecommendationsystem.models.crawlers.tgdd.TGDDLaptopCrawler;

import java.util.*;
import java.util.stream.Collectors;


public class ProductDatabase {
    private static final ProductDatabase instance = new ProductDatabase();
    private final List<Crawler> crawlers;
    private final List<Product> storeProducts;
    private final List<Product> favouriteProducts = new ArrayList<>();

    private ProductDatabase() {
        crawlers = new ArrayList<>();
        crawlers.add(new CellphoneSLaptopCrawler());
        crawlers.add(new CellphoneSSmartPhoneCrawler());
        crawlers.add(new FPTShopLaptopCrawler());
        crawlers.add(new TGDDLaptopCrawler());
        storeProducts = new ArrayList<>();
        crawlers.stream()
                .map(Crawler::getAllProducts)
                .flatMap(Collection::stream)
                .forEach(storeProducts::add);
    }

    public static ProductDatabase getInstance() {
        return instance;
    }

    public void crawl() {
        crawl(Integer.MAX_VALUE);
    }

    public void crawl (int limit) {
        storeProducts.clear();
        crawlers.forEach(crawler -> crawler.crawl(limit));
        crawlers.stream()
                .map(Crawler::getAllProducts)
                .flatMap(Collection::stream)
                .forEach(storeProducts::add);
    }

    public void crawl (Crawler crawler) {
        storeProducts.clear();
        crawler.crawl();
        storeProducts.addAll(crawler.getAllProducts());
    }

    public List<Product> findAllProducts () {
        return storeProducts;
    }

    public Optional<Product> findProductById (int id) {
        for (Product product : findAllProducts()) {
            if (product.getId() == id) {
                return Optional.of(product);
            }
        }
        return Optional.empty();
    }

    public List<Product> findProducts (List<String[]> query, int limit, int offset) {
        return storeProducts.stream()
                .filter(product -> product.match(query))
                .skip(offset)
                .limit(limit)
                .toList();
    }

    public List<Laptop> findAllLaptops() {
        return storeProducts.stream()
                .filter(product -> product instanceof Laptop)
                .map(product -> (Laptop) product)
                .collect(Collectors.toList());
    }

    public List<Laptop> findLaptops (List<String[]> query, int limit, int offset) {
        return storeProducts.stream()
                .filter(product -> product instanceof Laptop)
                .filter(product -> product.match(query))
                .map(product -> (Laptop) product)
                .skip(offset)
                .limit(limit)
                .toList();
    }

    public List<Product> getFavouriteProducts() {
        return favouriteProducts;
    }

    public boolean isFavourite(Product product){
        return favouriteProducts.contains(product);
    }

    public void addToFavourites(Product product) {
        if (!favouriteProducts.contains(product)) {
            favouriteProducts.add(product);
        }
    }

    public void removeFromFavourites(Product product) {
        favouriteProducts.remove(product);
    }

}
