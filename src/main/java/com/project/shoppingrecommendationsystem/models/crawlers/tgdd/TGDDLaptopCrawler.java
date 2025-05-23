package com.project.shoppingrecommendationsystem.models.crawlers.tgdd;

import com.fasterxml.jackson.databind.JsonNode;
import com.project.shoppingrecommendationsystem.ShoppingApplication;
import com.project.shoppingrecommendationsystem.models.Laptop;
import com.project.shoppingrecommendationsystem.models.Product;
import com.project.shoppingrecommendationsystem.models.Review;
import com.project.shoppingrecommendationsystem.models.components.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TGDDLaptopCrawler extends TGDDCrawler {
    private static final int MAX_THREADS = 10;
    private final String pageURL = "https://www.thegioididong.com";

    public TGDDLaptopCrawler() {
        this.resourceURL = Objects.requireNonNull(ShoppingApplication.class.getResource(""))
                .getPath()
                .replace("%20", " ") + "data/laptops/TGDD/" ;
        this.productColumn = new String[]{"local_id", "data-issetup", "data-maingroup", "data-subgroup",
                "data-type", "data-vehicle", "data-productcode", "data-price-root", "data-ordertypeid", "data-pos", "sourceURL",
                "data-s", "data-site", "data-pro", "data-cache", "data-sv", "data-name", "data-price",
                "data-brand", "data-cate", "data-box", "data-color", "data-productstatus", "data-premium",
                "data-promotiontype", "imageURL", "percent", "gift", "rating", "unit-sold"};
        this.propertyColumn = new String[]{"local_id", "Công nghệ CPU", "Số nhân", "Số luồng", "Tốc độ CPU",
                "Tốc độ tối đa", "RAM", "Loại RAM", "Tốc độ Bus RAM", "Hỗ trợ RAM tối đa", "Ổ cứng", "Màn hình",
                "Độ phân giải", "Tần số quét", "Độ phủ màu", "Công nghệ màn hình", "Card màn hình", "Công nghệ âm thanh",
                "Cổng giao tiếp", "Kết nối không dây", "Webcam", "Đèn bàn phím", "Kích thước", "Chất liệu", "Thông tin Pin",
                "Hệ điều hành", "Thời điểm ra mắt", "Khe đọc thẻ nhớ", "Tính năng khác", "Tản nhiệt", "Màn hình cảm ứng",
                "NPU", "Hiệu năng xử lý AI (TOPS)"};
        initializeMap();
    }

    /**
     *
     */
    @Override
    public void crawl () {
        crawl(Integer.MAX_VALUE);
    }

    @Override
    public void crawl (int limit) {
        resetSave();
        ExecutorService executor = Executors.newFixedThreadPool(MAX_THREADS);
        driver = new ChromeDriver(new ChromeOptions().addArguments("--headless"));
        driver.get(pageURL + "/laptop");
        int processed = 0;
        int currentPage = 1;
        int total = 1;
        while ((currentPage-1)*20 <= total && processed < limit) {
            JsonNode jsonNode = fetchHomepageAPI(currentPage++);
            if (jsonNode == null) {
                System.err.println("[ERROR] : An error occurred while crawling new laptops");
                break;
            }
            total = jsonNode.get("total").asInt();
            Element products = Jsoup.parse(jsonNode.path("listproducts").textValue()).body();
            for (Element product : products.children()) {
                executor.submit(() -> this.extractAndSaveLaptop(product));
                if (++processed == limit) {
                    break;
                }
            }
        }
        executor.shutdown();
        try {
            if(executor.awaitTermination(10, TimeUnit.MINUTES)) {
                System.out.println("[INFO] : TGDD have crawled all laptops");
            } else {
                System.out.println("[INFO] : TGDD have time-outed when crawling for laptops");
            }
        } catch (InterruptedException e) {
            System.out.println("[ERROR] : TGDD has stopped abnormally");
        }
    }

    private void extractAndSaveLaptop (Element product) {
        try {
            String[] laptopRow = extractProduct(product);
            System.out.println("[INFO] : Extracting #" + laptopRow[0]);
            Element body = Jsoup.connect(pageURL + laptopRow[10]).get().body();
            List<String[]> descriptions = extractDescriptions(laptopRow[0], body);
            String[] propertiesRow = extractProperties(laptopRow[0], body);
            List<String[]> reviews = extractReviews(laptopRow[0]);
            save(laptopRow, descriptions, propertiesRow, reviews);
        } catch (Exception e) {
            System.err.println("[ERROR] : An error occurred while extracting laptop's information");
            System.out.println(e.getMessage());
        }
    }

    @Override
    protected Product parseProduct(String[] productRow, List<String[]> descriptions, String[] propertiesRow, List<String[]> reviews) {
        Laptop.Builder builder = new Laptop.Builder()
                .setName(productRow[productsMap.get("data-name")])
                .setProductImage(productRow[productsMap.get("imageURL")])
                .setDiscountPrice((int)Double.parseDouble(productRow[productsMap.get("data-price")]))
                .setSource("TGDD")
                .setSourceURL("https://www.thegioididong.com/" + productRow[productsMap.get("sourceURL")])
                .setBrand(productRow[productsMap.get("data-brand")])
                .setColor(productRow[productsMap.get("data-color")])
                .setDescription(descriptions)
                .setCpu(parseCPU(propertiesRow))
                .setRam(parseRAM(propertiesRow))
                .setDisplay(parseDisplay(propertiesRow))
                .setStorage(parseStorage(propertiesRow))
                .setConnectivity(parseConnectivity(propertiesRow))
                .setBattery(parseBattery(propertiesRow))
                .setLaptopCase(parseLaptopCase(propertiesRow));
        for (String[] row : reviews) {
            Date created = null;
            if (row[0]!=null && !row[0].isBlank()) {
                created = new Date(Long.parseLong(row[0]));
            }
            builder.addReview(new Review(created, row[1], row[2], row[3]));
        }
        return builder.build();
    }

    private CPU parseCPU(String[] propertiesRow) {
        return new CPU.Builder()
                .setName(propertiesRow[1])
                .setBaseFrequency(propertiesRow[4])
                .setTurboFrequency(propertiesRow[5])
                .setCores(propertiesRow[2])
                .setThreads(propertiesRow[3])
                .build();
    }

    private RAM parseRAM(String[] propertiesRow) {
        return new RAM.Builder()
                .setSize(propertiesRow[6])
                .setClock(propertiesRow[8])
                .setType(propertiesRow[7])
                .setMaxSize(propertiesRow[9])
                .build();
    }

    private LaptopDisplay parseDisplay(String[] propertiesRow) {
        return new LaptopDisplay.Builder()
                .setScreenSize(propertiesRow[11])
                .setScreenResolution(propertiesRow[12])
                .setRefreshRate(propertiesRow[13])
                .setGpuName(propertiesRow[14])
                .build();
    }

    private Storage parseStorage(String[] propertiesRow) {
        return new Storage.Builder()
                .setSize(propertiesRow[10])
                .build();
    }

    private Connectivity parseConnectivity(String[] propertiesRow) {
        return new Connectivity.Builder()
                .setPorts(propertiesRow[18])
                .setWifi(propertiesRow[19])
                .setWebCam(propertiesRow[20])
                .build();
    }

    private Battery parseBattery(String[] propertiesRow) {
        return new Battery.Builder()
                .setCapacity(propertiesRow[24])
                .build();
    }

    private Case parseLaptopCase(String[] propertiesRow) {
        return new Case.Builder()
                .setDimensions(propertiesRow[22])
                .setMaterial(propertiesRow[23])
                .build();
    }
}
