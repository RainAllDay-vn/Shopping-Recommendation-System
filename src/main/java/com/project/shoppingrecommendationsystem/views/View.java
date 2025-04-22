package com.project.shoppingrecommendationsystem.views;

import com.project.shoppingrecommendationsystem.ShoppingApplication;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.FlowPane;

import java.io.IOException;
import java.util.Objects;

public abstract class View {
    Node root;
    FXMLLoader fxmlLoader;

    Node load (String path) {
        try {
            fxmlLoader = new FXMLLoader(Objects.requireNonNull(ShoppingApplication.class.getResource(path)));
            return fxmlLoader.load();
        } catch (IOException e) {
            System.err.println("[ERROR] Could not load " + path);
            System.out.println(e.getMessage());
            e.printStackTrace();
        } catch (NullPointerException e) {
            System.err.println("[ERROR] Could not find " + path) ;
            System.out.println(e.getMessage());
        }
        return new FlowPane();
    }

    public Node getRoot() {
        return root;
    }

    public Scene getScene () {
        return root.getScene();
    }

    public FXMLLoader getFxmlLoader() {
        if(fxmlLoader == null) System.out.println("[ERROR] FXMLLoader is null");
        return fxmlLoader;
    }
}
