package com.project.shoppingrecommendationsystem.llmagent;

import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.ChatModel;

public interface ConversationModel {
    ChatResponse generate(String userInput);
    ChatModel getChatModel();
}
