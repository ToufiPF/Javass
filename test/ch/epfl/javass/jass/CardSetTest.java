package ch.epfl.javass.jass;

import static ch.epfl.test.TestRandomizer.RANDOM_ITERATIONS;
import static ch.epfl.test.TestRandomizer.newRandom;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.SplittableRandom;

import org.junit.jupiter.api.Test;

class CardSetTest {

    @Test
    void testOfEmptyCardList() {
        ArrayList<Card> cards = new ArrayList<>();
        assertEquals(CardSet.EMPTY, CardSet.of(cards));
    }
    
    @Test
    void testOfNonTrivialCardList() {
        ArrayList<Card> cards = new ArrayList<>();
        SplittableRandom rng = newRandom();
        for (int i = 0 ; i < RANDOM_ITERATIONS ; ++i) {
            final int nbCards = rng.nextInt(CardSet.ALL_CARDS.size());
            for (int j = 0 ; j < nbCards ; ++j)
                if (!cards.contains(Card.of(Card.Color.ALL.get(rng.nextInt(Card.Color.COUNT)), Card.Rank.ALL.get(rng.nextInt(Card.Rank.COUNT)))))
                    cards.add(Card.of(Card.Color.ALL.get(rng.nextInt(Card.Color.COUNT)), Card.Rank.ALL.get(rng.nextInt(Card.Rank.COUNT))));
            
            CardSet set = CardSet.of(cards);
            
            for (Card c : cards)
                assertTrue(set.contains(c));
            for (Card c : cards)
                assertFalse(set.complement().contains(c));
        }
    }

}
