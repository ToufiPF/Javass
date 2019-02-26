package ch.epfl.javass.jass;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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

    private Map<Integer, Long> trumpAboveMap = computeTrumpAbove();

    private Map<Integer, Long> computeTrumpAbove() {
        Map<Integer, Long> trumpAboveRank = new HashMap<Integer, Long>();

        for (Card.Color color : Card.Color.ALL) {
            for (Card.Rank rankL : Card.Rank.ALL) {
                for (Card.Rank rankR : Card.Rank.ALL) {
                    if (rankL.trumpOrdinal() > rankR.trumpOrdinal()) {
                        int pkCardLeft = PackedCard.pack(color, rankL);
                        trumpAboveRank.put(pkCardLeft, trumpAboveRank.get(pkCardLeft) | singleton(PackedCard.pack(color, rankR)));
                    }
                }
            }
        }
        return trumpAboveRank;
    }
    
    /**
     * Méthode publique vérifiant que le long pkCardSet est valide, càd qu0aucun des 28 bits inutilisés ne vaut 1
     * @param pkCardSet (long) l'ensemble des cartes à vérifier
     * @return true (boolean) si l'ensemble est valide
     */
    public boolean isValid(long pkCardSet) {
        return Bits64.extract(pkCardSet, 9, 7) == 0
                && Bits64.extract(pkCardSet, 25, 7) == 0
                && Bits64.extract(pkCardSet, 41, 7) == 0
                && Bits64.extract(pkCardSet, 57, 7) == 0;
    }
    
    public long trumpAbove(int pkCard) {
        return trumpAboveMap.get(pkCard);
    }
    
    public long singleton(int pkCard) {
        return 1L << pkCard;
    }
    
    public boolean isEmpty(long pkCardSet) {
        return pkCardSet == EMPTY;
    }
    
    public int size(long pkCardSet) {
        return Long.bitCount(pkCardSet);
    }
}
