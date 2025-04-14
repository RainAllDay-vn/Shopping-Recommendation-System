package com.project.shoppingrecommendationsystem;


import com.project.shoppingrecommendationsystem.models.ProductDatabase;

public class Test {
    public static void main(String[] args) {
        ProductDatabase database = ProductDatabase.getInstance();
        System.out.println(database.findAllLaptops().size());
        database.saveLaptops();
    }
}
