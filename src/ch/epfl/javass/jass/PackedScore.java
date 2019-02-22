package ch.epfl.javass.jass;

import ch.epfl.javass.bits.Bits32;
import ch.epfl.javass.bits.Bits64;

/**
 * PackedScore Classe finale non instanciable permettant de manipuler les scores
 * d'une partie
 * 
 * @author Amaury Pierre (296498) et Aurélien Clergeot (302592)
 */
public final class PackedScore {

    private PackedScore() {
    }

    // constante permettant d'initialiser les scores
    public static final long INITIAL = 0L;

    private static boolean isValid(int pkScore32) {
        return Bits32.extract(pkScore32, 0, 4) <= 9
                && Bits32.extract(pkScore32, 4, 9) <= 257
                && Bits32.extract(pkScore32, 13, 11) <= 2000
                && Bits32.extract(pkScore32, 24, 8) == 0;
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
        return isValid(pkScore1) && isValid(pkScore2);
    }

    private static int pack(int turnTricks, int turnPoints, int gamePoints) {
        assert isValid(
                Bits32.pack(turnTricks, 4, turnPoints, 9, gamePoints, 11));
        return Bits32.pack(turnTricks, 4, turnPoints, 9, gamePoints, 11);
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
        return Bits64.pack(pack(turnTricks1, turnPoints1, gamePoints1), 32,
                pack(turnTricks2, turnPoints2, gamePoints2), 32);
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
        return (int) Bits64.extract(pkScore, 0 + t.ordinal() * 32, 4);
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
        return (int) Bits64.extract(pkScore, 4 + t.ordinal() * 32, 9);
    }

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
        return (int) Bits64.extract(pkScore, 13 + t.ordinal() * 32, 11);
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
    public static long withAdditionnalTrick(long pkScore, TeamId winningTeam,
            int trickPoints) {
        assert (isValid(pkScore) && isValid(trickPoints));

        // ajout d'un pli gagné à l'équipe winningTeam
        int tricksWon = turnTricks(pkScore, winningTeam) + 1;

        // ajout du score trickPoints au score du tour courant
        int scoreTurn = turnPoints(pkScore, winningTeam) + trickPoints;

        // si le nombre de plis gagnés et de 9, le score du tour augment de 100
        if (tricksWon == Jass.TRICKS_PER_TURN) {
            scoreTurn += Jass.MATCH_ADDITIONAL_POINTS;
        }

        // on pack les nouveaux scores dans un long
        if (winningTeam == TeamId.TEAM_1) {
            return Bits64.pack(
                    pack(tricksWon, scoreTurn,
                            gamePoints(pkScore, TeamId.TEAM_1)),
                    32, Bits64.extract(pkScore, 32, 32), 32);
        }
        return Bits64.pack(Bits64.extract(pkScore, 0, 32), 32,
                pack(tricksWon, scoreTurn, gamePoints(pkScore, TeamId.TEAM_2)),
                32);
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
     * Méthode retournant une représentation des scores
     * @param pkScore (long) les scores à représenter 
     * @return (String) la représentation des scores
     */
    public static String toString(long pkScore) {
        return "(" + turnTricks(pkScore, TeamId.TEAM_1) + ","
                + turnPoints(pkScore, TeamId.TEAM_1) + ","
                + gamePoints(pkScore, TeamId.TEAM_1) + ")/("
                + turnTricks(pkScore, TeamId.TEAM_2) + ","
                + turnPoints(pkScore, TeamId.TEAM_2) + ","
                + gamePoints(pkScore, TeamId.TEAM_2) + ")";
    }
}
