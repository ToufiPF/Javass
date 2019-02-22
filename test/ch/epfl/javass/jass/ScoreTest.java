package ch.epfl.javass.jass;

import static ch.epfl.test.TestRandomizer.RANDOM_ITERATIONS;
import static ch.epfl.test.TestRandomizer.newRandom;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.SplittableRandom;

import org.junit.jupiter.api.Test;

class ScoreTest {

    @Test
    void testHashCode() {
        fail("Not yet implemented");
    }

    @Test
    void testOfPacked() {
        fail("Not yet implemented");
    }

    @Test
    void testPacked() {
        fail("Not yet implemented");
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
        fail("Not yet implemented");
    }

    @Test
    void testGamePoints() {
        fail("Not yet implemented");
    }

    @Test
    void testTotalPoints() {
        fail("Not yet implemented");
    }

    @Test
    void testWithAdditionalTrick() {
        fail("Not yet implemented");
    }

    @Test
    void testNextTurn() {
        fail("Not yet implemented");
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
        int turnTricks1 = rng.nextInt(9);
        int turnPoints1 = rng.nextInt(257);
        int gamePoints1 = rng.nextInt(2000);
        
        int turnTricks2 = rng.nextInt(9 - turnTricks1);
        int turnPoints2 = rng.nextInt(257 - turnPoints1);
        int gamePoints2 = rng.nextInt(2000);
        
        return Score.ofPacked(
                PackedScore.pack(turnTricks1, turnPoints1, gamePoints1, 
                        turnTricks2, turnPoints2, gamePoints2));
    }
    
    private static Score getRandomScoreWithTurnTricks(int turnTricks1, int turnTricks2, SplittableRandom rng) {
        int turnPoints1 = rng.nextInt(257);
        int gamePoints1 = rng.nextInt(2000);
        int turnPoints2 = rng.nextInt(257 - turnPoints1);
        int gamePoints2 = rng.nextInt(2000);
        return Score.ofPacked(PackedScore.pack(turnTricks1, turnPoints1, gamePoints1, turnTricks2, turnPoints2, gamePoints2));
    }
    
    private static Score getRandomScoreWithTurnPoints(int turnPoints1, int turnPoints2, SplittableRandom rng) {
        int turnTricks1 = rng.nextInt(9);
        int gamePoints1 = rng.nextInt(2000);
        int turnTricks2 = rng.nextInt(9 - turnTricks1);
        int gamePoints2 = rng.nextInt(2000);
        
        return Score.ofPacked(PackedScore.pack(turnTricks1, turnPoints1, gamePoints1, turnTricks2, turnPoints2, gamePoints2));
    }
}