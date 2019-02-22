package ch.epfl.javass.jass;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

class TeamIdTest {
    private static TeamId[] getAllTeamId() {
        return new TeamId[] { TeamId.TEAM_1, TeamId.TEAM_2 };
    }

    @Test
    void TeamIdIsInTheRightOrder() {
        assertArrayEquals(getAllTeamId(), TeamId.values());
    }
    
    @Test
    void TeamIdAllIsCorrect() {
        assertEquals(Arrays.asList(TeamId.values()), TeamId.ALL);
    }

    @Test
    void TeamIdCountIsCorrect() {
        assertEquals(getAllTeamId().length, TeamId.COUNT);
    }
    
    @Test
    void TeamIdOtherIsCorrect() {
        assertEquals(TeamId.TEAM_1, TeamId.TEAM_2.other());
        assertEquals(TeamId.TEAM_2, TeamId.TEAM_1.other());
    }
}
