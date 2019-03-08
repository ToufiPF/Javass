package ch.epfl.javass.jass;

import static ch.epfl.test.TestRandomizer.RANDOM_ITERATIONS;
import static ch.epfl.test.TestRandomizer.newRandom;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.SplittableRandom;

import org.junit.jupiter.api.Test;

import ch.epfl.javass.bits.Bits32;
import ch.epfl.javass.bits.Bits64;
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
                assertEquals(PackedCard.pack(c, r), PackedCardSet.get(
                        PackedCardSet.singleton(PackedCard.pack(c, r)), 0));
    }

    @Test
    void testGetWorksOnComplexSets() {
        SplittableRandom rng = newRandom();
        ArrayList<Integer> cards = new ArrayList<>();

        for (int i = 0; i < RANDOM_ITERATIONS; ++i) {
            long cardSet = 0L;
            int nbCardsInSet = rng.nextInt(Long.SIZE);
            cards.clear();
            for (int j = 0; j < nbCardsInSet; ++j) {
                int packedCard = PackedCard.pack(
                        Card.Color.ALL.get(rng.nextInt(Card.Color.COUNT)),
                        Card.Rank.ALL.get(rng.nextInt(Card.Rank.COUNT)));
                if (!cards.contains(packedCard))
                    cards.add(packedCard);
                cardSet = PackedCardSet.add(cardSet, packedCard);
            }
            Collections.sort(cards);
            for (int j = 0; j < PackedCardSet.size(cardSet); ++j)
                assertEquals(cards.get(j).intValue(),
                        PackedCardSet.get(cardSet, j));
        }
    }

    @Test
    void testTrumpAboveReturnsEmptySetForJack() {
        for (Card.Color c : Card.Color.ALL)
            assertEquals(0L, PackedCardSet
                    .trumpAbove(PackedCard.pack(c, Card.Rank.JACK)));
    }

    @Test
    void testTrumpAboveWorks() {
        for (Card.Color c : Card.Color.ALL) {
            for (Card.Rank r : Card.Rank.ALL) {
                assertTrue(() -> {
                    long setAbove = PackedCardSet
                            .trumpAbove(PackedCard.pack(c, r));
                    for (Card.Rank r2 : Card.Rank.ALL) {
                        if (r.trumpOrdinal() >= r2.trumpOrdinal()
                                && PackedCardSet.contains(setAbove,
                                        PackedCard.pack(c, r2)))
                            return false;
                        if (r.trumpOrdinal() < r2.trumpOrdinal()
                                && !PackedCardSet.contains(setAbove,
                                        PackedCard.pack(c, r2)))
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
        for (int i = 0; i < RANDOM_ITERATIONS; ++i) {
            int nbCardsInSet = rng.nextInt(Long.SIZE);
            cardSet = PackedCardSet.EMPTY;

            for (int j = 0; j < nbCardsInSet; ++j) {
                int packedCard = PackedCard.pack(
                        Card.Color.ALL.get(rng.nextInt(Card.Color.COUNT)),
                        Card.Rank.ALL.get(rng.nextInt(Card.Rank.COUNT)));
                cardSet = PackedCardSet.add(cardSet, packedCard);
            }
            final long cardSet2 = cardSet;
            assertTrue(() -> {
                for (int j = 0; j < PackedCardSet.size(cardSet2); ++j) {
                    long subSet = PackedCardSet.subsetOfColor(cardSet2,
                            PackedCard.color(PackedCardSet.get(cardSet2, j)));
                    for (int h = 0; h < PackedCardSet.size(subSet); ++h)
                        if (!PackedCard.color(PackedCardSet.get(subSet, h))
                                .equals(PackedCard
                                        .color(PackedCardSet.get(cardSet2, j))))
                            return false;
                }
                return true;
            });
        }
    }

    @Test
    void isValidWorksWithAllValidNumbers() {
        for (int i = 0; i < (1L << 9); ++i) {
            for (int j = 0; j < (1L << 9); ++j) {
                for (int k = 0; k < (1L << 9); ++k) {
                    for (int l = 0; l < (1L << 9); ++l) {
                        int m = Bits32.pack(i, 16, j, 16);
                        int n = Bits32.pack(k, 16, l, 16);
                        long o = Bits64.pack(m, 32, n, 32);
                        assertTrue(PackedCardSet.isValid(o));
                    }
                }
            }
        }
    }

    @Test
    void isValidFailsWithAllUnvalidNumbers() {
        for (int i = 1; i < (1L << 7); ++i) {
            for (int j = 1; j < (1L << 7); ++j) {
                for (int k = 1; k < (1L << 7); ++k) {
                    for (int l = 1; l < (1L << 7); ++l) {
                        long m = (l << 57 | k << 41 | j << 25 | i << 9);
                        assertFalse(PackedCardSet.isValid(m));
                    }
                }
            }
        }
    }

    private static Card.Color[] getAllColors() {
        return new Card.Color[] { Card.Color.SPADE, Card.Color.HEART,
                Card.Color.DIAMOND, Card.Color.CLUB };
    }

    private static Card.Rank[] getAllRanks() {
        return new Card.Rank[] { Card.Rank.SIX, Card.Rank.SEVEN,
                Card.Rank.EIGHT, Card.Rank.NINE, Card.Rank.TEN, Card.Rank.JACK,
                Card.Rank.QUEEN, Card.Rank.KING, Card.Rank.ACE, };
    }

    private static Card[] getAllCards() {
        Card[] allCards = new Card[36];
        int i = 0;
        for (Card.Color c : getAllColors()) {
            for (Card.Rank r : getAllRanks()) {
                allCards[i++] = Card.of(c, r);
            }
        }
        return allCards;
    }

    @Test
    void singletonAndGetWork() {
        for (Card c : getAllCards()) {
            long singleton = PackedCardSet.singleton(c.packed());
            assertEquals(1, Long.bitCount(singleton));
            assertEquals(c.packed(), PackedCardSet.get(singleton, 0));
        }
    }

    @Test
    void isEmptyWorks() {
        assertTrue(PackedCardSet.isEmpty(PackedCardSet.EMPTY));
        assertTrue(PackedCardSet.isEmpty(0L));
        for (Card c : getAllCards()) {
            assertFalse(
                    PackedCardSet.isEmpty(PackedCardSet.singleton(c.packed())));
        }
    }

    @Test
    void addAndContainsWork() {
        long set = PackedCardSet.EMPTY;
        for (Card c : getAllCards()) {
            assertFalse(PackedCardSet.contains(set, c.packed()));
            set = PackedCardSet.add(set, c.packed());
            assertTrue(PackedCardSet.contains(set, c.packed()));
        }
    }

    @Test
    void removeAndContainsWork() {
        long set = PackedCardSet.ALL_CARDS;
        for (Card c : getAllCards()) {
            assertTrue(PackedCardSet.contains(set, c.packed()));
            set = PackedCardSet.remove(set, c.packed());
            assertFalse(PackedCardSet.contains(set, c.packed()));
        }
    }

    @Test
    void complementWorksWithEmptyFullSet() {
        assertEquals(PackedCardSet.EMPTY,
                PackedCardSet.complement(PackedCardSet.ALL_CARDS));
        assertEquals(PackedCardSet.ALL_CARDS,
                PackedCardSet.complement(PackedCardSet.EMPTY));

    }

}
