package com.project.shoppingrecommendationsystem.models;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import com.project.shoppingrecommendationsystem.HelloApplication;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

public class FPTShopCrawler {
    private final ObjectMapper mapper = new ObjectMapper();
    private final String resourceURL = Objects.requireNonNull(HelloApplication.class.getResource(""))
            .getPath()
            .replace("%20", " ") + "data/FPTShop";

    public static void main(String[] args) {
        FPTShopCrawler crawler = new FPTShopCrawler();
        try {
            crawler.crawlHomepageAPI();
            crawler.crawlLaptops();
        } catch (Exception e) {
            System.out.println("Crawling did not work");
            System.out.println(e.getMessage());
        }
    }

    // Crawl Homepage API for the list of Laptops
    private void crawlHomepageAPI () throws IOException {
        String[] columns = {"index", "score", "code", "name", "displayName", "typePim", "type", "slug", "price", "industry",
                "brand", "productType", "group", "keySellingPoints", "units", "image", "originalPrice", "currentPrice",
                "discountPercentage", "endTimeDiscount", "promotions", "totalInventory", "skus"};
        try (CSVWriter writer = new CSVWriter(new FileWriter(resourceURL + "/laptop.csv"))){
            writer.writeNext(columns);
        }
        int currentRow = 0;
        int count = 0;
        int max = 1;
        while (count < max) {
            String requestBody =  """
                    {
                        "categoryType": "category",
                        "maxResultCount": 50,
                        "skipCount": %d,
                        "slug": "may-tinh-xach-tay",
                        "sortMethod": "noi-bat"
                    }
                    """.formatted(count);
            Map<String, Object> jsonMap;
            try {
                Document response = Jsoup.connect("https://papi.fptshop.com.vn/gw/v1/public/fulltext-search-service/category")
                        .header("Content-Type", "application/json") // Gửi dữ liệu dạng JSON
                        .requestBody(requestBody)
                        .ignoreContentType(true)
                        .post();
                jsonMap = mapper.readValue(response.body().text(), new TypeReference<>() {});
            } catch (Exception e) {
                System.out.println(e.getMessage());
                break;
            }
            count+=50;
            max = (int) jsonMap.get("totalCount");
            List<Map<String, Object>> items = mapper.convertValue(jsonMap.get("items"), new TypeReference<>() {});
            try (CSVWriter writer = new CSVWriter(new FileWriter(resourceURL + "/laptop.csv", true))) {
                for (Map<String, Object> item : items) {
                    String[] row = new String[columns.length];
                    row[0] = String.valueOf(currentRow++);
                    for (int i = 1; i < columns.length; i++) {
                        row[i] = item.get(columns[i]) == null ? "" : item.get(columns[i]).toString();
                    }
                    writer.writeNext(row);
                }
            } catch (Exception ignored) {}
        }
    }

    // Crawl specific details of all laptops
    private void crawlLaptops () throws IOException, CsvValidationException {
        try (CSVWriter writer = new CSVWriter(new FileWriter(resourceURL + "/properties.csv"))) {
            String[] columns = {"index", "Bộ xử lý", "Đồ họa", "RAM", "Lưu trữ", "Màn hình", "Giao tiếp và kết nối", "Âm Thanh",
                    "Ổ đĩa quang", "Hệ điều hành", "Bảo mật", "Bàn phím & TouchPad", "Thông tin pin & sạc",
                    "Phụ kiện trong hộp", "Thông số cơ bản", "Thiết kế & Trọng lượng", "Thông tin hàng hóa"};
            writer.writeNext(columns);
        }
        try (CSVWriter writer = new CSVWriter(new FileWriter(resourceURL + "/description.csv"))) {
            String[] columns = {"index", "description"};
            writer.writeNext(columns);
        }

        try (CSVReader reader = new CSVReader(new FileReader(resourceURL + "/laptop.csv"))) {
            String[] columns = reader.readNext();
            int slugIndex = Arrays.asList(columns).indexOf("slug");
            for (String[] row : reader) {
                System.out.printf("Processing row #%s\n", row[0]);
                crawlLaptop(row[0], row[slugIndex]);
            }
        }
    }

    // Crawl specific details of a laptop
    private void crawlLaptop (String id, String slug) {
        try {
            Pattern compiledPattern = Pattern.compile("attributeItem");
            Document response = Jsoup.connect("https://fptshop.com.vn/" + slug).get();

            String script = response.getElementsByTag("script")
                    .stream()
                    .map(Node::toString)
                    .filter(compiledPattern.asPredicate())
                    .findFirst()
                    .orElse("");
            int index = script.indexOf("attributeItem");
            int bracketCount = 0;
            for(int i = index+16; i < script.length(); i++) {
                if(script.charAt(i) == '[') {
                    bracketCount++;
                } else if (script.charAt(i) == ']') {
                    bracketCount--;
                }
                if(bracketCount == 0) {
                    script = script.substring(index+16, i+1).replace("\\\"", "\"");
                    break;
                }
            }
            List<Map<String, Object>> jsonMap = mapper.readValue(script, new TypeReference<>() {});
            String[] propertiesRow = new String[jsonMap.size()+1];
            propertiesRow[0] = id;
            for (int i = 1; i <= jsonMap.size(); i++) {
                propertiesRow[i] = String.valueOf(jsonMap.get(i-1).getOrDefault("attributes", ""));
            }
            try (CSVWriter writer = new CSVWriter(new FileWriter(resourceURL + "/properties.csv", true))) {
                writer.writeNext(propertiesRow);
            }

            Element descriptionContainer = response.getElementsByClass("description-container").getFirst();
            StringBuilder stringBuilder = new StringBuilder();
            for(Element child : descriptionContainer.children()) {
                stringBuilder.append(child.text());
            }
            String description = stringBuilder.toString();
            try (CSVWriter writer = new CSVWriter(new FileWriter(resourceURL + "/description.csv", true))) {
                writer.writeNext(new String[]{id, description});
            }
        } catch (Exception e) {
            System.out.printf("Crawling laptop #%s failed\n", id);
            System.out.println(e.getMessage());
        }
    }
}

