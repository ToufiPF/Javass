package ch.epfl.javass.gui;

import java.util.SplittableRandom;

import org.junit.jupiter.api.Test;

import ch.epfl.javass.jass.PackedScore;
import ch.epfl.javass.jass.Score;
import ch.epfl.javass.jass.TeamId;
import ch.epfl.test.TestRandomizer;

class ScoreBeanTest {
    private static long randomPkScore(SplittableRandom rng) {
        int t1 = rng.nextInt(10);
        int p1 = rng.nextInt(158);
        int g1 = rng.nextInt(2000);
        int t2 = rng.nextInt(10 - t1);
        int p2 = rng.nextInt(158 - p1);
        int g2 = rng.nextInt(2000 - g1);
        return PackedScore.pack(t1, p1, g1, t2, p2, g2);
    }

    @Test
    void testTurnPoints() {
        SplittableRandom rng = TestRandomizer.newRandom();
        for(int i = 0; i < TestRandomizer.RANDOM_ITERATIONS; ++i) {
            Score s = Score.ofPacked(randomPkScore(rng));
            for(TeamId team : TeamId.ALL) {
                ScoreBean sb = new ScoreBean();

                sb.turnPointsProperty(team).addListener((o, oV, nV) -> System.out.println(nV));
                
                System.out.println("(Score :) TurnPoints initial de la " + team + " : " + s.turnPoints(team));
                System.out.println("(Propriété :) TurnPoints initial de la " + team + " : ");
                sb.setTurnPoints(team, s.turnPoints(team));  
                
                s = Score.ofPacked(randomPkScore(rng));
                System.out.println("(Score :) TurnPoints après changement de la " + team + " : " + s.turnPoints(team));
                System.out.println("(Propriété :) TurnPoints après changement de la " + team + " : ");
                sb.setTurnPoints(team, s.turnPoints(team));

            }
        }
    }

}
