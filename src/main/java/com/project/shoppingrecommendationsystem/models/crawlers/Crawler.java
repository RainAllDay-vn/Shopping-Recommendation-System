package com.project.shoppingrecommendationsystem.models.crawlers;

import com.project.shoppingrecommendationsystem.models.Product;

import java.util.List;

public interface Crawler {
    List<Product> getAllProducts();

    void crawl();

    void crawl(int limit);
}
