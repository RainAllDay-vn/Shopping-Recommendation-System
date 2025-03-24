package com.project.shoppingrecommendationsystem.models;

import java.util.LinkedHashMap;

public class Product {

    private final int id;
    private final String name;
    private final String description;
    private final String productImage;
    private final int price;
    private final String sourceURL;
    private final LinkedHashMap<String, String> hardware;

    // Default constructor example
    public Product(){
        this.id = 0;
        this.name = "Lenovo Laptop";
        this.description = "THis is a product";
        this.productImage = "/com/project/shoppingrecommendationsystem/app-icon.jpg";
        this.price = 0;
        this.sourceURL = "https://open.spotify.com/";
        this.hardware = new LinkedHashMap<>();
    }

    public Product(int id, String name, String description, String productImage, int price, String sourceURL) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.productImage = productImage;
        this.price = price;
        this.sourceURL = sourceURL;
        this.hardware = new LinkedHashMap<>();
    }

    public void updateHardware(String key, String value) {
        hardware.put(key, value);
    }

    public LinkedHashMap<String, String> getOverview() {
        LinkedHashMap<String, String> overview = new LinkedHashMap<>();
        overview.put("id", String.valueOf(id));
        overview.put("name", name);
        overview.put("description", description);
        overview.put("productImage", productImage);
        overview.put("price", String.valueOf(price));
        overview.put("sourceURL", String.valueOf(sourceURL));
        return overview;
    }

    public LinkedHashMap<String, String> getHardware() {
        return new LinkedHashMap<>(hardware);
    }

    public String getDescription() {
        return description;
    }

    /**
     * Evaluates whether the given query conditions match the object's attributes.
     *
     * @param query A 2D String array where each sub-array represents a constraint.
     *              - The first element of each constraint is the attribute name.
     *              - The second element is the operator (e.g., "in", "between").
     *              - The remaining elements are values used for comparison.
     * @return {@code true} if all constraints match the object's attributes, {@code false} otherwise.
     */
    public boolean match (String[][] query) {
        try {
            for (String[] constraint : query) {
                constraint[0] = switch (constraint[0]) {
                    case "name" -> name;
                    case "price" -> String.valueOf(price);
                    default -> hardware.getOrDefault(constraint[0], "");
                };
                if (constraint[0].isEmpty() || !matchField(constraint)) {
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Evaluates whether a single constraint matches the given field value.
     *
     * @param constraint A String array where:
     *                   - constraint[0] is the field value.
     *                   - constraint[1] is the operator ("in" or "between").
     *                   - Remaining elements are values for comparison.
     * @return {@code true} if the constraint is satisfied, {@code false} otherwise.
     */
    private boolean matchField (String[] constraint) {
        try {
            switch (constraint[1]) {
                case "in":
                    for (int i = 2; i < constraint.length; i++) {
                        if (constraint[i].equals(constraint[0])) return true;
                    }
                    return false;
                case "between":
                    int number = Integer.parseInt(constraint[0]);
                    int lower = Integer.parseInt(constraint[2]);
                    int upper = Integer.parseInt(constraint[3]);
                    return lower <= number && number <= upper;
                default:
                    return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    
}
