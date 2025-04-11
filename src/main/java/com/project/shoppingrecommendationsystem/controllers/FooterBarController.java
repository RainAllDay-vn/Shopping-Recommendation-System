package com.project.shoppingrecommendationsystem.controllers;

<<<<<<< HEAD
=======
import javafx.fxml.FXML;
import javafx.scene.control.Button;

>>>>>>> hieu/4-design-homepage
import java.awt.*;
import java.net.URI;

public class FooterBarController {

<<<<<<< HEAD
    public void goToRepo(){
        String url = "https://github.com/RainAllDay-vn/Shopping-Recommendation-System/blob/4-design-a-homepage/src/main/java/com/project/shoppingrecommendationsystem/ShoppingApplication.java";
=======
    @FXML
    Button footerLabel;

    @FXML
    public void initialize() {
        footerLabel.setOnMouseClicked(event -> {goToRepo();});
    }

    public void goToRepo(){
        String url = "https://github.com/RainAllDay-vn/Shopping-Recommendation-System";
>>>>>>> hieu/4-design-homepage
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
