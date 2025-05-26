package com.project.shoppingrecommendationsystem.llmagent.vectordatabase;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import java.util.List;

public interface VectorDatabase {
    VectorStore getVectorStore();
    void addDocuments(List<Document> documents);
}
