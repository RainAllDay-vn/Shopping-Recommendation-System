package com.project.shoppingrecommendationsystem.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.project.shoppingrecommendationsystem.HelloApplication;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.*;

public class TGDDCrawler {
    private final ObjectMapper mapper = new ObjectMapper();
    private WebDriver driver;
    private final String resourceURL = Objects.requireNonNull(HelloApplication.class.getResource(""))
            .getPath()
            .replace("%20", " ") + "data/TGDD";
    private final String pageURL = "https://www.thegioididong.com";
    private final String[] laptopColumns = {"data-index", "data-id", "data-issetup", "data-maingroup", "data-subgroup",
            "data-type", "data-vehicle", "data-productcode", "data-price-root", "data-ordertypeid", "data-pos", "sourceURL",
            "data-s", "data-site", "data-pro", "data-cache", "data-sv", "data-name", "data-id", "data-price",
            "data-brand", "data-cate", "data-box", "data-pos", "data-color", "data-productstatus", "data-premium",
            "data-promotiontype", "imageURL", "percent", "gift", "rating", "unit-sold"};
    private final String[] descriptionColumns = {"data-id", "description"};
    private final String[] propertiesColumns = {"data-id", "Công nghệ CPU", "Số nhân", "Số luồng", "Tốc độ CPU",
            "Tốc độ tối đa", "RAM", "Loại RAM", "Tốc độ Bus RAM", "Hỗ trợ RAM tối đa", "Ổ cứng", "Màn hình",
            "Độ phân giải", "Tần số quét", "Độ phủ màu", "Công nghệ màn hình", "Card màn hình", "Công nghệ âm thanh",
            "Cổng giao tiếp", "Kết nối không dây", "Webcam", "Đèn bàn phím", "Kích thước", "Chất liệu", "Thông tin Pin",
            "Hệ điều hành", "Thời điểm ra mắt", "Khe đọc thẻ nhớ", "Tính năng khác", "Tản nhiệt", "Màn hình cảm ứng",
            "NPU", "Hiệu năng xử lý AI (TOPS)"};
    private final HashMap<String, Integer> propertiesMap = new HashMap<>();

    /**
     * Constructs a TGDDCrawler object.
     * <p>
     * This constructor performs the following actions:
     * <ul>
     * <li>Initializes the resource directory where the scraped data will be saved. If the directory does not exist, it creates it.</li>
     * <li>Populates the {@code propertiesMap} with keys based on the {@code propertiesColumns} array, appending a colon (':') to each key. The values are the corresponding indices from the array.</li>
     * </ul>
     *
     * @throws RuntimeException if the resource directory cannot be created.
     */
    public TGDDCrawler() {
        File resourceDir = new File(resourceURL);
        if (!resourceDir.exists()) {
            if (!resourceDir.mkdirs()) {
                throw new RuntimeException("Unable to create directory " + resourceURL);
            }
        }
        for (int i = 0; i < propertiesColumns.length; i++) {
            propertiesMap.put(propertiesColumns[i] + ":", i);
        }
    }

    public static void main(String[] args) {
        TGDDCrawler crawler = new TGDDCrawler();
        try {
            crawler.resetSaveFile();
            crawler.crawlHomepageAPI();
            crawler.crawlLaptops();
        } catch (Exception e) {
            System.err.println("Crawling did not work");
            System.err.println(e.getMessage());
        }
    }

    /**
     * Crawls the homepage API for the list of laptop products.
     * It initializes a headless Chrome driver, navigates to the laptop page, and iterates through
     * the pages to fetch product data. The data is then extracted and saved to a CSV file.
     *
     * @throws JsonProcessingException if there is an error processing JSON data.
     * @throws RuntimeException if there is an error accessing save files.
     */
    private void crawlHomepageAPI () throws JsonProcessingException {
        driver = new ChromeDriver(new ChromeOptions().addArguments("--headless"));
        driver.get(pageURL + "/laptop");
        int currentPage = 1;
        int total = 1;

        while ((currentPage-1)*20 <= total) {
            JsonNode jsonNode = fetchHomepageAPI(currentPage);
            currentPage++;
            total = jsonNode.get("total").asInt();
            Element products = Jsoup.parse(jsonNode.path("listproducts").textValue()).body();
            List<String[]> rows = new ArrayList<>();
            products.children().stream()
                    .map(this::extractData)
                    .filter(Objects::nonNull)
                    .map(row -> row.toArray(new String[0]))
                    .forEach(rows::add);
            saveLaptop(rows, products);
        }
        driver.quit();
    }

