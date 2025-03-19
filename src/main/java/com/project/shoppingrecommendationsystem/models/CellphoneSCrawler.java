package com.project.shoppingrecommendationsystem.models;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.*;
import com.project.shoppingrecommendationsystem.ShoppingApplication;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.FileWriter;
import java.util.*;


public class CellphoneSCrawler {
    private final ObjectMapper mapper = new ObjectMapper();
    private final String resourceURL = Objects.requireNonNull(ShoppingApplication.class.getResource(""))
            .getPath()
            .replace("%20", " ") + "data/CellphoneS/";

    public static void main(String[] args) {
        CellphoneSCrawler crawler = new CellphoneSCrawler();
        try {
            crawler.crawlLaptops();
        } catch (Exception e) {
            System.out.println("Crawling did not work");
            System.out.println(e.getMessage());
        }
    }

    // Crawl Homepage API for the list of Laptops
    private void crawlLaptops () {
        try (CSVWriter csvWriter = new CSVWriter(new FileWriter(resourceURL + "laptop.csv"))) {
            csvWriter.writeNext(new String[]{"product_id", "name", "attributes", "sku", "doc_quyen", "manufacturer",
                    "url_key", "url_path", "categories", "review", "is_installment", "stock_available_id", "company_stock_id",
                    "filter", "is_parent", "price", "prices", "special_price", "promotion_information",
                    "thumbnail", "promotion_pack", "sticker", "flash_sale_types"});
        } catch (Exception e){
            System.out.println("There was an error when accessing saving file");
            System.out.println(e.getMessage());
            return;
        }
        String requestBody = "{" +
                "  \"query\": \"query " +
                    "GetProductsByCateId { products( filter: { static: { categories: [\\\"380\\\"], " +
                        "province_id: 30, stock: { from: 0 }, " +
                        "stock_available_id: [46, 56, 152, 4920], " +
                        "filter_price: { from: 0, to: 194990000 } }, " +
                        "dynamic: {} }, " +
                    "page: %s, size: 100, sort: [{ view: desc }] ) " +
                    "{ general { product_id name attributes sku doc_quyen manufacturer url_key url_path categories " +
                        "{ categoryId name uri } review { total_count average_rating } }, " +
                    "filterable { is_installment stock_available_id company_stock_id filter { id Label } " +
                        "is_parent price prices special_price promotion_information thumbnail promotion_pack sticker flash_sale_types } } }\"," +
                "  \"variables\": {}" +
                "}";
        List<String[]> laptopRows = new ArrayList<>();
        for (int i=1; i<=10; i++) {
            JsonNode products;
            try {
                Document response = Jsoup.connect("https://api.cellphones.com.vn/v2/graphql/query")
                        .header("Content-Type", "application/json") // Gửi dữ liệu dạng JSON
                        .requestBody(requestBody.formatted(i))
                        .ignoreContentType(true)
                        .post();
                JsonNode jsonNode = mapper.readTree(response.body().text());
                products = jsonNode.path("data").path("products");
                if (products == null) {
                    return;
                }
            } catch (Exception e) {
                System.out.println("An error occurred while crawling ...");
                System.out.println(e.getMessage());
                break;
            }
            for (JsonNode product : products) {
                List<String> laptopRow = new ArrayList<>();
                for (Iterator<Map.Entry<String, JsonNode>> it = product.path("general").fields(); it.hasNext(); ) {
                    Map.Entry<String, JsonNode> entry = it.next();
                    laptopRow.add(entry.getValue().toString());
                }
                for (Iterator<Map.Entry<String, JsonNode>> it = product.path("filterable").fields(); it.hasNext(); ) {
                    Map.Entry<String, JsonNode> entry = it.next();
                    JsonNode value = entry.getValue();
                    if (value.isNumber()) {
                        laptopRow.add(String.valueOf(value.asInt()));
                    } else {
                        laptopRow.add(value.toString());
                    }
                }
                laptopRows.add(laptopRow.toArray(new String[0]));
            }
        }
        try (CSVWriter csvWriter = new CSVWriter(new FileWriter(resourceURL + "laptop.csv", true))) {
            csvWriter.writeAll(laptopRows);
        } catch (Exception e){
            System.out.println("There was an error when accessing saving file");
            System.out.println(e.getMessage());
        }
    }

}
