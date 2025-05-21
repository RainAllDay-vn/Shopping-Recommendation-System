package com.project.shoppingrecommendationsystem.llmagent;

import com.project.shoppingrecommendationsystem.llmagent.conversationmodel.ConversationModel;
import com.project.shoppingrecommendationsystem.llmagent.conversationmodel.VertexConversationModel;
import com.project.shoppingrecommendationsystem.llmagent.embedmodel.VertexEmbedModel;
import com.project.shoppingrecommendationsystem.llmagent.vectordatabase.QdrantVectorDatabase;
import com.project.shoppingrecommendationsystem.llmagent.vectordatabase.VectorDatabase;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;

import org.springframework.ai.vectorstore.SearchRequest;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class QuestionAdviser {
    private ConversationModel conversationModel;
    private VectorDatabase vectorDatabase;
    private QuestionAnswerAdvisor qaAdvisor;

    public QuestionAdviser(VectorDatabase CurVectorDatabase, ConversationModel CurConversationModel) throws IOException, ExecutionException, InterruptedException {

        this.conversationModel = CurConversationModel;
        this.vectorDatabase = CurVectorDatabase;

        QuestionAnswerAdvisor qaAdvisor = new QuestionAnswerAdvisor(this.vectorDatabase.getVectorStore(),
                SearchRequest.builder().
                        similarityThreshold(0.8d).
                        topK(15).
                        build());
        this.qaAdvisor = qaAdvisor;
    }


    public String advise(String userText) {
        ChatResponse response = ChatClient.builder(this.conversationModel.getChatModel())
                .build().prompt()
                .advisors(new QuestionAnswerAdvisor(this.vectorDatabase.getVectorStore()))
                .user(userText)
                .call()
                .chatResponse();

        return extractTextContent(response);
    }
    public static String extractTextContent(ChatResponse response) {
        // Find the start of text content
        String x = response.getResults().toString();
        int startIndex = x.indexOf("textContent=");
        if (startIndex == -1) {
            return "Text content not found";
        }
        startIndex += "textContent=".length();

        // Find the end of text content (before metadata section)
        int endIndex = x.lastIndexOf(", metadata={");
        if (endIndex == -1) {
            return "Metadata marker not found";
        }

        // Extract the content between these positions
        return x.substring(startIndex, endIndex);
    }
    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        String storeName = System.getenv("VERTEX_AI_GEMINI_STORE_NAME");
        VectorDatabase vectorDatabase = new QdrantVectorDatabase(storeName, new VertexEmbedModel());
        ConversationModel curConversationModel = new VertexConversationModel();
        QuestionAdviser adviser = new QuestionAdviser(vectorDatabase, curConversationModel);
        String userText = "hello ";
        String response = adviser.advise(userText);

        System.out.println(response);
    }

}