package com.project.shoppingrecommendationsystem.controllers;

import com.project.shoppingrecommendationsystem.Messenger;
import com.project.shoppingrecommendationsystem.ShoppingApplication;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class TopBarController implements Initializable {
    private final List<String[]> query = Messenger.getInstance().getQuery();
    private final Image appLogo = new Image(Objects.requireNonNull(ShoppingApplication.class.
            getResource("images/app-icon.jpg")).toExternalForm());
    @FXML
    private TextField searchTextField;
    @FXML
    private Button searchButton;
    @FXML
    private Button goToList;
    @FXML
    private ImageView logo;
    @FXML
    private Label titleLabel;

    @Override
    public void initialize (URL url, ResourceBundle resourceBundle) {
        searchButton.setOnAction(event -> search());
        searchTextField.setOnAction(event -> search());
        goToList.setOnAction(e -> Messenger.getInstance().getMainPageController().displayMyList());
        logo.setImage(appLogo);
        logo.setOnMouseClicked(e -> Messenger.getInstance().getMainPageController().displayMain());
        titleLabel.setOnMouseClicked(e -> Messenger.getInstance().getMainPageController().displayMain());
    }

    private void search(){
        Messenger.getInstance().getMainPageController().displayMain();
        if (searchTextField.getText().isBlank()) {
            removeSearchFromQuery();
        } else {
            addSearchToQuery(searchTextField.getText());
        }
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