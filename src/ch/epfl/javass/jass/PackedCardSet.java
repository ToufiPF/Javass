package ch.epfl.javass.jass;

import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

import ch.epfl.javass.bits.Bits64;

/**
 * PackedCardSet Une classe publique, finale et non instanciable permettant de
 * manipuler des ensembles de cartes empaquetés dans des valeurs de type long
 * 
 * @author Amaury Pierre (296498)
 */
public final class PackedCardSet {

    private PackedCardSet() {
    }

    // (long) représente l'ensemble des cartes vide
    public static final long EMPTY = 0L;

    // (long) représente l'ensemble des 36 cartes du Jass
    public static final long ALL_CARDS = 0b0000000111111111_0000000111111111_0000000111111111_0000000111111111L;

    private static Map<Integer, Long> trumpAboveMap = computeTrumpAbove();

    private static long[] subsetOfColorTab = computeSubsetOfColor();

    private static Map<Integer, Long> computeTrumpAbove() {
        Map<Integer, Long> trumpAboveRank = new HashMap<Integer, Long>();

        for (Card.Color color : Card.Color.ALL) {
            for (Card.Rank rankL : Card.Rank.ALL) {
                for (Card.Rank rankR : Card.Rank.ALL) {
                    if (rankL.trumpOrdinal() < rankR.trumpOrdinal()) {
                        int pkCardLeft = PackedCard.pack(color, rankL);
                        trumpAboveRank.put(pkCardLeft,
                                trumpAboveRank.get(pkCardLeft) | singleton(
                                        PackedCard.pack(color, rankR)));
                    }
                }
            }
        }
        return trumpAboveRank;
    }

    private static long[] computeSubsetOfColor() {
        long[] subsetOfColorTab = new long[Card.Color.COUNT];

        for (int i = 0; i < Card.Color.COUNT; ++i) {
            long allCardsOfOneColor = (1L << 9) - 1L;
            allCardsOfOneColor = allCardsOfOneColor << (i * 16);
            subsetOfColorTab[i] = allCardsOfOneColor;
        }
        return subsetOfColorTab;
    }

    /**
     * Méthode publique et statique vérifiant que le long pkCardSet est valide, càd qu0aucun
     * des 28 bits inutilisés ne vaut 1
     * 
     * @param pkCardSet
     *            (long) l'ensemble des cartes à vérifier
     * @return true (boolean) si l'ensemble est valide
     */
    public static boolean isValid(long pkCardSet) {
        return Bits64.extract(pkCardSet, 9, 7) == 0
                && Bits64.extract(pkCardSet, 25, 7) == 0
                && Bits64.extract(pkCardSet, 41, 7) == 0
                && Bits64.extract(pkCardSet, 57, 7) == 0;
    }

    /**
     * Méthode publique et statique retournant un long contenant les cartes stictement plus
     * fortes que la carte donnée, sachant qu'elle est un atout
     * 
     * @param pkCard
     *            (int) la carte d'atout dont on veut conaitre les cartes plus
     *            fortes
     * @return (long) le long contenant les cartes strictement plus fortes que
     *         pkCard
     */
    public static long trumpAbove(int pkCard) {
        assert PackedCard.isValid(pkCard);
        return trumpAboveMap.get(pkCard);
    }

    /**
     * Méthode publique retournant un long contenant uniquement la carte pkCard
     * 
     * @param pkCard
     *            (int) la carte que l'on veut "packer" en long
     * @return (long) le long contenant uniquement la carte pkCard
     */
    public static long singleton(int pkCard) {
        assert PackedCard.isValid(pkCard);
        return 1L << pkCard;
    }

    /**
     * Méthode publique retournant true si un long est vide
     * 
     * @param pkCardSet
     *            (long) l'ensemble de cartes dont on veut savoir s'il est vide
     * @return true (boolean) ssi le pkCardSet est vide, càd qu'il est égal à
     *         EMPTY
     */
    public static boolean isEmpty(long pkCardSet) {
        assert isValid(pkCardSet);
        return pkCardSet == EMPTY;
    }

    /**
     * Méthode publique retournant le nombre de bits du long pkCardSet, et donc
     * le nombre de cartes contenues dans cet ensemble
     * 
     * @param pkCardSet
     *            (long) l'ensemble de cartes dont on veut connaitre la taille
     * @return le nombre de cartes de l'ensemble pkCardSet
     */
    public static int size(long pkCardSet) {
        assert isValid(pkCardSet);
        return Long.bitCount(pkCardSet);
    }

    /**
     * Méthode publique permettant d'obtenir la index-ième carte de l'ensemble
     * pkCardSet (la carte d'index 0 correspond à la carte de bit le plus faible
     * de pkCardSet)
     * 
     * @param pkCardSet
     *            (long) l'ensemble de cartes dont on veut connaitre la
     *            index-ieme carte
     * @param index
     *            (int) la place de la carte que l'on veut connaitre dans
     *            l'ensemble pkCardSet
     * @return (int) la index-ieme dans l'ensemble pkCardSet
     */
    public static int get(long pkCardSet, int index) {
        assert (index >= 0 && index < size(pkCardSet));

        for (int i = 0; i < index; ++i) {
            pkCardSet &= ~Long.lowestOneBit(pkCardSet);
        }

        return Long.numberOfTrailingZeros(pkCardSet);
    }

