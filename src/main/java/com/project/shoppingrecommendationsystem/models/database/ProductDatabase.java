package com.project.shoppingrecommendationsystem.models.database;

import com.project.shoppingrecommendationsystem.models.Product;
import com.project.shoppingrecommendationsystem.models.crawlers.Crawler;

import java.util.List;
import java.util.Optional;

public interface ProductDatabase {
    default void crawl() {
        crawl(Integer.MAX_VALUE);
    };

    void crawl(int limit);

    void crawl(Crawler crawler);

    List<Product> findAllProducts();

    Optional<Product> findProductById(int id);

    List<Product> findProducts(List<String[]> query, int limit, int offset);
}
