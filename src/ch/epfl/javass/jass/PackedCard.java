package ch.epfl.javass.jass;

import ch.epfl.javass.bits.Bits32;
import ch.epfl.javass.jass.Card.Rank;

/**
 * PackedCard Une classe non instanciable qui fournit des méthodes statiques
 * pour convertir des cartes en int et vice-versa
 *
 * @author Amaury Pierre (296498)
 * @author Aurélien Clergeot (302592)
 */
public final class PackedCard {
    /**
     * Represente un empaquetage de carte invalide
     */
    public static final int INVALID = 0b11_1111;

    private static final int RANK_START = 0;
    private static final int RANK_SIZE = 4;

    private static final int COLOR_START = 4;
    private static final int COLOR_SIZE = 2;

    /**
     * Donne la couleur d'une carte à partir de sa représentation en int
     *
     * @param pkCard
     *            (int) la représentation de la carte
     * @return (Card.Color) la couleur de la carte
     */
    public static Card.Color color(int pkCard) {
        assert isValid(pkCard);
        return Card.Color.ALL
                .get(Bits32.extract(pkCard, COLOR_START, COLOR_SIZE));
    }

    /**
     * Compare les deux cartes données et retourne true si et seulement si celle
     * de gauche est meilleure, en sachant que la couleur des atouts vaut trump.
     *
     * @param trump
     *            (Card.Color) la couleur des atouts
     * @param pkCardL
     *            (int) la carte de gauche
     * @param pkCardR
     *            (int) la carte de droite
     * @return (boolean) true ssi la carte de gauche est meilleure
     */
    public static boolean isBetter(Card.Color trump, int pkCardL, int pkCardR) {
        // Asserts are done in color()
        Card.Color lColor = color(pkCardL);
        Card.Color rColor = color(pkCardR);

        // Si les deux couleurs sont les mêmes
        if (lColor == rColor) {
            Card.Rank lRank = rank(pkCardL);
            Card.Rank rRank = rank(pkCardR);
            // Si ce sont des atouts
            if (lColor == trump)
                return lRank.trumpOrdinal() > rRank.trumpOrdinal();
            // Sinon
            return lRank.ordinal() > rRank.ordinal();
        }

        // Si les deux couleurs sont différentes
        // il faut que la carte de gauche soit
        // un atout pour être meilleure
        return lColor == trump;
    }

    /**
     * Verifie que l'int passé en argument représente une carte valide, càd que
     * seuls les 6 bits de poids faibles sont utilisés, et que l'id du rang soit
     * valide (<=8)
     *
     * @param packedCard
     *            (int) l'entier à vérifier
     * @return (boolean) true si l'entier est valide
     */
    public static boolean isValid(int packedCard) {
        // Pour qu'un int soit valide en tant que carte, il faut
        // - que les bits d'index 6 à 31 valent 0
        // (ie. Bits32.extract(packedCard, 6, 32-6) == 0)
        // - que l'id du rang soit inferieur ou égal à 8
        // (ie. Bits32.extract(packedCard, 0, 4) <= 8)

        return Bits32.extract(packedCard, 6, 32 - 6) == 0 && Bits32
                .extract(packedCard, RANK_START, RANK_SIZE) < Rank.COUNT;
    }

    /**
     * Crée l'int représentant la carte donnée (sa couleur et son rang)
     *
     * @param c
     *            (Card.Color) la couleur de la carte
     * @param r
     *            (Card.Rank) le rang de la carte
     * @return (int) la representation en int de la carte
     */
    public static int pack(Card.Color c, Card.Rank r) {
        return Bits32.pack(r.ordinal(), RANK_SIZE, c.ordinal(), COLOR_SIZE);
    }

    /**
     * Donne la valeur en points de la carte donnée
     *
     * @param trump
     *            (Card.Color) la couleur des atouts
     * @param pkCard
     *            (int) la carte à évaluer
     * @return (int) la valeur de la carte en points
     */
    public static int points(Card.Color trump, int pkCard) {
        // Assert done in color()
        // En cas d'atouts :
        if (color(pkCard) == trump) {
            switch (rank(pkCard)) {
            case ACE:
                return 11;
            case KING:
                return 4;
            case QUEEN:
                return 3;
            case JACK:
                return 20;
            case TEN:
                return 10;
            case NINE:
                return 14;
            default:
                return 0;
            }
        }
        // Sinon :
        switch (rank(pkCard)) {
        case ACE:
            return 11;
        case KING:
            return 4;
        case QUEEN:
            return 3;
        case JACK:
            return 2;
        case TEN:
            return 10;
        default:
            return 0;
        }
    }

    /**
     * Donne le rang d'une carte à partir de sa représentation en int
     *
     * @param pkCard
     *            (int) la représentation de la carte
     * @return (Card.Rank) le rang de la carte
     */
    public static Card.Rank rank(int pkCard) {
        assert isValid(pkCard);
        return Card.Rank.ALL.get(Bits32.extract(pkCard, RANK_START, RANK_SIZE));
    }

    /**
     * Donne la représentation d'une carte sous forme d'une chaîne de caractères
     * composée du symbole de la couleur et du nom abrégé du rang
     *
     * @param pkCard
     *            (int) la carte à afficher
     * @return (String) représentation de la carte
     */
    public static String toString(int pkCard) {
        return color(pkCard).toString() + rank(pkCard).toString();
    }

    private PackedCard() {
    }
}
