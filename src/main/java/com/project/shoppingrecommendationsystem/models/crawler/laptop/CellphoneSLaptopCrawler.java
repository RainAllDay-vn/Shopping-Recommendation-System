package com.project.shoppingrecommendationsystem.models.crawler.laptop;

import com.fasterxml.jackson.databind.JsonNode;
import com.project.shoppingrecommendationsystem.models.Laptop;
import com.project.shoppingrecommendationsystem.models.Review;
import com.project.shoppingrecommendationsystem.models.components.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * CellphoneSCrawler class is responsible for crawling laptop data from CellphoneS website.
 * It fetches product information using the website's API and extracts relevant details
 * such as product ID, name, description, and properties. The extracted data is then
 * saved into CSV files.
 */
public class CellphoneSLaptopCrawler extends LaptopCrawler {
    private static final int MAX_THREADS = 10;
    private final Map<String, Integer> propertiesMap = new HashMap<>();

    /**
     * Constructs a CellphoneSCrawler object.
     * <p>
     * This constructor performs the following actions:
     * <ul>
     * <li>Initialize the array of headers in laptop.csv, description.csv, properties.csv</li>
     * <li>Populates the {@code propertiesMap} with keys based on the {@code propertiesColumns} array. The values are the corresponding indices from the array.</li>
     * </ul>
     *
     * @throws RuntimeException if the resource directory cannot be created.
     */
    public CellphoneSLaptopCrawler() {
        super("CellphoneS/");
        this.laptopColumn = new String[]{"local_id", "name", "sku", "doc_quyen", "manufacturer",
                "url_key", "url_path", "categories", "review", "is_installment", "stock_available_id", "company_stock_id",
                "filter", "is_parent", "price", "prices", "special_price", "promotion_information",
                "thumbnail", "promotion_pack", "sticker", "flash_sale_types"};
        this.propertiesColumn = new String[]{"additional_information", "ads_base_image", "bao_hanh_1_doi_1", "basic",
                "battery", "best_discount_price", "bluetooth", "change_layout_preorder", "coupon_value", "cpu", "dimensions",
                "discount_price", "display_resolution", "display_size", "display_type", "fe_minimum_down_payment",
                "final_sale_price", "flash_sale_from", "flash_sale_price", "full_by_group", "hc_maximum_down_payment",
                "hc_minimum_down_payment", "hdd_sdd", "id", "image", "image_label", "included_accessories", "is_imported",
                "key_selling_points", "laptop_cam_ung", "laptop_camera_webcam", "laptop_cong_nghe_am_thanh", "laptop_cpu",
                "laptop_filter_gpu", "laptop_filter_tac_vu_su_dung", "laptop_loai_den_ban_phim", "laptop_loai_ram",
                "laptop_nganh_hoc", "laptop_ram", "laptop_resolution_filter", "laptop_screen_size_filter", "laptop_so_khe_ram",
                "laptop_special_feature", "laptop_tam_nen_man_hinh", "laptop_tan_so_quet", "laptop_vga_filter", "loaisp",
                "macbook_anh_bao_mat", "macbook_anh_dong_chip", "manufacturer", "meta_image", "meta_title", "mobile_accessory_type",
                "msrp", "msrp_display_actual_price_type", "msrp_enabled", "nhu_cau_su_dung", "o_cung_laptop", "options_container",
                "os_version", "ports_slots", "product_condition", "product_feed_type", "product_id", "product_state", "product_weight",
                "related_name", "short_description_hidden_time", "short_description_show_time", "sim_special_group", "small_image",
                "small_image_label", "smember_sms", "special_price", "status", "tag_sforum", "tax_vat", "thumbnail_label", "tien_coc",
                "title_price", "url_key", "url_path", "use_smd_colorswatch", "vga", "warranty_information", "weight", "wlan",
                "laptop_bao_mat", "visibility", "laptop_chat_lieu", "macbook_bao_mat", "macbook_cong_nghe_man_hinh", "macbook_dong_cpu",
                "macbook_gpu", "macbook_so_nhan_cpu", "macbook_thoi_luong_pin", "promotion_information", "promotion_percent",
                "short_description", "short_name", "visibility_date", "apple_computer_type", "hc_product_name", "hc_zero_install_month",
                "laptop_khe_doc_the_nho", "laptop_loaichip", "operating_system", "printer_loai_may", "special_from_date", "thu_cu_doi_moi",
                "tinh_nang_khac", "tragop", "uu_dai_tet", "laptop_bottom_case", "laptop_top_case", "laptop_cong_nghe_ai_filter",
                "pc_lap_rap_nguon", "laptop_vo_man_hinh", "maytinhdeban_hang_san_xuat", "cong_nghe_ai_laptop", "extra_promotion_information",
                "npu", "may_anh_loai_man_hinh", "youtube_videos", "pc_cong_io_canh_ben", "description", "technical_detail",
                "doc_quyen", "macbook_man_hinh", "pc_socket", "pc_lap_rap_cong_io_mat_sau", "color", "memory_card_slot",
                "promotion_actual_price_id", "usb", "related_new_product_id", "tinh_trang_may_cu", "tax_class_id"};
        for (int i = 0; i < propertiesColumn.length; i++) {
            propertiesMap.put(propertiesColumn[i], i);
        }
    }

