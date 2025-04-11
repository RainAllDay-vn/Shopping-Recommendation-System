package com.project.shoppingrecommendationsystem.controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URL;
import java.util.concurrent.TimeoutException;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.openqa.selenium.internal.Debug;

import com.project.shoppingrecommendationsystem.ShoppingApplication;
import com.project.shoppingrecommendationsystem.models.chatbots.ChatBot;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class ChatBoxController {

    @FXML
    private VBox chatContainer;
    @FXML
    private ListView<Message> chatListView;
    @FXML
    private TextField chatInput;
    @FXML
    private ImageView chatBotLogo;
    @FXML
    private Button sendButton;

    private final Image userImage = new Image(
            ShoppingApplication.class.getResource("images/user-icon.jpg").toExternalForm());
    private final Image botImage = new Image(
            ShoppingApplication.class.getResource("images/chat-bot-icon.png").toExternalForm());
    public static class Message {
        private final String text;
        private final String sender;

        public Message(String text, String sender) {
            this.text = text;
            this.sender = sender;
        }

        public String getText() {
            return text;
        }

        public String getSender() {
            return sender;
        }
    }

    @FXML
    public void initialize() {
        chatBotLogo.setOnMouseClicked(event -> {
            toggleChat();
        });
        chatInput.setOnAction(event -> sendMessage());
        sendButton.setOnAction(event -> sendMessage());
        chatBotLogo.setImage(botImage);
        chatListView.setCellFactory(lv -> new MessageCell());
    }

    public void toggleChat() {
        chatContainer.setVisible(!chatContainer.isVisible());
    }

    public void sendMessage() {
        String messageText = chatInput.getText().trim();
        if (!messageText.isEmpty()) {
            chatListView.getItems().add(new Message(messageText, "user"));
            chatInput.clear();
            System.out.println("Start Sending");
            final var chatList = chatListView;
            Task retreiveLLMAnswer = new Task<Void>() {

                @Override
                protected Void call() throws Exception {
                    String botReply = ChatBot.prompt(messageText);
            
                    Platform.runLater(() -> {
                        // Remove "Bot is typing..." message (if it exists)
                        chatList.getItems().removeIf(msg -> msg.getText().equals("Bot is typing..."));
            
                        // Add the bot's response to the chat list
                        chatList.getItems().add(new Message(botReply, "bot"));
                    });
                    return null;
                }

            };
            Thread th = new Thread(retreiveLLMAnswer);
            th.start();
        }
    }

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
                if ("user".equals(message.getSender())) {
                    avatar.setImage(userImage);
                    container.getChildren().setAll(messageText, avatar);
                    container.setStyle("-fx-alignment: center-right;");
                } else {
                    avatar.setImage(botImage);
                    container.getChildren().setAll(avatar, messageText);
                    container.setStyle("-fx-alignment: center-left;");
                }

                messageText.setText(message.getText());
                setGraphic(container);
            }
        }
    }
}