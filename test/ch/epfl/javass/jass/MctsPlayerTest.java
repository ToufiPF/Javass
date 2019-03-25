package ch.epfl.javass.jass;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import ch.epfl.javass.gametest.RandomPlayer;
import ch.epfl.javass.jass.Card.Color;
import ch.epfl.javass.jass.Card.Rank;

class MctsPlayerTest {
    
    Map<PlayerId, Player> playersVAure;
    Map<PlayerId, Player> playersV1;
    Map<PlayerId, Player> playersV2;
    Map<PlayerId, String> playerNames;

    //Original seed : 2019L
    static final long RNG_SEED = 0;
    static final int ITERATIONS = 10_000;
    static final double WAIT_TIME = 0;
    
    public MctsPlayerTest() {

        playersVAure = new HashMap<>();
        for (PlayerId pId: PlayerId.ALL) {
            Player p;
            if (pId == PlayerId.PLAYER_1)
                p = new MctsPlayer_Aure(pId, RNG_SEED, ITERATIONS);
            else if (pId == PlayerId.PLAYER_3)
                p = new MctsPlayer_Aure(pId, RNG_SEED, ITERATIONS);
            else 
                p = new RandomPlayer(RNG_SEED);
            
            playersVAure.put(pId, p);
        }

        playersV1 = new HashMap<>();
        for (PlayerId pId: PlayerId.ALL) {
            Player p;
            if (pId == PlayerId.PLAYER_1)
                p = new MctsPlayer_V1(pId, RNG_SEED, ITERATIONS);
            else if (pId == PlayerId.PLAYER_3)
                p = new MctsPlayer_V1(pId, RNG_SEED, ITERATIONS);
            else 
                p = new RandomPlayer(RNG_SEED);
            
            playersV1.put(pId, p);
        }

        playersV2 = new HashMap<>();
        for (PlayerId pId: PlayerId.ALL) {
            Player p;
            if (pId == PlayerId.PLAYER_1)
                p = new MctsPlayer_V2(pId, RNG_SEED, ITERATIONS);
            else if (pId == PlayerId.PLAYER_3)
                p = new MctsPlayer_V2(pId, RNG_SEED, ITERATIONS);
            else 
                p = new RandomPlayer(RNG_SEED);
            
            playersV2.put(pId, p);
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
        
        MctsPlayer_Aure playerAure = new MctsPlayer_Aure(PlayerId.PLAYER_2, RNG_SEED, 100_000);
        MctsPlayer_V1 playerV1 = new MctsPlayer_V1(PlayerId.PLAYER_2, RNG_SEED, 100_000);
        MctsPlayer_V2 playerV2 = new MctsPlayer_V2(PlayerId.PLAYER_2, RNG_SEED, 100_000);
        assertEquals(Card.of(Card.Color.SPADE, Card.Rank.EIGHT), playerAure.cardToPlay(state, hand));
        assertEquals(Card.of(Card.Color.SPADE, Card.Rank.EIGHT), playerV1.cardToPlay(state, hand));
        assertEquals(Card.of(Card.Color.SPADE, Card.Rank.EIGHT), playerV2.cardToPlay(state, hand));
    }
    
    @Test
    void tempsGameMoyen() {
        
        final long startAure = System.currentTimeMillis();
        run10GamesWith(playersVAure);
        final float tempsAure = (System.currentTimeMillis() - startAure) / 1000.f;
        System.out.println("temps auré : " + tempsAure);
        
        final long startV1 = System.currentTimeMillis();
        run10GamesWith(playersV1);
        final float tempsV1 = (System.currentTimeMillis() - startV1) / 1000.f;
        System.out.println("Temps v1 : " + tempsV1);
        
        final long startV2 = System.currentTimeMillis();
        run10GamesWith(playersV1);
        final float tempsV2 = (System.currentTimeMillis() - startV2) / 1000.f;
        System.out.println("Temps v2 : " + tempsV2);
    }
    
    private void run10GamesWith(Map<PlayerId, Player> map) {
        for (int i = 0 ; i < 10 ; ++i) {
            System.out.println("RunningGame " + (i + 1) + "/10");
            JassGame g = new JassGame(RNG_SEED + i, map, playerNames);
            while (!g.isGameOver()) {
                g.advanceToEndOfNextTrick();
            }
        }
    }
}
