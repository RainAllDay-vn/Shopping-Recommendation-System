package com.project.shoppingrecommendationsystem.views;

import com.project.shoppingrecommendationsystem.controllers.ChatBoxController;

public class ChatBox extends View<ChatBoxController> {
    public ChatBox() {
        root = load("components/chat-box.fxml", new ChatBoxController());
    }
}
