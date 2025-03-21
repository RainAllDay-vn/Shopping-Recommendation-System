package com.project.shoppingrecommendationsystem.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVWriter;
import com.project.shoppingrecommendationsystem.HelloApplication;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Element;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TGDDCrawler {
    private final ObjectMapper mapper = new ObjectMapper();
    WebDriver driver;
    private final String resourceURL = Objects.requireNonNull(HelloApplication.class.getResource(""))
            .getPath()
            .replace("%20", " ") + "data/TGDD";
    private final String[] columns = {"data-index", "data-id", "data-issetup", "data-maingroup", "data-subgroup",
            "data-type", "data-vehicle", "data-productcode", "data-price-root", "data-ordertypeid", "data-pos", "sourceURL",
            "data-s", "data-site", "data-pro", "data-cache", "data-sv", "data-name", "data-id", "data-price",
            "data-brand", "data-cate", "data-box", "data-pos", "data-color", "data-productstatus", "data-premium",
            "data-promotiontype", "imageURL", "percent", "gift", "rating", "unit-sold"};

    public static void main(String[] args) {
        TGDDCrawler crawler = new TGDDCrawler();
        try {
            crawler.crawlHomepageAPI();
        } catch (Exception e) {
            System.err.println("Crawling did not work");
            System.err.println(e.getMessage());
        }
    }

    // Crawl Homepage API for the list of Laptops
    private void crawlHomepageAPI () throws JsonProcessingException {
        List<String[]> rows = new ArrayList<>();
        driver = new ChromeDriver(new ChromeOptions().addArguments("--headless"));
        int currentPage = 1;
        int total = 1;
        String script = """
            return (async function() {
                try {
                    let response = await fetch("https://www.thegioididong.com/Category/FilterProductBox?c=44&o=13&pi=%s", {
                        method: "POST",
                        headers: {
                            "Accept": "*/*",
                            "Connection": "keep-alive",
                            "Content-Type": "application/x-www-form-urlencoded; charset=UTF-8",
                        },
                        body: "IsParentCate=False&IsShowCompare=True&prevent=true"
                    });
                    return await response.text();
                } catch (error) {
                    return 'Error: ' + error.toString();
                }
            })();
        """;


        try (CSVWriter csvWriter = new CSVWriter(new FileWriter(resourceURL + "/laptop.csv"));
        FileWriter ignored1 = new FileWriter(resourceURL + "/raw.txt")) {
            csvWriter.writeNext(columns);
        } catch (Exception e) {
            throw new RuntimeException("There was an error when accessing saving file");
        }
        driver.get("https://www.thegioididong.com/laptop");
        while ((currentPage-1)*20 <= total) {
            String response = (String) ((JavascriptExecutor) driver).executeScript(script.formatted(currentPage));
            currentPage++;
            JsonNode jsonNode = mapper.readTree(response);
            total = jsonNode.get("total").asInt();
            Element products = Jsoup.parse(jsonNode.path("listproducts").textValue()).body();
            try (FileWriter fileWriter = new FileWriter(resourceURL + "/raw.txt", true)) {
                fileWriter.write(products.toString());
            } catch (Exception ignored) {}
            products.stream()
                    .map(this::extractData)
                    .filter(Objects::nonNull)
                    .map(row -> row.toArray(new String[0]))
                    .forEach(rows::add);
        }
        try (CSVWriter csvWriter = new CSVWriter(new FileWriter(resourceURL + "/laptop.csv", true))) {
            csvWriter.writeAll(rows);
        } catch (Exception e) {
            throw new RuntimeException("There was an error when accessing saving file");
        }
        driver.quit();
    }

    private List<String> extractData (Element product) {
        List<String> row = new ArrayList<>();
        try {
            extractDataFromAttribute(product, row);
            extractDataFromAnchor(product, row);
            extractImage(product, row);
            extractDiscountedPercent(product, row);
            extractGift(product, row);
            extractRating(product, row);
            extractUnitSold(product, row);
            return row;
        } catch (Exception e) {
            System.err.println("Crawl an item failed");
            System.err.println("Printing collected data: ");
            System.out.println(row);
            return null;
        }
    }

    private void extractDataFromAttribute (Element product, List<String> row) {
        for (Attribute attribute : product.attributes()) {
            if (attribute.getKey().equals("class")) {
                continue;
            }
            row.add(attribute.getValue());
        }
    }

    private void extractDataFromAnchor (Element product, List<String> row) {
        Element a = product.selectFirst("a");
        if (a == null) {
            throw new RuntimeException("Cannot locate <a> element from product");
        }
        for (Attribute attribute : a.attributes()) {
            if (attribute.getKey().equals("class")) {
                continue;
            }
            row.add(attribute.getValue());
        }
    }

    private void extractImage(Element product, List<String> row) {
        Element img = product.select("img").first();
        if (img != null) {
            row.add(img.attr("data-src"));
            return;
        }
        row.add(null);
    }

    private void extractDiscountedPercent (Element product, List<String> row) {
        Element percent = product.selectFirst("span.percent");
        if (percent != null) {
            row.add(percent.text());
        }
        row.add("0%");
    }

    private void extractGift (Element product, List<String> row) {
        Element gift = product.selectFirst("p.item-gift");
        if (gift != null) {
            row.add(gift.text());
            return;
        }
        row.add(null);
    }

    private void extractRating (Element product, List<String> row) {
        Element rating = product.selectFirst("div.vote-txt");
        if (rating != null) {
            row.add(rating.text());
            return;
        }
        row.add(null);
    }

    private void extractUnitSold (Element product, List<String> row) {
        Element unitSold = product.selectFirst("div.rating_Compare span");
        if (unitSold != null) {
            row.add(unitSold.text());
            return;
        }
        Element comingSoon = product.selectFirst("p.item-txt-online");
        if (comingSoon != null) {
            row.add(comingSoon.text());
            return;
        }
        Element newModel = product.selectFirst("span.ln-new");
        if (newModel != null) {
            row.add(newModel.text());
            return;
        }
        row.add(null);
    }
}
