package com.project.shoppingrecommendationsystem.models.database;

import com.project.shoppingrecommendationsystem.models.Laptop;
import com.project.shoppingrecommendationsystem.models.Product;
import com.project.shoppingrecommendationsystem.models.crawlers.Crawler;
import com.project.shoppingrecommendationsystem.models.crawlers.cellphones.CellphoneSLaptopCrawler;
import com.project.shoppingrecommendationsystem.models.crawlers.fptshop.FPTShopLaptopCrawler;
import com.project.shoppingrecommendationsystem.models.crawlers.tgdd.TGDDLaptopCrawler;

import java.util.*;
import java.util.stream.Collectors;


public class ListDatabase implements ProductDatabase{
    private static final ListDatabase instance = new ListDatabase();
    private final List<Crawler> crawlers;
    private final List<Product> storeProducts;
    private final List<Product> favouriteProducts = new ArrayList<>();

    private ListDatabase() {
        crawlers = new ArrayList<>();
        crawlers.add(new CellphoneSLaptopCrawler());
        crawlers.add(new FPTShopLaptopCrawler());
        crawlers.add(new TGDDLaptopCrawler());
        storeProducts = new ArrayList<>();
        crawlers.stream()
                .map(Crawler::getAllProducts)
                .flatMap(Collection::stream)
                .forEach(storeProducts::add);
        Collections.shuffle(storeProducts);
    }

    public static ListDatabase getInstance() {
        return instance;
    }

    @Override
    public void crawl() {
        crawl(Integer.MAX_VALUE);
    }

    @Override
    public void crawl (int limit) {
        storeProducts.clear();
        crawlers.forEach(crawler -> crawler.crawl(limit));
        crawlers.stream()
                .map(Crawler::getAllProducts)
                .flatMap(Collection::stream)
                .forEach(storeProducts::add);
        Collections.shuffle(storeProducts);
    }

    @Override
    public void crawl (Crawler crawler) {
        storeProducts.clear();
        crawler.crawl();
        storeProducts.addAll(crawler.getAllProducts());
    }

    @Override
    public List<Product> findAllProducts () {
        return new ArrayList<>(storeProducts);
    }

    @Override
    public Optional<Product> findProductById (int id) {
        for (Product product : findAllProducts()) {
            if (product.getId() == id) {
                return Optional.of(product);
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Product> findProducts (List<String[]> query, int limit, int offset) {
        return findAllProducts().stream()
                .filter(product -> product.match(query))
                .skip(offset)
                .limit(limit)
                .toList();
    }

    public List<Laptop> findAllLaptops() {
        return findAllProducts().stream()
                .filter(product -> product instanceof Laptop)
                .map(product -> (Laptop) product)
                .collect(Collectors.toList());
    }

    public List<Laptop> findLaptops (List<String[]> query, int limit, int offset) {
        return findAllProducts().stream()
                .filter(product -> product instanceof Laptop)
                .filter(product -> product.match(query))
                .map(product -> (Laptop) product)
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
        storeProducts.sort(Comparator.comparing(Product::getName));
    }

    @Override
    public void sortByPrice(){
        storeProducts.sort(Comparator.comparingInt(Product::getPrice));
    }

    @Override
    public void sortByDiscountPrice(){
        storeProducts.sort(Comparator.comparingInt(Product::getDiscountPrice));
    }
}
