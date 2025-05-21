package com.project.shoppingrecommendationsystem.llmagent;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import java.util.List;

public interface VectorDatabase {
    void addDocuments(List<Document> documents);
    VectorStore getVectorStore();
}
