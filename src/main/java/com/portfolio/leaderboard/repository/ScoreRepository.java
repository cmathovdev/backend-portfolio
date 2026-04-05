package com.portfolio.leaderboard.repository;

import com.portfolio.leaderboard.model.Score;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScoreRepository extends JpaRepository<Score, Long> {

    List<Score> findTop20ByOrderByScoreDescCreatedAtAsc();

    @Modifying
    @Query(value = """
        INSERT INTO scores (player, score, created_at)
        SELECT :player, :score, NOW()
        WHERE (
            (SELECT COUNT(*) FROM scores) < 20
            OR :score > (SELECT MIN(score) FROM scores)
        )
    """, nativeQuery = true)
    void insertIfTop(@Param("player") String player,
                     @Param("score") int score);


    @Modifying
    @Query(value = """
        DELETE FROM scores
        WHERE id NOT IN (
            SELECT id FROM scores
            ORDER BY score DESC, created_at ASC
            LIMIT 20
        )
    """, nativeQuery = true)
    void cleanup();
}