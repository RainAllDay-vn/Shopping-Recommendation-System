package com.project.shoppingrecommendationsystem.controllers;

import java.util.List;

import com.project.shoppingrecommendationsystem.models.Product;
import com.project.shoppingrecommendationsystem.views.ProgramLayout;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class ProductPageController {

    @FXML
    private Text nameBox;

    @FXML
    private Text priceBox;

    @FXML
    private ImageView productImage;

    @FXML
    private VBox specBox;

    @FXML
    void onClickBack(ActionEvent event) {
        ProgramLayout.pop();
    }

    @FXML
    void onClickSeeWebsite(ActionEvent event) {

    }
    public void setProduct(Product product){
        Task<Void> setUpPageTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Platform.runLater(()->{
                    Image image = new Image(product.getOverview().get("productImage"));
                    productImage.setImage(image);
                    //productDiscount.setText("Discount: " + product.getOverview().get("discount"));
                });
                nameBox.setText(product.getOverview().get("name"));
                priceBox.setText(product.getOverview().get("price"));
                specBox.getChildren().clear();
                for(String key : product.getHardware().keySet()){
                    specBox.getChildren().add(new Text(key + " : " + product.getHardware().get(key)));
                }
                return null;
            }
            
        };
        new Thread(setUpPageTask).start();
    }

}