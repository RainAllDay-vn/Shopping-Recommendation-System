package com.project.shoppingrecommendationsystem;

import com.project.shoppingrecommendationsystem.models.database.ListDatabase;

import java.io.IOException;

public class Test {
    public static void main(String[] args) throws IOException {
        ListDatabase listDatabase = new ListDatabase();
        listDatabase.crawl();
    }
}