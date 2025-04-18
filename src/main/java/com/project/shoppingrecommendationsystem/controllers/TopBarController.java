package com.project.shoppingrecommendationsystem.controllers;

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
    }
}
