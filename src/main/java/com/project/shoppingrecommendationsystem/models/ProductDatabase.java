package com.project.shoppingrecommendationsystem.models;

import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import com.project.shoppingrecommendationsystem.ShoppingApplication;
import com.project.shoppingrecommendationsystem.models.crawler.CellphoneSCrawler;
import com.project.shoppingrecommendationsystem.models.crawler.Crawler;
import com.project.shoppingrecommendationsystem.models.crawler.FPTShopCrawler;
import com.project.shoppingrecommendationsystem.models.crawler.TGDDCrawler;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.*;

public class ProductDatabase {
    private static final ProductDatabase instance = new ProductDatabase();
    private final String resourceURL;
    private final List<Crawler> crawlers;
    private final List<Laptop> laptops;
    private final ObservableList<Laptop> favouriteProducts = FXCollections.observableArrayList();

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
        crawlers.add(new CellphoneSCrawler());
        crawlers.add(new FPTShopCrawler());
        crawlers.add(new TGDDCrawler());
        laptops = new ArrayList<>();
        crawlers.stream()
                .map(Crawler::getLaptops)
                .flatMap(Collection::stream)
                .forEach(laptops::add);
    }

    public static ProductDatabase getInstance() {
        return instance;
    }

    public void crawl() {
        crawl(Integer.MAX_VALUE);
    }

    public void crawl (int limit) {
        laptops.clear();
        crawlers.forEach(crawler -> crawler.crawlLaptops(limit));
        crawlers.stream()
                .map(Crawler::getLaptops)
                .flatMap(Collection::stream)
                .forEach(laptops::add);
    }

    public void crawl (Crawler crawler) {
        laptops.clear();
        crawler.crawlLaptops();
        laptops.addAll(crawler.getLaptops());
    }

    public void saveLaptops () {
        try (Writer writer = new FileWriter(resourceURL + "laptops.csv")){
            StatefulBeanToCsv<Laptop> beanToCsv = new StatefulBeanToCsvBuilder<Laptop>(writer).build();
            for (Laptop laptop : laptops) {
                beanToCsv.write(laptop);
            }
        } catch (IOException e) {
            System.err.println("[ERROR] : Unable to save file " + resourceURL);
        } catch (CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
            System.err.println("[ERROR] : An error occurred when parsing fields ");
            System.out.println(e.getMessage());
        }
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

    public List<Laptop> findLaptops (List<String[]> query, int limit, int offset) {
        return laptops.stream()
                .filter(laptop -> laptop.match(query))
                .skip(offset)
                .limit(limit)
                .toList();
    }

    public ObservableList<Laptop> getFavouriteProducts() {
        return favouriteProducts;
    }

    public boolean isFavourite(Laptop product){
        if (!favouriteProducts.contains(product)) return false;
        return true;
    }

    public void addToFavourites(Laptop product) {
        if (!favouriteProducts.contains(product)) {
            favouriteProducts.add(product);
        }
    }

    public void removeFromFavourites(Laptop product) {
        favouriteProducts.remove(product);
    }

}
