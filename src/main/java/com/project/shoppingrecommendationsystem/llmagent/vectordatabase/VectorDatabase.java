package com.project.shoppingrecommendationsystem.llmagent.vectordatabase;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import java.util.List;

public interface VectorDatabase {
    int BATCH_SIZE = 3;
    int THREAD_SLEEP = 20000;
    VectorStore getVectorStore();
    default void addDocuments(List<Document> documents) {

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
