package com.project.shoppingrecommendationsystem.models;

import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import com.project.shoppingrecommendationsystem.ShoppingApplication;
import com.project.shoppingrecommendationsystem.llmagent.VectorDatabase;
import com.project.shoppingrecommendationsystem.models.crawler.CellphoneSCrawler;
import com.project.shoppingrecommendationsystem.models.crawler.LaptopCrawler;
import com.project.shoppingrecommendationsystem.models.crawler.FPTShopCrawler;
import com.project.shoppingrecommendationsystem.models.crawler.TGDDCrawler;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.*;
import java.util.concurrent.ExecutionException;

import org.springframework.ai.document.Document;
import java.util.stream.Collectors; // Add this import

import com.project.shoppingrecommendationsystem.llmagent.VectorDatabase;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;

public class ProductDatabase {
    private static final ProductDatabase instance = new ProductDatabase();
    private final String resourceURL;
    private final List<LaptopCrawler> crawlers;
    private final List<Laptop> laptops;

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
                .map(LaptopCrawler::getLaptops)
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
                .map(LaptopCrawler::getLaptops)
                .flatMap(Collection::stream)
                .forEach(laptops::add);
    }

    public void crawl (LaptopCrawler crawler) {
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


    public static void main (String[] args) throws IOException, ExecutionException, InterruptedException {
        ProductDatabase rawDatabase = ProductDatabase.getInstance();

        String storeName = "Shopping Recommendation System";
        VectorDatabase vectorStore = new VectorDatabase(storeName);

        List<Laptop> laptopList = rawDatabase.findAllLaptops();
        List<Document> documents = laptopList.stream()
                .map(laptop -> {
                    Map<String, Object> metadata = new HashMap<>();
                    metadata.put("id", laptop.getId());
                    metadata.put("name", laptop.getName());
                    metadata.put("price", laptop.getPrice());
                    return new Document(laptop.getDescription() != null ? laptop.getDescription() : "", metadata); // Use description as content
                })
                .collect(Collectors.toList());

        TokenTextSplitter splitter =
                TokenTextSplitter.builder()
                        .withChunkSize(700)
                        .withKeepSeparator(true)
                        .build();

        vectorStore.addToQdrant(splitter.apply(documents));
        System.out.println("Laptops crawled, saved, and added to vector store.");
    }
}