    @Override
    public void crawl() {
        crawl(Integer.MAX_VALUE);
    }

    /**
     * @param limit The maximum number of laptops to crawl.
     */
    @Override
    public void crawl(int limit) {
        resetSave();
        crawlAllLaptops(limit);
    }

    /**
     * Crawls the CellphoneS website for laptop data.
     * Fetches product information from the homepage API and extracts laptop details,
     * descriptions, and properties. Saves the extracted data to CSV files.
     */
    private void crawlAllLaptops (int limit) {
        ExecutorService executor = Executors.newFixedThreadPool(MAX_THREADS);
        int count = 0;
        for (int pageIndex=1; pageIndex<=10; pageIndex++) {
            JsonNode products;
            try {
                JsonNode jsonNode = mapper.readTree(fetchHomepageAPI(pageIndex));
                products = jsonNode.path("data").path("products");
                if (products == null) {
                    break;
                }
            } catch (Exception e) {
                System.err.println("[ERROR] : An error occurred while crawling new laptops");
                System.out.println(e.getMessage());
                break;
            }
            for (JsonNode product : products) {
                executor.submit(() -> this.extractAndSaveLaptop(product));
                if (++count >= limit) {
                    pageIndex=100;
                    break;
                }
            }
        }
        executor.shutdown();
        try {
            if(executor.awaitTermination(10, TimeUnit.MINUTES)) {
                System.out.println("[INFO] : CellphoneS have crawled all laptops");
            } else {
                System.out.println("[INFO] : CellphoneS have time-outed when crawling for laptops");
            }
        } catch (InterruptedException e) {
            System.out.println("[ERROR] : CellphoneS has stopped abnormally");
        }
    }

    /**
     * Fetches the homepage API for product data.
     *
     * @param pageIndex The page index to fetch.
     * @return The JSON response from the API as a String.
     */
    private String fetchHomepageAPI (int pageIndex) {
        String requestBody = "{" +
                "  \"query\": \"query " +
                "GetProductsByCateId { products( filter: { static: { categories: [\\\"380\\\"], " +
                "province_id: 30, stock: { from: 0 }, " +
                "stock_available_id: [46, 56, 152, 4920], " +
                "filter_price: { from: 0, to: 194990000 } }, " +
                "dynamic: {} }, " +
                "page: %d, size: 100, sort: [{ view: desc }] ) " +
                "{ general { product_id name attributes sku doc_quyen manufacturer url_key url_path categories " +
                "{ categoryId name uri } review { total_count average_rating } }, " +
                "filterable { is_installment stock_available_id company_stock_id filter { id Label } " +
                "is_parent price prices special_price promotion_information thumbnail promotion_pack sticker flash_sale_types } } }\"," +
                "  \"variables\": {}" +
                "}";
        try {
            return Jsoup.connect("https://api.cellphones.com.vn/v2/graphql/query")
                    .header("Content-Type", "application/json") // Gửi dữ liệu dạng JSON
                    .requestBody(requestBody.formatted(pageIndex))
                    .ignoreContentType(true)
                    .post()
                    .body()
                    .text();
        } catch (Exception ignored) {
            return null;
        }
    }

    private void extractAndSaveLaptop (JsonNode product) {
        String productId = product.path("general").get("product_id").asText();
        System.out.println("[INFO] : Extracting information for laptop #" + productId);
        String[] laptopRow;
        List<String[]> descriptions;
        String[] propertiesRow;
        List<String[]> reviews;
        try {
            laptopRow = extractLaptop(product);
            descriptions = extractDescriptions(product);
            propertiesRow = extractProperties(product);
            reviews = extractReviews(laptopRow[0]);
            downloadImage(laptopRow);
        } catch (Exception e) {
            System.err.println("[ERROR] : An error occurred while extracting laptop's information");
            System.out.println(e.getMessage());
            return;
        }
        save(laptopRow, descriptions, propertiesRow, reviews);
    }

    private synchronized void save(String[] laptopRow, List<String[]> descriptions, String[] propertiesRow, List<String[]> reviews) {
        saveLaptopRow(laptopRow);
        saveDescriptions(descriptions);
        savePropertiesRow(propertiesRow);
        saveReviews(reviews);
    }

