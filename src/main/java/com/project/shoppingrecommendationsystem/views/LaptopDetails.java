package com.project.shoppingrecommendationsystem.views;

import com.project.shoppingrecommendationsystem.controllers.LaptopDetailsController;
import com.project.shoppingrecommendationsystem.models.Laptop;
import javafx.scene.Node;

public class LaptopDetails extends View {
    public LaptopDetails(Laptop product, Node productCard) {
        root = load("components/laptop-details.fxml");
        getController().setProductDetails(product, productCard);
    }
    public LaptopDetailsController getController() {
        return getFxmlLoader().getController();
    }
}