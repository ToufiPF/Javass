package ch.epfl.javass.jass;

import static ch.epfl.test.TestRandomizer.RANDOM_ITERATIONS;
import static ch.epfl.test.TestRandomizer.newRandom;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.SplittableRandom;

import org.junit.jupiter.api.Test;

class ScoreTest {

    @Test
    void testOfPackedLaunchExceptionWhenInvalidPacked() {
        assertThrows(IllegalArgumentException.class, () -> {
            Score.ofPacked(-1L);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            Score.ofPacked(0xFF00_0000_0000_0000L);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            Score.ofPacked(0x0000_FF00_FF00_0000L);
        });
    }

    @Test
    void testPacked() {
        Score score = Score.INITIAL;
        SplittableRandom rng = newRandom();
        for (int i = 0 ; i < RANDOM_ITERATIONS * 5; ++i) {
            int turnTricks1 = rng.nextInt(9);
            int turnTricks2 = rng.nextInt(9 - turnTricks1);
            int turnPoints1 = rng.nextInt(257);
            int turnPoints2 = rng.nextInt(257 - turnPoints1);
            int gamePoints1 = rng.nextInt(2000);
            int gamePoints2 = rng.nextInt(2000);
            
            score = Score.ofPacked(PackedScore.pack(turnTricks1, turnPoints1, gamePoints1, turnTricks2, turnPoints2, gamePoints2));
            assertEquals(PackedScore.pack(turnTricks1, turnPoints1, gamePoints1, turnTricks2, turnPoints2, gamePoints2), score.packed());
        }
    }

    @Test
    void testTurnTricks() {
        Score score = Score.INITIAL;
        SplittableRandom rng = newRandom();
        for (int i = 0 ; i < RANDOM_ITERATIONS * 5; ++i) {
            int turnTricks1 = rng.nextInt(9);
            int turnTricks2 = rng.nextInt(9 - turnTricks1);
            score = getRandomScoreWithTurnTricks(turnTricks1, turnTricks2, rng);
            assertEquals(turnTricks1, score.turnTricks(TeamId.TEAM_1));
            assertEquals(turnTricks2, score.turnTricks(TeamId.TEAM_2));
        }
    }

    @Test
    void testTurnPoints() {
        Score score = Score.INITIAL;
        SplittableRandom rng = newRandom();
        for (int i = 0 ; i < RANDOM_ITERATIONS * 5; ++i) {
            int turnPoints1 = rng.nextInt(257);
            int turnPoints2 = rng.nextInt(257 - turnPoints1);
            score = getRandomScoreWithTurnPoints(turnPoints1, turnPoints2, rng);
            assertEquals(turnPoints1, score.turnPoints(TeamId.TEAM_1));
            assertEquals(turnPoints2, score.turnPoints(TeamId.TEAM_2));
        }
    }

    @Test
    void testGamePoints() {
        Score score = Score.INITIAL;
        SplittableRandom rng = newRandom();
        for (int i = 0 ; i < RANDOM_ITERATIONS * 5; ++i) {
            int gamePoints1 = rng.nextInt(2000);
            int gamePoints2 = rng.nextInt(2000 - gamePoints1);
            score = getRandomScoreWithGamePoints(gamePoints1, gamePoints2, rng);
            assertEquals(gamePoints1, score.gamePoints(TeamId.TEAM_1));
            assertEquals(gamePoints2, score.gamePoints(TeamId.TEAM_2));
        }
    }

    @Test
    void testTotalPoints() {
        Score score = Score.INITIAL;
        SplittableRandom rng = newRandom();
        for (int i = 0 ; i < RANDOM_ITERATIONS * 5; ++i) {
            int turnTricks1 = rng.nextInt(9);
            int turnTricks2 = rng.nextInt(9 - turnTricks1);
            int turnPoints1 = rng.nextInt(257);
            int turnPoints2 = rng.nextInt(257 - turnPoints1);
            int gamePoints1 = rng.nextInt(2000);
            int gamePoints2 = rng.nextInt(2000);
            
            score = Score.ofPacked(PackedScore.pack(turnTricks1, turnPoints1, gamePoints1, turnTricks2, turnPoints2, gamePoints2));
            assertEquals(turnPoints1 + gamePoints1, score.totalPoints(TeamId.TEAM_1));
            assertEquals(turnPoints2 + gamePoints2, score.totalPoints(TeamId.TEAM_2));
        }
    }

