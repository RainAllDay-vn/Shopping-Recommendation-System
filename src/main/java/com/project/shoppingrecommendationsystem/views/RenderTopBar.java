package com.project.shoppingrecommendationsystem.views;

import com.project.shoppingrecommendationsystem.controllers.SearchController;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;

public class RenderTopBar extends HBox {

    private TextField searchTextField;
    private Button searchButton;
    private SearchController searchController;

    public RenderTopBar() {
        // Set HBox properties
        this.setPrefSize(1536, 77);
        this.setStyle("-fx-alignment: center;");

        // Logo
        ImageView logo = new ImageView();
        logo.setFitHeight(52);
        logo.setFitWidth(116);
        logo.setPreserveRatio(true);
        logo.setPickOnBounds(true);
        logo.setImage(new Image("file:src/main/resources/images/logo.png")); // Change path as needed

        // Website Title
        Label titleLabel = new Label("Shopping recommended system");
        titleLabel.setPrefSize(365, 78);
        titleLabel.setFont(new Font(22));

        // Search TextField
        searchTextField = new TextField();
        searchTextField.setPrefSize(525, 40);
        searchTextField.setPromptText("What product you have in mind today");

        // Search Button
        searchButton = new Button("Search");

        // Handle search action
        searchButton.setOnAction(e -> searchController.search(searchTextField.getText()));
        searchTextField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                searchController.search(searchTextField.getText());
            }
        });

        // Add components to HBox
        this.getChildren().addAll(logo, titleLabel, searchTextField, searchButton);
    }

}
