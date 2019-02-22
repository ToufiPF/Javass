package ch.epfl.javass.jass;

import static ch.epfl.test.TestRandomizer.RANDOM_ITERATIONS;
import static ch.epfl.test.TestRandomizer.newRandom;
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
        fail("Not yet implemented");
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
            score = getRandomValidScore(rng);
            assertTrue(score.equals(Score.ofPacked(score.packed())));
            assertTrue(Score.ofPacked(score.packed()).equals(score));
        }
    }
    
    private Score getRandomValidScore(SplittableRandom generator) {
        
        int tricks1 = generator.nextInt(9);
        int turnPoints1 = generator.nextInt(257);
        int gamePoints1 = generator.nextInt(2000);
        
        int tricks2 = generator.nextInt(9);
        int turnPoints2 = generator.nextInt(257);
        int gamePoints2 = generator.nextInt(2000);
        
        return Score.ofPacked(
                PackedScore.pack(tricks1, turnPoints1, gamePoints1, 
                tricks2, turnPoints2, gamePoints2));
    }
}