    @Test
    void testWithAdditionalTrick() {
        Score score = Score.INITIAL;
        SplittableRandom rng = newRandom();
        for (int i = 0 ; i < RANDOM_ITERATIONS; ++i) {
            int turnTricks1 = rng.nextInt(8);
            int turnTricks2 = rng.nextInt(8 - turnTricks1);
            int turnPoints1 = rng.nextInt(257);
            int turnPoints2 = rng.nextInt(257 - turnPoints1);
            
            int pointsWon = rng.nextInt(256 - Math.max(turnPoints1, turnPoints2));
            
            score = Score.ofPacked(PackedScore.pack(turnTricks1, turnPoints1, 100, turnTricks2, turnPoints2, 100));
            score = score.withAdditionalTrick(TeamId.ALL.get(i % 2), pointsWon);
            assertEquals((i % 2 == 0 ? turnTricks1 : turnTricks2) + 1, score.turnTricks(TeamId.ALL.get(i % 2)));
            assertEquals((i % 2 == 0 ? turnPoints1 : turnPoints2) + pointsWon, score.turnPoints(TeamId.ALL.get(i % 2)));
        }
    }

    @Test
    void testNextTurn() {
        Score score = Score.INITIAL;
        SplittableRandom rng = newRandom();
        for (int i = 0 ; i < RANDOM_ITERATIONS; ++i) {
            int turnTricks1 = rng.nextInt(9);
            int turnTricks2 = rng.nextInt(9 - turnTricks1);
            int turnPoints1 = rng.nextInt(257);
            int turnPoints2 = rng.nextInt(257 - turnPoints1);
            int gamePoints1 = rng.nextInt(1000);
            int gamePoints2 = rng.nextInt(1000);
            
            
            score = Score.ofPacked(PackedScore.pack(turnTricks1, turnPoints1, gamePoints1, turnTricks2, turnPoints2, gamePoints2));
            score = score.nextTurn();
            assertEquals(0, score.turnTricks(TeamId.TEAM_1));
            assertEquals(0, score.turnTricks(TeamId.TEAM_2));
            assertEquals(0, score.turnPoints(TeamId.TEAM_1));
            assertEquals(0, score.turnPoints(TeamId.TEAM_2));
            assertEquals(turnPoints1 + gamePoints1, score.gamePoints(TeamId.TEAM_1));
            assertEquals(turnPoints2 + gamePoints2, score.gamePoints(TeamId.TEAM_2));
        }
    }

    @Test
    void testToString() {
        Score score = Score.INITIAL;
        System.out.println(score);
        for (int i = 0; i < Jass.TRICKS_PER_TURN; ++i) {
          int p = (i == 0 ? 13 : 18);
          TeamId w = (i % 2 == 0 ? TeamId.TEAM_1 : TeamId.TEAM_2);
          score = score.withAdditionalTrick(w, p);
          System.out.println(score);
        }
        score = score.nextTurn();
        System.out.println(score);
    }

    @Test
    void equalsReturnsTrueOnSameScores() {
        Score score = Score.INITIAL;
        SplittableRandom rng = newRandom();
        for (int i = 0 ; i < RANDOM_ITERATIONS * 5; ++i) {
            score = getRandomScore(rng);
            assertTrue(score.equals(Score.ofPacked(score.packed())));
            assertTrue(Score.ofPacked(score.packed()).equals(score));
        }
    }
    
    
    private static Score getRandomScore(SplittableRandom rng) {
        return Score.ofPacked(PackedScoreTest.getRandomValidPackedScore(rng));
    }
    
    private static Score getRandomScoreWithTurnTricks(int turnTricks1, int turnTricks2, SplittableRandom rng) {
        return Score.ofPacked(PackedScoreTest.getRandomPackedScoreWithTurnTricks(turnTricks1, turnTricks2, rng));
    }
    
    private static Score getRandomScoreWithTurnPoints(int turnPoints1, int turnPoints2, SplittableRandom rng) {
        return Score.ofPacked(PackedScoreTest.getRandomPackedScoreWithTurnPoints(turnPoints1, turnPoints2, rng));
    }
    
    private static Score getRandomScoreWithGamePoints(int gamePoints1, int gamePoints2, SplittableRandom rng) {
        return Score.ofPacked(PackedScoreTest.getRandomPackedScoreWithGamePoints(gamePoints1, gamePoints2, rng));
    }
}
