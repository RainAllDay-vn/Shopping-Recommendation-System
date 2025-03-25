package com.project.shoppingrecommendationsystem.models.crawler;

import com.fasterxml.jackson.databind.JsonNode;
import com.opencsv.exceptions.CsvValidationException;
import com.project.shoppingrecommendationsystem.models.Laptop;
import com.project.shoppingrecommendationsystem.models.components.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * FPTShopCrawler class is responsible for crawling laptop data from FPTShop website.
 * It fetches product information using the website's API and extracts relevant details
 * such as product ID, name, description, and properties. The extracted data is then
 * saved into CSV files.
 */
public class FPTShopCrawler extends Crawler{
    private final Pattern compiledPattern = Pattern.compile("attributeItem");

    /**
     * Constructs a FPTShopCrawler object.
     * <p>
     * This constructor initializes the resource directory and sets up the column headers for the CSV files.
     */
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
     * Crawls all laptops from the FPTShop website up to the specified limit.
     *
     * @param limit The maximum number of laptops to crawl.
     */
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

    /**
     * Extracts the script containing product properties from the product page.
     *
     * @param productPage The product page Document.
     * @return The JSON representation of the product properties as a JsonNode.
     * @throws IOException If an I/O error occurs.
     * @throws CsvValidationException If a CSV validation error occurs.
     */
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

    /**
     * Extracts laptop information from a product JSON node.
     *
     * @param jsonNode The product JSON node.
     * @return An array of Strings containing laptop information.
     */
    private String[] extractLaptopRow(JsonNode jsonNode) {
        String[] laptopRow = new String[laptopColumn.length];
        laptopRow[0] = String.valueOf(jsonNode.get("code").toString());
        for (int i = 1; i < laptopColumn.length; i++) {
            laptopRow[i] = String.valueOf(jsonNode.get(laptopColumn[i]).asText());
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
    private String[] extractDescriptionRow (String code, Document productPage) {
        String[] descriptionRow = new String[descriptionColumn.length];
        descriptionRow[0] = code;
        Element descriptionContainer;
        try {
            descriptionContainer = productPage.getElementsByClass("description-container").getFirst();
        } catch (NoSuchElementException e) {
            descriptionRow[1] = "";
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

    /**
     * Extracts the properties of a product from its product page.
     *
     * @param code The product code.
     * @param productPage The product page Document.
     * @return An array of Strings containing the product properties.
     * @throws CsvValidationException If a CSV validation error occurs.
     * @throws IOException If an I/O error occurs.
     */
    private String[] extractPropertiesRow (String code, Document productPage) throws CsvValidationException, IOException {
        String[] propertiesRow = new String[propertiesColumn.length];
        propertiesRow[0] = code;
        JsonNode properties = extractScript(productPage);
        for (int i = 1; i < propertiesColumn.length; i++) {
            propertiesRow[i] = String.valueOf(properties.get(i-1).get("attributes"));
        }
        return propertiesRow;
    }

    private String parseJson(String json, String[] path) {
        try {
            List<JsonNode> nodes = List.of(mapper.readTree(json));
            for (int i = 0; i < path.length; i++) {
                String field = path[i];
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

    private CPU parseCPU(String[] propertiesRow) {
        return new CPU.CPUBuilder()
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

    private RAM parseRAM(String[] propertiesRow) {
        return new RAM.RAMBuilder()
                .setSize(parseJson(propertiesRow[3],new String[]{"0", "value"}))
                .setClock(parseJson(propertiesRow[3],new String[]{"2", "value", "displayValue"}))
                .setType(parseJson(propertiesRow[3],new String[]{"1", "value"}))
                .setSlots(parseJson(propertiesRow[3],new String[]{"5", "value"}))
                .setMaxSize(parseJson(propertiesRow[3],new String[]{"6", "value"}))
                .build();
    }

    private Storage parseStorage(String[] propertiesRow) {
        String size = parseJson(propertiesRow[4],new String[]{"6", "value", "displayValue"});
        if (size == null) {
            size = parseJson(propertiesRow[4],new String[]{"9", "value", "displayValue"});
        }
        String bus = parseJson(propertiesRow[4],new String[]{"5", "value"});
        if (bus == null) {
            bus = parseJson(propertiesRow[4],new String[]{"8", "value"});
        }
        return new Storage.StorageBuilder()
                .setSize(size)
                .setBus(bus)
                .setStorageType(parseJson(propertiesRow[4],new String[]{"0", "value"}))
                .setUpgradable(parseJson(propertiesRow[4],new String[]{"4", "value"}))
                .setSlots(parseJson(propertiesRow[4],new String[]{"2", "value"}))
                .build();
    }

    private Connectivity parseConnectivity(String[] propertiesRow) {
        return new Connectivity.ConnectivityBuilder()
                .setPorts(parseJson(propertiesRow[6],new String[]{"0", "value", "slice"}))
                .setWifi(parseJson(propertiesRow[6],new String[]{"1", "value", "slice"}))
                .setBluetooth(parseJson(propertiesRow[6],new String[]{"2", "value", "slice"}))
                .setWebCam(parseJson(propertiesRow[6],new String[]{"3", "value", "slice"}))
                .build();
    }

    private Battery parseBattery(String[] propertiesRow) {
        return new Battery.BatteryBuilder()
                .setCapacity(parseJson(propertiesRow[12], new String[]{"1", "value", "slice", "displayValue"}))
                .setChargePower(parseJson(propertiesRow[12],new String[]{"2", "value", "slice", "displayValue"}))
                .build();
    }

    private LaptopCase parseLaptopCase(String[] propertiesRow) {
        return new LaptopCase.LaptopCaseBuilder()
                .setWeight(parseJson(propertiesRow[15],new String[]{"1", "value", "slice", "displayValue"}))
                .setDimensions(parseJson(propertiesRow[15],new String[]{"0", "value"}))
                .setMaterial(parseJson(propertiesRow[15],new String[]{"4", "value", "slice"}))
                .build();
    }

    Laptop parseLaptop (String[] laptopRow, String[] descriptionRow, String[] propertiesRow) {
        return new Laptop.LaptopBuilder()
                .setName(laptopRow[3])  // Column 'displayName'
                .setProductImage(parseJson(laptopRow[14], new String[]{"src"}))  // Column 'image'
                .setPrice(Integer.parseInt(laptopRow[15]))  // Column 'originalPrice'
                .setDiscountPrice(Integer.parseInt(laptopRow[16]))  // Column 'currentPrice'
                .setSourceURL("https://fptshop.com.vn/" + laptopRow[6])  // Column 'slug'
                .setBrand(parseJson(laptopRow[9], new String[]{"name"}))  // Column 'brand'
                .setColor(parseJson(propertiesRow[16], new String[]{"6", "value", "0"}))
                .setDescription(descriptionRow[1])
                .setCpu(parseCPU(propertiesRow))
                .setRam(parseRAM(propertiesRow))
                .setStorage(parseStorage(propertiesRow))
                .setConnectivity(parseConnectivity(propertiesRow))
                .setBattery(parseBattery(propertiesRow))
                .setLaptopCase(parseLaptopCase(propertiesRow))
                .build();
    }
}

