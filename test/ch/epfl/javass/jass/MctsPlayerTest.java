package ch.epfl.javass.jass;

import static org.junit.Assert.assertEquals;

import org.junit.jupiter.api.Test;

import ch.epfl.javass.jass.Card.Color;
import ch.epfl.javass.jass.Card.Rank;

class MctsPlayerTest {

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
        
        MctsPlayer player1 = new MctsPlayer(PlayerId.PLAYER_2, 0, 100_000);
        Card playedCard = player1.cardToPlay(state, hand);
        assertEquals(Card.of(Card.Color.SPADE, Card.Rank.EIGHT), playedCard);
    }

}
