package com.project.shoppingrecommendationsystem.models.crawler;

import com.fasterxml.jackson.databind.JsonNode;
import com.opencsv.*;
import com.opencsv.exceptions.CsvValidationException;
import com.project.shoppingrecommendationsystem.models.Laptop;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

public class FPTShopCrawler extends Crawler{
    private final Pattern compiledPattern = Pattern.compile("attributeItem");

    public FPTShopCrawler() {
        super("data/FPTShop/");
        this.laptopColumn = new String[]{"product_id", "score", "name", "displayName", "typePim", "type", "slug", "price", "industry",
                "brand", "productType", "group", "keySellingPoints", "units", "image", "originalPrice", "currentPrice",
                "discountPercentage", "endTimeDiscount", "promotions", "totalInventory", "skus"};
        this.descriptionColumn = new String[]{"product_id", "description"};
        this.propertiesColumn = new String[]{"product_id", "Bộ xử lý", "Đồ họa", "RAM", "Lưu trữ", "Màn hình", "Giao tiếp và kết nối",
                "Âm Thanh", "Ổ đĩa quang", "Hệ điều hành", "Bảo mật", "Bàn phím & TouchPad", "Thông tin pin & sạc",
                "Phụ kiện trong hộp", "Thông số cơ bản", "Thiết kế & Trọng lượng", "Thông tin hàng hóa"};
    }

    @Override
    public void crawlLaptops() {
        crawlLaptops(Integer.MAX_VALUE);
    }

    /**
     * @param limit The maximum number of laptops to crawl.
     */
    @Override
    public void crawlLaptops(int limit) {
        resetSave();
        crawlAllLaptops(limit);
    }

    /**
     * @return
     */
    @Override
    public List<Laptop> getLaptops() {
        return List.of();
    }

    private void crawlAllLaptops (int limit) {
        int processed = 0;
        int count = 0;
        int max = 1;
        JsonNode jsonNode;
        while (count < max) {
            try {
                jsonNode = fetchHomepageAPI(count);
                count+=50;
                max = jsonNode.get("totalCount").asInt();
            } catch (Exception e) {
                System.out.println(e.getMessage());
                break;
            }
            for (JsonNode item : jsonNode.get("items")) {
                try {
                    Document productPage = Jsoup.connect("https://fptshop.com.vn/" + item.get("slug").asText()).get();
                    String code = item.get("code").asText();
                    saveLaptopRow(extractLaptopRow(item));
                    saveDescriptionRow(extractDescriptionRow(code, productPage));
                    savePropertiesRow(extractPropertiesRow(code, productPage));
                    if (++processed == limit) {
                        return;
                    }
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
            }
        }
    }

    private JsonNode fetchHomepageAPI(int count) throws IOException {
        String requestBody =  """
                    {
                        "categoryType": "category",
                        "maxResultCount": 50,
                        "skipCount": %d,
                        "slug": "may-tinh-xach-tay",
                        "sortMethod": "noi-bat"
                    }
                    """.formatted(count);
        Document response = Jsoup.connect("https://papi.fptshop.com.vn/gw/v1/public/fulltext-search-service/category")
                .header("Content-Type", "application/json")
                .requestBody(requestBody)
                .ignoreContentType(true)
                .post();
        return mapper.readTree(response.body().text());
    }

    private JsonNode extractScript (Document productPage) throws IOException, CsvValidationException {
        String script = productPage.getElementsByTag("script")
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
        return mapper.readTree(script);
    }

    private String[] extractLaptopRow(JsonNode jsonNode) {
        String[] laptopRow = new String[laptopColumn.length];
        laptopRow[0] = String.valueOf(jsonNode.get("code").toString());
        for (int i = 1; i < laptopColumn.length; i++) {
            laptopRow[i] = String.valueOf(jsonNode.get(laptopColumn[i]));
        }
        return laptopRow;
    }

    private String[] extractDescriptionRow (String code, Document productPage) {
        String[] descriptionRow = new String[descriptionColumn.length];
        descriptionRow[0] = code;
        Element descriptionContainer;
        try {
            descriptionContainer = productPage.getElementsByClass("description-container").getFirst();
        } catch (NoSuchElementException e) {
            descriptionRow[0] = "";
            return descriptionRow;
        }
        StringBuilder stringBuilder = new StringBuilder();
        for(Element child : descriptionContainer.children()) {
            stringBuilder.append(child.text());
        }
        String description = stringBuilder.toString();
        descriptionRow[1] = description;
        return descriptionRow;
    }

    private String[] extractPropertiesRow (String code, Document productPage) throws CsvValidationException, IOException {
        String[] propertiesRow = new String[propertiesColumn.length];
        propertiesRow[0] = code;
        JsonNode properties = extractScript(productPage);
        for (int i = 1; i < propertiesColumn.length; i++) {
            propertiesRow[i] = String.valueOf(properties.get(i-1).get("attributes"));
        }
        return propertiesRow;
    }
}

