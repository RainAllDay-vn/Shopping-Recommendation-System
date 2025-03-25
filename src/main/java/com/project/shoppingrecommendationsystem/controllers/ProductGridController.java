package com.project.shoppingrecommendationsystem.controllers;

import com.project.shoppingrecommendationsystem.models.Product;
import com.project.shoppingrecommendationsystem.models.Laptop;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.List;

public class ProductGridController {

    static ProductGridController instance;

    @FXML
    private VBox rootVBox;

    @FXML
    private GridPane gridPane;

    @FXML
    private Button expandButton;

    int currentCellCount = 0;
    private static final int PRODUCT_PER_PAGE = 5;
    private static final int COLUMNS = 5;
    List<Product> products;

    public static void addProducts(List<Product> products){
        instance.products = products;
        int end = products.size()< instance.currentCellCount + PRODUCT_PER_PAGE ? products.size() : instance.currentCellCount +PRODUCT_PER_PAGE;
        instance.addProductCard(products.subList(instance.currentCellCount, end));
    }

    

    void addProductCard(List<Product> products){
        Task<Void> addProductsTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                for(Product product : products){
                    VBox productCard = createProductCard(product);
                    final int cellCount = currentCellCount;
                    Platform.runLater(()->
                        gridPane.add(productCard,cellCount%COLUMNS,cellCount/COLUMNS)
                    );
                    currentCellCount++;
                }       
                return null;
            }
        };
        new Thread(addProductsTask).start();
    }

    public static void clear(){
        instance.gridPane.getChildren().clear();
        instance.currentCellCount = 0;
    }

    @FXML
    private void expandProducts() {
        int end = products.size()< instance.currentCellCount + PRODUCT_PER_PAGE ? products.size() : instance.currentCellCount + PRODUCT_PER_PAGE;
        addProductCard(products.subList(instance.currentCellCount, end));
    }

    @FXML
    public void initialize(){
        instance =  this;
    }

    private static VBox  createProductCard(Product product) {
        try {
            FXMLLoader card = new FXMLLoader(ProductGridController.class.getResource
                    ("/com/project/shoppingrecommendationsystem/components/product-card.fxml"));
            VBox productCard = card.load();
            ProductCardController controller = card.getController();
            controller.setProduct(product);
            return productCard;
        } catch (IOException e) {
            e.printStackTrace();
            return null; // Return null instead of an empty VBox for better error handling
        }
    }
}
