package ch.epfl.javass.jass;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.epfl.javass.bits.Bits32;
import ch.epfl.javass.bits.Bits64;
import ch.epfl.javass.jass.Card.Color;
import ch.epfl.javass.jass.Card.Rank;

class PackedCardSetTest {

    // @Test
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

    @Test
    void toStringWorks() {
        long s = PackedCardSet.EMPTY;
        int c1 = PackedCard.pack(Color.HEART, Rank.SIX);
        int c2 = PackedCard.pack(Color.SPADE, Rank.ACE);
        int c3 = PackedCard.pack(Color.SPADE, Rank.SIX);
        s = PackedCardSet.add(s, c1);
        s = PackedCardSet.add(s, c2);
        s = PackedCardSet.add(s, c3);
        System.out.println(PackedCardSet.toString(s));
    }
}