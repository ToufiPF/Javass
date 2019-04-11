package ch.epfl.javass.gui;

import org.junit.jupiter.api.Test;

import ch.epfl.javass.jass.Score;
import ch.epfl.javass.jass.TeamId;

class ScoreBeanTest {

    @Test
    void testTurnPoints() {
        for(TeamId team : TeamId.ALL) {
            Score s = Score.INITIAL;
            ScoreBean sb = new ScoreBean();
            sb.gamePointsProperty(team).addListener((o, oV, nV) -> System.out.println(nV));
            
            sb.setTurnPoints(team, s.turnPoints(team));
            s.nextTurn();
        }
    }

}
