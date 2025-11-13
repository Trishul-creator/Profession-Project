package com.example.jeopardy.dto;

public class QuestionResponse {
    private long id;
    private String category;
    private String text;

    public QuestionResponse() {}

    public QuestionResponse(long id, String category, String text) {
        this.id = id;
        this.category = category;
        this.text = text;
    }

    public long getId() {
        return id;
    }

    public String getCategory() {
        return category;
    }

    public String getText() {
        return text;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setText(String text) {
        this.text = text;
    }
}
