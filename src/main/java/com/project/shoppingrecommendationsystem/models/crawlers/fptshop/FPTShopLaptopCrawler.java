package com.project.shoppingrecommendationsystem.models.crawlers.fptshop;

import com.fasterxml.jackson.databind.JsonNode;
import com.project.shoppingrecommendationsystem.ShoppingApplication;
import com.project.shoppingrecommendationsystem.models.Laptop;
import com.project.shoppingrecommendationsystem.models.Product;
import com.project.shoppingrecommendationsystem.models.components.Review;
import com.project.shoppingrecommendationsystem.models.components.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class FPTShopLaptopCrawler extends FPTShopCrawler {
    private static final int MAX_THREADS = 10;

    public FPTShopLaptopCrawler() {
        this.resourceURL = Objects.requireNonNull(ShoppingApplication.class.getResource(""))
                .getPath()
                .replace("%20", " ") + "data/laptops/FPTShop/";
        this.productColumn = new String[]{"local_id", "score", "name", "displayName", "typePim", "type", "slug", "price", "industry",
                "brand", "productType", "group", "keySellingPoints", "units", "image", "originalPrice", "currentPrice",
                "discountPercentage", "endTimeDiscount", "promotions", "totalInventory", "skus"};
        this.propertyColumn = new String[]{"local_id", "Bộ xử lý", "Đồ họa", "RAM", "Lưu trữ", "Màn hình", "Giao tiếp và kết nối",
                "Âm Thanh", "Ổ đĩa quang", "Hệ điều hành", "Bảo mật", "Bàn phím & TouchPad", "Thông tin pin & sạc",
                "Phụ kiện trong hộp", "Thông số cơ bản", "Thiết kế & Trọng lượng", "Thông tin hàng hóa"};
        initializeMap();
    }

    @Override
    public void crawl () {
        crawl(Integer.MAX_VALUE);
    }

    @Override
    public void crawl (int limit){
        resetSave();
        ExecutorService executor = Executors.newFixedThreadPool(MAX_THREADS);
        int processed = 0;
        int count = 0;
        int max = 1;
        JsonNode jsonNode;
        while (count < max) {
            try {
                jsonNode = fetchHomepageAPI(count, "may-tinh-xach-tay");
                count+=50;
                max = jsonNode.get("totalCount").asInt();
            } catch (Exception e) {
                System.err.println("[ERROR] : An error occurred while crawling new laptops");
                System.out.println(e.getMessage());
                break;
            }
            for (JsonNode item : jsonNode.get("items")) {
                executor.submit(() -> this.extractAndSaveLaptop(item));
                if (++processed == limit) {
                    count=max;
                    break;
                }
            }
        }
        executor.shutdown();
        try {
            if(executor.awaitTermination(10, TimeUnit.MINUTES)) {
                System.out.println("[INFO] : FPTShop have crawled all laptops");
            } else {
                System.out.println("[INFO] : FPTShop have time-outed when crawling for laptops");
            }
        } catch (InterruptedException e) {
            System.out.println("[ERROR] : FPTShop has stopped abnormally");
        }
    }

    private void extractAndSaveLaptop (JsonNode item) {
        System.out.println("[INFO] : Extracting " + item.get("code").asText());
        try {
            Document productPage = Jsoup.connect("https://fptshop.com.vn/" + item.get("slug").asText()).get();
            String code = item.get("code").asText();
            String[] productRow = extractProduct(item);
            List<String[]> descriptions = extractDescriptions(code, productPage);
            String[] propertiesRow = extractProperties(code, productPage);
            List<String[]> reviews = extractReviews(code);
            save(productRow, descriptions, propertiesRow, reviews);
        } catch (Exception e) {
            System.err.println("[ERROR] : An error occurred while extracting laptop's information");
            System.out.println(e.getMessage());
        }
    }

    @Override
    protected Product parseProduct (String[] productRow, List<String[]> descriptions, String[] propertiesRow, List<String[]> reviews) {
        Laptop.Builder builder = new Laptop.Builder()
                .setName(productRow[productsMap.get("displayName")])
                .setProductImage(productRow[productsMap.get("image")])
                .setPrice(Integer.parseInt(productRow[productsMap.get("originalPrice")]))
                .setDiscountPrice(Integer.parseInt(productRow[productsMap.get("currentPrice")]))
                .setSource("FPTShop")
                .setSourceURL("https://fptshop.com.vn/" + productRow[productsMap.get("slug")])
                .setBrand(parseJson(productRow[productsMap.get("brand")], new String[]{"name"}))  // Column 'brand'
                .setColor(parseJson(propertiesRow[16], new String[]{"6", "value", "slice"}))
                .setDescription(descriptions)
                .setCpu(parseCPU(propertiesRow))
                .setRam(parseRAM(propertiesRow))
                .setDisplay(parseDisplay(propertiesRow))
                .setStorage(parseStorage(propertiesRow))
                .setConnectivity(parseConnectivity(propertiesRow))
                .setBattery(parseBattery(propertiesRow))
                .setLaptopCase(parseLaptopCase(propertiesRow));
        for (String[] row : reviews) {
            builder.addReview(new Review(parseDate(row[0]), row[1], row[2], row[3]));
        }
        return builder.build();
    }

    private CPU parseCPU(String[] propertiesRow) {
        return new CPU.Builder()
                .setName("%s %s %s".formatted(
                        parseJson(propertiesRow[1],new String[]{"0", "value"}),
                        parseJson(propertiesRow[1],new String[]{"1", "value"}),
                        parseJson(propertiesRow[1],new String[]{"2", "value"})))
                .setBaseFrequency(parseJson(propertiesRow[1],new String[]{"3", "value", "displayValue"}))
                .setTurboFrequency(parseJson(propertiesRow[1],new String[]{"4", "value", "displayValue"}))
                .setCores(parseJson(propertiesRow[1],new String[]{"5", "value"}))
                .setThreads(parseJson(propertiesRow[1],new String[]{"6", "value"}))
                .setCache(parseJson(propertiesRow[1],new String[]{"7", "value"}))
                .build();
    }

    private RAM parseRAM(String[] propertiesRow) {
        return new RAM.Builder()
                .setSize(parseJson(propertiesRow[3],new String[]{"0", "value"}))
                .setClock(parseJson(propertiesRow[3],new String[]{"2", "value", "displayValue"}))
                .setType(parseJson(propertiesRow[3],new String[]{"1", "value"}))
                .setSlots(parseJson(propertiesRow[3],new String[]{"5", "value"}))
                .setMaxSize(parseJson(propertiesRow[3],new String[]{"6", "value"}))
                .build();
    }

    private LaptopDisplay parseDisplay(String[] propertiesRow) {
        if (parseJson(propertiesRow[2],new String[]{"2", "value"}) != null) {
            return new LaptopDisplay.Builder()
                    .setGpuName(parseJson(propertiesRow[2],new String[]{"2", "value"}))
                    .setGpuBaseClock(parseJson(propertiesRow[2],new String[]{"3", "value"}))
                    .setGpuBoostClock(parseJson(propertiesRow[2],new String[]{"4", "value"}))
                    .setScreenSize(parseJson(propertiesRow[5],new String[]{"0", "value", "displayValue"}))
                    .setScreenResolution(parseJson(propertiesRow[5],new String[]{"2", "value"}))
                    .setRefreshRate(parseJson(propertiesRow[5],new String[]{"4", "value", "displayValue"}))
                    .build();
        } else {
            return new LaptopDisplay.Builder()
                    .setGpuName(parseJson(propertiesRow[2],new String[]{"8", "value"}))
                    .setGpuBaseClock(parseJson(propertiesRow[2],new String[]{"9", "value"}))
                    .setGpuBoostClock(parseJson(propertiesRow[2],new String[]{"10", "value"}))
                    .setScreenSize(parseJson(propertiesRow[5],new String[]{"0", "value", "displayValue"}))
                    .setScreenResolution(parseJson(propertiesRow[5],new String[]{"2", "value"}))
                    .setRefreshRate(parseJson(propertiesRow[5],new String[]{"4", "value", "displayValue"}))
                    .build();
        }
    }

    private Storage parseStorage(String[] propertiesRow) {
        String size = parseJson(propertiesRow[4],new String[]{"6", "value", "displayValue"});
        if (size == null) {
            size = parseJson(propertiesRow[4],new String[]{"9", "value", "displayValue"});
        }
        String bus = parseJson(propertiesRow[4],new String[]{"5", "value"});
        if (bus == null) {
            bus = parseJson(propertiesRow[4],new String[]{"8", "value"});
        }
        return new Storage.Builder()
                .setSize(size)
                .setBus(bus)
                .setStorageType(parseJson(propertiesRow[4],new String[]{"0", "value"}))
                .setUpgradable(parseJson(propertiesRow[4],new String[]{"4", "value"}))
                .setSlots(parseJson(propertiesRow[4],new String[]{"2", "value"}))
                .build();
    }

    private Connectivity parseConnectivity(String[] propertiesRow) {
        return new Connectivity.Builder()
                .setPorts(parseJson(propertiesRow[6],new String[]{"0", "value", "slice"}))
                .setWifi(parseJson(propertiesRow[6],new String[]{"1", "value", "slice"}))
                .setBluetooth(parseJson(propertiesRow[6],new String[]{"2", "value", "slice"}))
                .setWebCam(parseJson(propertiesRow[6],new String[]{"3", "value", "slice"}))
                .build();
    }

    private Battery parseBattery(String[] propertiesRow) {
        return new Battery.Builder()
                .setCapacity(parseJson(propertiesRow[12], new String[]{"1", "value", "slice", "displayValue"}))
                .setChargePower(parseJson(propertiesRow[12],new String[]{"2", "value", "slice", "displayValue"}))
                .build();
    }

    private Case parseLaptopCase(String[] propertiesRow) {
        return new Case.Builder()
                .setWeight(parseJson(propertiesRow[15],new String[]{"1", "value", "slice", "displayValue"}))
                .setDimensions(parseJson(propertiesRow[15],new String[]{"0", "value"}))
                .setMaterial(parseJson(propertiesRow[15],new String[]{"4", "value", "slice"}))
                .build();
    }
}

