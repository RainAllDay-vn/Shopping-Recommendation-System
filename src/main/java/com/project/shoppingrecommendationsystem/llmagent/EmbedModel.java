package com.project.shoppingrecommendationsystem.llmagent;

import org.springframework.ai.embedding.EmbeddingResponse;
import java.util.List;
import org.springframework.ai.embedding.EmbeddingModel;

public interface EmbedModel {
    EmbeddingResponse getEmbeddings(List<String> texts);
    EmbeddingModel getEmbeddingModel();
}
