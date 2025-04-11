package com.project.shoppingrecommendationsystem.controllers;

<<<<<<< HEAD
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
    private static final int COLUMNS = 4;
    List<Product> products;

    public static void addProducts(List<Product> products){
        instance.products = products;
        int end = instance.currentCellCount + PRODUCT_PER_PAGE;
        if(end >= products.size()){
            end = products.size();
            instance.expandButton.setDisable(true);
        }
        else{
            instance.expandButton.setDisable(false);
        }
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
        int end = instance.currentCellCount + PRODUCT_PER_PAGE;
        if(end >= products.size()){
            end = products.size();
            expandButton.setDisable(true);
        }
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
=======
import com.project.shoppingrecommendationsystem.Messenger;
import com.project.shoppingrecommendationsystem.models.Laptop;
import com.project.shoppingrecommendationsystem.models.ProductDatabase;
import com.project.shoppingrecommendationsystem.views.ProductCard;
import com.project.shoppingrecommendationsystem.views.ProductDetails;
import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;

import java.util.List;

public class ProductGridController {
    protected static final int PRODUCTS_PER_PAGE = 12;
    protected final ProductDatabase productDatabase;
    protected final ObservableList<Laptop> laptops;
    protected final ObservableList<String[]> query;
    protected InvalidationListener queryListener;

    @FXML private FlowPane flowPane;
    @FXML private Button expandButton;

    public ProductGridController() {
        this(Messenger.getInstance().getQuery());
    }

    public ProductGridController(ObservableList<String[]> query, ObservableList<Laptop> initialLaptops) {
        this.productDatabase = ProductDatabase.getInstance();
        this.query = query;
        this.laptops = FXCollections.observableArrayList(initialLaptops);
    }

    public ProductGridController(ObservableList<String[]> query) {
        this.productDatabase = ProductDatabase.getInstance();
        this.query = query;
        this.laptops = FXCollections.observableArrayList(fetchNextPage(0));
    }

    @FXML
    public void initialize() {
        updateProductCards();
        setupListeners();
        expandButton.setOnAction(event -> expand());
    }

    protected void setupListeners() {
        laptops.addListener((InvalidationListener) observable -> updateProductCards());

        queryListener = observable -> {
            laptops.clear();
            laptops.addAll(fetchNextPage(0));
        };
        query.addListener(queryListener);
    }

    protected void updateProductCards() {
        flowPane.getChildren().clear();
        laptops.forEach(laptop -> {
            ProductCard card = new ProductCard(laptop, event -> goToProductDetails(laptop));
            flowPane.getChildren().add(card.getRoot());
            }
        );
    }


    public void goToProductDetails(Laptop product) {
        ProductDetails productDetails = new ProductDetails(product);
        MainPageController.getInstance().displayDetails(productDetails.getRoot());
    }

    protected List<Laptop> fetchNextPage(int offset) {
        return productDatabase.findLaptops(query, PRODUCTS_PER_PAGE, offset);
    }

    public void expand() {
        List<Laptop> nextPage = fetchNextPage(laptops.size());
        laptops.addAll(nextPage);
    }

    protected void removeQueryListener() {
        query.removeListener(queryListener);
    }
}
>>>>>>> hieu/4-design-homepage
