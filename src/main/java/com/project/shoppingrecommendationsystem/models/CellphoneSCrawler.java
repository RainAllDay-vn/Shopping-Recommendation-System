package com.project.shoppingrecommendationsystem.models;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.*;
import com.project.shoppingrecommendationsystem.HelloApplication;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.io.File;
import java.io.FileWriter;
import java.util.*;

/**
 * CellphoneSCrawler class is responsible for crawling laptop data from CellphoneS website.
 * It fetches product information using the website's API and extracts relevant details
 * such as product ID, name, description, and properties. The extracted data is then
 * saved into CSV files.
 */
public class CellphoneSCrawler {
    private final ObjectMapper mapper = new ObjectMapper();
    private final String resourceURL = Objects.requireNonNull(HelloApplication.class.getResource(""))
            .getPath()
            .replace("%20", " ") + "data/CellphoneS/";
    private final String[] laptopColumn = {"product_id", "name", "sku", "doc_quyen", "manufacturer",
            "url_key", "url_path", "categories", "review", "is_installment", "stock_available_id", "company_stock_id",
            "filter", "is_parent", "price", "prices", "special_price", "promotion_information",
            "thumbnail", "promotion_pack", "sticker", "flash_sale_types"};
    private final String[] descriptionColumn = {"product_id", "description"};
    private final String[] propertiesColumn = {"additional_information", "ads_base_image", "bao_hanh_1_doi_1", "basic",
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
    private final Map<String, Integer> propertiesMap = new HashMap<>();

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
    public CellphoneSCrawler() {
        File resourceDir = new File(resourceURL);
        if (!resourceDir.exists()) {
            if (!resourceDir.mkdirs()) {
                throw new RuntimeException("Unable to create directory " + resourceURL);
            }
        }
        for (int i = 0; i < propertiesColumn.length; i++) {
            propertiesMap.put(propertiesColumn[i], i);
        }
    }

    public static void main(String[] args) {
        CellphoneSCrawler crawler = new CellphoneSCrawler();
        try {
            crawler.crawlLaptops();
        } catch (Exception e) {
            System.out.println("Crawling did not work");
            System.out.println(e.getMessage());
        }
    }

    /**
     * Crawls the CellphoneS website for laptop data.
     * Fetches product information from the homepage API and extracts laptop details,
     * descriptions, and properties. Saves the extracted data to CSV files.
     */
    private void crawlLaptops () {
        List<String[]> laptopRows = new ArrayList<>(laptopColumn.length);
        List<String[]> descriptionRows = new ArrayList<>(descriptionColumn.length);
        List<String[]> propertiesRows = new ArrayList<>(propertiesColumn.length);
        for (int pageIndex=1; pageIndex<=10; pageIndex++) {
            JsonNode products;
            try {
                JsonNode jsonNode = mapper.readTree(fetchHomepageAPI(pageIndex));
                products = jsonNode.path("data").path("products");
                if (products == null) {
                    break;
                }
            } catch (Exception e) {
                System.err.println("An error occurred while crawling ...");
                System.err.println(e.getMessage());
                break;
            }
            for (JsonNode product : products) {
                laptopRows.add(extractLaptop(product));
                descriptionRows.add(extractDescription(product));
                propertiesRows.add(extractProperties(product));
            }
        }
        saveLaptop(laptopRows);
        saveDescriptions(descriptionRows);
        saveProperties(propertiesRows);
    }

    /**
     * Fetches the homepage API for product data.
     *
     * @param pageIndex The page index to fetch.
     * @return The JSON response from the API as a String.
     */
    private String fetchHomepageAPI (int pageIndex) {
        resetSave();
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
    private String[] extractDescription (JsonNode product) {
        String productId = product.path("general").get("product_id").asText();
        System.out.println("Extracting description for " + productId);
        String[] descriptionRow = new String[2];
        descriptionRow[0] = productId;
        String productURL = product.path("general").get("url_path").asText();
        try {
            String pageURL = "https://cellphones.com.vn/";
            Element body = Jsoup.connect(pageURL + productURL).get().body();
            Element description = body.selectFirst("#cpsContentSEO");
            assert description != null;
            StringBuilder str = new StringBuilder();
            for (Element child : description.children()) {
                if (child.className().equals("table-content")) {
                    continue;
                }
                str.append(child.text());
            }
            descriptionRow[1] = str.toString();
        } catch (Exception ignored) {}
        return descriptionRow;
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

    /**
     * Resets the CSV files and write the column headers.
     */
    private void resetSave () {
        try (CSVWriter laptopWriter = new CSVWriter(new FileWriter(resourceURL + "laptop.csv"));
             CSVWriter descriptionWriter = new CSVWriter(new FileWriter(resourceURL + "description.csv"));
             CSVWriter propertiesWriter = new CSVWriter(new FileWriter(resourceURL + "properties.csv"))) {
            laptopWriter.writeNext(laptopColumn);
            descriptionWriter.writeNext(descriptionColumn);
            propertiesWriter.writeNext(propertiesColumn);
        } catch (Exception e){
            System.out.println("There was an error when accessing saving file");
            System.out.println(e.getMessage());
        }
    }

    /**
     * Saves the extracted laptop rows to the laptop CSV file.
     *
     * @param laptopRows A list of String arrays containing laptop information.
     */
    private void saveLaptop (List<String[]> laptopRows) {
        try (CSVWriter csvWriter = new CSVWriter(new FileWriter(resourceURL + "laptop.csv", true))) {
            csvWriter.writeAll(laptopRows);
        } catch (Exception e){
            System.out.println("There was an error when accessing saving file");
            System.out.println(e.getMessage());
        }
    }

    /**
     * Saves the extracted description rows to the description CSV file.
     *
     * @param descriptionRows A list of String arrays containing product descriptions.
     */
    private void saveDescriptions (List<String[]> descriptionRows) {
        try (CSVWriter csvWriter = new CSVWriter(new FileWriter(resourceURL + "description.csv", true))) {
            csvWriter.writeAll(descriptionRows);
        } catch (Exception e){
            System.out.println("There was an error when accessing saving file");
            System.out.println(e.getMessage());
        }
    }

    /**
     * Saves the extracted properties rows to the properties CSV file.
     *
     * @param propertiesRows A list of String arrays containing product properties.
     */
    private void saveProperties (List<String[]> propertiesRows) {
        try (CSVWriter csvWriter = new CSVWriter(new FileWriter(resourceURL + "properties.csv", true))) {
            csvWriter.writeAll(propertiesRows);
        } catch (Exception e){
            System.out.println("There was an error when accessing saving file");
            System.out.println(e.getMessage());
        }
    }
}