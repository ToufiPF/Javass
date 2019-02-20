package ch.epfl.javass.jass;

import ch.epfl.javass.bits.Bits32;

/**
 * PackedCard
 * Une classe non instanciable qui fournit des méthodes statiques
 * pour convertir des cartes en int et vice-versa
 * 
 * @author Aurélien Clergeot (302592)
 */
public final class PackedCard {
    private PackedCard() {
    }

    public static final int INVALID = 0b11_1111;

    /**
     * Verifie que l'int passé en argument représente une carte 
     * valide, càd que seuls les 6 bits de poids faibles sont 
     * utilisés, et que l'id du rang soit valide (<=8)
     * @param packedCard (int) l'entier à vérifier
     * @return (boolean) true si l'entier est valide
     */
    public boolean isValid(int packedCard) {
        // Pour qu'un int soit valide en tant que carte, il faut
        // - que les bits d'index 6 à 31 valent 0
        // (ie. Bits32.extract(packedCard, 6, 32-6) == 0)
        // - que l'id du rang soit inferieur ou égal à 8
        // (ie. Bits32.extract(packedCard, 0, 4) <= 8)

        return Bits32.extract(packedCard, 6, 32-6) == 0 && Bits32.extract(packedCard, 0, 4) <= 8;
    }
    /**
     * Crée l'int représentant la carte donnée
     * (sa couleur et son rang)
     * @param c (Card.Color) la couleur de la carte
     * @param r (Card.Rank) le rang de la carte
     * @return (int) la representation en int de la carte
     */
    public int pack(Card.Color c, Card.Rank r) {
        return Bits32.pack(r.ordinal(), 4, c.ordinal(), 2);
    }

    /**
     * Donne la couleur d'une carte à partir de sa 
     * représentation en int
     * @param pkCard (int) la représentation de la carte
     * @return (Card.Color) la couleur de la carte
     */
    public Card.Color color(int pkCard) {
        assert isValid(pkCard);
        return Card.Color.ALL.get(Bits32.extract(pkCard, 4, 2));
    }
    /**
     * Donne le rang d'une carte à partir de sa 
     * représentation en int
     * @param pkCard (int) la représentation de la carte
     * @return (Card.Rank) le rang de la carte
     */
    public Card.Rank rank(int pkCard) {
        assert isValid(pkCard);
        return Card.Rank.ALL.get(Bits32.extract(pkCard, 0, 4));
    }
    /**
     * @param trump
     * @param pkCardL
     * @param pkCardR
     * @return
     */
    public boolean isBetter(Card.Color trump, int pkCardL, int pkCardR) {

        return false;
    }
    /**
     * @param trump
     * @param pkCard
     * @return
     */
    public int points(Card.Color trump, int pkCard) {

        return 0;
    }

    /**
     * @param pkCard
     * @return
     */
    public String toString(int pkCard) {
        return color(pkCard).toString() + rank(pkCard).toString();
    }
}
