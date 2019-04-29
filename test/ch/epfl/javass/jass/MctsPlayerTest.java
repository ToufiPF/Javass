package ch.epfl.javass.jass;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import ch.epfl.javass.gametest.PrintingPlayer;
import ch.epfl.javass.gametest.RandomPlayer;
import ch.epfl.javass.jass.Card.Color;
import ch.epfl.javass.jass.Card.Rank;

class MctsPlayerTest {
    
    Map<PlayerId, String> playerNames;
    
    public MctsPlayerTest() {        
        playerNames = new HashMap<>();
        for (PlayerId pId: PlayerId.ALL)
            playerNames.put(pId, pId.name());
    }

    @Test
    void testParagraphe1_3() {
        TurnState state = TurnState.initial(Card.Color.SPADE, Score.INITIAL, PlayerId.PLAYER_1);
        state = state.withNewCardPlayed(Card.of(Card.Color.SPADE, Card.Rank.JACK));
        
        CardSet hand = CardSet.EMPTY
                .add(Card.of(Color.SPADE, Rank.EIGHT))
                .add(Card.of(Color.SPADE, Rank.NINE))
                .add(Card.of(Color.SPADE, Rank.TEN))
                .add(Card.of(Color.HEART, Rank.SIX))
                .add(Card.of(Color.HEART, Rank.SEVEN))
                .add(Card.of(Color.HEART, Rank.EIGHT))
                .add(Card.of(Color.HEART, Rank.NINE))
                .add(Card.of(Color.HEART, Rank.TEN))
                .add(Card.of(Color.HEART, Rank.JACK));
        
        MctsPlayer player = new MctsPlayer(PlayerId.PLAYER_2, 0, 100_000);
        assertEquals(Card.of(Card.Color.SPADE, Card.Rank.EIGHT), player.cardToPlay(state, hand));
    }
    
    @Disabled
    @Test
    void test4Mcts() {
        final int seed = 2019;
        Map<PlayerId, Player> players = new HashMap<>();
        
        players.put(PlayerId.PLAYER_1, new PrintingPlayer(new MctsPlayer(PlayerId.PLAYER_1, seed, 100_000)));
        players.put(PlayerId.PLAYER_2, new MctsPlayer(PlayerId.PLAYER_2, seed, 1000));
        players.put(PlayerId.PLAYER_3, new MctsPlayer(PlayerId.PLAYER_3, seed, 100_000));
        players.put(PlayerId.PLAYER_4, new MctsPlayer(PlayerId.PLAYER_4, seed, 1000));

        JassGame g = new JassGame(seed, players, playerNames);
        while (!g.isGameOver())
            g.advanceToEndOfNextTrick();
    }
    /*
    @Disabled
    @Test
    void test2000Games() {        
        int winsT1 = 0;
        int winsT2 = 0;
        
        Map<PlayerId, Player> players = createMapMctsPlayers(2019, 1000);
        
        final long start = System.currentTimeMillis();
        for (int i = 1000 ; i < 3000 ; ++i) {
            System.out.println("RunningGame " + (i - 1000 + 1) + "/" + 2000);
            
            JassGame g = new JassGame(i, players, playerNames);
            while (!g.isGameOver())
                g.advanceToEndOfNextTrick();
            
            if (g.getTeamWithMostPoints() == TeamId.TEAM_1)
                ++winsT1;
            else
                ++winsT2;
        }
        final float tempsEcoule = (System.currentTimeMillis() - start) / 1000.f;
        
        System.out.println(" - Wins of team 1 : " + winsT1);
        System.out.println(" - Wins of team 2 : " + winsT2);
        
        System.out.println("Temps écoulé : " + tempsEcoule);
    }
    */
    
    @SuppressWarnings("unused")
    private static Map<PlayerId, Player> createMapMctsPlayers(int seed, int iterations) {
        Map<PlayerId, Player> players = new HashMap<>();
        
        for (PlayerId pId: PlayerId.ALL) {
            Player p;
            if (pId == PlayerId.PLAYER_1)
                p = new MctsPlayer(pId, seed, iterations);
            else if (pId == PlayerId.PLAYER_3)
                p = new MctsPlayer(pId, seed, iterations);
            else 
                p = new RandomPlayer(seed);
            
            players.put(pId, p);
        }
        return players;
    }
}
