package com.project.shoppingrecommendationsystem.controllers;

import com.project.shoppingrecommendationsystem.models.Laptop;
import com.project.shoppingrecommendationsystem.models.ProductDatabase;
import com.project.shoppingrecommendationsystem.views.ProductCard;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class ProductGridController implements Initializable {
    private final ProductDatabase productDatabase;
    private final ObservableList<Laptop> laptops;
    int currentCellCount = 0;
    private static final int PRODUCT_PER_PAGE = 5;
    private static final int COLUMNS = 4;

    @FXML
    private VBox rootVBox;

    @FXML
    private FlowPane flowPane;

    @FXML
    private Button expandButton;

    public ProductGridController() {
        this.productDatabase = ProductDatabase.getInstance();
        this.laptops = FXCollections.observableArrayList(productDatabase.findAllLaptops(12, 0));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        for (Laptop laptop : laptops) {
            flowPane.getChildren().add(new ProductCard(laptop).getRoot());
        }
    }

//    public static void addProducts(List<Product> products){
//        instance.products = products;
//        int end = instance.currentCellCount + PRODUCT_PER_PAGE;
//        if(end >= products.size()){
//            end = products.size();
//            instance.expandButton.setDisable(true);
//        }
//        else{
//            instance.expandButton.setDisable(false);
//        }
//        instance.addProductCard(products.subList(instance.currentCellCount, end));
//    }
//
//
//
//    void addProductCard(List<Product> products){
//        Task<Void> addProductsTask = new Task<Void>() {
//            @Override
//            protected Void call() throws Exception {
//                for(Product product : products){
//                    VBox productCard = createProductCard(product);
//                    final int cellCount = currentCellCount;
//                    Platform.runLater(()->
//                        gridPane.add(productCard,cellCount%COLUMNS,cellCount/COLUMNS)
//                    );
//                    currentCellCount++;
//                }
//                return null;
//            }
//        };
//        new Thread(addProductsTask).start();
//    }

//    public static void clear(){
//        instance.gridPane.getChildren().clear();
//        instance.currentCellCount = 0;
//    }
//
//    @FXML
//    private void expandProducts() {
//        int end = instance.currentCellCount + PRODUCT_PER_PAGE;
//        if(end >= products.size()){
//            end = products.size();
//            expandButton.setDisable(true);
//        }
//        addProductCard(products.subList(instance.currentCellCount, end));
//    }
//
//    @FXML
//    public void initialize(){
//        instance =  this;
//    }
//
//    private static VBox  createProductCard(Product product) {
//        try {
//            FXMLLoader card = new FXMLLoader(ProductGridController.class.getResource
//                    ("/com/project/shoppingrecommendationsystem/components/product-card.fxml"));
//            VBox productCard = card.load();
//            ProductCardController controller = card.getController();
//            controller.setProduct(product);
//            return productCard;
//        } catch (IOException e) {
//            e.printStackTrace();
//            return null; // Return null instead of an empty VBox for better error handling
//        }
//    }
}
