package com.project.shoppingrecommendationsystem.llmagent.conversationmodel;

import com.google.cloud.vertexai.VertexAI;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.vertexai.gemini.VertexAiGeminiChatModel;
import org.springframework.ai.vertexai.gemini.VertexAiGeminiChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.model.ChatModel;

import java.util.*;

// need to set GOOGLE_APPLICATION_CREDENTIALS = "path/to/your/credentials.json"

public class VertexConversationModel implements ConversationModel {
    private VertexAI vertexApi;
    private VertexAiGeminiChatModel chatModel;
    private static final double TEMPERATURE = 0.7;
    private static final double TOP_P = 0.8;
    private static final int TOP_K = 40;
    private static final int MAX_OUTPUT_TOKENS = 1500;
    private static final int CANDIDATE_COUNT = 1;
    private static final List<String> STOP_SEQUENCES = List.of();
    private static final VertexAiGeminiChatModel.ChatModel MODEL = VertexAiGeminiChatModel.ChatModel.GEMINI_2_0_FLASH;

    private static final VertexAiGeminiChatOptions DEFAULT_CHAT_OPTIONS =
            VertexAiGeminiChatOptions.builder()
                    .temperature(TEMPERATURE)
                    .topP(TOP_P)
                    .topK(TOP_K)
                    .maxOutputTokens(MAX_OUTPUT_TOKENS)
                    .stopSequences(STOP_SEQUENCES)
                    .candidateCount(CANDIDATE_COUNT)
                    .model(MODEL)
                    .build();

    public VertexConversationModel() {
        String projectId = System.getenv("VERTEX_AI_GEMINI_PROJECT_ID");
        String location = System.getenv("VERTEX_AI_GEMINI_LOCATION");

        this.vertexApi = new VertexAI(projectId, location);
        this.chatModel = VertexAiGeminiChatModel.builder()
                .vertexAI(vertexApi)
                .defaultOptions(DEFAULT_CHAT_OPTIONS)
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

    @Override
    public String extractTextContent(ChatResponse response) {
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
