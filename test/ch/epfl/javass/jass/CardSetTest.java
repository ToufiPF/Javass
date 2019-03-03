package ch.epfl.javass.jass;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

class CardSetTest {

    @Test
    void testOfNullListCard() {
        ArrayList<Card> cards = null;
        assertEquals(CardSet.EMPTY, CardSet.of(cards));
    }

}
