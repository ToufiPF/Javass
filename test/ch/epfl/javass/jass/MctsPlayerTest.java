package ch.epfl.javass.jass;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import ch.epfl.javass.gametest.RandomPlayer;
import ch.epfl.javass.jass.Card.Color;
import ch.epfl.javass.jass.Card.Rank;

class MctsPlayerTest {
    
    Map<PlayerId, Player> players;
    Map<PlayerId, String> playerNames;

    //Original seed : 2019L
    static final long RNG_SEED = 0;
    static final int ITERATIONS = 10;
    static final double WAIT_TIME = 0;
    
    public MctsPlayerTest() {

        players = new HashMap<>();
        for (PlayerId pId: PlayerId.ALL) {
            Player p;
            if (pId == PlayerId.PLAYER_1)
                p = new MctsPlayer(pId, RNG_SEED, ITERATIONS);
            else if (pId == PlayerId.PLAYER_3)
                p = new MctsPlayer(pId, RNG_SEED, ITERATIONS);
            else 
                p = new RandomPlayer(RNG_SEED);
            
            players.put(pId, p);
        }
        
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
        
        MctsPlayer player = new MctsPlayer(PlayerId.PLAYER_2, RNG_SEED, 100_000);
        assertEquals(Card.of(Card.Color.SPADE, Card.Rank.EIGHT), player.cardToPlay(state, hand));
    }
    
    @Test
    void test1000Games() {
        final long start = System.currentTimeMillis();
        runXGames(1000);
        final float temps = (System.currentTimeMillis() - start) / 1000.f;
        System.out.println("Temps écoulé : " + temps);
    }
    
    private void runXGames(int nbGames) {
        int winsT1 = 0;
        int winsT2 = 0;
        for (int i = 0 ; i < nbGames ; ++i) {
            System.out.println("RunningGame " + (i + 1) + "/" + nbGames);
            JassGame g = new JassGame(RNG_SEED + i, players, playerNames);
            while (!g.isGameOver()) {
                g.advanceToEndOfNextTrick();
            }
            if (g.getTeamWithMostPoints() == TeamId.TEAM_1)
                ++winsT1;
            else
                ++winsT2;
        }
        System.out.println(" - Wins of team 1 : " + winsT1);
        System.out.println(" - Wins of team 2 : " + winsT2);
    }
}
