package com.project.shoppingrecommendationsystem.llmagent;

import java.io.FileInputStream;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.aiplatform.v1.PredictionServiceSettings;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.ai.vertexai.embedding.VertexAiEmbeddingConnectionDetails;
import org.springframework.ai.vertexai.embedding.text.VertexAiTextEmbeddingModel;
import org.springframework.ai.vertexai.embedding.text.VertexAiTextEmbeddingOptions;


import java.io.IOException;
import java.util.List;

// using command setx VARIABLE_NAME "VALUE"
public class VertexEmbedModel implements EmbedModel {
    private final VertexAiTextEmbeddingModel embeddingModel;

    public VertexEmbedModel() throws IOException {
        String credentialsJson = System.getenv("GOOGLE_APPLICATION_CREDENTIALS");

        GoogleCredentials credentials = GoogleCredentials
                .fromStream(new FileInputStream(System.getenv("GOOGLE_APPLICATION_CREDENTIALS")))
                .createScoped("https://www.googleapis.com/auth/cloud-platform");

        credentials.refreshIfExpired();

        String endpoint = System.getenv("VERTEX_AI_ENDPOINT");
        if (endpoint == null) {
            endpoint = "us-central1-aiplatform.googleapis.com:443";
        }

        VertexAiEmbeddingConnectionDetails connectionDetails = VertexAiEmbeddingConnectionDetails.builder()
                .projectId(System.getenv("VERTEX_AI_GEMINI_PROJECT_ID"))
                .location(System.getenv("VERTEX_AI_GEMINI_LOCATION"))
                .apiEndpoint(endpoint)
                .predictionServiceSettings(
                        PredictionServiceSettings.newBuilder()
                                .setEndpoint(endpoint)
                                .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
                                .build()
                )
                .build();

        VertexAiTextEmbeddingOptions options = VertexAiTextEmbeddingOptions.builder()
                .model("text-multilingual-embedding-002")
                .build();

        this.embeddingModel = new VertexAiTextEmbeddingModel(connectionDetails, options);
    }

    @Override
    public EmbeddingModel getEmbeddingModel() {
        return this.embeddingModel;
    }

    @Override
    public EmbeddingResponse getEmbeddings(List<String> texts) {
        return this.embeddingModel.embedForResponse(texts);
    }
    public static void main(String[] args) throws IOException {
        System.out.println("Checking environment variables:");
        System.out.println("GOOGLE_APPLICATION_CREDENTIALS: " + System.getenv("GOOGLE_APPLICATION_CREDENTIALS"));
        System.out.println("VERTEX_AI_GEMINI_PROJECT_ID: " + System.getenv("VERTEX_AI_GEMINI_PROJECT_ID"));
        System.out.println("VERTEX_AI_GEMINI_LOCATION: " + System.getenv("VERTEX_AI_GEMINI_LOCATION"));

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
}
