package ch.epfl.javass.jass;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Type énuméré permettant d'identifier les différents joueurs
 * 
 * @author Amaury Pierre (296498)
 */
public enum PlayerId {
    PLAYER_1, PLAYER_2, PLAYER_3, PLAYER_4;

    // Liste immuable contenant toutes les valeurs du type énuméré PlayerId
    public static final List<PlayerId> ALL = Collections
            .unmodifiableList(Arrays.asList(values()));

    // Constante du nombre de valeur du type énuméré PlayerId
    public static final int COUNT = 4;

    /**
     * Méthode retournant l'équipe à laquelle appartient le joueur auquel on
     * l'applique.
     * 
     * @return l'équipe à laquelle appartient le joueur à auquel on applique la
     *         méthode
     */
    public TeamId team() {
        return TeamId.ALL.get((this.ordinal()) % 2);
    }
}
