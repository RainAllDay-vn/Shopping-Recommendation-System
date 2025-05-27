package com.project.shoppingrecommendationsystem.models.database;

import com.project.shoppingrecommendationsystem.models.Product;
import com.project.shoppingrecommendationsystem.models.crawlers.Crawler;
import com.project.shoppingrecommendationsystem.models.crawlers.cellphones.CellphoneSLaptopCrawler;
import com.project.shoppingrecommendationsystem.models.crawlers.cellphones.CellphoneSSmartPhoneCrawler;
import com.project.shoppingrecommendationsystem.models.crawlers.fptshop.FPTShopLaptopCrawler;
import com.project.shoppingrecommendationsystem.models.crawlers.tgdd.TGDDLaptopCrawler;

import java.util.*;

public class HashDatabase implements ProductDatabase {
    private final List<Crawler> crawlers;
    private final HashMap<Integer, Product> storeProducts;
    private final List<Product> favouriteProducts = new ArrayList<>();

    public HashDatabase() {
        crawlers = new ArrayList<>();
        crawlers.add(new CellphoneSLaptopCrawler());
        crawlers.add(new CellphoneSSmartPhoneCrawler());
        crawlers.add(new FPTShopLaptopCrawler());
        crawlers.add(new TGDDLaptopCrawler());
        storeProducts = new HashMap<>();
        crawlers.stream()
                .map(Crawler::getAllProducts)
                .flatMap(Collection::stream)
                .forEach(p -> storeProducts.put(p.getId(), p));
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
                .forEach(p -> storeProducts.put(p.getId(), p));
    }

    @Override
    public void crawl(Crawler crawler) {
        storeProducts.clear();
        crawler.crawl();
        crawler.getAllProducts().forEach(p -> storeProducts.put(p.getId(), p));
    }

    @Override
    public List<Product> findAllProducts() {
        return new ArrayList<>(storeProducts.values());
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
        return storeProducts.values()
                .stream()
                .filter(product -> product.match(query))
                .skip(offset)
                .limit(limit)
                .toList();
    }

    @Override
    public List<Product> getFavouriteProducts() {
        return new ArrayList<>(favouriteProducts);
    }

    @Override
    public boolean isFavourite(Product product){
        return favouriteProducts.contains(product);
    }

    @Override
    public void addToFavourites(Product product) {
        if (!favouriteProducts.contains(product)) {
            favouriteProducts.add(product);
        }
    }

    @Override
    public void removeFromFavourites(Product product) {
        favouriteProducts.remove(product);
    }

    @Override
    public void sortByName(){
        // To be implemented
    }

    @Override
    public void sortByPrice(){
        // To be implemented
    }

    @Override
    public void sortByDiscountPrice(){
        // To bo implemented
    }
}
