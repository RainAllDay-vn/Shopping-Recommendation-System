package com.project.shoppingrecommendationsystem.models.crawlers.fptshop;

import com.fasterxml.jackson.databind.JsonNode;
import com.project.shoppingrecommendationsystem.models.crawlers.CSVCrawler;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public abstract class FPTShopCrawler extends CSVCrawler {
    JsonNode fetchHomepageAPI(int count, String category) throws IOException {
        String requestBody =  """
                    {
                        "categoryType": "category",
                        "maxResultCount": 50,
                        "skipCount": %d,
                        "slug": "%s",
                        "sortMethod": "noi-bat"
                    }
                    """.formatted(count, category);
        Document response = Jsoup.connect("https://papi.fptshop.com.vn/gw/v1/public/fulltext-search-service/category")
                .header("Content-Type", "application/json")
                .requestBody(requestBody)
                .ignoreContentType(true)
                .post();
        return MAPPER.readTree(response.body().text());
    }

    private void addToProductRow(String[] row, String key, String value) {
        if(productsMap.containsKey(key)) {
            row[productsMap.get(key)] = value;
        }
    }

    String[] extractProduct(JsonNode node) {
        String[] productRow = new String[productColumn.length];
        Arrays.fill(productRow, "");
        addToProductRow(productRow, "local_id", node.get("code").asText());
        for (Iterator<Map.Entry<String, JsonNode>> it = node.fields(); it.hasNext();) {
            Map.Entry<String, JsonNode> entry = it.next();
            if (entry.getKey().equals("image")) {
                try {
                    String imageURL = MAPPER.readTree(entry.getValue().toString()).get("src").asText();
                    if (imageURL == null) continue;
                    addToProductRow(productRow, "image", downloadImage(imageURL, productRow[0]));
                } catch (Exception ignored){}
            }
            else if (entry.getValue().isTextual()) {
                addToProductRow(productRow, entry.getKey(), entry.getValue().asText());
            }
            else {
                addToProductRow(productRow, entry.getKey(), entry.getValue().toString());
            }
        }
        return productRow;
    }

    List<String[]> extractDescriptions (String code, Document productPage) {
        List<String[]> descriptions = new LinkedList<>();
        Element descriptionContainer;
        try {
            descriptionContainer = productPage.getElementsByAttributeValueStarting("class", "ProductContent_description-container").getFirst();
        } catch (NoSuchElementException e) {
            return descriptions;
        }
        descriptions.add(new String[]{code, "header", "Product Description"});
        for(Element child : descriptionContainer.children()) {
            String text = child.text();
            if (text.isBlank()) continue;
            if (child.getElementsByTag("strong").isEmpty()) {
                descriptions.add(new String[]{code, "normal", text});
            } else {
                descriptions.add(new String[]{code, "bold", text});
            }
        }
        return descriptions;
    }

    private void addToPropertiesRow(String[] row, String key, String value) {
        if(propertiesMap.containsKey(key)) {
            row[propertiesMap.get(key)] = value;
        }
    }

    String[] extractProperties(String code, Document productPage) throws IOException {
        String[] propertiesRow = new String[propertyColumn.length];
        Arrays.fill(propertiesRow, "");
        addToPropertiesRow(propertiesRow, "local_id", code);
        JsonNode properties = extractPropertiesScript(productPage);
        for (int i=0; i<properties.size(); i++) {
            addToPropertiesRow(propertiesRow, properties.get(i).get("groupName").asText(), properties.get(i).get("attributes").toString());
        }
        return propertiesRow;
    }

    private JsonNode extractPropertiesScript(Document productPage) throws IOException {
        String script = productPage.getElementsByTag("script")
                .stream()
                .map(Node::toString)
                .filter(Pattern.compile("attributeItem").asPredicate())
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
        return MAPPER.readTree(script);
    }

    List<String[]> extractReviews(String id) throws IOException {
        List<String[]> reviews = new LinkedList<>();
        String requestBody =  """
                {
                  "content": {
                    "id": "%s",
                    "type": "PRODUCT"
                  },
                  "state": [
                    "ACTIVE"
                  ],
                  "skipCount": 0,
                  "maxResultCount": 100,
                  "sortMethod": 1
                }
                """.formatted(id);
        Document response = Jsoup.connect("https://papi.fptshop.com.vn/gw/v1/public/bff-before-order/comment/list")
                .header("Content-Type", "application/json")
                .header("order-channel", "1")
                .requestBody(requestBody)
                .ignoreContentType(true)
                .post();
        JsonNode nodes = MAPPER.readTree(response.body().text()).get("data").get("items");
        for(JsonNode reviewNode : nodes) {
            String[] review = new String[5];
            review[0] = id;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            sdf.setTimeZone(TimeZone.getTimeZone("UTC")); // Set timezone to UTC
            try {
                review[1] = Long.toString(sdf.parse(reviewNode.get("creationTime").asText()).getTime());
            } catch (Exception e) {
                review[1] = "";
            }
            review[2] = reviewNode.get("content").asText();
            review[3] = reviewNode.get("score").asText();
            if (review[3].equals("null")) review[3] = null;
            review[4] = reviewNode.get("fullName").asText();
            reviews.add(review);
        }
        return reviews;
    }

    String parseJson(String json, String[] path) {
        try {
            List<JsonNode> nodes = List.of(MAPPER.readTree(json));
            for (String field : path) {
                if (field.matches("\\d+")) {
                    nodes = nodes.stream()
                            .map(node -> node.get(Integer.parseInt(field)))
                            .toList();
                } else if (field.equals("slice")) {
                    List<JsonNode> temp = new ArrayList<>();
                    for (JsonNode node : nodes) {
                        node.forEach(temp::add);
                    }
                    nodes = temp;
                } else {
                    nodes = nodes.stream()
                            .map(node -> node.get(field))
                            .toList();
                }
            }
            return nodes.stream()
                    .map(JsonNode::asText)
                    .collect(Collectors.joining(", "));
        } catch (Exception ignored) {
            return "";
        }
    }

    Date parseDate(String date) {
        Date created = null;
        if (date!=null && !date.isBlank()) {
            created = new Date(Long.parseLong(date));
        }
        return created;
    }
}
