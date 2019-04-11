package ch.epfl.javass.gui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.junit.jupiter.api.Test;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.jass.Trick;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;

public class TrickBeanTest {

    static int totalCalls = 0;

    @Test
    void trumpWorks() {
        TrickBean bean = new TrickBean();
        assertNull(bean.trump().get());
        bean.setTrick(Trick.firstEmpty(Card.Color.SPADE, PlayerId.PLAYER_3));
        assertNull(bean.trump().get());

        bean.setTrump(Card.Color.DIAMOND);
        assertEquals(Card.Color.DIAMOND, bean.trump().get());
    }

    @Test
    void winningPlayerIsNullWhenTrickEmpty() {
        TrickBean bean = new TrickBean();
        assertNull(bean.winningPlayer().get());
        bean.setTrump(Card.Color.CLUB);
        assertNull(bean.winningPlayer().get());
        bean.setTrick(Trick.firstEmpty(Card.Color.DIAMOND, PlayerId.PLAYER_2));
        assertNull(bean.winningPlayer().get());
    }

    @Test
    void setTrickWorks() {
        TrickBean bean = new TrickBean();

        bean.setTrick(Trick.firstEmpty(Card.Color.HEART, PlayerId.PLAYER_3));
        for (PlayerId p : PlayerId.ALL)
            assertNull(bean.trick().get(p));

        Trick trick = Trick.firstEmpty(Card.Color.DIAMOND, PlayerId.PLAYER_2);
        trick = trick.withAddedCard(Card.of(Card.Color.SPADE, Card.Rank.JACK));
        trick = trick.withAddedCard(Card.of(Card.Color.SPADE, Card.Rank.QUEEN));
        trick = trick.withAddedCard(Card.of(Card.Color.SPADE, Card.Rank.TEN));

        bean.setTrick(trick);
        ObservableMap<PlayerId, Card> cards = bean.trick();
        for (Map.Entry<PlayerId, Card> e : cards.entrySet()) {
            int index = e.getKey().ordinal() - trick.player(0).ordinal();
            if (index < 0)
                index += PlayerId.COUNT;
            if (e.getValue() != null) {
                assertEquals(trick.player(index), e.getKey());
                assertEquals(trick.card(index), e.getValue());
            }
            else {
                assertTrue(index >= trick.size());
            }
        }

        bean.setTrick(Trick.firstEmpty(Card.Color.HEART, PlayerId.PLAYER_3));
        for (PlayerId p : PlayerId.ALL)
            assertNull(bean.trick().get(p));
    }

    @Test
    void trickListener() {
        totalCalls = 0;
        TrickBean bean = new TrickBean();
        MapChangeListener<PlayerId, Card> listener = (e) -> {
            System.out.println(e);
            ++totalCalls;
        };
        bean.trick().addListener(listener);

        bean.setTrick(Trick.firstEmpty(Card.Color.HEART, PlayerId.PLAYER_3));

        Trick trick = Trick.firstEmpty(Card.Color.DIAMOND, PlayerId.PLAYER_2);
        trick = trick.withAddedCard(Card.of(Card.Color.SPADE, Card.Rank.JACK));
        trick = trick.withAddedCard(Card.of(Card.Color.SPADE, Card.Rank.QUEEN));
        trick = trick.withAddedCard(Card.of(Card.Color.DIAMOND, Card.Rank.QUEEN));

        bean.setTrick(trick);
        assertEquals(3, totalCalls);
    }
    
    @Test
    void trumpListener() {
        totalCalls = 0;
        TrickBean bean = new TrickBean();
        bean.trump().addListener((e) -> {
            System.out.println(e);
            ++totalCalls;
        });
        bean.setTrump(Card.Color.DIAMOND);
        assertEquals(Card.Color.DIAMOND, bean.trump().get());
        bean.setTrump(Card.Color.SPADE);
        bean.setTrump(Card.Color.SPADE);
        assertEquals(2, totalCalls);
    }
}
