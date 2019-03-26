package ch.epfl.javass.gametest;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.CardSet;
import ch.epfl.javass.jass.MctsPlayer;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.jass.Score;
import ch.epfl.javass.jass.TurnState;

public final class MctsJassGame {
    /*
    public static void main(String[] args) {
        Map<PlayerId, Player> players = new HashMap<>();
        Map<PlayerId, String> playerNames = new HashMap<>();

        //Original seed : 2019L
        final long RNG_SEED = 0;
        final int ITERATIONS = 10_000;
        final double WAIT_TIME = 0;

        for (PlayerId pId: PlayerId.ALL) {
            Player player;
            if (pId == PlayerId.PLAYER_1)
                player = new PrintingPlayer(new PacedPlayer(new MctsPlayer(pId, RNG_SEED, ITERATIONS), WAIT_TIME));
            else if (pId == PlayerId.PLAYER_3)
                player = new PacedPlayer(new MctsPlayer(pId, RNG_SEED, ITERATIONS), WAIT_TIME);
            else 
                player = new RandomPlayer(RNG_SEED);

            players.put(pId, player);
            playerNames.put(pId, pId.name());
        }

        final int NB_GAMES = 1;
        float tempsMoyen = 0.f;
        for (int i = 0 ; i < NB_GAMES ; ++i) {
            final long startTime = System.currentTimeMillis();
            JassGame g = new JassGame(RNG_SEED, players, playerNames);
            while (!g.isGameOver()) {
                g.advanceToEndOfNextTrick();
                System.out.println("----");
            }
            final long endTime = System.currentTimeMillis();
            tempsMoyen += endTime - startTime;
        }
        tempsMoyen /= NB_GAMES;
        System.out.println("Temps écoulé (Algo terminal) : " + tempsMoyen / 1000.f + "s.");
    }
    */

    
    public static void main(String[] args) {
        TurnState state = TurnState.initial(Card.Color.SPADE, Score.INITIAL, PlayerId.PLAYER_1);
        state = state.withNewCardPlayed(Card.of(Card.Color.SPADE, Card.Rank.JACK));

        CardSet hand = CardSet.EMPTY
                .add(Card.of(Card.Color.SPADE, Card.Rank.EIGHT))
                .add(Card.of(Card.Color.SPADE, Card.Rank.NINE))
                .add(Card.of(Card.Color.SPADE, Card.Rank.TEN))
                .add(Card.of(Card.Color.HEART, Card.Rank.SIX))
                .add(Card.of(Card.Color.HEART, Card.Rank.SEVEN))
                .add(Card.of(Card.Color.HEART, Card.Rank.EIGHT))
                .add(Card.of(Card.Color.HEART, Card.Rank.NINE))
                .add(Card.of(Card.Color.HEART, Card.Rank.TEN))
                .add(Card.of(Card.Color.HEART, Card.Rank.JACK));

        MctsPlayer player2 = new MctsPlayer(PlayerId.PLAYER_2, 0, 100_000);
        final long startTime = System.currentTimeMillis();

        Card playedCard = player2.cardToPlay(state, hand);
        final long endTime = System.currentTimeMillis();
        long tempsMoyen = endTime - startTime;
        System.out.println(tempsMoyen/1000.f);
        System.out.println(playedCard);
        //assertEquals(Card.of(Card.Color.SPADE, Card.Rank.EIGHT), playedCard);
    }
    

}
