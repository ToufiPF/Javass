package ch.epfl.javass.jass;

/**
 * Jass Une interface contenant diverses constantes utiles pour la suite du
 * programme
 *
 * @author Amaury Pierre (296498)
 * @author Aurélien Clergeot (302592)
 */
public interface Jass {
    /** Taille maximale de la main d'un joueur */
    public final static int HAND_SIZE = 9;
    /** Nombre de plis dans un tour */
    public final static int TRICKS_PER_TURN = 9;
    /** Points requis pour gagner */
    public final static int WINNING_POINTS = 1000;
    /** Points bonus quand une équipe remporte tous les plis d'un tour */
    public final static int MATCH_ADDITIONAL_POINTS = 100;
    /** Points bonus pour le dernier pli */
    public final static int LAST_TRICK_ADDITIONAL_POINTS = 5;

    /** Temps d'attente à la fin d'un pli */
    public static final long WAIT_TIME_TRICK_END = 1000;

    /** Nom par défault des joueurs */
    public static final String[] DEFAULT_NAMES = { "Aline", "Bastien",
            "Colette", "David" };
    /** Temps d'attente minimum pour les joueurs simulés */
    public static final double WAIT_TIME_MCTS_PLAYER = 2.;
    /** Nombre d'itérations par défault pour l'algorithme des joueurs simulés */
    public static final int DEFAULT_ITERATIONS = 10_000;
    /** Adresse IP par défault des joueurs distants */
    public static final String DEFAULT_IP = "localhost";
}
