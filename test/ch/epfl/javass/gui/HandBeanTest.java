package ch.epfl.javass.gui;

import org.junit.jupiter.api.Test;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.Card.Color;
import ch.epfl.javass.jass.Card.Rank;
import ch.epfl.javass.jass.CardSet;
import javafx.collections.ListChangeListener;

class HandBeanTest {

    @Test
    void testEnonce() {
        HandBean hb = new HandBean();
        ListChangeListener<Card> listener = e -> System.out.println(e);
        hb.handProperty().addListener(listener);

        System.out.println(hb.handProperty());

        CardSet h = CardSet.EMPTY.add(Card.of(Color.SPADE, Rank.SIX))
                .add(Card.of(Color.SPADE, Rank.NINE))
                .add(Card.of(Color.SPADE, Rank.JACK))
                .add(Card.of(Color.HEART, Rank.SEVEN))
                .add(Card.of(Color.HEART, Rank.ACE))
                .add(Card.of(Color.DIAMOND, Rank.KING))
                .add(Card.of(Color.DIAMOND, Rank.ACE))
                .add(Card.of(Color.CLUB, Rank.TEN))
                .add(Card.of(Color.CLUB, Rank.QUEEN));
        hb.setHand(h);

        System.out.println(hb.handProperty());

        while (!h.isEmpty()) {
            h = h.remove(h.get(0));
            hb.setHand(h);
        }
        System.out.println(hb.handProperty());
    }

}
