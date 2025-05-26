package com.project.shoppingrecommendationsystem.llmagent;

import com.project.shoppingrecommendationsystem.llmagent.conversationmodel.ConversationModel;
import com.project.shoppingrecommendationsystem.llmagent.conversationmodel.VertexConversationModel;
import com.project.shoppingrecommendationsystem.llmagent.embedmodel.EmbedModel;
import com.project.shoppingrecommendationsystem.llmagent.embedmodel.VertexEmbedModel;
import com.project.shoppingrecommendationsystem.llmagent.vectordatabase.QdrantVectorDatabase;
import com.project.shoppingrecommendationsystem.llmagent.vectordatabase.VectorDatabase;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class TestLlmAgents {
    public void testVectorDatabase() throws IOException, ExecutionException, InterruptedException {
        String storeName = "laptop";
        String prjid = System.getenv("VERTEX_AI_GEMINI_PROJECT_ID");
        String location = System.getenv("VERTEX_AI_GEMINI_LOCATION");

        EmbedModel vertexEmbedModel = new VertexEmbedModel();

        VectorDatabase vectorDatabase = new QdrantVectorDatabase(storeName, vertexEmbedModel);

        List<Document> documents = List.of(
                new Document("Spring AI rocks!! Spring AI rocks!! Spring AI rocks!! Spring AI rocks!! Spring AI rocks!!", Map.of("meta1", "meta1")),
                new Document("The World is Big and Salvation Lurks Around the Corner"),
                new Document("You walk forward facing the past and you turn back toward the future.", Map.of("meta2", "meta2")),
                new Document("turn back toward the future.", Map.of("meta2", "meta2")),
                new Document("You walk forward facing the past and you turn back toward the future.", Map.of("meta2", "meta2")),
                new Document("You walk forward facing the past and.", Map.of("meta2", "meta2")),
                new Document(" you turn back toward the future.", Map.of("meta2", "meta2")),
                new Document("You walk forward facing the past and you turn back toward the future.", Map.of("meta2", "meta2")),
                new Document("Past and you turn back toward the future.", Map.of("meta2", "meta2")),
                new Document("future.", Map.of("meta2", "meta2")));
        vectorDatabase.addDocuments(documents);
    }

    public void testEmbedModel() throws IOException {
        VertexEmbedModel vertexEmbedModel = new VertexEmbedModel();
        List<String> texts = List.of("Hello world", "This is a test");
        EmbeddingResponse response = vertexEmbedModel.getEmbeddings(texts);

        System.out.println("\nEmbedding Response:");
        System.out.println("Number of embeddings: " + response.getResults().size());
        for (int i = 0; i < response.getResults().size(); i++) {
            System.out.println("\nText " + i + ": " + texts.get(i));
            System.out.println("Embedding dimension: " + response.getResults().get(i).getOutput());
            System.out.println("View embedding" + response.getResults().get(i).getOutput());
        }
    }

    public void testConversationChat() {
        ConversationModel conversationModel = new VertexConversationModel();
        String response = conversationModel.generate("Can you explain Polynorpishm in OOP in detail");
        System.out.println(response);
    }

    public void testQA() throws IOException{
        String storeName = System.getenv("VERTEX_AI_GEMINI_STORE_NAME");
        VectorDatabase vectorDatabase = new QdrantVectorDatabase(storeName, new VertexEmbedModel());
        ConversationModel curConversationModel = new VertexConversationModel();
        System.out.println(vectorDatabase.getVectorStore());
        QuestionAdviser adviser = new QuestionAdviser(vectorDatabase, curConversationModel);
        String userText = "Tôi muốn tìm một chiếc laptop có mức giá và hiệu năng phù hợp với sinh viên, chạy hệ điều hành Windows và có màn hình lớn. Bạn có thể gợi ý cho tôi một vài mẫu được không?";
        String response = adviser.advise(userText);

        System.out.println(response);
    }

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        System.out.println("Checking environment variables:");
        System.out.println("GOOGLE_APPLICATION_CREDENTIALS: " + System.getenv("GOOGLE_APPLICATION_CREDENTIALS"));
        System.out.println("VERTEX_AI_GEMINI_PROJECT_ID: " + System.getenv("VERTEX_AI_GEMINI_PROJECT_ID"));
        System.out.println("VERTEX_AI_GEMINI_LOCATION: " + System.getenv("VERTEX_AI_GEMINI_LOCATION"));
        System.out.println("VERTEX_AI_GEMINI_STORE_NAME: " + System.getenv("VERTEX_AI_GEMINI_STORE_NAME"));
        TestLlmAgents test = new TestLlmAgents();
        test.testConversationChat();
    }
}
