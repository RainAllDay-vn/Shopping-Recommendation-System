package com.project.shoppingrecommendationsystem.llmagent;

import com.project.shoppingrecommendationsystem.llmagent.conversationmodel.ConversationModel;
import com.project.shoppingrecommendationsystem.llmagent.conversationmodel.VertexConversationModel;
import com.project.shoppingrecommendationsystem.llmagent.embedmodel.VertexEmbedModel;
import com.project.shoppingrecommendationsystem.llmagent.vectordatabase.QdrantVectorDatabase;
import com.project.shoppingrecommendationsystem.llmagent.vectordatabase.VectorDatabase;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class Main {
    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        String storeName = System.getenv("VERTEX_AI_GEMINI_STORE_NAME");
        String userText = "Bạn hãy gợi ý cho tôi top 3 laptop dành cho học sinh sinh viên giá cả phải chăng";
        String response;

        ConversationModel chatModel = new VertexConversationModel();

        try {
            if (storeName == null || storeName.trim().isEmpty()) {
                throw new IllegalArgumentException("Vector store name not found in environment variables");
            }

            System.out.println("Using vector database: " + storeName);
            // Try to create vector database and question adviser
            VectorDatabase vectorDatabase = new QdrantVectorDatabase(storeName, new VertexEmbedModel());
            QuestionAdviser adviser = new QuestionAdviser(vectorDatabase, chatModel);
            response = adviser.advise(userText);

        } catch (Exception e) {
            System.out.println("Error initializing vector database: " + e.getMessage());
            System.out.println("Falling back to direct chat model without RAG...");
            response = chatModel.generate(userText);
        }

        System.out.println(response);
    }
}