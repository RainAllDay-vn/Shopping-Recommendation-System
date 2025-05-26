package com.project.shoppingrecommendationsystem.llmagent;

import com.project.shoppingrecommendationsystem.llmagent.embedmodel.EmbedModel;
import com.project.shoppingrecommendationsystem.llmagent.embedmodel.VertexEmbedModel;
import com.project.shoppingrecommendationsystem.llmagent.vectordatabase.QdrantVectorDatabase;
import com.project.shoppingrecommendationsystem.models.Laptop;
import com.project.shoppingrecommendationsystem.models.database.LaptopDatabase;
import org.springframework.ai.document.Document;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class ProcessEmbedding {
    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        LaptopDatabase rawDatabase = LaptopDatabase.getInstance();
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

                    String content = laptop.getDescription() != null ? laptop.getDescription().toString() : "";
                    return new Document(content, metadata);
                })
                .collect(Collectors.toList());

        vectorStore.addDocuments(documents);
    }
}