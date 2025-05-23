package com.project.shoppingrecommendationsystem.models.crawlers.cellphones;

import com.fasterxml.jackson.databind.JsonNode;
import com.project.shoppingrecommendationsystem.models.crawlers.CSVCrawler;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public abstract class CellphoneSCrawler extends CSVCrawler {
    String fetchHomepageAPI(int pageIndex, int category) {
        String requestBody = "{" +
                "  \"query\": \"query " +
                "GetProductsByCateId { products( filter: { static: { categories: [\\\"%d\\\"], " +
                "province_id: 30, stock: { from: 0 }, " +
                "stock_available_id: [46, 56, 152, 4920], " +
                "filter_price: { from: 0, to: 194990000 } }, " +
                "dynamic: {} }, " +
                "page: %d, size: 10, sort: [{ view: desc }] ) " +
                "{ general { product_id name attributes sku doc_quyen manufacturer url_key url_path categories " +
                "{ categoryId name uri } review { total_count average_rating } }, " +
                "filterable { is_installment stock_available_id company_stock_id filter { id Label } " +
                "is_parent price prices special_price promotion_information thumbnail promotion_pack sticker flash_sale_types } } }\"," +
                "  \"variables\": {}" +
                "}";
        try {
            return Jsoup.connect("https://api.cellphones.com.vn/v2/graphql/query")
                    .header("Content-Type", "application/json")
                    .requestBody(requestBody.formatted(category, pageIndex))
                    .ignoreContentType(true)
                    .post()
                    .body()
                    .text();
        } catch (Exception ignored) {
            return null;
        }
    }

    private void addToProductRow(String[] row, String key, String value) {
        if(key.equals("product_id")) {
            row[productsMap.get("local_id")] = value;
        }else if(productsMap.containsKey(key)) {
            row[productsMap.get(key)] = value;
        }
    }

    String[] extractProduct(JsonNode product) {
        String[] productRow = new String[productColumn.length];
        Arrays.fill(productRow, "");
        for (Iterator<Map.Entry<String, JsonNode>> it = product.path("general").fields(); it.hasNext(); ) {
            Map.Entry<String, JsonNode> entry = it.next();
            if (entry.getKey().equals("attributes")) {
                continue;
            }
            addToProductRow(productRow, entry.getKey(), entry.getValue().asText());
        }
        for (Iterator<Map.Entry<String, JsonNode>> it = product.path("filterable").fields(); it.hasNext(); ) {
            Map.Entry<String, JsonNode> entry = it.next();
            if (entry.getKey().equals("thumbnail")) {
                addToProductRow(
                        productRow,
                        "thumbnail",
                        downloadImage("https://cellphones.com.vn/media/catalog/product"+entry.getValue().asText(), productRow[0]));
            }
            else if (entry.getValue().isNumber()) {
                addToProductRow(productRow, entry.getKey(), String.valueOf(entry.getValue().asInt()));
            }
            else if(!entry.getValue().isEmpty()){
                addToProductRow(productRow, entry.getKey(), entry.getValue().asText());
            }

        }
        return productRow;
    }

    List<String[]> extractDescriptions (JsonNode product) {
        List<String[]> descriptions = new LinkedList<>();
        String productId = product.path("general").get("product_id").asText();
        String productURL = product.path("general").get("url_path").asText();
        try {
            String pageURL = "https://cellphones.com.vn/";
            Element body = Jsoup.connect(pageURL + productURL).get().body();
            Element description = body.selectFirst("#cpsContentSEO");
            assert description != null;
            descriptions.add(new String[]{productId, "header", "Product Description"});
            for (Element child : description.children()) {
                if (child.className().equals("table-content")||child.text().isBlank()) {
                    continue;
                }
                if (child.tag().getName().equals("p")) {
                    descriptions.add(new String[]{productId, "normal", child.text()});
                    continue;
                }
                if (child.tag().getName().equals("h2") || child.tag().getName().equals("h3")) {
                    descriptions.add(new String[]{productId, "bold", child.text()});
                }
            }
        } catch (Exception ignored) {}
        return descriptions;
    }

    private void addToPropertiesRow(String[] row, String key, String value) {
        if(key.equals("product_id")) {
            row[productsMap.get("local_id")] = value;
        }
        if(propertiesMap.containsKey(key)) {
            row[propertiesMap.get(key)] = value;
        }
    }

    String[] extractProperties (JsonNode product) {
        String[] propertiesRow = new String[propertyColumn.length];
        Arrays.fill(propertiesRow, "");
        for (Iterator<Map.Entry<String, JsonNode>> it = product.path("general").path("attributes").fields(); it.hasNext(); ) {
            Map.Entry<String, JsonNode> entry = it.next();
            addToPropertiesRow(propertiesRow, entry.getKey(), entry.getValue().asText().strip());
        }
        return propertiesRow;
    }

    List<String[]> extractReviews (String localId) throws IOException {
        String requestBody = """
                {
                  "variables": {},
                  "query": "query {\\n  reviews(filter: { product_id: %s }, page: %d) {\\n    matches {\\n      id\\n      content\\n      status\\n      customer {\\n        id\\n        fullname\\n      }\\n      product_id\\n      created_at\\n      rating_id\\n      sent_from\\n      is_pinned\\n      children\\n      photos\\n      is_admin\\n      is_purchased\\n      attributes {\\n        attribute_name\\n        label\\n      }\\n    }\\n    total\\n  }\\n}"
                }
                """;
        List<String[]> reviews = new ArrayList<>();
        for (int i=1; i<=100; i++) {
            Document response = Jsoup.connect("https://api.cellphones.com.vn/graphql-customer/graphql/query")
                    .header("Content-Type", "application/json")
                    .requestBody(requestBody.formatted(localId,i))
                    .ignoreContentType(true)
                    .post();
            JsonNode nodes = MAPPER.readTree(response.body().text()).get("data").get("reviews").get("matches");
            if(nodes==null || nodes.isEmpty()) break;
            for(JsonNode reviewNode : nodes) {
                String[] review = new String[5];
                review[0] = localId;
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                sdf.setTimeZone(TimeZone.getTimeZone("UTC")); // Set timezone to UTC
                try {
                    review[1] = Long.toString(sdf.parse(reviewNode.get("created_at").asText()).getTime());
                } catch (ParseException e) {
                    review[1] = "";
                }
                review[2] = reviewNode.get("content").asText();
                review[3] = reviewNode.get("rating_id").asText();
                if (review[3].equals("null")) review[3] = null;
                review[4] = reviewNode.get("customer").get("fullname").asText();
                reviews.add(review);
            }
        }
        return reviews;
    }

    Date parseDate(String date) {
        Date created = null;
        if (date!=null && !date.isBlank()) {
            created = new Date(Long.parseLong(date));
        }
        return created;
    }
}
