package com.project.shoppingrecommendationsystem;

import com.project.shoppingrecommendationsystem.controllers.MainPageController;
import com.project.shoppingrecommendationsystem.models.database.ListDatabase;
import com.project.shoppingrecommendationsystem.models.database.ProductDatabase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Messenger {
    private MainPageController mainPageController;
    public static Messenger instance = new Messenger();
    private final ObservableList<String[]> query = FXCollections.observableArrayList();
    private final ProductDatabase productDatabase;

    private Messenger() {
        productDatabase = new ListDatabase();
    }

    public void setMainPageController(MainPageController mainPageController) {
        this.mainPageController = mainPageController;
    }

    public MainPageController getMainPageController() {
        return mainPageController;
    }

    public static Messenger getInstance() {
        return instance;
    }

    public ObservableList<String[]> getQuery() {
        return query;
    }

    public ProductDatabase getProductDatabase() {
        return productDatabase;
    }
}
