package com.project.shoppingrecommendationsystem.models.crawler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.project.shoppingrecommendationsystem.models.Laptop;
import com.project.shoppingrecommendationsystem.models.Review;
import com.project.shoppingrecommendationsystem.models.components.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

/**
 * CellphoneSCrawler class is responsible for crawling laptop data from CellphoneS website
 * and parsing saved data into Laptop objects. It fetches product information using the
 * website's API and extracts relevant details such as product ID, name, description,
 * and properties.
 */
public class TGDDCrawler extends LaptopCrawler {
    private static final int MAX_THREADS = 10;
    private WebDriver driver;
    private final String pageURL = "https://www.thegioididong.com";
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
        this.laptopColumn = new String[]{"local_id", "data-issetup", "data-maingroup", "data-subgroup",
                "data-type", "data-vehicle", "data-productcode", "data-price-root", "data-ordertypeid", "data-pos", "sourceURL",
                "data-s", "data-site", "data-pro", "data-cache", "data-sv", "data-name", "data-id", "data-price",
                "data-brand", "data-cate", "data-box", "data-pos", "data-color", "data-productstatus", "data-premium",
                "data-promotiontype", "imageURL", "percent", "gift", "rating", "unit-sold"};
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
    public void crawl () {
        crawl(Integer.MAX_VALUE);
    }

    /**
     * @param limit The maximum number of laptops to crawl.
     */
    @Override
    public void crawl (int limit) {
        resetSave();
        crawlAllLaptops(limit);
    }

    /**
     * Crawls the CellphoneS website for laptop data.
     * Fetches product information from the homepage API and extracts laptop details,
     * descriptions, and properties. Saves the extracted data to CSV files.
     */
    private void crawlAllLaptops(int limit) {
        ExecutorService executor = Executors.newFixedThreadPool(MAX_THREADS);
        driver = new ChromeDriver(new ChromeOptions().addArguments("--headless"));
        driver.get(pageURL + "/laptop");
        int processed = 0;
        int currentPage = 1;
        int total = 1;
        while ((currentPage-1)*20 <= total && processed < limit) {
            JsonNode jsonNode = fetchHomepageAPI(currentPage++);
            if (jsonNode == null) {
                System.err.println("[ERROR] : An error occurred while crawling new laptops");
                break;
            }
            total = jsonNode.get("total").asInt();
            Element products = Jsoup.parse(jsonNode.path("listproducts").textValue()).body();
            for (Element product : products.children()) {
                executor.submit(() -> this.extractAndSaveLaptop(product));
                if (++processed == limit) {
                    break;
                }
            }
        }
        executor.shutdown();
        try {
            if(executor.awaitTermination(10, TimeUnit.MINUTES)) {
                System.out.println("[INFO] : TGDD have crawled all laptops");
            } else {
                System.out.println("[INFO] : TGDD have time-outed when crawling for laptops");
            }
        } catch (InterruptedException e) {
            System.out.println("[ERROR] : TGDD has stopped abnormally");
        }
    }

    private void extractAndSaveLaptop (Element product) {
        try {
            String[] laptopRow = extractLaptop(product);
            System.out.println("[INFO] : Extracting #" + laptopRow[0]);
            Element body = Jsoup.connect(pageURL + laptopRow[10]).get().body();
            List<String[]> descriptions = extractDescriptions(laptopRow[0], body);
            String[] propertiesRow = extractProperties(laptopRow[0], body);
            List<String[]> reviews = extractReviews(laptopRow[0]);
            downloadImage(laptopRow);
            save(laptopRow, descriptions, propertiesRow, reviews);
        } catch (Exception e) {
            System.err.println("[ERROR] : An error occurred while extracting laptop's information");
            System.out.println(e.getMessage());
        }
    }

    private synchronized void save(String[] laptopRow, List<String[]> descriptionRow, String[] propertiesRow, List<String[]> reviews) {
        saveLaptopRow(laptopRow);
        saveDescriptions(descriptionRow);
        savePropertiesRow(propertiesRow);
        saveReviews(reviews);
    }

