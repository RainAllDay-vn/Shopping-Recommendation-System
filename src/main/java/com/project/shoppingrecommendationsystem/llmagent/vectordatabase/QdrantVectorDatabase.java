package com.project.shoppingrecommendationsystem.llmagent.vectordatabase;

import com.project.shoppingrecommendationsystem.llmagent.embedmodel.EmbedModel;
import com.project.shoppingrecommendationsystem.llmagent.embedmodel.VertexEmbedModel;
import io.qdrant.client.QdrantClient;
import io.qdrant.client.QdrantGrpcClient;
import io.qdrant.client.grpc.Collections.Distance;
import io.qdrant.client.grpc.Collections.VectorParams;

import org.springframework.ai.vectorstore.qdrant.QdrantVectorStore;
import org.springframework.ai.embedding.TokenCountBatchingStrategy;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;

//(uncheck) add documentation to existing vector database

public class QdrantVectorDatabase implements VectorDatabase {
    private QdrantVectorStore vectorStore;
    private QdrantClient qdrantClient;
    private EmbedModel embeddingModel;

    private static final int BATCH_SIZE = 3;
    private static final int THREAD_SLEEP = 20000;

    public QdrantClient createQdrantClient(String StoreName) throws ExecutionException, InterruptedException {
        String hostname = "localhost";
        int port = 6334;
        boolean isTls = false;
        String apiKey = System.getenv("QDRANT_API_KEY");

        QdrantGrpcClient.Builder grpcClientBuilder = QdrantGrpcClient.newBuilder(hostname, port, isTls);

        if (apiKey != null && !apiKey.isEmpty()) {
            grpcClientBuilder.withApiKey(apiKey);
        }
        QdrantClient client = new QdrantClient(grpcClientBuilder.build());

        if(!client.collectionExistsAsync(StoreName).get()) {
            client.createCollectionAsync(StoreName,
                    VectorParams.newBuilder()
                            .setDistance(Distance.Cosine)
                            .setSize(768)
                            .build()).get();
        }
        else {
            System.out.println("Collection may already exist" );
        }

        return client;
    }

    public QdrantVectorDatabase(String storeName, EmbedModel curEmbed) {
        try {
            this.qdrantClient = createQdrantClient(storeName);
            this.embeddingModel = curEmbed;

            this.vectorStore = QdrantVectorStore.builder(this.qdrantClient, this.embeddingModel.getEmbeddingModel())
                    .collectionName(storeName)
                    .initializeSchema(true)
                    .batchingStrategy(new TokenCountBatchingStrategy())
                    .build();

        } catch (Exception e) {
            System.err.println("Failed to connect to Qdrant or initialize vector store: " + e.getMessage());
            this.vectorStore = null;
        }
    }

    @Override
    public VectorStore getVectorStore() {
        return vectorStore;
    }

    @Override
    public void addDocuments(List<Document> documents){
        for (int i = 0; i < documents.size(); i += BATCH_SIZE) {
            int endIndex = Math.min(i + BATCH_SIZE, documents.size());
            List<Document> batch = documents.subList(i, endIndex);

            try {
                this.getVectorStore().add(batch);
                System.out.println("Embedded " + i + " data points");

                Thread.sleep(THREAD_SLEEP);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Sleep interrupted: " + e.getMessage());
                break;
            } catch (Exception e) {
                System.out.println("Failed to embed batch " + i + "-" + endIndex + ": " + e.getMessage());
                continue;
            }
        }
    }
}
