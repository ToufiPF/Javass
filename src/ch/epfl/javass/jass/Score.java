package ch.epfl.javass.jass;

import ch.epfl.javass.Preconditions;

/**
 * Score Une classe immuable représentant un score
 *
 * @author Amaury Pierre (296498)
 * @author Aurélien Clergeot (302592)
 */
public final class Score {

    /**
     * Représente le score initial, avec le nombre de plis, le nombre de points
     * du tour, et le nombre de points de la partie mis à 0 pour chaque team
     */
    public static final Score INITIAL = new Score(PackedScore.INITIAL);

    /**
     * Methode statique pour créer un Score à partir de sa version empaquetée
     * 
     * @param packed
     *            (long) la version empaquetée du Score
     * @return (Score) le score correspondant à packed
     * @throws IllegalArgumentException
     *             si packed est invalide
     */
    public static Score ofPacked(long packed) throws IllegalArgumentException {
        Preconditions.checkArgument(PackedScore.isValid(packed));
        return new Score(packed);
    }

    private final long mPackedScore;

    private Score(long packed) {
        mPackedScore = packed;
    }

    /**
     * Donne le nombre de points de la team donnée pour cette partie (SANS
     * compter le tour courant)
     * 
     * @param t
     *            (TeamId) la team qui nous interesse
     * @return (int) le nombre de points de la partie
     */
    public int gamePoints(TeamId t) {
        return PackedScore.gamePoints(mPackedScore, t);
    }

    /**
     * Retourne un Score mis à jour pour le tour prochain, càd avec les points
     * obtenus lors du tour ajoutés à ceux de la partie, ainsi que le nombre de
     * plis gagnés et de points pour le tour remis à 0
     * 
     * @return (Score) le score du prochain tour
     */
    public Score nextTurn() {
        return new Score(PackedScore.nextTurn(mPackedScore));
    }

    /**
     * Getter pour la version empaquetée de ce Score
     * 
     * @return (long) ce score empaqueté
     */
    public long packed() {
        return mPackedScore;
    }

    /**
     * Donne le nombre de points TOTAL de la team donnée pour cette partie, càd
     * le nombre de points du tour ajouté au nombre de points de la partie
     * 
     * @param t
     *            (TeamId) la team qui nous interesse
     * @return (int) le nombre de points TOTAL de la partie
     */
    public int totalPoints(TeamId t) {
        return PackedScore.totalPoints(mPackedScore, t);
    }

    /**
     * Donne le nombre de points de la team donnée dans le tour courant
     * 
     * @param t
     *            (TeamId) la team qui nous interesse
     * @return (int) le nombre de points dans le tour courant
     */
    public int turnPoints(TeamId t) {
        return PackedScore.turnPoints(mPackedScore, t);
    }

    /**
     * Donne le nombre de plis remportés par la team donnée dans le tour courant
     * 
     * @param t
     *            (TeamId) la team qui nous interesse
     * @return (int) le nombre de plis remportés
     */
    public int turnTricks(TeamId t) {
        return PackedScore.turnTricks(mPackedScore, t);
    }

    /**
     * Retourne un Score mis à jour pour le prochain pli, càd avec trickPoints
     * ajouté au nombre de points du tour, et un incrément du nombre de plis
     * remportés pour l'équipe gagnante
     * 
     * @param winner
     *            (TeamId) la team qui a remporté le pli
     * @param trickPoints
     *            (int) le nombre de points du pli
     * @return (Score) le score du prochain pli
     * @throws IllegalArgumentException
     *             si trickPoints < 0
     */
    public Score withAdditionalTrick(TeamId winner, int trickPoints)
            throws IllegalArgumentException {
        Preconditions.checkArgument(trickPoints >= 0);
        return new Score(PackedScore.withAdditionalTrick(mPackedScore, winner,
                trickPoints));
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
    
    @Override
    public String toString() {
        return PackedScore.toString(mPackedScore);
    }

}
