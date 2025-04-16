package com.project.shoppingrecommendationsystem.controllers;

<<<<<<< HEAD
import com.project.shoppingrecommendationsystem.Messenger;
=======
<<<<<<< HEAD
import com.project.shoppingrecommendationsystem.ShoppingApplication;
>>>>>>> d828c32cb350deece54358bda09faf9f6a7b28c1
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class TopBarController implements Initializable {
    private final List<String[]> query = Messenger.getInstance().getQuery();
    @FXML
<<<<<<< HEAD
=======
    public void initialize() {
        goToList.setOnAction(e -> MainPageController.getInstance().displayMyList());
        logo.setImage(appLogo);
        logo.setOnMouseClicked(e -> MainPageController.getInstance().displayMain());
        titleLabel.setOnMouseClicked(e -> MainPageController.getInstance().displayMain());
=======
import com.project.shoppingrecommendationsystem.Messenger;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class TopBarController implements Initializable {
    private final List<String[]> query = Messenger.getInstance().getQuery();
    @FXML
>>>>>>> d828c32cb350deece54358bda09faf9f6a7b28c1
    private TextField searchTextField;
    @FXML
    private Button searchButton;

    @Override
    public void initialize (URL url, ResourceBundle resourceBundle) {
        searchButton.setOnAction(event -> {
            if (searchTextField.getText().isBlank()) {
                removeSearchFromQuery();
            } else {
                addSearchToQuery(searchTextField.getText());
            }
        });
    }

    private void addSearchToQuery (String searchText) {
        removeSearchFromQuery();
        query.add(new String[]{"name", "contain", searchText});
    }

    private void removeSearchFromQuery () {
        for (int i = 0; i < query.size(); i++) {
            if (query.get(i)[0].equals("name")){
                query.remove(i);
                return;
            }
        }
<<<<<<< HEAD
=======
>>>>>>> 3fb4f8371ec22213294e88dd2c87cd5ea4e8b321
>>>>>>> d828c32cb350deece54358bda09faf9f6a7b28c1
    }
}
