package com.example.jeopardy.dto;

import java.util.List;

public class GameStartRequest {
    private List<String> teamNames;

    public GameStartRequest() {}

    public List<String> getTeamNames() {
        return teamNames;
    }

    public void setTeamNames(List<String> teamNames) {
        this.teamNames = teamNames;
    }
}
