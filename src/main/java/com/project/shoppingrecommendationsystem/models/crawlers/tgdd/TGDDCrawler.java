package com.project.shoppingrecommendationsystem.models.crawlers.tgdd;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.project.shoppingrecommendationsystem.models.crawlers.CSVCrawler;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public abstract class TGDDCrawler extends CSVCrawler {
    WebDriver driver;

    JsonNode fetchHomepageAPI (int pageIndex) {
        String script = """
            return (async function() {
                try {
                    let response = await fetch("https://www.thegioididong.com/Category/FilterProductBox?c=44&o=13&pi=%d", {
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
        """.formatted(pageIndex);
        String response = (String) ((JavascriptExecutor) driver).executeScript(script);
        try {
            return MAPPER.readTree(response);
        } catch (JsonProcessingException ignored) {
            return null;
        }
    }

    private void addToProductRow (String[] row, String key, String value) {
        if (productsMap.containsKey(key)) {
            row[productsMap.get(key)] = value;
        }
    }

    String[] extractProduct(Element product) {
        String[] productRow = new String[productColumn.length];
        Arrays.fill(productRow, "");
        extractDataFromAttribute(product, productRow);
        extractDataFromAnchor(product, productRow);
        extractImage(product, productRow);
        extractDiscountPercent(product, productRow);
        extractGift(product, productRow);
        extractRating(product, productRow);
        extractUnitSold(product, productRow);
        return productRow;
    }

    private void extractDataFromAttribute (Element product, String[] row) {
        for (Attribute attribute : product.attributes()) {
            switch (attribute.getKey()) {
                case "data-index", "class" -> {
                    continue;
                }
                case "data-id" -> addToProductRow(row, "local_id", attribute.getValue());
                case "data-price" -> addToProductRow(row, "data-price-root", attribute.getValue());
                default -> addToProductRow(row, attribute.getKey(), attribute.getValue());
            }
        }
    }

    private void extractDataFromAnchor (Element product, String[] row) {
        Element a = product.selectFirst("a");
        if (a == null) {
            return;
        }
        for (Attribute attribute : a.attributes()) {
            switch (attribute.getKey()) {
                case "class" -> {
                    continue;
                }
                case "href" -> addToProductRow(row, "sourceURL", attribute.getValue());
                default -> addToProductRow(row, attribute.getKey(), attribute.getValue());
            }
            addToProductRow(row, attribute.getKey(), attribute.getValue());
        }
    }

    private void extractImage(Element product, String[] row) {
        Element img = product.select("img").first();
        if (img != null) {
            addToProductRow(row, "imageURL", downloadImage(img.attr("data-src"), row[0]));
        }
    }

    private void extractDiscountPercent (Element product, String[] row) {
        Element percent = product.selectFirst("span.percent");
        if (percent != null) {
            addToProductRow(row, "percent", percent.text());
        }
    }

    private void extractGift (Element product, String[] row) {
        Element gift = product.selectFirst("p.item-gift");
        if (gift != null) {
            addToProductRow(row, "gift", gift.text());
        }
    }

    private void extractRating (Element product, String[] row) {
        Element rating = product.selectFirst("div.vote-txt");
        if (rating != null) {
            addToProductRow(row, "rating", rating.text());
        }
    }

    private void extractUnitSold (Element product, String[] row) {
        Element unitSold = product.selectFirst("div.rating_Compare span");
        if (unitSold != null) {
            addToProductRow(row, "unit_sold", unitSold.text());
        }
        Element comingSoon = product.selectFirst("p.item-txt-online");
        if (comingSoon != null) {
            addToProductRow(row, "unit_sold", comingSoon.text());
        }
        Element newModel = product.selectFirst("span.ln-new");
        if (newModel != null) {
            addToProductRow(row, "unit_sold", newModel.text());
        }
    }

    List<String[]> extractDescriptions (String id, Element body) {
        List<String[]> descriptions = new LinkedList<>();
        try {
            Element description = body.selectFirst("div.description");
            assert description != null;
            for (Element child : description.child(0).children()) {
                if (child.tagName().equals("h3")) {
                    descriptions.add(new String[]{id, "header", child.text()});
                }
                else if (child.tagName().equals("p")) {
                    descriptions.add(new String[]{id, "normal", child.text()});
                }
            }
        } catch (Exception ignored) {}
        return descriptions;
    }

    private void addToPropertiesRow(String[] row, String key, String value) {
        if (propertiesMap.containsKey(key)) {
            row[propertiesMap.get(key)] = value;
        }
    }

    String[] extractProperties(String id, Element body) {
        String[] propertiesRow = new String[propertyColumn.length];
        Arrays.fill(propertiesRow, "");
        addToPropertiesRow(propertiesRow, "local_id", id);
        Element properties = body.selectFirst("div.specification-item");
        if (properties != null) {
            Elements propertiesPairs = properties.select("li");
            for (Element pair : propertiesPairs) {
                addToPropertiesRow(propertiesRow, pair.child(0).text(), pair.child(1).text());
            }
        }
        return propertiesRow;
    }

    List<String[]> extractReviews (String localId) throws IOException {
        List<String[]> reviews = new ArrayList<>();
        String requestBody = "objectId=%s&objectType=2&pageIndex=%d";
        for (int i=0; i<100; i++) {
            Document response = Jsoup.connect("https://www.thegioididong.com/comment/allrating")
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .requestBody(requestBody.formatted(localId,i))
                    .ignoreContentType(true)
                    .post();
            Elements reviewList = response.getElementsByClass("comment-list");
            if (reviewList.isEmpty()) break;
            for(Element reviewElement : reviewList.getFirst().children()) {
                String[] review = new String[5];
                review[0] = localId;
                review[1] = "";
                Element reviewContent = reviewElement.getElementsByClass("cmt-content").first();
                if (reviewContent != null) review[2] = reviewContent.text();
                review[3] = Integer.toString(reviewElement.getElementsByClass("iconcmt-starbuy").size());
                Element reviewUsername = reviewElement.getElementsByClass("cmt-top-name").first();
                if (reviewUsername != null) review[4] = reviewUsername.text();
                reviews.add(review);
            }
        }
        return reviews;
    }

    @Override
    protected String downloadImage (String url, String id) {
        url = url.replace(" ", "%20");
        String extension = url.substring(url.lastIndexOf(".") + 1);
        String outputPath = resourceURL + "images/" + id + "." + extension;
        String script = """
                return (async function getImageBytes() {
                    let imageUrl = '%s';
                    try {
                        const response = await fetch(imageUrl);
                    if (!response.ok) {
                        throw new Error(`HTTP error! status: ${response.status}`);
                    }
                    const blob = await response.blob();
                    const arrayBuffer = await blob.arrayBuffer();
                    return Array.from(new Uint8Array(arrayBuffer));
                    } catch (error) {
                        console.error("Error fetching or converting image:", error);
                        return null;
                    }
                })();
                """.formatted(url);
        JavascriptExecutor js = (JavascriptExecutor) driver;
        try {
            List<Number> byteList = (List<Number>)js.executeScript(script);
            if (byteList != null) {
                byte[] byteArray = new byte[byteList.size()];
                for (int i = 0; i < byteList.size(); i++) {
                    byteArray[i] = byteList.get(i).byteValue();
                }
                try (FileOutputStream out = new FileOutputStream(outputPath)) {
                    out.write(byteArray);
                }
            }
        } catch (Exception e) {
            System.err.println("[ERROR] : Can not download Image");
            return url;
        }
        return outputPath;
    }
}
