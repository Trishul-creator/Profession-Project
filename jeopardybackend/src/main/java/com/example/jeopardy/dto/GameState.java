package com.example.jeopardy.dto;

import java.util.List;
import com.example.jeopardy.model.Team;

public class GameState {
    private List<Team> teams;

    public GameState() {}

    public GameState(List<Team> teams) {
        this.teams = teams;
    }

    public List<Team> getTeams() {
        return teams;
    }

    public void setTeams(List<Team> teams) {
        this.teams = teams;
    }
}
