package com.example.jeopardy.controller;

import com.example.jeopardy.dto.*;
import com.example.jeopardy.model.Question;
import com.example.jeopardy.model.Team;
import com.example.jeopardy.service.GameService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/game")
@CrossOrigin(origins = "http://localhost:3000")
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping("/categories")
    public ResponseEntity<List<String>> getCategories() {
        return ResponseEntity.ok(gameService.getCategories());
    }

    @PostMapping("/start")
    public ResponseEntity<GameState> startGame(@RequestBody GameStartRequest request) {
        gameService.startGame(request.getTeamNames());
        return ResponseEntity.ok(new GameState(gameService.getTeams()));
    }

    @GetMapping("/state")
    public ResponseEntity<GameState> getState() {
        return ResponseEntity.ok(new GameState(gameService.getTeams()));
    }

    @GetMapping("/random-question")
    public ResponseEntity<QuestionResponse> getRandomQuestion(@RequestParam("category") String category) {
        Question q = gameService.getRandomQuestion(category);
        if (q == null) {
            return ResponseEntity.notFound().build();
        }
        QuestionResponse dto = new QuestionResponse(q.getId(), q.getCategory(), q.getText());
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/answer")
    public ResponseEntity<AnswerResult> submitAnswer(@RequestBody AnswerRequest request) {
        boolean correct = gameService.checkAnswer(request.getQuestionId(), request.getTeamName(), request.getAnswer());
        String correctAnswer = gameService.getCorrectAnswer(request.getQuestionId());
        List<Team> teams = gameService.getTeams();
        AnswerResult result = new AnswerResult(correct, correctAnswer, teams);
        return ResponseEntity.ok(result);
    }
}
