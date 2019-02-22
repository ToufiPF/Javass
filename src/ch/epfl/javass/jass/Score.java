package ch.epfl.javass.jass;

import ch.epfl.javass.Preconditions;

/**
 * Score
 * Une classe immuable représentant un score
 * 
 * @author Aurélien Clergeot (302592)
 */
public final class Score {
    
    /**
     * Représente le score initial, avec le nombre de plis,
     * le nombre de points du tour, et le nombre de points de la partie
     * mis à 0 pour chaque team
     */
    public static final Score INITIAL = new Score(PackedScore.INITIAL);

    private final long mPackedScore;

    private Score(long packed) {
        mPackedScore = packed;
    }
    
    /**
     * Methode statique pour créer un Score à partir
     * de sa version empaquetée
     * @param packed (long) la version empaquetée du Score
     * @return (Score) le score correspondant à packed
     */
    public static Score ofPacked(long packed) {
        Preconditions.checkArgument(PackedScore.isValid(packed));

        return new Score(packed);
    }
    
    /**
     * Getter pour la version empaquetée de ce Score
     * @return (long) ce score empaqueté
     */
    public long packed() {
        return mPackedScore;
    }
    
    /**
     * Donne le nombre de plis remportés par la team t dans le tour courant
     * @param t (TeamID) la team qui nous interesse
     * @return (int) le nombre de plis remportés
     */
    public int turnTricks(TeamID t) {
        return PackedScore.turnTricks(mPackedScore, t);
    }

    /**
     * Donne le nombre de points de la team t dans le tour courant
     * @param t (TeamID) la team qui nous interesse
     * @return (int) le nombre de points dans le tour courant
     */
    public int turnPoints(TeamID t) {
        return PackedScore.turnPoints(mPackedScore, t);
    }
    
    /**
     * Donne le nombre de points de la partie (SANS compter le tour courant)
     * @param t (TeamID) la team qui nous interesse
     * @return (int) le nombre de points de la partie
     */
    public int gamePoints(TeamID t) {
        return PackedScore.gamePints(mPackedScore, t);
    }

    /**
     * Donne le nombre de points TOTAL de la partie courante
     * @param t (TeamID) la team qui nous interesse
     * @return (int) le nombre de points TOTAL de la partie
     */
    public int totalPoints(TeamID t) {
        return PackedScore.totalPoints(mPackedScore, t);
    }
    
    /**
     * Retourne un Score mis à jour pour le prochain pli,
     * càd avec trickPoints ajouté au nombre de points du tour
     * @param winner (TeamID) la team qui a remporté le pli
     * @param trickPoints (int) le nombre de points du pli
     * @return (Score) le score du prochain pli
     */
    public Score withAdditionalTrick(TeamID winner, int trickPoints) {
        Preconditions.checkArgument(trickPoints >= 0);
        return new Score(PackedScore.withAdditionnalTrick(mPackedScore, winner, trickPoints));
    }
    /**
     * Retourne un Score mis à jour pour le tour prochain,
     * càd avec les points obtenus lors du tour ajoutés à ceux de
     * la partie, ainsi que le nombre de plis gagnés et de points
     * pour le tour remis à 0
     * @return (Score) le score du prochain tour
     */
    public Score nextTurn() {
        return new Score(PackedScore.nextTurn(mPackedScore));
    }
    
    @Override
    public String toString() {
        return PackedScore.toString(mPackedScore);
    }
    @Override
    public boolean equals(Object obj) {
        if (obj.getClass() == Score.class) {
            Score objScore = (Score) obj;
            return objScore.packed() == this.packed();
        }
        return false;
    }
    @Override
    public int hashCode() {
        return Long.hashCode(mPackedScore);
    }
}
