package com.project.shoppingrecommendationsystem.llmagent;

import io.qdrant.client.QdrantClient;
import io.qdrant.client.QdrantGrpcClient;
import io.qdrant.client.grpc.Collections.Distance;
import io.qdrant.client.grpc.Collections.VectorParams;

import org.springframework.ai.vectorstore.qdrant.QdrantVectorStore;
import org.springframework.ai.embedding.TokenCountBatchingStrategy;
import org.springframework.ai.document.Document;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;

//(uncheck) add documentation to existing vector database

public class VectorDatabase {
    private QdrantVectorStore vectorStore;
    private QdrantClient qdrantClient;
    private EmbedModel embeddingModel;
    private final int BATCH_SIZE = 5;
    private final int THREAD_SLEEP = 20000;

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

    public VectorDatabase (String StoreName) throws ExecutionException, InterruptedException, IOException {
        this.qdrantClient = createQdrantClient(StoreName);
        this.embeddingModel = new EmbedModel();
        this.vectorStore = QdrantVectorStore.builder(this.qdrantClient, this.embeddingModel.getEmbeddingModel())
                .collectionName(StoreName)
                .initializeSchema(true)
                .batchingStrategy(new TokenCountBatchingStrategy())
                .build();
    }

    public void addToQdrant(List<Document> documents) {

        for (int i = 0; i < documents.size(); i += BATCH_SIZE) {
            int endIndex = Math.min(i + BATCH_SIZE, documents.size());
            List<Document> batch = documents.subList(i, endIndex);

            try {
                vectorStore.add(batch);
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
    public QdrantVectorStore getVectorStore() {
        return vectorStore;
    }

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        String storeName = "laptop";
        VectorDatabase vectorStore = new VectorDatabase(storeName);

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
        vectorStore.addToQdrant(documents);
    }
}
