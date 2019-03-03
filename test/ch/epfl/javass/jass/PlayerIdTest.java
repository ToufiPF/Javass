package ch.epfl.javass.jass;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class PlayerIdTest {
    
    private static PlayerId[] getAllPlayers() {
        return new PlayerId[] {
          PlayerId.PLAYER_1,
          PlayerId.PLAYER_2,
          PlayerId.PLAYER_3,
          PlayerId.PLAYER_4
        };
    }
    
    @Test
    void ALLisInRightOrder() {
        assertArrayEquals(getAllPlayers(), PlayerId.values());
    }
    
    @Test
    void countIsGood() {
        assertEquals(4, PlayerId.COUNT);
        assertEquals(PlayerId.values().length, PlayerId.COUNT);
    }
    
    @Test
    void teamReturnsRightTeam() {
        assertEquals(TeamId.TEAM_1, PlayerId.PLAYER_1.team());
        assertEquals(TeamId.TEAM_2, PlayerId.PLAYER_2.team());
        assertEquals(TeamId.TEAM_1, PlayerId.PLAYER_1.team());
        assertEquals(TeamId.TEAM_2, PlayerId.PLAYER_2.team());
    }

}
