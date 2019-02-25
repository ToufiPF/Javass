package ch.epfl.javass.jass;

import java.util.List;

import ch.epfl.javass.Preconditions;

/**
 * @author Toufi
 */
public final class CardSet {
    /**
     * Represente un ensemble vide de carte
     */
    public static final CardSet EMPTY = new CardSet(PackedCardSet.EMPTY);
    /**
     * Represente l'ensemble de toutes les cartes
     */
    public static final CardSet ALL_CARDS = new CardSet(PackedCardSet.ALL_CARDS);

    private final long mPkCardSet;

    private CardSet(long pkCardSet) {
        mPkCardSet = pkCardSet;
    }

    /**
     * Methode statique pour créer un nouveau CardSet à partir
     * d'une liste de Card
     * @param cards (List<Card>) la liste de cartes comprises dans l'ensemble
     * @return (CardSet) l'ensemble comprenant les cartes de cards 
     */
    public static CardSet of(List<Card> cards) {
        long pkSet = PackedCardSet.EMPTY;
        for (Card c : cards)
            pkSet = PackedCardSet.add(pkSet, c.packed());
        return ofPacked(pkSet);
    }
    /**
     * Methode statique pour créer un nouveau CardSet
     * à partir de sa version empaquetée
     * @param pkCardSet (long) la version paquetée du CardSet
     * @return (CardSet) l'ensemble correspondant à pkCardSet
     */
    public static CardSet ofPacked(long pkCardSet) {
        Preconditions.checkArgument(PackedCardSet.isValid(pkCardSet));
        return new CardSet(pkCardSet);
    }

    /**
     * Retourne la version empaquetée du CardSet
     * @return (long) la version paquetée de ce CardSet
     */
    public long packed() {
        return mPkCardSet;
    }
    /**
     * Donne le nombre de cartes de ce CardSet
     * @return (int) nombre de cartes dans ce CardSet
     */
    public int size() {
        return PackedCardSet.size(mPkCardSet);
    }
    
    /**
     * Retourne la carte à l'index donné de ce CardSet
     * @param index (int) index de la carte à retourner
     * @return (Card) la carte à l'index donné
     */
    public Card get(int index) {
        return Card.ofPacked(PackedCardSet.get(mPkCardSet, index));
    }

    /**
     * Retourne cet ensemble de cartes,
     * auquel on a ajouté card
     * @param card (Card) la carte à ajouter
     * @return (CardSet) le nouvel ensemble avec card ajoutée
     */
    public CardSet add(Card card) {
        return new CardSet(PackedCardSet.add(mPkCardSet, card.packed()));
    }

    /**
     * Retourne cet ensemble de cartes,
     * auquel on a retiré card
     * @param card (Card) la carte à retirer
     * @return (CardSet) le nouvel ensemble avec card retirée
     */
    public CardSet remove(Card card) {
        return new CardSet(PackedCardSet.remove(mPkCardSet, card.packed()));
    }

    /**
     * Verifie si ce CardSet contient la carte donnée
     * @param card (Card) carte à chercher
     * @return (boolean) true si ce CardSet contient card, false sinon
     */
    public boolean contains(Card card) {
        return PackedCardSet.contains(mPkCardSet, card.packed());
    }

    /**
     * Donne le complement de ce CardSet,
     * càd l'ensemble des cartes qui ne sont pas
     * contenues dans celui-ci
     * @return (CardSet) le complement de ce CardSet
     */
    public CardSet complement() {
        return new CardSet(PackedCardSet.complement(mPkCardSet));
    }

    /**
     * Donne l'union de ce CardSet et de other
     * @param other (CardSet) le 2e CardSet
     * @return (CardSet) un nouveau CardSet correspondant à l'union des deux autres
     */
    public CardSet union(CardSet other) {
        return new CardSet(PackedCardSet.union(mPkCardSet, other.packed()));
    }
    /**
     * Donne l'intersection de ce CardSet et de other
     * @param other (CardSet) le 2e CardSet
     * @return (CardSet) un nouveau CardSet correspondant à l'intersection des deux autres
     */
    public CardSet intersection(CardSet other) {
        return new CardSet(PackedCardSet.intersection(mPkCardSet, other.packed()));
    }
    /**
     * Donne la difference entre ce CardSet et other,
     * càd les cartes qui sont dans ce CardSet, mais pas dans other
     * @param other (CardSet) le 2e CardSet
     * @return (CardSet) un nouveau CardSet correspondant à la difference des deux autres
     */
    public CardSet difference(CardSet other) {
        return new CardSet(PackedCardSet.difference(mPkCardSet, other.packed()));
    }
    /**
     * Donne le CardSet contenant toutes les cartes de la couleur donnée
     * @param color (Card.Color) la couleur voulue
     * @return (CardSet) l'ensemble de cartes de couleur color
     */
    public CardSet subsetOfColor(Card.Color color) {
        return new CardSet(PackedCardSet.subsetOfColor(color));
    }
    
    @Override
    public String toString() {
        return PackedCardSet.toString(mPkCardSet);
    }
    @Override
    public boolean equals(Object obj) {
        if (obj.getClass() == CardSet.class) {
            CardSet otherCardSet = (CardSet) obj;
            return mPkCardSet == otherCardSet.packed();
        }
        return false;
    }
    @Override
    public int hashCode() {
        return Long.hashCode(mPkCardSet);
    }
}
