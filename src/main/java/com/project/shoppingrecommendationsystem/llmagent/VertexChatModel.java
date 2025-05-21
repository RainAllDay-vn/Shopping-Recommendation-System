package com.project.shoppingrecommendationsystem.llmagent;

import com.google.cloud.vertexai.VertexAI;
import org.springframework.ai.vertexai.gemini.VertexAiGeminiChatModel;
import org.springframework.ai.vertexai.gemini.VertexAiGeminiChatOptions;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.model.ChatModel;

import java.util.*;

// need to set GOOGLE_APPLICATION_CREDENTIALS = "path/to/your/credentials.json"

public class VertexChatModel implements ConversationModel {
    private VertexAI vertexApi;
    private VertexAiGeminiChatModel chatModel;
    private final VertexAiGeminiChatOptions OPTION = VertexAiGeminiChatOptions.builder()
            .temperature(0.7)
            .topP(0.8)
            .topK(40)
            .maxOutputTokens(1000)
            .stopSequences(List.of())
            .candidateCount(1)
            .model(VertexAiGeminiChatModel.ChatModel.GEMINI_2_0_FLASH)
            .build();
            /* 1st tuning option
        VertexAiGeminiChatOptions options = VertexAiGeminiChatOptions.builder()
                .temperature(0.2)
                .topP(0.2)
                .topK(3)
                .maxOutputTokens(1000)
                .model(VertexAiGeminiChatModel.ChatModel.GEMINI_2_0_FLASH)
                .build();
        */
        /* 2nd tuning option
        VertexAiGeminiChatOptions options = VertexAiGeminiChatOptions.builder()
                .temperature((double)0.7F)
                .topP((double)1.0F)
                .topK(40)
                .maxOutputTokens(500)
                .stopSequences(List.of())
                .candidateCount(1)
                .model(VertexAiGeminiChatModel.ChatModel.GEMINI_2_0_FLASH)
                .build();
        */

    public VertexChatModel() {
        String projectId = System.getenv("VERTEX_AI_GEMINI_PROJECT_ID");
        String location = System.getenv("VERTEX_AI_GEMINI_LOCATION");

        this.vertexApi = new VertexAI(projectId, location);
        this.chatModel = VertexAiGeminiChatModel.builder()
                .vertexAI(vertexApi)
                .defaultOptions(OPTION)
                .build();
    }

    @Override
    public ChatModel getChatModel() {
        return this.chatModel;
    }

    @Override
    public String generate(String userPrompt) {
        Prompt chatPrompt = new Prompt(userPrompt);
        return extractTextContent(this.chatModel.call(chatPrompt));
    }

    //test the chat model
    public static void main(String[] args) {
        String projectId = System.getenv("VERTEX_AI_GEMINI_PROJECT_ID");
        String location = System.getenv("VERTEX_AI_GEMINI_LOCATION");

        System.out.println("VERTEX_AI_GEMINI_PROJECT_ID: " + projectId);
        System.out.println("VERTEX_AI_GEMINI_LOCATION: " + location);
        ConversationModel conversationModel = new VertexChatModel();
        String response = conversationModel.generate("Can you explain Polynorpishm in OOP in detail");
        System.out.println(response);
    }

}
