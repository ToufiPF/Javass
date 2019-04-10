/*
 * Auteur : Amaury Pierre
 * Date :   18 févr. 2019
 */
package ch.epfl.javass.jass;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import ch.epfl.javass.Preconditions;

/**
 * Card Une classe immuable représentant une carte
 * 
 * @author Amaury Pierre (296498) 
 * @author Aurélien Clergeot (302592)
 */
public final class Card {
    /**
     * Color Une classe imbriquée énumérant les couleurs des cartes
     * 
     * @author Amaury Pierre (296498)
     */
    public enum Color {
        SPADE("\u2660"), HEART("\u2665"), DIAMOND("\u2666"), CLUB("\u2663");

        /** Liste immuable contenant toutes les valeurs du type énuméré Color */
        public static final List<Color> ALL = Collections
                .unmodifiableList(Arrays.asList(values()));

        /** Constante du nombre de valeur du type énuméré Color */
        public static final int COUNT = values().length;


        private final String symbol;
        
        private Color(String symbol) {
            this.symbol = symbol;
        }

        @Override
        public String toString() {
            return symbol;
        }
    }

    /**
     * Rank Une classe imbriquée énumérant les rangs des cartes
     * 
     * @author Amaury Pierre (296498)
     */
    public enum Rank {
        SIX("6"), SEVEN("7"), EIGHT("8"), NINE("9"), TEN("10"), JACK(
                "J"), QUEEN("Q"), KING("K"), ACE("A");
        
        /** Liste immuable contenant toutes les valeurs du type énuméré Rank */
        public static final List<Rank> ALL = Collections
                .unmodifiableList(Arrays.asList(values()));
        
        /** Constante du nombre de valeur du type énuméré Rank */
        public static final int COUNT = values().length;

        
        private String rank;
        
        private Rank(String rank) {
            this.rank = rank;
        }

        /**
         * @return la position de la carte d'atout ayant ce rang dans l'ordre
         *         des cartes d'atout
         */
        public int trumpOrdinal() {
            switch (rank) {
            case "9":
                return 7;
            case "J":
                return 8;
            case "10":
                return 3;
            case "Q":
                return 4;
            case "K":
                return 5;
            case "A":
                return 6;
            default:
                return ordinal();
            }
        }

        @Override
        public String toString() {
            return rank;
        }
    }

    private final int pkCard;

    private Card(int pkCard) {
        this.pkCard = pkCard;
    }

    /**
     * Méthode statique permettant de construire une carte à partir d'un rang et
     * d'une couleur
     * 
     * @param c
     *            (Color)la couleur de la carte à construire
     * @param r
     *            (Rank) le rang de la classe à construire
     * @return (Card) la carte de couleur c et de rang r
     */
    public static Card of(Color c, Rank r) {
        Card card = new Card(PackedCard.pack(c, r));
        return card;
    }

    /**
     * Méthode statique construisant une carte à partir de sa version empaquetée
     * 
     * @param packed
     *            (int) la valeur empaquetée de la carte à construire
     * @return (Card) la carte créée correspondant à la valeur packed
     * @throws IllegalArgumentException
     *             si la valeur empaquetée n'est pas valide
     */
    public static Card ofPacked(int packed) throws IllegalArgumentException {
        Preconditions.checkArgument(PackedCard.isValid(packed));
        Card card = new Card(packed);
        return card;
    }

    /**
     * @return (int) la valeur empaquetée de la carte
     */
    public int packed() {
        return pkCard;
    }

    /**
     * @return (Color) la couleur de la carte
     */
    public Color color() {
        return PackedCard.color(pkCard);
    }

    /**
     * @return (Rank) le rang de la carte
     */
    public Rank rank() {
        return PackedCard.rank(pkCard);
    }

    /**
     * Méthode renvoyant true si et seulement si la carte that est meilleure que
     * le récepteur
     * 
     * @param trump
     *            (Color) la couleur de l'atout
     * @param that
     *            (Card) la carte dont on veut tester la supériorité
     * @return true (boolean) si et seulement si la carte that est meilleure que
     *         le récepteur
     */
    public boolean isBetter(Color trump, Card that) {
        int thatPacked = PackedCard.pack(that.color(), that.rank());
        return PackedCard.isBetter(trump, pkCard, thatPacked);
    }

    /**
     * Méthode renvoyant le nombre de points de la carte, en sachant que trump
     * est la couleur de l'atout
     * 
     * @param trump
     *            (Color) la couleur de l'atout
     * @return (int) le nombre de points de la carte, en sachant que trump est
     *         la couleur de l'atout
     */
    public int points(Color trump) {
        return PackedCard.points(trump, pkCard);
    }
    
    @Override
    public boolean equals(Object that0) {
        if (that0.getClass() == Card.class) {
            Card that0Card = (Card) that0;
            return pkCard == that0Card.packed();
        }
        return false;
    }

    @Override
    public int hashCode() {
        return packed();
    }

    @Override
    public String toString() {
        return PackedCard.toString(pkCard);
    }
}
