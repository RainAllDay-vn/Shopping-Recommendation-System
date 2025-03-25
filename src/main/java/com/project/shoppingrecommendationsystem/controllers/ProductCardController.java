package com.project.shoppingrecommendationsystem.controllers;

import com.project.shoppingrecommendationsystem.ShoppingApplication;
import com.project.shoppingrecommendationsystem.models.Product;
import com.project.shoppingrecommendationsystem.views.ProductPageController;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;


public class ProductCardController {

     @FXML
     private VBox productCardContainer;

     @FXML
     private ImageView productImage;

     @FXML
     private Label productName;

     @FXML
     private Label productPrice;

     @FXML
     private Label productDiscount;

     @FXML
     private Button showDetailsButton;

     private Product product;

     @FXML
     public void goToProductPage() {
          if (this.product != null) {
               NavigationController.push(ShoppingApplication.productPage);
               ((ProductPageController)ShoppingApplication.productPage.getController()).setProduct(product);
          }
     }

     @FXML
     public void addToList() {
          System.out.println("Add to list" + this.product.getOverview());
     }

     public void setProduct(Product product) {
          this.product = product;

          Task<Void> setProductTask = new Task<Void>() {
               @Override
               protected Void call() throws Exception {
                    if (product != null) {
                         Image img;
                         try{
                              img = new Image(product.getOverview().get("productImage"));
                         }catch(Exception e){
                              img = new Image(getClass().getResource("/com/project/shoppingrecommendationsystem/app-icon.jpg").toExternalForm());
                              System.out.println("Cant load image " + product.getOverview().get("productImage"));
                         }
                         final Image image = img;
                         Platform.runLater(()->{
                              productImage.setImage(image);
                              productName.setText(product.getOverview().get("name"));
                              productPrice.setText("Price: " + product.getOverview().get("price"));
                              productDiscount.setText("Discount: " + product.getOverview().get("discount"));
                         });
                    }    
                    return null;
               }   
          };
          Thread thread = new Thread(setProductTask);
          thread.setDaemon(true);
          try{
               thread.start();  
          }catch(Exception e){
               System.err.println(e);
          }
     }

}
