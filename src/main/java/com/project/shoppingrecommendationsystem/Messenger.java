package com.project.shoppingrecommendationsystem;

import com.project.shoppingrecommendationsystem.controllers.MainPageController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Messenger {
    private MainPageController mainPageController;
    public static Messenger instance = new Messenger();
    private final ObservableList<String[]> query = FXCollections.observableArrayList();

    private Messenger() {}

    public void setMainPageController(MainPageController mainPageController) {
        this.mainPageController = mainPageController;
    }

    public static Messenger getInstance() {
        return instance;
    }

    public ObservableList<String[]> getQuery() {
        return query;
    }

    public MainPageController getMainPageController() {
        return mainPageController;
    }
}
