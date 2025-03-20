package com.project.shoppingrecommendationsystem.models;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

public class ProductDatabase {
    private final List<Laptop> laptops;

    public ProductDatabase() {
        laptops = new ArrayList<>();
    }

    public void loadTestLaptop() {
        for (int i = 0; i < 10; i++) {
            laptops.add(Laptop.buildTestLaptop());
        }
    }

    public List<Laptop> findAllLaptops() {
        return laptops;
    }

    public Optional<Laptop> findLaptopById(int id) {
        for (Laptop laptop : laptops) {
            LinkedHashMap<String, String> overview = laptop.getOverview();
            if (overview.getOrDefault("id", "").equals(String.valueOf(id))) {
                return Optional.of(laptop);
            }
        }
        return Optional.empty();
    }
}
