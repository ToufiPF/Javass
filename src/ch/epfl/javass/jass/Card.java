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
     * Classe imbriquées énumérant les couleur des cartes
     */
    public enum Color {
        SPADE("\u2660"), 
        HEART("\u2661"), 
        DIAMOND("\u2662"), 
        CLUB("\u2663");

        private String symbol;
        public static final List<Color> ALL = Collections.unmodifiableList(Arrays.asList(values()));
        private static final int COUNT = 4;
        
        private Color(String symbol) {
            this.symbol = symbol;
        }
        
        public String toString() {
            return symbol;
        }
        
    }
}
