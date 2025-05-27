package com.project.shoppingrecommendationsystem.llmagent;

import com.project.shoppingrecommendationsystem.Messenger;
import com.project.shoppingrecommendationsystem.llmagent.embedmodel.EmbedModel;
import com.project.shoppingrecommendationsystem.llmagent.embedmodel.VertexEmbedModel;
import com.project.shoppingrecommendationsystem.llmagent.vectordatabase.QdrantVectorDatabase;
import com.project.shoppingrecommendationsystem.models.Product;
import com.project.shoppingrecommendationsystem.models.database.ProductDatabase;
import org.springframework.ai.document.Document;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class ProcessEmbedding {
    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        embedDatabase();
    }

    public static void embedDatabase() throws IOException {
        ProductDatabase rawDatabase = Messenger.getInstance().getProductDatabase();
        EmbedModel embedding = new VertexEmbedModel();
        String storeName = "Shopping Recommendation System";
        QdrantVectorDatabase vectorStore = new QdrantVectorDatabase(storeName, embedding);

        List<Product> products = rawDatabase.findAllProducts();

        List<Document> documents = products.stream()
                .map(product -> {
                    Map<String, Object> metadata = new HashMap<>();
                    metadata.put("id", product.getId());
                    metadata.put("name", product.getName());
                    metadata.put("price", product.getPrice());

                    String content = product.getDescription() != null ? product.getDescription().toString() : "";
                    return new Document(content, metadata);
                })
                .collect(Collectors.toList());
        vectorStore.addDocuments(documents);
    }
}