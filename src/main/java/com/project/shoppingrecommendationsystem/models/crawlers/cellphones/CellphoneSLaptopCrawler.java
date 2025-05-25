package com.project.shoppingrecommendationsystem.models.crawlers.cellphones;

import com.fasterxml.jackson.databind.JsonNode;
import com.project.shoppingrecommendationsystem.ShoppingApplication;
import com.project.shoppingrecommendationsystem.models.Laptop;
import com.project.shoppingrecommendationsystem.models.Product;
import com.project.shoppingrecommendationsystem.models.components.Review;
import com.project.shoppingrecommendationsystem.models.components.*;

import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CellphoneSLaptopCrawler extends CellphoneSCrawler {
    private static final int MAX_THREADS = 10;

    public CellphoneSLaptopCrawler() {
        this.resourceURL = Objects.requireNonNull(ShoppingApplication.class.getResource(""))
                .getPath()
                .replace("%20", " ") + "data/laptops/CellphoneS/" ;
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
        this.propertyColumn = new String[]{"local_id", "additional_information", "ads_base_image", "bao_hanh_1_doi_1", "basic",
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
                JsonNode jsonNode = MAPPER.readTree(fetchHomepageAPI(pageIndex, 380));
                products = jsonNode.path("data").path("products");
                if (products.isEmpty()) break;
            }
            catch (NullPointerException e) {
                break;
            }
            catch (Exception e) {
                System.err.println("[ERROR] : An error occurred while crawling new laptops");
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
                System.out.println("[INFO] : CellphoneS have crawled all laptops");
            } else {
                System.out.println("[INFO] : CellphoneS have time-outed when crawling for laptops");
            }
        } catch (InterruptedException e) {
            System.out.println("[ERROR] : CellphoneS has stopped abnormally");
        }
    }

    private void extractAndSave(JsonNode product) {
        String productId = product.path("general").get("product_id").asText();
        System.out.println("[INFO] : Extracting information for laptop #" + productId);
        try {
            String[] productRow = extractProduct(product);
            List<String[]> descriptions = extractDescriptions(product);
            String[] propertiesRow = extractProperties(product);
            List<String[]> reviews = extractReviews(productRow[0]);
            save(productRow, descriptions, propertiesRow, reviews);
        } catch (Exception e) {
            System.err.println("[ERROR] : An error occurred while extracting laptop's information");
            System.out.println(e.getMessage());
        }
    }

    @Override
    protected Product parseProduct (String[] productRow, List<String[]> descriptions, String[] propertiesRow, List<String[]> reviews) {
        Laptop.Builder builder = new Laptop.Builder()
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
                .setDisplay(parseDisplay(propertiesRow))
                .setStorage(parseStorage(propertiesRow))
                .setConnectivity(parseConnectivity(propertiesRow))
                .setBattery(parseBattery(propertiesRow))
                .setLaptopCase(parseLaptopCase(propertiesRow));
        for (String[] row: reviews) {
            builder.addReview(new Review(parseDate(row[0]), row[1], row[2], row[3]));
        }
        return builder.build();
    }

    private CPU parseCPU(String[] propertiesRow) {
        return new CPU.Builder()
                .setName(propertiesRow[propertiesMap.get("laptop_cpu")])
                .build();
    }

    private RAM parseRAM(String[] propertiesRow) {
        return new RAM.Builder()
                .setType(propertiesRow[propertiesMap.get("laptop_loai_ram")])
                .setSize(propertiesRow[propertiesMap.get("laptop_ram")])
                .setSlots(propertiesRow[propertiesMap.get("laptop_so_khe_ram")])
                .build();
    }

    private LaptopDisplay parseDisplay(String[] propertiesRow) {
        return new LaptopDisplay.Builder()
                .setScreenResolution(propertiesRow[propertiesMap.get("display_resolution")])
                .setScreenSize(propertiesRow[propertiesMap.get("display_size")])
                .setRefreshRate(propertiesRow[propertiesMap.get("laptop_tan_so_quet")])
                .setGpuType(propertiesRow[propertiesMap.get("laptop_vga_filter")])
                .setGpuName(propertiesRow[propertiesMap.get("vga")])
                .build();
    }

    private Storage parseStorage(String[] propertiesRow) {
        return new Storage.Builder()
                .setSize(propertiesRow[propertiesMap.get("hdd_sdd")])
                .build();
    }

    private Connectivity parseConnectivity(String[] propertiesRow) {
        return new Connectivity.Builder()
                .setBluetooth(propertiesRow[propertiesMap.get("bluetooth")])
                .setWebCam(propertiesRow[propertiesMap.get("laptop_camera_webcam")])
                .setPorts(propertiesRow[propertiesMap.get("ports_slots")])
                .setWifi(propertiesRow[propertiesMap.get("wlan")])
                .build();
    }

    private Battery parseBattery(String[] propertiesRow) {
        return new Battery.Builder()
                .setCapacity(propertiesRow[propertiesMap.get("battery")])
                .build();
    }

    private Case parseLaptopCase(String[] propertiesRow) {
        return new Case.Builder()
                .setDimensions(propertiesRow[propertiesMap.get("dimensions")])
                .setWeight(propertiesRow[propertiesMap.get("product_weight")])
                .setMaterial(propertiesRow[propertiesMap.get("laptop_chat_lieu")])
                .build();
    }
}