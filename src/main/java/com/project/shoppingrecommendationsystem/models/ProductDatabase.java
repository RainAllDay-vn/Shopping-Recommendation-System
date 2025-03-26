package com.project.shoppingrecommendationsystem.models;

import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import com.project.shoppingrecommendationsystem.HelloApplication;
import com.project.shoppingrecommendationsystem.models.crawler.CellphoneSCrawler;
import com.project.shoppingrecommendationsystem.models.crawler.Crawler;
import com.project.shoppingrecommendationsystem.models.crawler.FPTShopCrawler;
import com.project.shoppingrecommendationsystem.models.crawler.TGDDCrawler;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.*;

public class ProductDatabase {
    private final String resourceURL;
    private final List<Crawler> crawlers;
    private final List<Laptop> laptops;

    public ProductDatabase() {
        resourceURL = Objects.requireNonNull(HelloApplication.class.getResource(""))
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

    public List<Laptop> findLaptop (String[][] query) {
        return laptops.stream()
                .filter(laptop -> laptop.match(query))
                .toList();
    }
}
