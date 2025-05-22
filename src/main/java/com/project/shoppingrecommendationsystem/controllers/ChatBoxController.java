package com.project.shoppingrecommendationsystem.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.Initializable;

import com.project.shoppingrecommendationsystem.ShoppingApplication;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

public class ChatBoxController implements Initializable {

    @FXML
    private TextField chatInput;
    @FXML
    private Button sendButton;
    @FXML
    private ListView<HBox> chatListView;

    private Image userImage;
    private Image botImage;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        URL userImageURL = ShoppingApplication.class.getResource("images/user-icon.jpg");
        URL botImageURL = ShoppingApplication.class.getResource("images/chat-bot-icon.png");
        try {
            assert userImageURL != null;
            userImage = new Image(userImageURL.toExternalForm());
            assert botImageURL != null;
            botImage = new Image(botImageURL.toExternalForm());
        } catch (Exception e) {
            System.err.println("[ERROR] : Can not load user or bot chat icon.");
        }
        sendButton.setOnAction(event -> askChatBox(chatInput.getText()));
        chatInput.setOnAction(event -> askChatBox(chatInput.getText()));
    }

    private void askChatBox(String message) {
        if (message == null || message.trim().isEmpty()) return;

        chatInput.clear(); // Clear input after sending

        HBox userMessageBox = new HBox(10);
        userMessageBox.setStyle("-fx-padding: 5;");
        userMessageBox.setPrefWidth(chatListView.getPrefWidth());
        userMessageBox.setMaxWidth(Double.MAX_VALUE);

        Text userText = new Text(message);
        userText.wrappingWidthProperty().set(180);

        HBox userBubble = new HBox(userText);
        userBubble.setStyle("-fx-background-color: #DCF8C6; -fx-background-radius: 10; -fx-padding: 8;");
        userBubble.setMaxWidth(200);

        Region spacer = new Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        userMessageBox.getChildren().addAll(spacer, userBubble, createChatIcon(userImage)); // Right-aligned
        chatListView.getItems().add(userMessageBox);

        HBox botMessageBox = new HBox(10);
        botMessageBox.setStyle("-fx-padding: 5;");
        botMessageBox.setPrefWidth(chatListView.getPrefWidth());
        botMessageBox.setMaxWidth(Double.MAX_VALUE);

        Text botText = new Text("Here's a sample reply.");
        botText.wrappingWidthProperty().set(180);

        HBox botBubble = new HBox(botText);
        botBubble.setStyle("-fx-background-color: #F1F0F0; -fx-background-radius: 10; -fx-padding: 8;");
        botBubble.setMaxWidth(200);

        botMessageBox.getChildren().addAll(createChatIcon(botImage), botBubble); // Left-aligned
        chatListView.getItems().add(botMessageBox);
    }


    private ImageView createChatIcon(Image image) {
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(40);
        imageView.setFitHeight(40);
        imageView.setPreserveRatio(true);

        Circle clip = new Circle(20, 20, 20);
        imageView.setClip(clip);

        return imageView;
    }

}