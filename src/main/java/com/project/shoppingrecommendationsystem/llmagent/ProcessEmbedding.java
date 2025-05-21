package com.project.shoppingrecommendationsystem.llmagent;

import com.project.shoppingrecommendationsystem.models.ProductDatabase;
import com.project.shoppingrecommendationsystem.models.Laptop;
import org.springframework.ai.document.Document;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class ProcessEmbedding {
    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        ProductDatabase rawDatabase = ProductDatabase.getInstance();
        EmbedModel embedding = new VertexEmbedModel();
        String storeName = "Shopping Recommendation System";
        QdrantVectorDatabase vectorStore = new QdrantVectorDatabase(storeName, embedding);

        List<Laptop> laptopList = rawDatabase.findAllLaptops();

        List<Document> documents = laptopList.stream()
                .map(laptop -> {
                    Map<String, Object> metadata = new HashMap<>();
                    metadata.put("id", laptop.getId());
                    metadata.put("name", laptop.getName());
                    metadata.put("price", laptop.getPrice());

                    String content = laptop.getDescription() != null ? laptop.getDescription() : "";
                    return new Document(content, metadata);
                })
                .collect(Collectors.toList());

        vectorStore.addDocuments(documents);
    }
}