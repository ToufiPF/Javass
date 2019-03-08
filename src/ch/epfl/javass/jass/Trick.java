package ch.epfl.javass.jass;

import ch.epfl.javass.Preconditions;
import ch.epfl.javass.jass.Card.Color;

/**
 * Trick 
 * Une classe publique, finale et immuable représenant un pli
 * 
 * @author Amaury Pierre (296498)
 */
public final class Trick {

    // représente un pli invalide
    public final static Trick INVALID = new Trick(PackedTrick.INVALID);

    private int pkTrick;

    private Trick(int pkTrick) {
        this.pkTrick = pkTrick;
    }

    private void exceptionIfFull() throws IllegalStateException {
        if (isFull()) {
            throw new IllegalStateException();
        }
    }

    private void exceptionIfNotFull() throws IllegalStateException {
        if(!isFull()) {
            throw new IllegalStateException();
        }
    }
    
    private void exceptionIfEmpty() throws IllegalStateException {
        if (isEmpty()) {
            throw new IllegalStateException();
        }
    }

    /**
     * Méthode publique statique retournant le pli vide d'index 0, d'atout et de
     * premier joueur donnés
     * 
     * @param trump
     *            (Color) la couleur d'atout
     * @param firstPlayer
     *            (PlayerId) le premier joueur à jouer
     * @return (Trick) le pli vide d'index 0, d'atout trump et de premier joueur
     *         firstPlayer
     */
    public static Trick firstEmpty(Color trump, PlayerId firstPlayer) {
        return new Trick(PackedTrick.firstEmpty(trump, firstPlayer));
    }

    /**
     * Méthode publique statique renvoyant le pli correspondant à la version
     * empaquetée donnée
     * 
     * @param packed
     *            (int) la version empaquetée du pli à créer
     * @return (Trick) le pli correspondant à la version empaquetée packed
     */
    public static Trick ofPacked(int packed) {
        Preconditions.checkArgument(PackedTrick.isValid(packed));
        return new Trick(packed);
    }

    /**
     * Méthode publique retournant la version empaqueté du pli
     * 
     * @return (int) la version empaqueté du pli
     */
    public int packed() {
        return pkTrick;
    }

    /**
     * Méthode publique retournant le pli empaqueté vide suivant ce pli
     * 
     * @return (int) le pli empaqueté vide suivant ce pli
     */
    public Trick nextEmpty() {
        exceptionIfNotFull();
        return ofPacked(PackedTrick.nextEmpty(pkTrick));
    }

    /**
     * Méthode publique retournant si le pli est vide
     * 
     * @return true (boolean) si le pli est vide, càd qu'il ne contient aucune
     *         carte
     */
    public boolean isEmpty() {
        return PackedTrick.isEmpty(pkTrick);
    }

    /**
     * Méthode publique retournant si le pli est plein
     * 
     * @return true (boolean) si le pli est plein, càd s'il contient 4 cartes
     */
    public boolean isFull() {
        return PackedTrick.isFull(pkTrick);
    }

    /**
     * Méthode publique retournant si le pli est le dernier du tour
     * 
     * @return true (boolean) si le pli est le dernier du tour, càd si son index
     *         vaut 8
     */
    public boolean isLast() {
        return PackedTrick.isLast(pkTrick);
    }

    /**
     * Méthode publique retournant la taille du pli
     * 
     * @return (int) le nombre de cartes que le pli contient
     */
    public int size() {
        return PackedTrick.size(pkTrick);
    }

    /**
     * Méthode publique retournant la couleur d'atout
     * 
     * @return (Color) la couleur d'atout
     */
    public Color trump() {
        return PackedTrick.trump(pkTrick);
    }

    /**
     * Méthode publique retournant l'index du pli
     * 
     * @return (int) l'index du pli
     */
    public int index() {
        return PackedTrick.index(pkTrick);
    }

    /**
     * Méthode publique retournant le joueur d'index donné dans le pli
     * 
     * @param index
     *            (int) l'index du joueur à retourner
     * @return (PlayerId) le joueur d'index donné dans le pli
     */
    public PlayerId player(int index) {
        Preconditions.checkIndex(index, 4);
        return PackedTrick.player(pkTrick, index);
    }

    /**
     * Méthode publique retournant la carte du pli à l'index donné
     * 
     * @param index
     *            (int) l'index de la carte à retourner
     * @return (Card) la carte du pli à'index donné
     */
    public Card card(int index) {
        Preconditions.checkIndex(index, size());
        return Card.ofPacked(PackedTrick.card(pkTrick, index));
    }

    /**
     * Méthode publique retournant le pli auquel on a ajouté la carte c
     * 
     * @param c
     *            (Card) la carte que l'on veut ajouter au pli
     * @return (Trick) le pli auquel on a ajouté la carte c
     */
    public Trick withAddedCard(Card c) {
        exceptionIfFull();
        return ofPacked(PackedTrick.withAddedCard(pkTrick, c.packed()));
    }

    /**
     * Méthode publique retournant la couleur de base du pli
     * 
     * @return (Color) la couleur de base du pli, càd la couleur de la première
     *         carte jouée
     */
    public Color baseColor() {
        exceptionIfEmpty();
        return PackedTrick.baseColor(pkTrick);
    }

    /**
     * Méthode publique retournant l'ensemble des cartes de la main pouvant être
     * jouées comme prochaine carte du pli
     * 
     * @param hand
     *            (CardSet) la main dont on veut connaître les cartes pouvant
     *            être jouées
     * @return (CardSet) l'ensemble des cartes de la main pouvant être jouées
     *         comme prochaine carte du pli
     */
    public CardSet playableCards(CardSet hand) {
        exceptionIfFull();
        return CardSet
                .ofPacked(PackedTrick.playableCards(pkTrick, hand.packed()));
    }

    /**
     * Méthode publique retournant la valeur du pli
     * 
     * @return (int) la valeur du pli, en comptant les "5 de der"
     */
    public int points() {
        return PackedTrick.points(pkTrick);
    }

    /**
     * Méthode publique retournant le joueur menant le pli
     * 
     * @return (PlayerId) le joueur menant le pli
     */
    public PlayerId winningPlayer() {
        exceptionIfEmpty();
        return PackedTrick.winningPlayer(pkTrick);
    }

    @Override
    public String toString() {
        return PackedTrick.toString(pkTrick);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj.getClass() == Trick.class) {
            Trick otherTrick = (Trick) obj;
            return this.packed() == otherTrick.packed();
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.packed();
    }
}
