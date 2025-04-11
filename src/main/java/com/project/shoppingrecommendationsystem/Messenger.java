package com.project.shoppingrecommendationsystem;

import com.project.shoppingrecommendationsystem.models.Product;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Messenger {
    public static Messenger instance = new Messenger();
    private final ObservableList<String[]> query = FXCollections.observableArrayList();

    private Messenger() {}

    public static Messenger getInstance() {
        return instance;
    }

    public ObservableList<String[]> getQuery() {
        return query;
    }
}
