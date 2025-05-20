package com.project.shoppingrecommendationsystem.models;

import com.project.shoppingrecommendationsystem.ShoppingApplication;
import com.project.shoppingrecommendationsystem.models.crawler.CellphoneSLaptopCrawler;
import com.project.shoppingrecommendationsystem.models.crawler.Crawler;
import com.project.shoppingrecommendationsystem.models.crawler.FPTShopCrawler;
import com.project.shoppingrecommendationsystem.models.crawler.TGDDCrawler;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class ProductDatabase {
    private static final ProductDatabase instance = new ProductDatabase();
    private final String resourceURL;
    private final List<Crawler> crawlers;
    private final List<Product> storeProducts;
    private final List<Product> favouriteProducts = new ArrayList<>();
    private final List<Product> compareList = new ArrayList<>();

    private ProductDatabase() {
        resourceURL = Objects.requireNonNull(ShoppingApplication.class.getResource(""))
                .getPath()
                .replace("%20", " ") + "data/database/";
        File resourceDir = new File(this.resourceURL);
        if (!resourceDir.exists()) {
            if (!resourceDir.mkdirs()) {
                throw new RuntimeException("Unable to create directory " + this.resourceURL);
            }
        }
        crawlers = new ArrayList<>();
        crawlers.add(new CellphoneSLaptopCrawler());
        crawlers.add(new FPTShopCrawler());
        crawlers.add(new TGDDCrawler());
        storeProducts = new ArrayList<>();
        crawlers.stream()
                .map(Crawler::getAll)
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
                .map(Crawler::getAll)
                .flatMap(Collection::stream)
                .forEach(storeProducts::add);
    }

    public void crawl (Crawler crawler) {
        storeProducts.clear();
        crawler.crawl();
        storeProducts.addAll(crawler.getAll());
    }

    public List<Laptop> findAllLaptops() {
        return storeProducts.stream()
                .filter(product -> product instanceof Laptop)
                .map(product -> (Laptop) product)
                .collect(Collectors.toList());
    }

    public Optional<Laptop> findLaptopById(int id) {
        for (Laptop laptop : findAllLaptops()) {
            if (laptop.getId() == id) {
                return Optional.of(laptop);
            }
        }
        return Optional.empty();
    }

    public List<Laptop> findLaptops (List<String[]> query, int limit, int offset) {
        return findAllLaptops().stream()
                .filter(product -> product.match(query))
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

    public List<Product> getCompareList() {
        return compareList;
    }

    public void addToCompareList(Product product) {
        if(!compareList.contains(product)) {
            compareList.add(product);
        }
    }

    public void removeFromCompareList(Product product) {
        compareList.remove(product);
    }

    public void sortByName(){
        laptops.sort(Comparator.comparing(Product::getName));
    }

    public void sortByPrice(){
        laptops.sort(Comparator.comparingInt(Product::getPrice));
    }

    public void sortByDiscountPrice(){
        laptops.sort(Comparator.comparingInt(Product::getDiscountPrice));
    }
}
