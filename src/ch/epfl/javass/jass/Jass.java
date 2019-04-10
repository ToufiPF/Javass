/*
 * Auteur : Amaury Pierre
 * Date :   18 févr. 2019
 */
package ch.epfl.javass.jass;

/**
 * Jass 
 * Une interface contenant diverses constantes utiles pour la suite du programme
 * 
 * @author Amaury Pierre (296498) 
 * @author Aurélien Clergeot (302592)
 */
public interface Jass {
    /** Taille maximale de la main d'un joueur */
    public final static int HAND_SIZE = 9;
    /** Nombre de plis dans un tour */
    public final static int TRICKS_PER_TURN = 9;
    /** Nombre de points requis pour gagner */
    public final static int WINNING_POINTS = 1000;
    /** Nombre de points bonus quand une équipe remporte tous les plis d'un tour */
    public final static int MATCH_ADDITIONAL_POINTS = 100;
    /** Nombre de points bonus pour le dernier pli */
    public final static int LAST_TRICK_ADDITIONAL_POINTS = 5;
}
