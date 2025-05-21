package com.project.shoppingrecommendationsystem.llmagent;

import java.io.IOException;
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
        VectorDatabase vectorDatabase = new QdrantVectorDatabase(storeName, new VertexEmbedModel());
        ConversationModel curConversationModel = new VertexChatModel();
        QuestionAdviser adviser = new QuestionAdviser(vectorDatabase, curConversationModel);
        String userText = "Bạn hãy gợi ý cho tôi top 3 laptop dành cho học sinh sinh viên giá cả phải chăng";
        String response = adviser.advise(userText);

        System.out.println(response);

    }
}
