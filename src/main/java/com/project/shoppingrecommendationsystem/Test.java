package com.project.shoppingrecommendationsystem;

import com.project.shoppingrecommendationsystem.models.Product;
import com.project.shoppingrecommendationsystem.models.ProductDatabase;

import java.io.IOException;

public class Test {
    public static void main(String[] args) throws IOException {
        ProductDatabase database = ProductDatabase.getInstance();
        database.crawl();
    }
}