    /**
     * Crawls laptop product data from a list of URLs, extracting descriptions and properties.
     * <p>
     * This method iterates through a list of laptop URLs, fetches the HTML content of each page,
     * extracts the product description and properties, and then saves the extracted data.
     *
     * @see #loadSourceURL()
     * @see #extractDescription(String, Element)
     * @see #extractProperties(String, Element)
     * @see #saveDescription(List)
     * @see #saveProperties(List)
     */
    private void crawlLaptops () {
        List<String[]> descriptionRows = new LinkedList<>();
        List<String[]> propertiesRows = new LinkedList<>();
        for (String[] URL: loadSourceURL()) {
            try {
                Element body = Jsoup.connect(pageURL + URL[1]).get().body();
                descriptionRows.add(extractDescription(URL[0], body));
                propertiesRows.add(extractProperties(URL[0], body));
            } catch (Exception e) {
                System.err.printf("Crawl laptop #%s failed\n", URL[0]);
            }
        }
        saveDescription(descriptionRows);
        saveProperties(propertiesRows);
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
        String[] descriptionRow = new String[descriptionColumns.length];
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
        String[] propertiesRow = new String[propertiesColumns.length];
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
     * Fetches the homepage API at a specific page index using JavaScript call.
     *
     * @param pageIndex The index of the page to fetch.
     * @return JsonNode representing the fetched data.
     * @throws JsonProcessingException if there is an error processing JSON data.
     */
    private JsonNode fetchHomepageAPI (int pageIndex) throws JsonProcessingException {
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
        return mapper.readTree(response);
    }

    /**
     * Extracts product data from a product Element.
     *
     * @param product The product element.
     * @return List of strings representing the extracted data, or null if extraction fails.
     */
    private List<String> extractData (Element product) {
        List<String> row = new ArrayList<>();
        try {
            extractDataFromAttribute(product, row);
            extractDataFromAnchor(product, row);
            extractImage(product, row);
            extractDiscountPercent(product, row);
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
     * Resets the save file and write the column headers to a new CSV file.
     *
     * @throws RuntimeException if there is an error accessing the save file.
     */
    private void resetSaveFile () {
        try (CSVWriter laptopWriter = new CSVWriter(new FileWriter(resourceURL + "/laptop.csv"));
             FileWriter ignored1 = new FileWriter(resourceURL + "/raw.txt");
             CSVWriter descriptionWriter = new CSVWriter(new FileWriter(resourceURL + "/description.csv"));
             CSVWriter propertiesWriter = new CSVWriter(new FileWriter(resourceURL + "/properties.csv"))) {
            laptopWriter.writeNext(laptopColumns);
            descriptionWriter.writeNext(descriptionColumns);
            propertiesWriter.writeNext(propertiesColumns);
        } catch (Exception e) {
            throw new RuntimeException("There was an error when accessing save file");
        }
    }

    /**
     * Saves the extracted product data to CSV and raw HTML files.
     *
     * @param rows     List of string arrays representing the rows of data to be saved.
     * @param products Element containing the raw HTML of the products.
     * @throws RuntimeException if there is an error accessing the save file.
     */
    private void saveLaptop (List<String[]> rows, Element products) {
        try (CSVWriter laptopWriter = new CSVWriter(new FileWriter(resourceURL + "/laptop.csv", true));
             FileWriter rawWriter = new FileWriter(resourceURL + "/raw.txt", true)) {
            laptopWriter.writeAll(rows);
            rawWriter.write(products.toString());
        } catch (Exception e) {
            throw new RuntimeException("There was an error when accessing save file");
        }
    }

    /**
     * Saves the extracted product descriptions to a CSV file.
     * <p>
     * This method writes the provided list of string arrays, representing product descriptions,
     * to a CSV file.
     *
     * @param rows The list of string arrays containing product descriptions.
     * @throws RuntimeException if there is an error accessing the save file.
     */
    private void saveDescription (List<String[]> rows) {
        try (CSVWriter descriptionWriter = new CSVWriter(new FileWriter(resourceURL + "/description.csv", true))) {
            descriptionWriter.writeAll(rows);
        } catch (Exception e) {
            throw new RuntimeException("There was an error when accessing save file");
        }
    }

    /**
     * Saves the extracted product properties to a CSV file.
     * <p>
     * This method writes the provided list of string arrays, representing product properties,
     * to a CSV file.
     *
     * @param rows The list of string arrays containing product properties.
     * @throws RuntimeException if there is an error accessing the save file.
     */
    private void saveProperties (List<String[]> rows) {
        try (CSVWriter propertiesWriter = new CSVWriter(new FileWriter(resourceURL + "/properties.csv", true))){
            propertiesWriter.writeAll(rows);
        } catch (Exception e) {
            throw new RuntimeException("There was an error when accessing save file");
        }
    }

    /**
     * Loads product IDs and source URLs from the laptop CSV file.
     * <p>
     * This method reads the laptop CSV file, extracts the product IDs and source URLs,
     * and returns them as a list of string arrays.
     *
     * @return A list of string arrays, where each array contains a product ID and source URL.
     * Returns an empty list if there is an error accessing the file.
     */
    private List<String[]> loadSourceURL () {
        try (CSVReader reader = new CSVReader(new FileReader(resourceURL + "/laptop.csv"))) {
            String[] header = reader.readNext();
            int IdIndex = Arrays.asList(header).indexOf("data-id");
            int URLIndex = Arrays.asList(header).indexOf("sourceURL");
            return reader.readAll()
                    .stream()
                    .map(row -> new String[]{row[IdIndex], row[URLIndex]})
                    .toList();
        } catch (Exception e) {
            System.err.println("There was an error when accessing save file");
            System.err.println(e.getMessage());
            return new ArrayList<>();
        }
    }
}
