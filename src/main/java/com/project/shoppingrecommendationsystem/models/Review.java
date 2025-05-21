package com.project.shoppingrecommendationsystem.models;

import java.util.Date;

public class Review {
    private final Date created;
    private final String content;
    private final String score;
    private final String username;

    public Review(Date created, String content, String score, String username) {
        this.created = created;
        this.content = content;
        this.score = score;
        this.username = username;
    }

    @Override
    public String toString() {
        return "Review{" + "\n" +
                "created: " + created + "\n" +
                "content: " + content + "\n" +
                "score: " + score + "\n" +
                "username: " + username + "\n" +
                "}";
    }
}
