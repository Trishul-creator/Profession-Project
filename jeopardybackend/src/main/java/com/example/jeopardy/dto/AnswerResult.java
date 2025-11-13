package com.example.jeopardy.dto;

import java.util.List;
import com.example.jeopardy.model.Team;

public class AnswerResult {
    private boolean correct;
    private String correctAnswer;
    private List<Team> teams;

    public AnswerResult() {}

    public AnswerResult(boolean correct, String correctAnswer, List<Team> teams) {
        this.correct = correct;
        this.correctAnswer = correctAnswer;
        this.teams = teams;
    }

    public boolean isCorrect() {
        return correct;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public List<Team> getTeams() {
        return teams;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public void setTeams(List<Team> teams) {
        this.teams = teams;
    }
}
