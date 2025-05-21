package com.project.shoppingrecommendationsystem.llmagent.conversationmodel;

import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.ChatModel;

public interface ConversationModel {
    String generate(String userInput);
    ChatModel getChatModel();
    default String extractTextContent(ChatResponse response) {
        String x = response.getResults().toString();
        int startIndex = x.indexOf("textContent=");
        if (startIndex == -1) {
            return "Text content not found";
        }
        startIndex += "textContent=".length();

        int endIndex = x.lastIndexOf(", metadata={");
        if (endIndex == -1) {
            return "Metadata marker not found";
        }

        return x.substring(startIndex, endIndex);
    }
}
