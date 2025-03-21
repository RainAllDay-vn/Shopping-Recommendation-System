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
            for (Element product: products.children()) {

                List<String> row = new ArrayList<>();
                try {
                    for (Attribute attribute : product.attributes()) {
                        if (attribute.getKey().equals("class")) {
                            continue;
                        }
                        row.add(attribute.getValue());
                    }
                    Element a = product.selectFirst("a");
                    assert a != null;
                    for (Attribute attribute : a.attributes()) {
                        if (attribute.getKey().equals("class")) {
                            continue;
                        }
                        row.add(attribute.getValue());
                    }
                    Element img = product.select("img").first();
                    assert img != null;
                    row.add(img.attr("data-src"));
                    Element percent = product.select("span.percent").first();
                    if (percent != null) {
                        row.add(percent.text());
                    } else {
                        row.add("0%");
                    }
                    Element gift = product.select("p.item-gift").first();
                    if (gift != null) {
                        row.add(gift.text());
                    } else {
                        row.add(null);
                    }

                    Element rating = product.select("div.vote-txt").first();
                    if (rating != null) {
                        row.add(rating.text());
                    } else {
                        row.add(null);
                    }
                    Element unitSold = product.select("div.rating_Compare span").first();
                    if (unitSold != null) {
                        row.add(unitSold.text());
                    } else {
                        Element comingSoon = product.select("p.item-txt-online").first();
                        if (comingSoon != null) {
                            row.add(comingSoon.text());
                        } else {
                            Element newModel = product.select("span.ln-new").first();
                            if (newModel != null) {
                                row.add(newModel.text());
                            } else {
                                row.add(null);
                            }
                        }
                    }

                    rows.add(row.toArray(new String[0]));
                } catch (Exception e) {
                    System.err.println("Crawl an item failed");
                    System.err.println("Printing collected data: ");
                    System.out.println(row);
                }
            }
        }
        try (CSVWriter csvWriter = new CSVWriter(new FileWriter(resourceURL + "/laptop.csv", true))) {
            csvWriter.writeAll(rows);
        } catch (Exception e) {
            throw new RuntimeException("There was an error when accessing saving file");
        }
        driver.quit();
    }
}
