/*
 * Auteur : Amaury Pierre
 * Date :   18 févr. 2019
 */
package ch.epfl.javass.jass;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class Card {
    /**
     * Color
     * Une classe imbriquée énumérant les couleurs des cartes
     * 
     * @author Amaury Pierre (296498)
     */
    public enum Color {
        SPADE("\u2660"), HEART("\u2661"), DIAMOND("\u2662"), CLUB("\u2663");

        private String symbol;
        public static final List<Color> ALL = Collections
                .unmodifiableList(Arrays.asList(values()));
        public static final int COUNT = 4;

        private Color(String symbol) {
            this.symbol = symbol;
        }

        public String toString() {
            return symbol;
        }
    }

    /**
     * Rank
     * Une classe imbriquée énumérant les rangs des cartes
     * 
     * @author Amaury Pierre (296498)
     */
    public enum Rank {
        SIX("6"),
        SEVEN("7"),
        EIGHT("8"),
        NINE("9"),
        TEN("10"),
        JACK("J"),
        QUEEN("Q"),
        KING("K"),
        ACE("A");

        private String rank;
        public static final List<Rank> ALL = Collections
                .unmodifiableList(Arrays.asList(values()));
        public static final int COUNT = 9;
        
        private Rank(String rank) {
            this.rank = rank;
        }
        
        /**
         * @return la position de la carte d'atout ayant ce rang dans l'ordre des cartes d'atout
         */
        public int trumpOrdinal() {
            switch (rank) {
            case "9": return 7;
            case "J": return 8;
            case "10": return 3;
            case "Q": return 4;
            case "K": return 5;
            case "A": return 6;
            default : return ordinal();
            }
        }
        
        public String toString() {
            return rank;
        }
    }
}
