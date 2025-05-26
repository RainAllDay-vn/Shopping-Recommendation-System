package com.project.shoppingrecommendationsystem.llmagent;

import com.project.shoppingrecommendationsystem.llmagent.conversationmodel.ConversationModel;
import com.project.shoppingrecommendationsystem.llmagent.vectordatabase.VectorDatabase;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.chat.prompt.PromptTemplate;
//import org.springframework.ai.template.st.StTemplateRenderer;


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
                String userAdvise = "You are a helpful and knowledgeable assistant. Below is context retrieved from a trusted source (e.g. database, product catalog, or help article), enclosed between the lines.\n" +
                        "\n" +
                        "---------------------\n" +
                        "{question_answer_context}\n" +
                        "---------------------\n" +
                        "\n" +
                        "Based on the above context, answer the user’s question as helpfully and thoroughly as possible. If the context is not sufficient, you may also use your own general knowledge to provide a complete response.\n" +
                        "\n" +
                        "Do not mention the context explicitly. Use natural language, provide examples or reasoning when helpful, and be friendly and clear.";
                this.qaAdvisor = new QuestionAnswerAdvisor(
                        this.vectorDatabase.getVectorStore(),
                        SearchRequest.builder()
                                .similarityThreshold(THRESHOLD)
                                .topK(TOP_K)
                                .build(),
                        userAdvise
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
            /*
            ChatResponse response = ChatClient.builder(this.conversationModel.getChatModel())
                    .build().prompt()
                    .advisors(this.qaAdvisor)
                    .user(userText)
                    .call()
                    .chatResponse();
            return conversationModel.extractTextContent(response);

             */
            String response = ChatClient.builder(conversationModel.getChatModel()).build()
                    .prompt(userText)
                    .advisors(qaAdvisor)
                    .call()
                    .content();
            return response;

        } catch (Exception e) {
            System.out.println(" Error while calling question advisor: " + e.getMessage());
            return conversationModel.generate(userText);
        }
    }

}