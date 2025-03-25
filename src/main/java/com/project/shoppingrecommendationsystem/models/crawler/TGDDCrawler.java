package com.project.shoppingrecommendationsystem.models.crawler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.project.shoppingrecommendationsystem.models.Laptop;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.*;

/**
 * CellphoneSCrawler class is responsible for crawling laptop data from CellphoneS website
 * and parsing saved data into Laptop objects. It fetches product information using the
 * website's API and extracts relevant details such as product ID, name, description,
 * and properties.
 */
public class TGDDCrawler extends Crawler {
    private WebDriver driver;
    private final HashMap<String, Integer> propertiesMap = new HashMap<>();

    /**
     * Constructs a CellphoneSCrawler object.
     * <p>
     * This constructor performs the following actions:
     * <ul>
     * <li>Initializes the resource directory where the scraped data will be saved. If the directory does not exist, it creates it.</li>
     * <li>Populates the {@code propertiesMap} with keys based on the {@code propertiesColumns} array. The values are the corresponding indices from the array.</li>
     * </ul>
     *
     * @throws RuntimeException if the resource directory cannot be created.
     */
    public TGDDCrawler() {
        super("data/TGDD/");
        this.laptopColumn = new String[]{"data-index", "data-id", "data-issetup", "data-maingroup", "data-subgroup",
                "data-type", "data-vehicle", "data-productcode", "data-price-root", "data-ordertypeid", "data-pos", "sourceURL",
                "data-s", "data-site", "data-pro", "data-cache", "data-sv", "data-name", "data-id", "data-price",
                "data-brand", "data-cate", "data-box", "data-pos", "data-color", "data-productstatus", "data-premium",
                "data-promotiontype", "imageURL", "percent", "gift", "rating", "unit-sold"};
        this.descriptionColumn = new String[]{"data-id", "description"};
        this.propertiesColumn = new String[]{"data-id", "Công nghệ CPU", "Số nhân", "Số luồng", "Tốc độ CPU",
                "Tốc độ tối đa", "RAM", "Loại RAM", "Tốc độ Bus RAM", "Hỗ trợ RAM tối đa", "Ổ cứng", "Màn hình",
                "Độ phân giải", "Tần số quét", "Độ phủ màu", "Công nghệ màn hình", "Card màn hình", "Công nghệ âm thanh",
                "Cổng giao tiếp", "Kết nối không dây", "Webcam", "Đèn bàn phím", "Kích thước", "Chất liệu", "Thông tin Pin",
                "Hệ điều hành", "Thời điểm ra mắt", "Khe đọc thẻ nhớ", "Tính năng khác", "Tản nhiệt", "Màn hình cảm ứng",
                "NPU", "Hiệu năng xử lý AI (TOPS)"};
        for (int i = 0; i < this.propertiesColumn.length; i++) {
            propertiesMap.put(this.propertiesColumn[i] + ":", i);
        }
    }

    /**
     *
     */
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
     * Crawls the CellphoneS website for laptop data.
     * Fetches product information from the homepage API and extracts laptop details,
     * descriptions, and properties. Saves the extracted data to CSV files.
     */
    private void crawlAllLaptops(int limit) {
        String pageURL = "https://www.thegioididong.com";
        driver = new ChromeDriver(new ChromeOptions().addArguments("--headless"));
        driver.get(pageURL + "/laptop");
        int processed = 0;
        int currentPage = 1;
        int total = 1;
        while ((currentPage-1)*20 <= total && processed < limit) {
            JsonNode jsonNode = fetchHomepageAPI(currentPage++);
            if (jsonNode == null) {
                System.err.println("Fetching error");
                break;
            }
            total = jsonNode.get("total").asInt();
            Element products = Jsoup.parse(jsonNode.path("listproducts").textValue()).body();
            for (Element product : products.children()) {
                try {
                    String[] laptopRow = extractLaptop(product);
                    Element body = Jsoup.connect(pageURL + laptopRow[11]).get().body();
                    saveLaptopRow(laptopRow);
                    saveDescriptionRow(extractDescription(laptopRow[1], body));
                    savePropertiesRow(extractProperties(laptopRow[1], body));
                } catch (Exception e) {
                    System.err.println("Crawl an item failed");
                    System.out.println(product);
                }
                if (++processed == limit) {
                    break;
                }
            }
        }
        driver.quit();
    }

    /**
     * Extracts laptop information from a product Element.
     *
     * @param product The product Element.
     * @return An array of Strings containing laptop information.
     */
    private String[] extractLaptop (Element product) {
        List<String> row = new ArrayList<>();
        extractDataFromAttribute(product, row);
        extractDataFromAnchor(product, row);
        extractImage(product, row);
        extractDiscountPercent(product, row);
        extractGift(product, row);
        extractRating(product, row);
        extractUnitSold(product, row);
        return row.toArray(new String[0]);
    }

    /**
     * Extracts the product description from the HTML body.
     * <p>
     * This method selects the description element from the HTML body, extracts its text content,
     * and returns it as a string array.
     *
     * @param id   The product ID.
     * @param body The HTML body of the product page.
     * @return A string array containing the product ID and description.
     */
    private String[] extractDescription(String id, Element body) {
        String[] descriptionRow = new String[descriptionColumn.length];
        descriptionRow[0] = id;
        Element description = body.selectFirst("div.description");
        if (description != null) {
            descriptionRow[1] = description.text();
        }
        return descriptionRow;
    }

