package com.project.shoppingrecommendationsystem.llmagent;

import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.ai.chat.model.ChatResponse;

import java.io.IOException;
import java.util.*;
import java.nio.file.Path;
import java.util.concurrent.ExecutionException;

public class Main {
    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        /* Processing Document Test
        DocProcess enntity = new DocProcess();
        try {
            List<Document> documents = enntity.loadAndSplit(Path.of("data/TGDD/laptop.csv"), Path.of("data/TGDD/description.csv"));
            for (Document doc : documents) {
                System.out.println(doc);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        */

        String storeName = System.getenv("VERTEX_AI_GEMINI_STORE_NAME");
        System.out.println(storeName);
        QuestionAdviser shoppingRec = new QuestionAdviser(storeName);
        String userText = "Bạn hãy gợi ý cho tôi top 3 laptop dành cho học sinh sinh viên giá cả phải chăng";
        String response = shoppingRec.advise(userText);

        System.out.println(response);

    }
}
