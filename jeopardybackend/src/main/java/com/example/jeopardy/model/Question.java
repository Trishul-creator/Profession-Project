package com.example.jeopardy.model;

public class Question {
    private final long id;
    private final String category;
    private final String text;
    private final String answer;

    public Question(long id, String category, String text, String answer) {
        this.id = id;
        this.category = category;
        this.text = text;
        this.answer = answer;
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

    public String getAnswer() {
        return answer;
    }
}
