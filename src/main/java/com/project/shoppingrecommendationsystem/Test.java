package com.project.shoppingrecommendationsystem;

import com.project.shoppingrecommendationsystem.models.database.ListDatabase;
import com.project.shoppingrecommendationsystem.models.database.ProductDatabase;

import java.io.IOException;

public class Test {
    public static void main(String[] args) throws IOException {
        ProductDatabase productDatabase = new ListDatabase();
        productDatabase.crawl();
    }
}
