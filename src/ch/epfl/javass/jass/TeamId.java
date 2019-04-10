package ch.epfl.javass.jass;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Type énuméré permettant d'identifier les 2 équipes
 * 
 * @author Amaury Pierre (296498) 
 * @author Aurélien Clergeot (302592)
 */
public enum TeamId {
    TEAM_1, TEAM_2;

    /** Liste immuable contenant toutes les valeurs du type énuméré TeamId */
    public static final List<TeamId> ALL = Collections
            .unmodifiableList(Arrays.asList(values()));

    /** Constante du nombre de valeur du type énuméré TeamId */
    public static final int COUNT = values().length;

    /**
     * Methode permettant de retourner l'équipe adverse à celle-ci
     * @return (TeamId) l'équipe adverse
     */
    public TeamId other() {
        //(si on l'applique sur TEAM_1, la méthode retourne l'équipe à
        //l'emplacement 1-0 = 1, càd TEAM_2)
        return ALL.get(1 - this.ordinal());
    }
}