    /**
     * Méthode publique permettant d'ajouter une carte à un ensemble de cartes
     *
     * @param pkCardSet
     *            (long) l'ensemble de cartes auquel on veut ajouter la carte
     *            pkCard
     * @param pkCard
     *            (int) la carte à ajouter dans l'ensemble pkCardSet
     * @return (long) le long pkCardSet auquel on a ajouté la carte pkCard
     */
    public static long add(long pkCardSet, int pkCard) {
        assert isValid(pkCardSet) && PackedCard.isValid(pkCard);

        return pkCardSet |= singleton(pkCard);
    }

    /**
     * Méthode publique permettant de retirer une carte d'un ensemble de cartes
     * 
     * @param pkCardSet
     *            (long) l'ensemble de cartes auquel on veut retirer la carte
     *            pkcard
     * @param pkCard
     *            (int) la carte à retirer de l'ensemble pkCardSet
     * @return (long) le long pkCardSet auquel on a retiré la carte pkCard
     */
    public static long remove(long pkCardSet, int pkCard) {
        assert isValid(pkCardSet) && PackedCard.isValid(pkCard);

        return pkCardSet &= ~singleton(pkCard);
    }

    /**
     * Méthode publique retournant si la carte pkCard appartient à l'ensemble de
     * cartes pkCardSet
     * 
     * @param pkCardSet
     *            (long) l'ensemble de cartes dont on veut savoir si pkCard en
     *            fait partie
     * @param pkCard
     *            (int) la carte dont on veut savoir si elle fait partie de
     *            l'ensemble pkCardSet
     * @return true (boolean) si l'ensemble empaqueté pkCardSet contient la
     *         carte empaquetée pkCard
     */
    public static boolean contains(long pkCardSet, int pkCard) {
        assert isValid(pkCardSet) && PackedCard.isValid(pkCard);

        return (pkCardSet &= singleton(pkCard)) != 0;
    }

    /**
     * Méthode publique retournant le complément du long pkCardSet
     * 
     * @param pkCardSet
     *            (long) l'ensemble de cartes dont on veut le complément
     * @return (long) le complément de pkCardSet
     */
    public static long complement(long pkCardSet) {
        assert isValid(pkCardSet);

        return ~pkCardSet;
    }

    /**
     * Méthode publique retournant l'union de deux long
     * 
     * @param pkCardSet1
     *            (long) le long dont on veut l'union avec pkCardSet2
     * @param pkCardSet2
     *            (long) le long dont on veut l'union avec pkCardSet1
     * @return (long) l'union de pkCardSet1 et pkCardSet2
     */
    public static long union(long pkCardSet1, long pkCardSet2) {
        assert isValid(pkCardSet1) && isValid(pkCardSet2);

        return pkCardSet1 | pkCardSet2;
    }

    /**
     * Méthode publique retournant l'intersection de deux long
     * 
     * @param pkCardSet1
     *            (long) le long dont on veut l'intersection avec pkCardSet2
     * @param pkCardSet2
     *            (long) le long dont on veut l'intersection avec pkCardSet1
     * @return (long) l'intersection de pkCardSet1 et pkCardSet2
     */
    public static long intersection(long pkCardSet1, long pkCardSet2) {
        assert isValid(pkCardSet1) && isValid(pkCardSet2);

        return pkCardSet1 & pkCardSet2;
    }

    /**
     * Méthode publique retournant la différence des deux long
     * 
     * @param pkCardSet1
     *            (long) le long dont on veut garder les cartes n'appartenant
     *            pas à pkCardSet2
     * @param pkCardSet2
     *            (long) le long avec lequel on veut comparer pkCardSet1
     * @return la différence entre pkCardSet1 et pkCardSet2, càd l'ensemble des
     *         cartes appartenant à pkCardSet1 mais pas à pkCardSet2
     */
    public static long difference(long pkCardSet1, long pkCardSet2) {
        assert isValid(pkCardSet1) && isValid(pkCardSet2);

        return pkCardSet1 & ~pkCardSet2;
    }

    /**
     * Méthode publique retournant les cartes de la couleur color de l'ensemble
     * pkCardSet
     * 
     * @param pkCardSet
     *            (long) l'ensemble dont on veut connaitre les cartes de couleur
     *            color
     * @param color
     *            (Card.Color) la couleur dont on veut connaitre les cartes
     *            appartenant à l'ensemble pkcardSet
     * @return (ong) l'ensemble des cartes de pkCardSet de couleur color
     */
    public static long subsetOfColor(long pkCardSet, Card.Color color) {
        assert isValid(pkCardSet);

        return pkCardSet & subsetOfColorTab[color.ordinal()];
    }

    public static String toString(long pkCardSet) {
        assert isValid(pkCardSet);

        StringJoiner j = new StringJoiner(",", "{", "}");
        for (int i = 0; i < Long.SIZE; ++i) {
            if (contains(pkCardSet, i)) {
                j.add(PackedCard.toString(i));
            }
        }
        return j.toString();
    }
}