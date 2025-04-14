package com.project.shoppingrecommendationsystem.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.Initializable;

import com.project.shoppingrecommendationsystem.ShoppingApplication;
import com.project.shoppingrecommendationsystem.models.chatbots.ChatBot;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

public class ChatBoxController implements Initializable {
    @FXML
    private ListView<Message> chatListView;
    @FXML
    private TextField chatInput;
    @FXML
    private Button sendButton;

    private Image userImage;
    private Image botImage;

    public ChatBoxController() {
        URL userImageURL = ShoppingApplication.class.getResource("images/user-icon.png");
        URL botImageURL = ShoppingApplication.class.getResource("images/bot-icon.png");
        try {
            assert userImageURL != null;
            userImage = new Image(userImageURL.toExternalForm());
            assert botImageURL != null;
            botImage = new Image(botImageURL.toExternalForm());
        } catch (Exception e) {
            System.err.println("[ERROR] : Can not load user or bot chat icon.");
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        chatListView.setCellFactory(lv -> new MessageCell());
    }

    @FXML
    public void sendMessage() {
        String messageText = chatInput.getText().trim();
        if (!messageText.isEmpty()) {
            chatListView.getItems().add(new Message(messageText, true));
            chatInput.clear();
            System.out.println("[INFO] : Start Sending");
            Thread thread = new Thread(() -> {
                String botReply = ChatBot.prompt(messageText);
                Platform.runLater(() -> chatListView.getItems().add(new Message(botReply, false)));
            });
            thread.start();
        }
    }

    private record Message(String text, boolean fromUser) {}

    private class MessageCell extends ListCell<Message> {
        private final HBox container = new HBox();
        private final ImageView avatar = new ImageView();
        private final Text messageText = new Text();

        public MessageCell() {
            container.setSpacing(10);
            avatar.setFitHeight(30);
            avatar.setFitWidth(30);
            avatar.setPreserveRatio(true);
            messageText.wrappingWidthProperty().bind(
                    chatListView.widthProperty().subtract(60));
        }

        @Override
        protected void updateItem(Message message, boolean empty) {
            super.updateItem(message, empty);
            if (empty || message == null) {
                setGraphic(null);
            } else {
                if (message.fromUser) {
                    avatar.setImage(userImage);
                    container.getChildren().setAll(messageText, avatar);
                    container.setStyle("-fx-alignment: center-right;");
                } else {
                    avatar.setImage(botImage);
                    container.getChildren().setAll(avatar, messageText);
                    container.setStyle("-fx-alignment: center-left;");
                }
                messageText.setText(message.text);
                setGraphic(container);
            }
        }
    }
}