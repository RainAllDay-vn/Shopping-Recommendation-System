//package com.project.shoppingrecommendationsystem.controller;
//import  com.project.shoppingrecommendationsystem.models.Product;
//
//import javafx.fxml.FXML;
//import javafx.fxml.FXMLLoader;
//import javafx.scene.layout.GridPane;
//import javafx.scene.layout.AnchorPane;
//
//import java.io.IOException;
//import java.util.List;
//
//public class ProductGridController {
//
//    @FXML
//    private GridPane productGrid;
//
//    public void initialize() {
//        loadProducts();
//    }
//
//    private void loadProducts() {
//        List<Product> products = DatabaseManager.getAllProducts();
//        int column = 0, row = 0;
//
//        for (Product product : products) {
//            try {
//                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/product_card.fxml"));
//                AnchorPane productCard = loader.load();
//
//                ProductCardController cardController = loader.getController();
//                cardController.setProduct(product);
//
//                productGrid.add(productCard, column, row);
//
//                column++;
//                if (column == 4) { // 4 products per row
//                    column = 0;
//                    row++;
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//}
