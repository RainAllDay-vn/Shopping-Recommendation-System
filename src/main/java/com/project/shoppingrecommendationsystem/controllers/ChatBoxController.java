package com.project.shoppingrecommendationsystem.controllers;

import com.project.shoppingrecommendationsystem.views.RenderChatBox;

public class ChatBoxController {

    private RenderChatBox chatBox;

    public ChatBoxController(RenderChatBox chatBox) {
        this.chatBox = chatBox;
    }

    // Toggles chat visibility
    public void toggleChat() {
        chatBox.getChatContainer().setVisible(!chatBox.getChatContainer().isVisible());
    }

    // Handles sending messages
    public void sendMessage() {
        String message = chatBox.getChatInput().getText().trim();
        if (!message.isEmpty()) {
            chatBox.addMessage(message);
            chatBox.getChatInput().clear();
        }
    }
}