    /**
     * Extracts the product properties from the HTML body.
     * <p>
     * This method selects the properties element from the HTML body, extracts the property names and values,
     * and returns them as a string array. It uses the {@code propertiesMap} to map property
     * names to indices in the properties array.
     *
     * @param id   The product ID.
     * @param body The HTML body of the product page.
     * @return A string array containing the product ID and properties.
     * @see #propertiesMap
     */
    private String[] extractProperties(String id, Element body) {
        String[] propertiesRow = new String[propertiesColumn.length];
        propertiesRow[0] = id;
        Element properties = body.selectFirst("div.specification-item");
        if (properties != null) {
            Elements propertiesPair = properties.select("li");
            propertiesPair.forEach(pair ->
                    propertiesRow[propertiesMap.get(pair.child(0).text())] = pair.child(1).text());
        }
        return propertiesRow;
    }

    /**
     * Fetches the homepage API for product data.
     *
     * @param pageIndex The page index to fetch.
     * @return The JSON response from the API as a JsonNode.
     */
    private JsonNode fetchHomepageAPI (int pageIndex) {
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
            return mapper.readTree(response);
        } catch (JsonProcessingException e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    /**
     * Extracts data from the attributes of the list item ({@code <li>}) element.
     * This method retrieves the following attributes:
     * <ul>
     * <li>data-index</li>
     * <li>data-id</li>
     * <li>data-issetup</li>
     * <li>data-maingroup</li>
     * <li>data-subgroup</li>
     * <li>data-type</li>
     * <li>data-vehicle</li>
     * <li>data-productcode</li>
     * <li>data-price-root</li>
     * <li>data-ordertypeid</li>
     * <li>data-pos</li>
     * </ul>
     *
     * @param product The Element to extract data from.
     * @param row     The list to store the extracted data.
     */
    private void extractDataFromAttribute (Element product, List<String> row) {
        for (Attribute attribute : product.attributes()) {
            if (attribute.getKey().equals("class")) {
                continue;
            }
            row.add(attribute.getValue());
        }
    }

    /**
     * Extracts data from the attributes of the anchor ({@code <a>}) element within a product Element.
     * This method retrieves the following attributes:
     * <ul>
     * <li>sourceURL</li>
     * <li>data-s</li>
     * <li>data-site</li>
     * <li>data-pro</li>
     * <li>data-cache</li>
     * <li>data-sv</li>
     * <li>data-name</li>
     * <li>data-id</li>
     * <li>data-price</li>
     * <li>data-brand</li>
     * <li>data-cate</li>
     * <li>data-box</li>
     * <li>data-pos</li>
     * <li>data-color</li>
     * <li>data-productstatus</li>
     * <li>data-premium</li>
     * <li>data-promotiontype</li>
     * </ul>
     * @param product The Element containing the product data.
     * @param row     The list to store the extracted data.
     * @throws RuntimeException if the anchor element cannot be located.
     */
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

    /**
     * Extracts the image URL from a product Element.
     *
     * @param product The Element containing the product data.
     * @param row     The list to store the extracted data.
     */
    private void extractImage(Element product, List<String> row) {
        Element img = product.select("img").first();
        if (img != null) {
            row.add(img.attr("data-src"));
            return;
        }
        row.add(null);
    }

    /**
     * Extracts the discount percentage from a product Element.
     *
     * @param product The Element containing the product data.
     * @param row     The list to store the extracted data.
     */
    private void extractDiscountPercent (Element product, List<String> row) {
        Element percent = product.selectFirst("span.percent");
        if (percent != null) {
            row.add(percent.text());
            return;
        }
        row.add("0%");
    }

    /**
     * Extracts the gift information from a product Element.
     *
     * @param product The Element containing the product data.
     * @param row     The list to store the extracted data.
     */
    private void extractGift (Element product, List<String> row) {
        Element gift = product.selectFirst("p.item-gift");
        if (gift != null) {
            row.add(gift.text());
            return;
        }
        row.add(null);
    }

    /**
     * Extracts the rating information from a product Element.
     *
     * @param product The Element containing the product data.
     * @param row     The list to store the extracted data.
     */
    private void extractRating (Element product, List<String> row) {
        Element rating = product.selectFirst("div.vote-txt");
        if (rating != null) {
            row.add(rating.text());
            return;
        }
        row.add(null);
    }

    /**
     * Extracts the unit sold information from a product Element.
     *
     * @param product The Element containing the product data.
     * @param row     The list to store the extracted data.
     */
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

    /**
     * @param laptopRow
     * @param descriptionRow
     * @param propertiesRow
     * @return
     */
    @Override
    Laptop parseLaptop(String[] laptopRow, String[] descriptionRow, String[] propertiesRow) {
        return null;
    }

    /**
     * @return
     */
    @Override
    public List<Laptop> getLaptops() {
        return super.getLaptops();
    }
}
