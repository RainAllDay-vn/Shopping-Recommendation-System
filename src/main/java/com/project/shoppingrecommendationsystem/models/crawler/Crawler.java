package com.project.shoppingrecommendationsystem.models.crawler;

import com.project.shoppingrecommendationsystem.models.Product;

import java.util.List;

public interface Crawler {
    public List<Product> getAll();

    public void crawl();

    public void crawl(int limit);
}
