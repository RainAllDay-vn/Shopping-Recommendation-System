package com.project.shoppingrecommendationsystem.llmagent;

import com.project.shoppingrecommendationsystem.llmagent.conversationmodel.ConversationModel;
import com.project.shoppingrecommendationsystem.llmagent.vectordatabase.VectorDatabase;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.vectorstore.SearchRequest;


public class QuestionAdviser {
    private ConversationModel conversationModel;
    private VectorDatabase vectorDatabase;
    private QuestionAnswerAdvisor qaAdvisor;

    private static final double THRESHOLD = 0.8;
    private static final int TOP_K = 15;
    private static final String FALLBACK_MESSAGE = "Sorry, I'm currently unable to generate a response.";

    public QuestionAdviser(VectorDatabase curVectorDatabase, ConversationModel curConversationModel) {
        this.conversationModel = curConversationModel;
        this.vectorDatabase = curVectorDatabase;

        if (this.vectorDatabase != null) {
            try {
                this.qaAdvisor = new QuestionAnswerAdvisor(
                        this.vectorDatabase.getVectorStore(),
                        SearchRequest.builder()
                                .similarityThreshold(THRESHOLD)
                                .topK(TOP_K)
                                .build()
                );
            } catch (Exception e) {
                System.out.println("Failed to initialize QuestionAnswerAdvisor: " + e.getMessage());
                this.qaAdvisor = null; // fallback to vanilla mode
            }
        } else {
            System.out.println("Vector database is null. Proceeding without retrieval.");
            this.qaAdvisor = null;
        }
    }

    public String advise(String userText) {
        if (conversationModel == null || conversationModel.getChatModel() == null) {
            System.out.println("Chat model is not available");
            return FALLBACK_MESSAGE;
        }

        try {
            ChatResponse response = ChatClient.builder(this.conversationModel.getChatModel())
                    .build().prompt()
                    .advisors(this.qaAdvisor)
                    .user(userText)
                    .call()
                    .chatResponse();
            return conversationModel.extractTextContent(response);

        } catch (Exception e) {
            System.out.println(" Error while calling question advisor: " + e.getMessage());
            return conversationModel.generate(userText);
        }
    }

}