package com.project.shoppingrecommendationsystem.controllers;

import com.project.shoppingrecommendationsystem.views.RenderChatBox;
import com.project.shoppingrecommendationsystem.views.RenderProductGrid;
import com.project.shoppingrecommendationsystem.views.RenderTopBar;
import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;

public class MainPage {

    @FXML
    private BorderPane rootPane; // Injected from FXML

    public void initialize() {
        // Add components dynamically

        // Top Bar
        RenderTopBar topBar = new RenderTopBar();
        rootPane.setTop(topBar);

        // Product Grid (Pass actual product list)
        RenderProductGrid productGrid ;

        // Chat Box
        RenderChatBox chatBox = new RenderChatBox();
        rootPane.setRight(chatBox);
    }
}
