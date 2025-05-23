package com.project.shoppingrecommendationsystem.models.crawlers.cellphones;

import com.fasterxml.jackson.databind.JsonNode;
import com.opencsv.CSVReader;
import com.project.shoppingrecommendationsystem.ShoppingApplication;
import com.project.shoppingrecommendationsystem.models.Product;
import com.project.shoppingrecommendationsystem.models.Review;
import com.project.shoppingrecommendationsystem.models.SmartPhone;
import com.project.shoppingrecommendationsystem.models.components.*;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class CellphoneSSmartPhoneCrawler extends CellphoneSCrawler {
    private static final int MAX_THREADS = 10;

    public CellphoneSSmartPhoneCrawler() {
        this.resourceURL = Objects.requireNonNull(ShoppingApplication.class.getResource(""))
                .getPath()
                .replace("%20", " ") + "data/smartphones/CellphoneS/" ;
        File resourceDir = new File(this.resourceURL + "images/");
        if (!resourceDir.exists()) {
            if (!resourceDir.mkdirs()) {
                throw new RuntimeException("Unable to create directory " + this.resourceURL);
            }
        }
        this.productColumn = new String[]{"local_id", "name", "sku", "doc_quyen", "manufacturer",
                "url_key", "url_path", "categories", "review", "is_installment", "stock_available_id", "company_stock_id",
                "filter", "is_parent", "price", "prices", "special_price", "promotion_information",
                "thumbnail", "promotion_pack", "sticker", "flash_sale_types"};
        this.propertyColumn = new String[]{"local_id", "ads_base_image", "additional_information", "anh_chong_nuoc", "anh_dieu_khien_camera",
                "anh_intelligence", "anh_sac_khong_day", "anh_thu_phong_quang_hoc", "bao_hanh_1_doi_1", "basic", "battery",
                "best_discount_price", "bluetooth", "camera_primary", "camera_secondary", "camera_video", "canonical_link",
                "change_layout_preorder", "chipset", "chong_nuoc", "cjm_imageswitcher", "color", "coupon_value", "cpu",
                "custom_layout_update", "description", "dieu_khien_camera", "dimensions", "discount_price", "display_resolution",
                "display_size", "display_type", "doc_quyen", "dont_update_stock", "fe_maximum_down_payment", "fe_minimum_down_payment",
                "fe_zero_install", "fe_zero_install_month", "final_sale_price", "flash_sale_from", "flash_sale_price", "full_by_group",
                "gia_vo_doi", "gps", "gpu", "hc_maximum_down_payment", "hc_minimum_down_payment", "hc_product_name", "hc_zero_install",
                "hc_zero_install_month", "hc_zero_install_promotion", "hc_zero_sale_price", "hub_cong_nghe", "id", "image",
                "image_label", "included_accessories", "infrared_port", "inteligent_text", "ipad_am_thanh", "ipad_apple_pencil",
                "ipad_camera_truoc", "ipad_magic_keyboard", "ipad_wifi_6e", "iphone_5g", "iphone_5g_text", "iphone_anh_cong_sac",
                "iphone_anh_chat_lieu", "iphone_bao_mat", "iphone_bao_mat_text", "iphone_camera", "iphone_camera_text", "iphone_chat_lieu",
                "iphone_chedo_camera", "iphone_chedo_camera_text", "iphone_chip", "iphone_chip_text", "iphone_cong_sac",
                "iphone_dynamic_island_image", "iphone_dynamic_island_text", "iphone_man_hinh", "iphone_name", "iphone_pin",
                "iphone_pin_text", "iphone_sos", "iphone_sos_vacham", "iphone_sos_vacham_text", "is_imported", "key_selling_points",
                "last_send", "loai_mang", "loaisp", "manufacturer", "may_anh_loai_man_hinh", "memory_card_slot", "memory_internal",
                "meta_image", "meta_title", "mobile_accessory_type", "mobile_am_thanh", "mobile_cam_bien", "mobile_cam_bien_van_tay",
                "mobile_camera_feature", "mobile_chat_lieu_khung_vien", "mobile_chat_lieu_mat_lung", "mobile_chip_filter",
                "mobile_cong_nghe_sac", "mobile_cong_sac", "mobile_display_features", "mobile_hong_ngoai", "mobile_ht_lam_mat",
                "mobile_jack_tai_nghe", "mobile_khang_nuoc_bui", "mobile_kieu_man_hinh", "mobile_loaimay_poco", "mobile_nfc",
                "mobile_nhu_cau_sd", "mobile_os_filter", "mobile_quay_video_truoc", "mobile_ra_mat", "mobile_ram_filter",
                "mobile_storage_filter", "mobile_tan_so_quet", "mobile_tinh_nang_camera", "mobile_tinh_nang_dac_biet",
                "mobile_type_of_display", "model_dienthoai_mtb", "msrp", "msrp_display_actual_price_type", "msrp_enabled",
                "nfc", "operating_system", "options_container", "os_version", "product_condition", "product_feed_type",
                "product_id", "product_state", "product_weight", "promotion_information", "promotion_percent", "related_name",
                "related_new_product_id", "sac_khong_day", "sac_khong_day_text", "sale_ap_dung_den", "sale_ap_dung_tu",
                "sale_so_luong_toi_da", "sensors", "short_description", "short_description_hidden_time", "short_description_show_time",
                "sim", "sim_card", "sim_special_group", "small_image", "small_image_label", "smember_sms", "sms_box", "sms_content",
                "speed_3g", "speed_4g", "speed_5g", "special_from_date", "special_price", "special_to_date", "sticker_bh_18_thang",
                "storage", "tag_sforum", "tablet_hang_san_xuat", "tablet_he_dieu_hanh", "tablet_nhu_cau_su_dng", "tablet_tien_ich",
                "tablet_tinh_nang_dac_biet", "tablet_tuong_thich", "tax_vat", "technical_detail", "thu_cu_doi_moi", "thu_phong_quang_hoc",
                "thumbnail_label", "tien_coc", "tinh_trang_may_cu", "title_price", "tragop", "u_dai_smember", "uu_dai_tet", "url_key",
                "url_path", "usb", "use_smd_colorswatch", "visibility", "visibility_date", "wlan", "warranty_information", "weight"};
        initializeMap();
    }

    @Override
    public void crawl () {
        crawl(Integer.MAX_VALUE);
    }

    @Override
    public void crawl (int limit){
        resetSave();
        ExecutorService executor = Executors.newFixedThreadPool(MAX_THREADS);
        int count = 0;
        for (int pageIndex=1; pageIndex<=100; pageIndex++) {
            JsonNode products;
            try {
                JsonNode jsonNode = MAPPER.readTree(fetchHomepageAPI(pageIndex, 3));
                products = jsonNode.path("data").path("products");
                if (products.isEmpty()) break;
            }
            catch (NullPointerException e) {
                break;
            }
            catch (Exception e) {
                System.err.println("[ERROR] : An error occurred while crawling new phones");
                System.out.println(e.getMessage());
                break;
            }
            for (JsonNode product : products) {
                executor.submit(() -> this.extractAndSave(product));
                if (++count >= limit) {
                    pageIndex=100;
                    break;
                }
            }
        }
        executor.shutdown();
        try {
            if(executor.awaitTermination(10, TimeUnit.MINUTES)) {
                System.out.println("[INFO] : CellphoneS have crawled all smartphones");
            } else {
                System.out.println("[INFO] : CellphoneS have time-outed when crawling for smartphones");
            }
        } catch (InterruptedException e) {
            System.out.println("[ERROR] : CellphoneS has stopped abnormally");
        }
    }

    private void extractAndSave (JsonNode product) {
        String productId = product.path("general").get("product_id").asText();
        System.out.println("[INFO] : Extracting information for smartphone #" + productId);
        try {
            String[] productRow = extractProduct(product);
            List<String[]> descriptions = extractDescriptions(product);
            String[] propertiesRow = extractProperties(product);
            List<String[]> reviews = extractReviews(productRow[0]);
            save(productRow, descriptions, propertiesRow, reviews);
        } catch (Exception e) {
            System.err.println("[ERROR] : An error occurred while extracting smartphone's information");
            System.out.println(e.getMessage());
        }
    }

    @Override
    protected Product parseProduct(String[] productRow, List<String[]> descriptions, String[] propertiesRow, List<String[]> reviews) {
        SmartPhone.Builder builder = new SmartPhone.Builder()
                .setName(productRow[productsMap.get("name")])
                .setProductImage(productRow[productsMap.get("thumbnail")])
                .setPrice(Integer.parseInt(productRow[productsMap.get("price")]))
                .setDiscountPrice(Integer.parseInt(productRow[productsMap.get("special_price")]))
                .setSource("CellphoneS")
                .setSourceURL("https://cellphones.com.vn/"+productRow[productsMap.get("url_path")])
                .setBrand(productRow[productsMap.get("manufacturer")])
                .setColor(propertiesRow[propertiesMap.get("color")])
                .setDescription(descriptions)
                .setCpu(parseCPU(propertiesRow))
                .setRam(parseRAM(propertiesRow))
                .setStorage(parseStorage(propertiesRow))
                .setConnectivity(parseConnectivity(propertiesRow))
                .setCamera(parseCamera(propertiesRow))
                .setBattery(parseBattery(propertiesRow))
                .setPhoneCase(parseCase(propertiesRow))
                .setDisplay(parseDisplay(propertiesRow));
        for (String[] row: reviews) {
            builder.addReview(new Review(parseDate(row[0]), row[1], row[2], row[3]));
        }
        return builder.build();
    }

    private CPU parseCPU(String[] propertiesRow) {
        return new CPU.Builder()
                .setName(propertiesRow[propertiesMap.get("chipset")])
                .setBaseFrequency(propertiesRow[propertiesMap.get("cpu")].split("<br>")[0])
                .build();
    }

    private RAM parseRAM(String[] propertiesRow) {
        return new RAM.Builder()
                .setSize(propertiesRow[propertiesMap.get("memory_internal")])
                .build();
    }

    private Storage parseStorage(String[] propertiesRow) {
        return new Storage.Builder()
                .setSize(propertiesRow[propertiesMap.get("storage")])
                .build();
    }

    private Connectivity parseConnectivity(String[] propertiesRow) {
        return new Connectivity.Builder()
                .setBluetooth(propertiesRow[propertiesMap.get("bluetooth")])
                .setFingerprint(propertiesRow[propertiesMap.get("mobile_cam_bien_van_tay")])
                .setPorts(propertiesRow[propertiesMap.get("sim")])
                .setWifi(propertiesRow[propertiesMap.get("wlan")].split("<br>")[0])
                .build();
    }

    private Camera parseCamera(String[] propertiesRow) {
        return new Camera.Builder()
                .setFrontCameraName(propertiesRow[propertiesMap.get("camera_primary")].split("<br>")[0])
                .build();
    }

    private Battery parseBattery(String[] propertiesRow) {
        return new Battery.Builder()
                .setCapacity(propertiesRow[propertiesMap.get("battery")])
                .setChargePower(propertiesRow[propertiesMap.get("mobile_cong_nghe_sac")])
                .build();
    }

    private Case parseCase(String[] propertiesRow) {
        return new Case.Builder()
                .setDimensions(propertiesRow[propertiesMap.get("dimensions")])
                .setMaterial(propertiesRow[propertiesMap.get("mobile_chat_lieu_khung_vien")])
                .setWeight(propertiesRow[propertiesMap.get("product_weight")])
                .build();
    }

    private PhoneDisplay parseDisplay(String[] propertiesRow) {
        return new PhoneDisplay.Builder()
                .setResolution(propertiesRow[propertiesMap.get("display_resolution")])
                .setSize(propertiesRow[propertiesMap.get("display_size")])
                .setName(propertiesRow[propertiesMap.get("mobile_type_of_display")])
                .build();
    }
}
