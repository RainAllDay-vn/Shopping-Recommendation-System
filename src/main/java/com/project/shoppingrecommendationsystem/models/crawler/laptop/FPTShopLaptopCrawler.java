package com.project.shoppingrecommendationsystem.models.crawler.laptop;

import com.fasterxml.jackson.databind.JsonNode;
import com.project.shoppingrecommendationsystem.models.Laptop;
import com.project.shoppingrecommendationsystem.models.Review;
import com.project.shoppingrecommendationsystem.models.components.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.*;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * FPTShopCrawler class is responsible for crawling laptop data from FPTShop website.
 * It fetches product information using the website's API and extracts relevant details
 * such as product ID, name, description, and properties. The extracted data is then
 * saved into CSV files.
 */
public class FPTShopLaptopCrawler extends LaptopCrawler {
    private final Pattern compiledPattern = Pattern.compile("attributeItem");
    private static final int MAX_THREADS = 10;

    /**
     * Constructs a FPTShopCrawler object.
     * <p>
     * This constructor initializes the resource directory and sets up the column headers for the CSV files.
     */
    public FPTShopLaptopCrawler() {
        super("FPTShop/");
        this.laptopColumn = new String[]{"local_id", "score", "name", "displayName", "typePim", "type", "slug", "price", "industry",
                "brand", "productType", "group", "keySellingPoints", "units", "image", "originalPrice", "currentPrice",
                "discountPercentage", "endTimeDiscount", "promotions", "totalInventory", "skus"};
        this.propertiesColumn = new String[]{"product_id", "Bộ xử lý", "Đồ họa", "RAM", "Lưu trữ", "Màn hình", "Giao tiếp và kết nối",
                "Âm Thanh", "Ổ đĩa quang", "Hệ điều hành", "Bảo mật", "Bàn phím & TouchPad", "Thông tin pin & sạc",
                "Phụ kiện trong hộp", "Thông số cơ bản", "Thiết kế & Trọng lượng", "Thông tin hàng hóa"};
    }

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
     * Crawls all laptops from the FPTShop website up to the specified limit.
     *
     * @param limit The maximum number of laptops to crawl.
     */
    private void crawlAllLaptops (int limit) {
        ExecutorService executor = Executors.newFixedThreadPool(MAX_THREADS);
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
                System.err.println("[ERROR] : An error occurred while crawling new laptops");
                System.out.println(e.getMessage());
                break;
            }
            for (JsonNode item : jsonNode.get("items")) {
                executor.submit(() -> this.extractAndSaveLaptop(item));
                if (++processed == limit) {
                    count=max;
                    break;
                }
            }
        }
        executor.shutdown();
        try {
            if(executor.awaitTermination(10, TimeUnit.MINUTES)) {
                System.out.println("[INFO] : FPTShop have crawled all laptops");
            } else {
                System.out.println("[INFO] : FPTShop have time-outed when crawling for laptops");
            }
        } catch (InterruptedException e) {
            System.out.println("[ERROR] : FPTShop has stopped abnormally");
        }
    }

    /**
     * Fetches the homepage API for product data.
     *
     * @param count The skip count for pagination.
     * @return The JSON response from the API as a JsonNode.
     * @throws IOException If an I/O error occurs.
     */
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

    private void extractAndSaveLaptop (JsonNode item) {
        System.out.println("[INFO] : Extracting " + item.get("code").asText());
        try {
            Document productPage = Jsoup.connect("https://fptshop.com.vn/" + item.get("slug").asText()).get();
            String code = item.get("code").asText();
            String[] laptopRow = extractLaptopRow(item);
            List<String[]> descriptions = extractDescriptions(code, productPage);
            String[] propertiesRow = extractPropertiesRow(code, productPage);
            List<String[]> reviews = extractReviews(laptopRow[0]);
            downloadImage(laptopRow);
            save(laptopRow, descriptions, propertiesRow, reviews);
        } catch (Exception e) {
            System.err.println("[ERROR] : An error occurred while extracting laptop's information");
            System.out.println(e.getMessage());
        }
    }

    private synchronized void save(String[] laptopRow, List<String[]> descriptions, String[] propertiesRow, List<String[]> reviews) {
        saveLaptopRow(laptopRow);
        saveDescriptions(descriptions);
        savePropertiesRow(propertiesRow);
        saveReviews(reviews);
    }

    private void downloadImage (String[] laptopRow) {
        String imageUrl = parseJson(laptopRow[14], new String[]{"src"});
        if (imageUrl == null) {
            laptopRow[14] = "";
            return;
        }
        imageUrl = imageUrl.replace(" ", "%20");
        String extension = imageUrl.substring(imageUrl.lastIndexOf(".") + 1);
        String outputPath = resourceURL + "images/" + laptopRow[0] + "." + extension;
        try (ReadableByteChannel in = Channels.newChannel(new URI(imageUrl).toURL().openStream());
             FileOutputStream out = new FileOutputStream(outputPath)) {

            FileChannel channel = out.getChannel();
            channel.transferFrom(in, 0, Long.MAX_VALUE);
        } catch (Exception e) {
            System.err.println("[ERROR] : An error occurred while downloading image");
            System.out.println(e.getMessage());
            laptopRow[14] = "";
            return;
        }
        laptopRow[14] = outputPath;
    }

    /**
     * Extracts the script containing product properties from the product page.
     *
     * @param productPage The product page Document.
     * @return The JSON representation of the product properties as a JsonNode.
     * @throws IOException If an I/O error occurs.
     */
    private JsonNode extractScript (Document productPage) throws IOException {
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

    /**
     * Extracts laptop information from a product JSON node.
     *
     * @param jsonNode The product JSON node.
     * @return An array of Strings containing laptop information.
     */
    private String[] extractLaptopRow(JsonNode jsonNode) {
        String[] laptopRow = new String[laptopColumn.length];
        laptopRow[0] = jsonNode.get("code").asText();
        for (int i = 1; i < laptopColumn.length; i++) {
            JsonNode node = jsonNode.get(laptopColumn[i]);
            if (node == null) {
                laptopRow[i] = "";
            } else if (node.isObject()) {
                laptopRow[i] = String.valueOf(node);
            } else {
                laptopRow[i] = node.asText();
            }
        }
        return laptopRow;
    }

    /**
     * Extracts the description of a product from its product page.
     *
     * @param code The product code.
     * @param productPage The product page Document.
     * @return An array of Strings containing the product ID and description.
     */
    private List<String[]> extractDescriptions (String code, Document productPage) {
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

    /**
     * Extracts the properties of a product from its product page.
     *
     * @param code The product code.
     * @param productPage The product page Document.
     * @return An array of Strings containing the product properties.
     * @throws IOException If an I/O error occurs.
     */
    private String[] extractPropertiesRow (String code, Document productPage) throws IOException {
        String[] propertiesRow = new String[propertiesColumn.length];
        propertiesRow[0] = code;
        JsonNode properties = extractScript(productPage);
        for (int i = 1; i < propertiesColumn.length; i++) {
            propertiesRow[i] = String.valueOf(properties.get(i-1).get("attributes"));
        }
        return propertiesRow;
    }

    private List<String[]> extractReviews(String id) throws IOException {
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
        JsonNode nodes = mapper.readTree(response.body().text()).get("data").get("items");
        List<String[]> reviews = new ArrayList<>();
        for(JsonNode reviewNode : nodes) {
            String[] review = new String[5];
            review[0] = id;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            sdf.setTimeZone(TimeZone.getTimeZone("UTC")); // Set timezone to UTC
            try {
                review[1] = Long.toString(sdf.parse(reviewNode.get("creationTime").asText()).getTime());
            } catch (ParseException e) {
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

    /**
     * Parses a JSON string based on a given path.
     *
     * @param json The JSON string to parse.
     * @param path An array of strings representing the path to the desired value.
     * Path elements can be field names or array indices (as strings).
     * The special keyword "slice" will iterate through all elements of the current JsonNode.
     * @return The parsed value as a string, joined with commas if multiple values are found, or null if parsing fails.
     */
    private String parseJson(String json, String[] path) {
        try {
            List<JsonNode> nodes = List.of(mapper.readTree(json));
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
        } catch (Exception e) {
            System.out.println("[INFO] : " + "Parsing has failed (%s)".formatted(Arrays.toString(path)));
            return null;
        }
    }

    /**
     * Parses CPU information from a properties row.
     *
     * @param propertiesRow An array of Strings containing product properties.
     * @return A CPU object.
     */
    private CPU parseCPU(String[] propertiesRow) {
        return new CPU.Builder()
                .setName("%s %s %s".formatted(
                        parseJson(propertiesRow[1],new String[]{"0", "value"}),
                        parseJson(propertiesRow[1],new String[]{"1", "value"}),
                        parseJson(propertiesRow[1],new String[]{"2", "value"})))
                .setBaseFrequency(parseJson(propertiesRow[1],new String[]{"3", "value", "displayValue"}))
                .setTurboFrequency(parseJson(propertiesRow[1],new String[]{"4", "value", "displayValue"}))
                .setCores(parseJson(propertiesRow[1],new String[]{"5", "value"}))
                .setThreads(parseJson(propertiesRow[1],new String[]{"6", "value"}))
                .setCache(parseJson(propertiesRow[1],new String[]{"7", "value"}))
                .build();
    }

    /**
     * Parses RAM information from a properties row.
     *
     * @param propertiesRow An array of Strings containing product properties.
     * @return A RAM object.
     */
    private RAM parseRAM(String[] propertiesRow) {
        return new RAM.Builder()
                .setSize(parseJson(propertiesRow[3],new String[]{"0", "value"}))
                .setClock(parseJson(propertiesRow[3],new String[]{"2", "value", "displayValue"}))
                .setType(parseJson(propertiesRow[3],new String[]{"1", "value"}))
                .setSlots(parseJson(propertiesRow[3],new String[]{"5", "value"}))
                .setMaxSize(parseJson(propertiesRow[3],new String[]{"6", "value"}))
                .build();
    }

    /**
     * Parses Display information from a properties row.
     *
     * @param propertiesRow An array of Strings containing product properties.
     * @return A Display object.
     */
    private LaptopDisplay parseDisplay(String[] propertiesRow) {
        if (parseJson(propertiesRow[2],new String[]{"2", "value"}) != null) {
            return new LaptopDisplay.Builder()
                    .setGpuName(parseJson(propertiesRow[2],new String[]{"2", "value"}))
                    .setGpuBaseClock(parseJson(propertiesRow[2],new String[]{"3", "value"}))
                    .setGpuBoostClock(parseJson(propertiesRow[2],new String[]{"4", "value"}))
                    .setScreenSize(parseJson(propertiesRow[5],new String[]{"0", "value", "displayValue"}))
                    .setScreenResolution(parseJson(propertiesRow[5],new String[]{"2", "value"}))
                    .setRefreshRate(parseJson(propertiesRow[5],new String[]{"4", "value", "displayValue"}))
                    .build();
        } else {
            return new LaptopDisplay.Builder()
                    .setGpuName(parseJson(propertiesRow[2],new String[]{"8", "value"}))
                    .setGpuBaseClock(parseJson(propertiesRow[2],new String[]{"9", "value"}))
                    .setGpuBoostClock(parseJson(propertiesRow[2],new String[]{"10", "value"}))
                    .setScreenSize(parseJson(propertiesRow[5],new String[]{"0", "value", "displayValue"}))
                    .setScreenResolution(parseJson(propertiesRow[5],new String[]{"2", "value"}))
                    .setRefreshRate(parseJson(propertiesRow[5],new String[]{"4", "value", "displayValue"}))
                    .build();
        }
    }

    /**
     * Parses storage information from a properties row.
     *
     * @param propertiesRow An array of Strings containing product properties.
     * @return A Storage object.
     */
    private Storage parseStorage(String[] propertiesRow) {
        String size = parseJson(propertiesRow[4],new String[]{"6", "value", "displayValue"});
        if (size == null) {
            size = parseJson(propertiesRow[4],new String[]{"9", "value", "displayValue"});
        }
        String bus = parseJson(propertiesRow[4],new String[]{"5", "value"});
        if (bus == null) {
            bus = parseJson(propertiesRow[4],new String[]{"8", "value"});
        }
        return new Storage.Builder()
                .setSize(size)
                .setBus(bus)
                .setStorageType(parseJson(propertiesRow[4],new String[]{"0", "value"}))
                .setUpgradable(parseJson(propertiesRow[4],new String[]{"4", "value"}))
                .setSlots(parseJson(propertiesRow[4],new String[]{"2", "value"}))
                .build();
    }

    /**
     * Parses connectivity information from a properties row.
     *
     * @param propertiesRow An array of Strings containing product properties.
     * @return A Connectivity object.
     */
    private Connectivity parseConnectivity(String[] propertiesRow) {
        return new Connectivity.Builder()
                .setPorts(parseJson(propertiesRow[6],new String[]{"0", "value", "slice"}))
                .setWifi(parseJson(propertiesRow[6],new String[]{"1", "value", "slice"}))
                .setBluetooth(parseJson(propertiesRow[6],new String[]{"2", "value", "slice"}))
                .setWebCam(parseJson(propertiesRow[6],new String[]{"3", "value", "slice"}))
                .build();
    }

    /**
     * Parses battery information from a properties row.
     *
     * @param propertiesRow An array of Strings containing product properties.
     * @return A Battery object.
     */
    private Battery parseBattery(String[] propertiesRow) {
        return new Battery.Builder()
                .setCapacity(parseJson(propertiesRow[12], new String[]{"1", "value", "slice", "displayValue"}))
                .setChargePower(parseJson(propertiesRow[12],new String[]{"2", "value", "slice", "displayValue"}))
                .build();
    }

    /**
     * Parses laptop case information from a properties row.
     *
     * @param propertiesRow An array of Strings containing product properties.
     * @return A LaptopCase object.
     */
    private LaptopCase parseLaptopCase(String[] propertiesRow) {
        return new LaptopCase.Builder()
                .setWeight(parseJson(propertiesRow[15],new String[]{"1", "value", "slice", "displayValue"}))
                .setDimensions(parseJson(propertiesRow[15],new String[]{"0", "value"}))
                .setMaterial(parseJson(propertiesRow[15],new String[]{"4", "value", "slice"}))
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
    Laptop parseLaptop (String[] laptopRow, List<String[]> descriptions, String[] propertiesRow, List<String[]> reviews) {
        Laptop.Builder builder = new Laptop.Builder()
                .setName(laptopRow[3])  // Column 'displayName'
                .setProductImage(laptopRow[14])  // Column 'image'
                .setPrice(Integer.parseInt(laptopRow[15]))  // Column 'originalPrice'
                .setDiscountPrice(Integer.parseInt(laptopRow[16]))  // Column 'currentPrice'
                .setSource("FPTShop")
                .setSourceURL("https://fptshop.com.vn/" + laptopRow[6])  // Column 'slug'
                .setBrand(parseJson(laptopRow[9], new String[]{"name"}))  // Column 'brand'
                .setColor(parseJson(propertiesRow[16], new String[]{"6", "value", "0"}))
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

