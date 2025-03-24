package com.project.shoppingrecommendationsystem.models;

import java.util.Random;

public class Laptop extends Product {

    public Laptop(int id, String name, String description, String productImage, int price, String sourceURL) {
        super(id, name, description, productImage, price, sourceURL);
    }

    public static Laptop buildTestLaptop() {
        String[] testImageUrl = {
            "https://i.pinimg.com/736x/81/88/ac/8188ace6b51eec6cbec3d5742a9d0f11.jpg",
            "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRr8Z6YdkgBwp9o_wqnPB5zJovemcaEVet2Bw&s",
            "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRPns1krHVPeqmjNrtBM2WyzEHOlohHua7Leg&s",
            "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcS7Fh72KCZ173goJ_6D1MVYn6gQYzn18eGuXg&s"
        };
        int rando = (int)(Math.random() * 3);
        int id = (int)(Math.random()*100);
        String name = "laptop #" + id;
        String description = "laptop #%d's description".formatted(id);
        String productImage = testImageUrl[rando];
        int price = (int)(Math.random()*100)*100000;
        String sourceURL = "https://laptop%d".formatted(id);
        return new Laptop(id, name, description, productImage, price, sourceURL);
    }
}
