package com.portfolio.leaderboard.controller;

import com.portfolio.leaderboard.model.Score;
import com.portfolio.leaderboard.service.ScoreService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/scores")
@CrossOrigin
public class ScoreController {

    private final ScoreService scoreService;

    public ScoreController(ScoreService scoreService) {
        this.scoreService = scoreService;
    }

    @GetMapping
    public List<Score> getLeaderboard() {
        return scoreService.getTop20();
    }

    @PostMapping
    public List<Score> saveScore(@RequestBody @Valid Score score) {
        return scoreService.saveScore(score.getPlayer(), score.getScore());
    }
}