package ch.epfl.javass.jass;

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
    public static final long ALL_CARDS = 0b111111111000000011111111100000001111111110000000111111111L;

    private PackedCard[][] trumpAboveTab = computeTrumpAbove();

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

    private PackedCard[][] computeTrumpAbove() {
        PackedCard[][] trumpAboveRankTab = new PackedCard[Card.Rank.COUNT][Card.Rank.COUNT];

        for (int i = 0; i < Card.Rank.COUNT; ++i) {

            for (int j = 0; j < Card.Rank.COUNT; ++j) {
                if (Card.Rank.ALL.get(i).trumpOrdinal() < Card.Rank.ALL.get(j)
                        .trumpOrdinal()) {
                    int cardRank = Card.Rank.ALL.get(j).hashCode();
                }

            }
        }
        return trumpAboveRankTab;
    }
    
    public long singleton(int pkCard) {
        return 1L << pkCard;
    }
    
    public boolean isEmpty(long pkCardSet) {
        return pkCardSet == EMPTY;
    }
    
    public int size() {
        
    }
}
