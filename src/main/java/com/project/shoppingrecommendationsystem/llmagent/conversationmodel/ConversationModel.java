package com.project.shoppingrecommendationsystem.llmagent.conversationmodel;

import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.ChatModel;

public interface ConversationModel {
    String generate(String userInput);
    ChatModel getChatModel();
    String extractTextContent(ChatResponse response);
}
