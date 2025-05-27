package com.project.shoppingrecommendationsystem.models;

import com.project.shoppingrecommendationsystem.models.components.Review;

import java.util.Arrays;
import java.util.List;

public abstract class Product {
    private static int counter = 0;
    final int id;
    final String name;
    final String productImage;
    final int price;
    final int discountPrice;
    final List<Review> reviews;
    final String source;
    final String sourceURL;

    final String brand;
    final String color;
    final List<String[]> description;

    Product(String name, String productImage, int price, int discountPrice, List<Review> reviews, String source, String sourceURL, String brand, String color, List<String[]> description) {
        this.id = ++counter;
        this.name = name;
        this.productImage = productImage;
        this.price = price;
        this.discountPrice = discountPrice;
        this.reviews = reviews;
        this.source = source;
        this.sourceURL = sourceURL;
        this.brand = brand;
        this.color = color;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getProductImage() {
        return productImage;
    }

    public int getPrice() {
        return price;
    }

    public int getDiscountPrice() {
        if(discountPrice == 0) return price;
        return discountPrice;
    }

    public String getSource() {
        return source;
    }

    public String getSourceURL() {
        return sourceURL;
    }

    public List<Review> getReviews() {return reviews;}

    public String getBrand() {
        return brand;
    }

    public String getColor() {
        return color;
    }

    public List<String[]> getDescription() {
        return description;
    }

    public boolean match (List<String[]> query) {
        try {
            for (String[] field : query) {
                String[] copy = Arrays.copyOf(field, field.length);
                copy[0] = switch (copy[0]) {
                    case "name" -> name;
                    case "brand" -> brand;
                    case "price" -> String.valueOf(discountPrice);
                    case "vendor" -> source;
                    case "type" -> this.getClass().getSimpleName();
                    case "description" -> {
                        StringBuilder compactedDescription = new StringBuilder();
                        for (String[] paragraph: description) {
                            compactedDescription.append(paragraph[0]);
                            compactedDescription.append(" ");
                        }
                        yield compactedDescription.toString();
                    }
                    default -> "false";
                };
                if (copy[0].equals("false") || !matchField(copy)) {
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            System.err.println("Invalid query");
            return false;
        }
    }

    boolean matchField(String[] field) {
        try {
            return switch (field[1].toLowerCase()) {
                case "contain" -> field[0].toLowerCase().contains(field[2].toLowerCase());
                case "in" -> {
                    for (int i = 2; i < field.length; i++) {
                        if (field[i].equalsIgnoreCase(field[0])) {
                            yield true;
                        }
                    }
                    yield false;
                }
                case "between" -> {
                    int lower = field[2].isEmpty() ? 0 : Integer.parseInt(field[2]);
                    int upper = field[3].isEmpty() ? Integer.MAX_VALUE : Integer.parseInt(field[3]);
                    int value = Integer.parseInt(field[0]);
                    yield lower <= value && value <= upper;
                }
                default -> false;
            };
        } catch (Exception e) {
            System.err.println("Invalid query");
            return false;
        }
    }
}