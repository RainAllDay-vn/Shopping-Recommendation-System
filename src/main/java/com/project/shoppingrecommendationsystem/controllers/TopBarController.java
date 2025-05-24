// package com.project.shoppingrecommendationsystem.controllers;

// import com.project.shoppingrecommendationsystem.Messenger;
// import com.project.shoppingrecommendationsystem.ShoppingApplication;
// import javafx.fxml.FXML;
// import javafx.fxml.Initializable;
// import javafx.scene.control.Button;
// import javafx.scene.control.Label;
// import javafx.scene.control.TextField;
// import javafx.scene.image.Image;
// import javafx.scene.image.ImageView;

// import java.net.URL;
// import java.util.List;
// import java.util.Objects;
// import java.util.ResourceBundle;

// public class TopBarController implements Initializable {
//     private final List<String[]> query = Messenger.getInstance().getQuery();
//     private final Image appLogo = new Image(Objects.requireNonNull(ShoppingApplication.class.getResource("images/app-icon.jpg")).toExternalForm());
//     @FXML private TextField searchTextField;
//     @FXML private Button searchButton;
//     @FXML private Button goToList;
//     @FXML private ImageView logo;
//     @FXML private Label titleLabel;

//     @Override
//     public void initialize (URL url, ResourceBundle resourceBundle) {
//         searchButton.setOnAction(event -> search());
//         searchTextField.setOnAction(event -> search());
//         goToList.setOnAction(e -> Messenger.getInstance().getMainPageController().displayMyList());
//         logo.setImage(appLogo);
//         logo.setOnMouseClicked(e -> Messenger.getInstance().getMainPageController().displayMain());
//         titleLabel.setOnMouseClicked(e -> Messenger.getInstance().getMainPageController().displayMain());
//     }

//     private void search(){
//         if (searchTextField.getText().isBlank()) {
//             removeSearchFromQuery();
//         } else {
//             addSearchToQuery(searchTextField.getText());
//         }
//     }

//     private void addSearchToQuery (String searchText) {
//         removeSearchFromQuery();
//         query.add(new String[]{"name", "contain", searchText});
//     }

//     private void removeSearchFromQuery () {
//         for (int i = 0; i < query.size(); i++) {
//             if (query.get(i)[0].equals("name")){
//                 query.remove(i);
//                 return;
//             }
//         }
//     }
// }




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
    private final Image appLogo = new Image(Objects.requireNonNull(ShoppingApplication.class.getResource("images/app-icon.jpg")).toExternalForm());
    
    @FXML private TextField searchTextField;
    @FXML private Button searchButton;
    @FXML private Button goToList;
    @FXML private Button backButton;
    @FXML private Button homeButton;
    @FXML private Button resetButton;
    @FXML private ImageView logo;
    @FXML private Label titleLabel;

    @Override
    public void initialize (URL url, ResourceBundle resourceBundle) {
        // Existing functionality
        searchButton.setOnAction(event -> search());
        searchTextField.setOnAction(event -> search());
        goToList.setOnAction(e -> Messenger.getInstance().getMainPageController().displayMyList());
        logo.setImage(appLogo);
        logo.setOnMouseClicked(e -> Messenger.getInstance().getMainPageController().displayMain());
        titleLabel.setOnMouseClicked(e -> Messenger.getInstance().getMainPageController().displayMain());
        
        // New functionality for navigation buttons
        backButton.setOnAction(e -> goBack());
        homeButton.setOnAction(e -> goHome());
        resetButton.setOnAction(e -> resetSearch());
    }

    private void search(){
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
    
    // New methods for navigation buttons
    private void goBack() {
        // Implement back functionality - you might need to track navigation history
        // For now, this goes to the main page as a fallback
        Messenger.getInstance().getMainPageController().displayMain();
        
        // Alternative implementation if you have navigation history:
        // NavigationManager.getInstance().goBack();
    }
    
    private void goHome() {
        // Navigate to home/main page
        Messenger.getInstance().getMainPageController().displayMain();
    }
    
    private void resetSearch() {
        // Clear search field and remove search from query
        searchTextField.clear();
        removeSearchFromQuery();
        
        // Optionally refresh the current view or reset all filters
        // You might want to add more reset functionality here depending on your app's needs
        query.clear(); // This clears all filters, adjust as needed
        
        // Refresh the current view to show all products
        // Assuming you have a method to refresh the product display
        // Messenger.getInstance().getMainPageController().refreshProductDisplay();
    }
}