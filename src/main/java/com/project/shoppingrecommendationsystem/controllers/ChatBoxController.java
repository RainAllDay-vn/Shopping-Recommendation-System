package com.project.shoppingrecommendationsystem.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class ChatBoxController {

    @FXML
    private VBox chatContainer;

    @FXML
    private ListView<String> chatListView;

    @FXML
    private TextField chatInput;

    @FXML
    private Button sendButton;

    @FXML
    private Button chatToggleButton;

    @FXML
    public void toggleChat() {
        chatContainer.setVisible(!chatContainer.isVisible());
    }

    @FXML
    public void sendMessage() {
        String message = chatInput.getText().trim();
        if (!message.isEmpty()) {
            chatListView.getItems().add(message);
            chatInput.clear();
        }
    }
}
