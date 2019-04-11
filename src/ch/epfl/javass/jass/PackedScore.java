package ch.epfl.javass.jass;

import ch.epfl.javass.bits.Bits32;
import ch.epfl.javass.bits.Bits64;

/**
 * PackedScore Classe finale non instanciable permettant de manipuler les scores
 * d'une partie
 *
 * @author Amaury Pierre (296498)
 * @author Aurélien Clergeot (302592)
 */
public final class PackedScore {

    /** Représente un score packé initial (tous les scores de toutes les équipes à 0) */
    public static final long INITIAL = 0L;
    
    private static final int TRICK_START = 0;
    private static final int TRICK_SIZE = 4;
    private static final int TURN_START = 4;
    private static final int TURN_SIZE = 9;
    private static final int GAME_START = 13;
    private static final int GAME_SIZE = 11;
    
    private static final int MAX_TURN_POINTS = 257;
    private static final int MAX_GAME_POINTS = Jass.WINNING_POINTS * 2;

    /**
     * Méthode publique et statique retournant le nombre total de points
     * remportés par l'équipe t dans les tours précédents
     *
     * @param pkScore
     *            (long) le score empaqueté du tour courant
     * @param t
     *            (TeamId) l'équipe dont on veut connaitre le nombre total de
     *            points remportés dans les tours précédents
     * @return (int) le nombre total de points remportés par l'équipe t dans les
     *         tours précédents
     */
    public static int gamePoints(long pkScore, TeamId t) {
        assert isValid(pkScore);
        return (int) Bits64.extract(pkScore, GAME_START + t.ordinal() * 32, GAME_SIZE);
    }

    /**
     * Méthode publique statique permettant de vérifier si les scores des 2
     * équipes sont valides
     *
     * @param pkScore
     *            (long) les scores des deux équipes empaquetées
     * @return (boolean) true ssi les scores des deux équipes sont valides
     */
    public static boolean isValid(long pkScore) {
        int pkScore1 = (int) Bits64.extract(pkScore, 0, 32);
        int pkScore2 = (int) Bits64.extract(pkScore, 32, 32);
        return isValid32(pkScore1) && isValid32(pkScore2);
    }

    private static boolean isValid32(int pkScore32) {
        return Bits32.extract(pkScore32, TRICK_START, TRICK_SIZE) <= Jass.TRICKS_PER_TURN
                && Bits32.extract(pkScore32, TURN_START, TURN_SIZE) <= MAX_TURN_POINTS
                && Bits32.extract(pkScore32, GAME_START, GAME_SIZE) <= MAX_GAME_POINTS
                && Bits32.extract(pkScore32, 24, 8) == 0;
    }

    /**
     * Méthode publique statique retournant les scores empaquetés donnés mis à
     * jour pour le prochain tour
     *
     * @param pkScore
     *            (long) les scores empaquetés à mettre à jour
     * @return (long) les scores empaquetés mis à jour
     */
    public static long nextTurn(long pkScore) {
        return pack(0, 0, totalPoints(pkScore, TeamId.TEAM_1), 0, 0,
                totalPoints(pkScore, TeamId.TEAM_2));
    }

    /**
     * Méthode publique statique permettant de créer un long contenant les
     * scores des deux équipes
     *
     * @param turnTricks1
     *            (int) le nombre de plis remportés par l'équipe 1 dans le tour
     *            courant
     * @param turnPoints1
     *            (int) le nombre de points remportés par l'équipe 1 dans le
     *            tour courant
     * @param gamePoints1
     *            (int) le nombre de points remportés par l'équipe 1 dans la
     *            partie courante
     * @param turnTricks2
     *            (int) le nombre de plis remportés par l'équipe 2 dans le tour
     *            courant
     * @param turnPoints2
     *            (int) le nombre de points remportés par l'équipe 2 dans le
     *            tour courant
     * @param gamePoints2
     *            (int) le nombre de points remportés par l'équipe 2 dans la
     *            partie courante
     * @return (long) les scores empaquetés dans un long
     */
    public static long pack(int turnTricks1, int turnPoints1, int gamePoints1,
            int turnTricks2, int turnPoints2, int gamePoints2) {
        return Bits64.pack(pack32(turnTricks1, turnPoints1, gamePoints1), 32,
                pack32(turnTricks2, turnPoints2, gamePoints2), 32);
    }

    private static int pack32(int turnTricks, int turnPoints, int gamePoints) {
        final int packed = Bits32.pack(turnTricks, TRICK_SIZE, turnPoints, TURN_SIZE, gamePoints, GAME_SIZE);
        assert isValid32(packed);
        return packed;
    }