    private void downloadImage (String[] laptopRow) {
        String imageUrl = "https://cellphones.com.vn/media/catalog/product" + laptopRow[18];
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
            laptopRow[18] = "";
            return;
        }
        laptopRow[18] = outputPath;
    }

    /**
     * Extracts laptop information from a product JSON node.
     *
     * @param product The product JSON node.
     * @return An array of Strings containing laptop information.
     */
    private String[] extractLaptop (JsonNode product) {
        List<String> laptopRow = new ArrayList<>();
        for (Iterator<Map.Entry<String, JsonNode>> it = product.path("general").fields(); it.hasNext(); ) {
            Map.Entry<String, JsonNode> entry = it.next();
            if (entry.getKey().equals("attributes")) {
                continue;
            }
            laptopRow.add(entry.getValue().toString());
        }
        for (Iterator<Map.Entry<String, JsonNode>> it = product.path("filterable").fields(); it.hasNext(); ) {
            Map.Entry<String, JsonNode> entry = it.next();
            JsonNode value = entry.getValue();
            if (value.isNumber()) {
                laptopRow.add(String.valueOf(value.asInt()));
            } else if (value.isTextual()){
                laptopRow.add(value.asText());
            } else {
                laptopRow.add(value.toString());
            }
        }
        return laptopRow.toArray(new String[0]);
    }

    /**
     * Extracts the description of a product from its product page.
     *
     * @param product The product JSON node.
     * @return An array of Strings containing the product ID and description.
     */
    private List<String[]> extractDescriptions (JsonNode product) {
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
                if (child.className().equals("table-content")) {
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

    /**
     * Extracts the properties of a product from its attributes.
     *
     * @param product The product JSON node.
     * @return An array of Strings containing the product properties.
     */
    private String[] extractProperties (JsonNode product) {
        String[] propertiesRow = new String[propertiesColumn.length];
        for (Iterator<Map.Entry<String, JsonNode>> it = product.path("general").path("attributes").fields(); it.hasNext(); ) {
            Map.Entry<String, JsonNode> entry = it.next();
            propertiesRow[propertiesMap.get(entry.getKey())] = entry.getValue().toString();
        }
        return propertiesRow;
    }


    private List<String[]> extractReviews (String localId) throws IOException {
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
            JsonNode nodes = mapper.readTree(response.body().text()).get("data").get("reviews").get("matches");
            if(nodes.isEmpty()) break;
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

    /**
     * Parses CPU information from a properties row.
     *
     * @param propertiesRow An array of Strings containing product properties.
     * @return A CPU object.
     */
    private CPU parseCPU(String[] propertiesRow) {
        return new CPU.Builder()
                .setName(propertiesRow[32])  // Column "laptop_cpu"
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
                .setType(propertiesRow[36])  // Column "laptop_loai_ram"
                .setSize(propertiesRow[38])  // Column "laptop_ram"
                .setSlots(propertiesRow[41])  // Column "laptop_so_khe_ram"
                .build();
    }

    /**
     * Parses Display information from a properties row.
     *
     * @param propertiesRow An array of Strings containing product properties.
     * @return A Display object.
     */
    private LaptopDisplay parseDisplay(String[] propertiesRow) {
        return new LaptopDisplay.Builder()
                .setScreenResolution(propertiesRow[12])
                .setScreenSize(propertiesRow[13])
                .setRefreshRate(propertiesRow[44])
                .setGpuType(propertiesRow[45])
                .setGpuName(propertiesRow[83])
                .build();
    }

    /**
     * Parses storage information from a properties row.
     *
     * @param propertiesRow An array of Strings containing product properties.
     * @return A Storage object.
     */
    private Storage parseStorage(String[] propertiesRow) {
        return new Storage.Builder()
                .setSize(propertiesRow[22])  // Column "hdd_sdd"
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
                .setBluetooth(propertiesRow[6])  // Column "bluetooth"
                .setWebCam(propertiesRow[31])  // Column "laptop_camera_webcam"
                .setPorts(propertiesRow[60])  // Column "ports_slots"
                .setWifi(propertiesRow[86])  // Column "wlan"
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
                .setCapacity(propertiesRow[4])  // Column "battery"
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
                .setDimensions(propertiesRow[10])  // Column "dimension"
                .setWeight(propertiesRow[65])  // Column "product_weight"
                .setMaterial(propertiesRow[89])  // Column "laptop_chat_lieu"
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
                .setName(laptopRow[1].replace("\"", ""))  // Column "name"
                .setBrand(laptopRow[4])  // Column "manufacturer"
                .setSource("CellphoneS")
                .setSourceURL("https://cellphones.com.vn/" + laptopRow[6])  // Column "url_path"
                .setPrice(Integer.parseInt(laptopRow[14]))  // Column "price"
                .setDiscountPrice(Integer.parseInt(laptopRow[16]))  // Column "special_price"
                .setProductImage(laptopRow[18])  // Column "thumbnail"
                .setColor(propertiesRow[131])  // Column "color"
                .setDescription(descriptions)
                .setCpu(parseCPU(propertiesRow))
                .setRam(parseRAM(propertiesRow))
                .setDisplay(parseDisplay(propertiesRow))
                .setStorage(parseStorage(propertiesRow))
                .setConnectivity(parseConnectivity(propertiesRow))
                .setBattery(parseBattery(propertiesRow))
                .setLaptopCase(parseLaptopCase(propertiesRow));
        for (String[] row: reviews) {
            Date created = null;
            if (row[0]!=null && !row[0].isBlank()) {
                created = new Date(Long.parseLong(row[0]));
            }
            builder.addReview(new Review(created, row[1], row[2], row[3]));
        }
        return builder.build();
    }
}