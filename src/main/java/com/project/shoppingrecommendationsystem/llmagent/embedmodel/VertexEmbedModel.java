package com.project.shoppingrecommendationsystem.llmagent.embedmodel;

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
    private final VertexAiTextEmbeddingModel EMBEDDING_MODEL;
    private final String MODELNAME = "text-multilingual-embedding-002";
    private static final String GOOGLE_APPLICATION_CREDENTIALS= System.getenv("GOOGLE_APPLICATION_CREDENTIALS");
    private static final String SCOPED_URL = "https://www.googleapis.com/auth/cloud-platform";
    private static final String VERTEX_AI_ENDPOINT = System.getenv("VERTEX_AI_ENDPOINT");
    private static final String VERTEX_AI_GEMINI_PROJECT_ID = System.getenv("VERTEX_AI_GEMINI_PROJECT_ID");
    private static final String VERTEX_AI_GEMINI_LOCATION = System.getenv("VERTEX_AI_GEMINI_LOCATION");
    public VertexEmbedModel() throws IOException {

        GoogleCredentials credentials = GoogleCredentials
                .fromStream(new FileInputStream(GOOGLE_APPLICATION_CREDENTIALS))
                .createScoped(SCOPED_URL);

        credentials.refreshIfExpired();

        String endpoint = VERTEX_AI_ENDPOINT;
        if (endpoint == null) {
            endpoint = "us-central1-aiplatform.googleapis.com:443";
        }

        VertexAiEmbeddingConnectionDetails connectionDetails = VertexAiEmbeddingConnectionDetails.builder()
                .projectId(VERTEX_AI_GEMINI_PROJECT_ID)
                .location(VERTEX_AI_GEMINI_LOCATION)
                .apiEndpoint(endpoint)
                .predictionServiceSettings(
                        PredictionServiceSettings.newBuilder()
                                .setEndpoint(endpoint)
                                .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
                                .build()
                )
                .build();

        VertexAiTextEmbeddingOptions options = VertexAiTextEmbeddingOptions.builder()
                .model(MODELNAME)
                .build();

        this.EMBEDDING_MODEL = new VertexAiTextEmbeddingModel(connectionDetails, options);
    }

    @Override
    public EmbeddingModel getEmbeddingModel() {
        return this.EMBEDDING_MODEL;
    }

    @Override
    public EmbeddingResponse getEmbeddings(List<String> texts) {
        return this.EMBEDDING_MODEL.embedForResponse(texts);
    }


}
