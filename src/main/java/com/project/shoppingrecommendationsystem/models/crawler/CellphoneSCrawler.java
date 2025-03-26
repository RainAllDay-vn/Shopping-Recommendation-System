package com.project.shoppingrecommendationsystem.models.crawler;

import com.fasterxml.jackson.databind.JsonNode;
import com.project.shoppingrecommendationsystem.models.Laptop;
import com.project.shoppingrecommendationsystem.models.components.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.util.*;

/**
 * CellphoneSCrawler class is responsible for crawling laptop data from CellphoneS website.
 * It fetches product information using the website's API and extracts relevant details
 * such as product ID, name, description, and properties. The extracted data is then
 * saved into CSV files.
 */
public class CellphoneSCrawler extends Crawler {
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
    public CellphoneSCrawler() {
        super("data/CellphoneS/");
        this.laptopColumn = new String[]{"product_id", "name", "sku", "doc_quyen", "manufacturer",
                "url_key", "url_path", "categories", "review", "is_installment", "stock_available_id", "company_stock_id",
                "filter", "is_parent", "price", "prices", "special_price", "promotion_information",
                "thumbnail", "promotion_pack", "sticker", "flash_sale_types"};
        this.descriptionColumn = new String[]{"product_id", "description"};
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
    private void crawlAllLaptops (int limit) {
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
                String productId = product.path("general").get("product_id").asText();
                System.out.println("[INFO] : Extracting information for laptop #" + productId);
                String[] laptopRow;
                String[] descriptionRow;
                String[] propertiesRow;
                try {
                    laptopRow = extractLaptop(product);
                    descriptionRow = extractDescription(product);
                    propertiesRow = extractProperties(product);
                } catch (Exception e) {
                    System.err.println("[ERROR] : An error occurred while extracting laptop's information");
                    System.out.println(e.getMessage());
                    continue;
                }
                saveLaptopRow(laptopRow);
                saveDescriptionRow(descriptionRow);
                savePropertiesRow(propertiesRow);
                count++;
                if (count >= limit) {
                    return;
                }
            }
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
     * Parses CPU information from a properties row.
     *
     * @param propertiesRow An array of Strings containing product properties.
     * @return A CPU object.
     */
    private CPU parseCPU(String[] propertiesRow) {
        return new CPU.CPUBuilder()
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
        return new RAM.RAMBuilder()
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
    private Display parseDisplay(String[] propertiesRow) {
        return new Display.DisplayBuilder()
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
        return new Storage.StorageBuilder()
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
        return new Connectivity.ConnectivityBuilder()
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
        return new Battery.BatteryBuilder()
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
        return new LaptopCase.LaptopCaseBuilder()
                .setDimensions(propertiesRow[10])  // Column "dimension"
                .setWeight(propertiesRow[65])  // Column "product_weight"
                .setMaterial(propertiesRow[89])  // Column "laptop_chat_lieu"
                .build();
    }

    /**
     * Parses laptop information from laptop, description, and properties rows.
     *
     * @param laptopRow      An array of Strings containing laptop information.
     * @param descriptionRow An array of Strings containing product description.
     * @param propertiesRow  An array of Strings containing product properties.
     * @return A Laptop object.
     */

    Laptop parseLaptop (String[] laptopRow, String[] descriptionRow, String[] propertiesRow) {
        return new Laptop.LaptopBuilder()
                .setName(laptopRow[1])  // Column "name"
                .setBrand(laptopRow[4])  // Column "manufacturer"
                .setSource("CellphoneS")
                .setSourceURL("https://cellphones.com.vn/" + laptopRow[6])  // Column "url_path"
                .setPrice(Integer.parseInt(laptopRow[14]))  // Column "price"
                .setDiscountPrice(Integer.parseInt(laptopRow[16]))  // Column "special_price"
                .setProductImage("https://cellphones.com.vn/media/catalog/product" + laptopRow[18])  // Column "thumbnail"
                .setColor(propertiesRow[131])  // Column "color"
                .setDescription(descriptionRow[1])
                .setCpu(parseCPU(propertiesRow))
                .setRam(parseRAM(propertiesRow))
                .setDisplay(parseDisplay(propertiesRow))
                .setStorage(parseStorage(propertiesRow))
                .setConnectivity(parseConnectivity(propertiesRow))
                .setBattery(parseBattery(propertiesRow))
                .setLaptopCase(parseLaptopCase(propertiesRow))
                .build();
    }
}