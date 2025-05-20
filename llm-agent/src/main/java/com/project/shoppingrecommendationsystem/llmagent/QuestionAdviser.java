package com.project.shoppingrecommendationsystem.llmagent;

import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;

import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vertexai.gemini.VertexAiGeminiChatModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.qdrant.QdrantVectorStore;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class QuestionAdviser {
    private VertexAiGeminiChatModel chatModel;
    private VectorStore vectorStore;
    private QuestionAnswerAdvisor qaAdvisor;

    public QuestionAdviser(String storeName) throws IOException, ExecutionException, InterruptedException {
        String projectId = System.getenv("VERTEX_AI_GEMINI_PROJECT_ID");
        String location = System.getenv("VERTEX_AI_GEMINI_LOCATION");
        VertexAiGeminiChatModel chatModel = new ChatModel(projectId, location).getChatModel();
        this.chatModel = chatModel;

        QdrantVectorStore vectorStore = new VectorDatabase(storeName).getVectorStore();
        this.vectorStore = vectorStore;

        QuestionAnswerAdvisor qaAdvisor = new QuestionAnswerAdvisor(this.vectorStore,
                SearchRequest.builder().
                        similarityThreshold(0.8d).
                        topK(15).
                        build());
        this.qaAdvisor = qaAdvisor;
    }


    public String advise(String userText) {
        ChatResponse response = ChatClient.builder(this.chatModel)
                .build().prompt()
                .advisors(new QuestionAnswerAdvisor(this.vectorStore))
                .user(userText)
                .call()
                .chatResponse();

        return extractTextContent(response.getResults().toString());
    }
    public static String extractTextContent(String responseString) {
        // Find the start of text content
        int startIndex = responseString.indexOf("textContent=");
        if (startIndex == -1) {
            return "Text content not found";
        }
        startIndex += "textContent=".length();

        // Find the end of text content (before metadata section)
        int endIndex = responseString.lastIndexOf(", metadata={");
        if (endIndex == -1) {
            return "Metadata marker not found";
        }

        // Extract the content between these positions
        return responseString.substring(startIndex, endIndex);
    }
    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        String storeName = "Shopping Recommendation System";
        QuestionAdviser adviser = new QuestionAdviser(storeName);
        String userText = "hello ";
        String response = adviser.advise(userText);

        System.out.println(response);
    }



}