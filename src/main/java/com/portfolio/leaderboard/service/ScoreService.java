package com.portfolio.leaderboard.service;

import com.portfolio.leaderboard.model.Score;
import com.portfolio.leaderboard.repository.ScoreRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ScoreService {

    private final ScoreRepository scoreRepository;

    public ScoreService(ScoreRepository scoreRepository) {
        this.scoreRepository = scoreRepository;
    }

    public List<Score> getTop20() {
        return scoreRepository.findTop20ByOrderByScoreDescCreatedAtAsc();
    }

    @Transactional
    public List<Score> saveScore(String player, int score) {
        scoreRepository.insertIfTop(player, score);
        scoreRepository.cleanup();
        return scoreRepository.findTop20ByOrderByScoreDescCreatedAtAsc();
    }
}