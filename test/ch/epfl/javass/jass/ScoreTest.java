package ch.epfl.javass.jass;

import static org.junit.jupiter.api.Assertions.*;

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
    void testEqualsObject() {
        fail("Not yet implemented");
    }

}
