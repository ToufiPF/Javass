package ch.epfl.javass.jass;

import ch.epfl.javass.jass.Card.Color;
import ch.epfl.javass.jass.Card.Rank;

class PackedCardSetTest {

    public static void main(String[] args) {
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