    private void downloadImage (String[] laptopRow) {
        String imageUrl = laptopRow[27];
        imageUrl = imageUrl.replace(" ", "%20");
        String extension = imageUrl.substring(imageUrl.lastIndexOf(".") + 1);
        String outputPath = resourceURL + "images/" + laptopRow[0] + "." + extension;
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
                """.formatted(imageUrl);
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
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (Exception e) {
            System.err.println("[ERROR] : Can not download Image");
            laptopRow[27] = "";
        }
        laptopRow[27] = outputPath;
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
    private List<String[]> extractDescriptions (String id, Element body) {
        List<String[]> descriptions = new LinkedList<>();
        try {
            Element description = body.selectFirst("div.description");
            assert description != null;
            for (Element child : description.child(0).children()) {
                if (child.tagName().equals("h3")) {
                    descriptions.add(new String[]{id, "header", child.text()});
                    continue;
                }
                if (child.tagName().equals("p")) {
                    descriptions.add(new String[]{id, "normal", child.text()});
                }
            }
        } catch (Exception ignored) {}
        return descriptions;
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

    private List<String[]> extractReviews (String localId) throws IOException {
        String requestBody = "objectId=%s&objectType=2&pageIndex=%d";
        List<String[]> reviews = new ArrayList<>();
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
        } catch (JsonProcessingException ignored) {
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
            if (attribute.getKey().equals("data-index") || attribute.getKey().equals("class")) {
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
     * Parses CPU information from a properties row.
     *
     * @param propertiesRow An array of Strings containing product properties.
     * @return A CPU object.
     */
    private CPU parseCPU(String[] propertiesRow) {
        return new CPU.CPUBuilder()
                .setName(propertiesRow[1])
                .setBaseFrequency(propertiesRow[4])
                .setTurboFrequency(propertiesRow[5])
                .setCores(propertiesRow[2])
                .setThreads(propertiesRow[3])
                .build();
    }

    /**
     * Parses RAM information from a properties row.
     *
     * @param propertiesRow An array of Strings containing product properties.
     * @return A RAM object.
     */
    private RAM parseRAM(String[] propertiesRow) {
        return new RAM.RAMBuilder()
                .setSize(propertiesRow[6])
                .setClock(propertiesRow[8])
                .setType(propertiesRow[7])
                .setMaxSize(propertiesRow[9])
                .build();
    }

    /**
     * Parses Display information from a properties row.
     *
     * @param propertiesRow An array of Strings containing product properties.
     * @return A Display object.
     */
    private Display parseDisplay(String[] propertiesRow) {
        return new Display.DisplayBuilder()
                .setScreenSize(propertiesRow[11])
                .setScreenResolution(propertiesRow[12])
                .setRefreshRate(propertiesRow[13])
                .setGpuName(propertiesRow[14])
                .build();
    }

    /**
     * Parses storage information from a properties row.
     *
     * @param propertiesRow An array of Strings containing product properties.
     * @return A Storage object.
     */
    private Storage parseStorage(String[] propertiesRow) {
        return new Storage.StorageBuilder()
                .setSize(propertiesRow[10])
                .build();
    }

    /**
     * Parses connectivity information from a properties row.
     *
     * @param propertiesRow An array of Strings containing product properties.
     * @return A Connectivity object.
     */
    private Connectivity parseConnectivity(String[] propertiesRow) {
        return new Connectivity.ConnectivityBuilder()
                .setPorts(propertiesRow[18])
                .setWifi(propertiesRow[19])
                .setWebCam(propertiesRow[20])
                .build();
    }

    /**
     * Parses battery information from a properties row.
     *
     * @param propertiesRow An array of Strings containing product properties.
     * @return A Battery object.
     */
    private Battery parseBattery(String[] propertiesRow) {
        return new Battery.BatteryBuilder()
                .setCapacity(propertiesRow[24])
                .build();
    }

    /**
     * Parses laptop case information from a properties row.
     *
     * @param propertiesRow An array of Strings containing product properties.
     * @return A LaptopCase object.
     */
    private LaptopCase parseLaptopCase(String[] propertiesRow) {
        return new LaptopCase.LaptopCaseBuilder()
                .setDimensions(propertiesRow[22])
                .setMaterial(propertiesRow[23])
                .build();
    }

    /**
     * Parses laptop information from laptop, description, and properties rows.
     *
     * @param laptopRow      An array of Strings containing laptop information.
     * @param descriptions An array of Strings containing product description.
     * @param propertiesRow  An array of Strings containing product properties.
     * @return A Laptop object.
     */
    @Override
    Laptop parseLaptop(String[] laptopRow, List<String[]> descriptions, String[] propertiesRow, List<String[]> reviews) {
        Laptop.LaptopBuilder builder = new Laptop.LaptopBuilder()
                .setName(laptopRow[16])  // Column 'data-name'
                .setProductImage(laptopRow[27])  // Column 'imageURL'
                .setPrice((int)Double.parseDouble(laptopRow[7]))  // Column 'data-price-root'
                .setDiscountPrice((int)Double.parseDouble(laptopRow[18]))  // Column 'data-price'
                .setSource("TGDD")
                .setSourceURL("https://www.thegioididong.com/" + laptopRow[10])  // Column 'sourceURL'
                .setBrand(laptopRow[19])  // Column 'data-brand'
                .setColor(laptopRow[23])
                .setDescription(descriptions)
                .setCpu(parseCPU(propertiesRow))
                .setRam(parseRAM(propertiesRow))
                .setDisplay(parseDisplay(propertiesRow))
                .setStorage(parseStorage(propertiesRow))
                .setConnectivity(parseConnectivity(propertiesRow))
                .setBattery(parseBattery(propertiesRow))
                .setLaptopCase(parseLaptopCase(propertiesRow));
        for (String[] row : reviews) {
            Date created = null;
            if (row[0]!=null && !row[0].isBlank()) {
                created = new Date(Long.parseLong(row[0]));
            }
            builder.addReview(new Review(created, row[1], row[2], row[3]));
        }
        return builder.build();
    }
}