    /**
     * Méthode retournant une représentation des scores sous la forme
     * (Plis_remportés,Points_du_tour,Points_de_la_partie,Points_totaux), et ce
     * pour chaque équipe
     * 
     * @param pkScore
     *            (long) les scores à représenter
     * @return (String) la représentation des scores
     */
    public static String toString(long pkScore) {
        return new StringBuilder().append('(')
                .append(turnTricks(pkScore, TeamId.TEAM_1)).append(',')
                .append(turnPoints(pkScore, TeamId.TEAM_1)).append(',')
                .append(gamePoints(pkScore, TeamId.TEAM_1)).append(',')
                .append(totalPoints(pkScore, TeamId.TEAM_1)).append(")/(")
                .append(turnTricks(pkScore, TeamId.TEAM_2)).append(',')
                .append(turnPoints(pkScore, TeamId.TEAM_2)).append(',')
                .append(gamePoints(pkScore, TeamId.TEAM_2)).append(',')
                .append(totalPoints(pkScore, TeamId.TEAM_2)).append(')').toString();
    }

    /**
     * Méthode publique et statique retournant le nombre total de points de
     * l'équipe t dans le tour courant
     *
     * @param (long)
     *            pkScore le score empaqueté du tour courant
     * @param t
     *            (TeamId) l'équipe dont on veut connaitre le nombre total de
     *            points dans le tour courant
     * @return (int) le nombre total de points remportés par l'équipe t dans le
     *         tour courant
     */
    public static int totalPoints(long pkScore, TeamId t) {
        // l'assertion est faite dans les méthodes gamePoints et turnPoints
        return gamePoints(pkScore, t) + turnPoints(pkScore, t);
    }

    /**
     * Méthode publique et statique retournant le nombre de points remportés par
     * l'équipe t dans le tour courant
     *
     * @param pkScore
     *            (long) le score empaqueté du tour courant
     * @param t
     *            (TeamId) l'équipe dont on veut connaitre le nombre de points
     *            remportés lors du tour courant
     * @return (int) le nombre de points remportés par l'équipe t dans le tour
     *         courant
     */
    public static int turnPoints(long pkScore, TeamId t) {
        assert isValid(pkScore);
        return (int) Bits64.extract(pkScore, TURN_START + t.ordinal() * 32, TURN_SIZE);
    }

    /**
     * Méthode publique statique retournant le nombre de plis remportés par
     * l'équipe t dans le tour courant
     *
     * @param pkScore
     *            (long) le score empaqueté du tour courant
     * @param t
     *            (TeamId) l'équipe dont on veut connaitre le nombre de plis
     *            remportés
     * @return le nombre de plis remportés par l'équipe t dans le tour courant
     */
    public static int turnTricks(long pkScore, TeamId t) {
        assert isValid(pkScore);
        return (int) Bits64.extract(pkScore, TRICK_START + t.ordinal() * 32, TRICK_SIZE);
    }

    /**
     * Méthode retournant les scores empaquetés pkScore mis à jour
     *
     * @param pkScore
     *            (long) les scores empaquetés à mettre à jour
     * @param winningTeam
     *            (TeamId) l'équipe dont on met les points à jour
     * @param trickPoints
     *            (int) les points à ajouter au score de winningTeam
     * @return les scores empaquetés mis à jour (ajout de trickPoints et de
     *         MATCH_ADDITIONAL_POINTS si winningTeam a remporté les 9 plis)
     */
    public static long withAdditionalTrick(long pkScore, TeamId winningTeam,
            int trickPoints) {
        assert (isValid(pkScore) && trickPoints >= 0);

        // ajout d'un pli gagné à l'équipe winningTeam
        final int tricksWon = turnTricks(pkScore, winningTeam) + 1;

        // ajout du score trickPoints au score du tour courant
        int scoreTurn = turnPoints(pkScore, winningTeam) + trickPoints;

        // si le nombre de plis gagnés est de 9, le score du tour augment de 100
        if (tricksWon == Jass.TRICKS_PER_TURN)
            scoreTurn += Jass.MATCH_ADDITIONAL_POINTS;

        // on pack les nouveaux scores dans un long
        if (winningTeam == TeamId.TEAM_1) {
            return Bits64.pack(
                    pack32(tricksWon, scoreTurn,
                            gamePoints(pkScore, TeamId.TEAM_1)),
                    32, Bits64.extract(pkScore, 32, 32), 32);
        }
        return Bits64.pack(Bits64.extract(pkScore, 0, 32), 32, pack32(tricksWon,
                scoreTurn, gamePoints(pkScore, TeamId.TEAM_2)), 32);
    }

    private PackedScore() {
    }
}
