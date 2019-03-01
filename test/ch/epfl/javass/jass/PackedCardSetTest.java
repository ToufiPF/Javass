package ch.epfl.javass.jass;

import static ch.epfl.test.TestRandomizer.RANDOM_ITERATIONS;
import static ch.epfl.test.TestRandomizer.newRandom;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.SplittableRandom;

import org.junit.jupiter.api.Test;

import ch.epfl.javass.jass.Card.Color;
import ch.epfl.javass.jass.Card.Rank;

class PackedCardSetTest {
    
    @Test
    void testToString() {
        long s = PackedCardSet.EMPTY;
        int c1 = PackedCard.pack(Color.HEART, Rank.SIX);
        int c2 = PackedCard.pack(Color.SPADE, Rank.ACE);
        int c3 = PackedCard.pack(Color.SPADE, Rank.SIX);
        s = PackedCardSet.add(s, c1);
        s = PackedCardSet.add(s, c2);
        s = PackedCardSet.add(s, c3);
        System.out.println(PackedCardSet.toString(s));
    }
    
    @Test
    void testGetFailsOnInvalidIndex() {
        assertThrows(AssertionError.class, () -> {
            PackedCardSet.get(0L, 0);
        });
        assertThrows(AssertionError.class, () -> {
            PackedCardSet.get(0b01000L, 1);
        });
        assertThrows(AssertionError.class, () -> {
            PackedCardSet.get(0b01000L, 1);
        });
        assertThrows(AssertionError.class, () -> {
            PackedCardSet.get(0b01110L, -1);
        });
    }
    
    @Test
    void testGetWorksOnSingletons() {
        for (Card.Color c : Card.Color.ALL)
            for (Card.Rank r : Card.Rank.ALL)
                assertEquals(PackedCard.pack(c, r), PackedCardSet.get(PackedCardSet.singleton(PackedCard.pack(c, r)), 0));
    }
    
    @Test 
    void testGetWorksOnComplexSets() {
        SplittableRandom rng = newRandom();
        ArrayList<Integer> cards = new ArrayList<>();
        
        for (int i = 0 ; i < RANDOM_ITERATIONS ; ++i) {
            long cardSet = 0L;
            int nbCardsInSet = rng.nextInt(Long.SIZE);
            cards.clear();
            for (int j = 0 ; j < nbCardsInSet ; ++j) {
                int packedCard = PackedCard.pack(Card.Color.ALL.get(rng.nextInt(Card.Color.COUNT)), Card.Rank.ALL.get(rng.nextInt(Card.Rank.COUNT)));
                if (!cards.contains(packedCard))
                    cards.add(packedCard);
                cardSet = PackedCardSet.add(cardSet, packedCard);
            }
            Collections.sort(cards);
            for (int j = 0 ; j < PackedCardSet.size(cardSet) ; ++j)
                assertEquals(cards.get(j).intValue(), PackedCardSet.get(cardSet, j));
        }
    }
    
    @Test
    void testTrumpAboveReturnsEmptySetForJack() {
        for (Card.Color c : Card.Color.ALL)
            assertEquals(0L, PackedCardSet.trumpAbove(PackedCard.pack(c, Card.Rank.JACK)));
    }
    
    @Test
    void testTrumpAboveWorks() {
        for (Card.Color c : Card.Color.ALL) {
            for (Card.Rank r : Card.Rank.ALL) {
                assertTrue(() -> {
                    long setAbove = PackedCardSet.trumpAbove(PackedCard.pack(c, r));
                    for (Card.Rank r2 : Card.Rank.ALL) {
                        if (r.trumpOrdinal() >= r2.trumpOrdinal() && PackedCardSet.contains(setAbove, PackedCard.pack(c, r2)))
                            return false;
                        if (r.trumpOrdinal() < r2.trumpOrdinal() && !PackedCardSet.contains(setAbove, PackedCard.pack(c, r2)))
                            return false;
                    }
                    return true;
                });
            }
        }
    }
    
    @Test
    void testSubsetOfColorWorksOnComplexSets() {
        SplittableRandom rng = newRandom();

        long cardSet = 0L;
        for (int i = 0 ; i < RANDOM_ITERATIONS ; ++i) {
            int nbCardsInSet = rng.nextInt(Long.SIZE);
            cardSet = PackedCardSet.EMPTY;
            
            for (int j = 0 ; j < nbCardsInSet ; ++j) {
                int packedCard = PackedCard.pack(Card.Color.ALL.get(rng.nextInt(Card.Color.COUNT)), Card.Rank.ALL.get(rng.nextInt(Card.Rank.COUNT)));
                cardSet = PackedCardSet.add(cardSet, packedCard);
            }
            final long cardSet2 = cardSet;
            assertTrue(() -> {
                for (int j = 0 ; j < PackedCardSet.size(cardSet2) ; ++j) {
                    long subSet = PackedCardSet.subsetOfColor(cardSet2, PackedCard.color(PackedCardSet.get(cardSet2, j)));
                    for (int h = 0 ; h < PackedCardSet.size(subSet) ; ++h)
                        if (!PackedCard.color(PackedCardSet.get(subSet, h)).equals(PackedCard.color(PackedCardSet.get(cardSet2, j))))
                            return false;
                }
                return true;
            });
        }
    }
}