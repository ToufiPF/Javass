package ch.epfl.javass.jass;

import static org.junit.Assert.assertEquals;

import org.junit.jupiter.api.Test;

class MctsPlayerTest {

    @Test
    void testParagraphe1_3() {
        TurnState state = TurnState.initial(Card.Color.SPADE, Score.INITIAL, PlayerId.PLAYER_1);
        state = state.withNewCardPlayed(Card.of(Card.Color.SPADE, Card.Rank.JACK));
        
        CardSet hand = CardSet.EMPTY.add(Card.of(Card.Color.SPADE, Card.Rank.EIGHT));
        hand = hand.add(Card.of(Card.Color.SPADE, Card.Rank.NINE)).add(Card.of(Card.Color.SPADE, Card.Rank.TEN));
        hand = hand.add(Card.of(Card.Color.DIAMOND, Card.Rank.SIX)).add(Card.of(Card.Color.DIAMOND, Card.Rank.KING));
        hand = hand.add(Card.of(Card.Color.DIAMOND, Card.Rank.JACK)).add(Card.of(Card.Color.CLUB, Card.Rank.NINE));
        hand = hand.add(Card.of(Card.Color.CLUB, Card.Rank.SIX)).add(Card.of(Card.Color.DIAMOND, Card.Rank.NINE));
        
        MctsPlayer player1 = new MctsPlayer(PlayerId.PLAYER_2, 0, 100_000);
        Card playedCard = player1.cardToPlay(state, hand);
        assertEquals(Card.of(Card.Color.SPADE, Card.Rank.EIGHT), playedCard);
    }

}
