package ch.epfl.javass;

import java.util.Arrays;
import java.util.List;

/**
 * PlayerSpecificator
 * Un enum contenant diverses informations concernant
 * les spécificateurs des joueurs et leurs arguments.
 * 
 * @author Amaury Pierre (296498)
 * @author Aurélien Clergeot (302592)
 */
public enum PlayerSpecificator {
    HUMAN("h", 1, 2, "Humain"), SIMULATED("s", 1, 3, "Simulé"), 
    REMOTE("r", 1, 3, "Distant");
    
    /** Liste de tous les PlayerSpecificator */
    public static final List<PlayerSpecificator> ALL = Arrays.asList(values());

    private final String spec;
    private final int minFields;
    private final int maxFields;
    private final String name;

    private PlayerSpecificator(String specificator, int minFields,
            int maxFields, String name) {
        this.spec = specificator;
        this.minFields = minFields;
        this.maxFields = maxFields;
        this.name = name;
    }
    
    /**
     * Donne le nom en français du type de joueur correspondant
     * @return (String) type en français
     */
    public String frenchName() {
        return name;
    }
    
    /**
     * Donne le nombre de champs maximal pour ce type de joueur
     * @return (int) nombre d'arguments maximal du type
     */
    public int maxNbFields() {
        return maxFields;
    }
    
    /**
     * Donne le nombre de champs minimal pour ce type de joueur
     * @return (int) nombre d'arguments minimal du type
     */
    public int minNbFields() {
        return minFields;
    }
    
    /**
     * Donne le spécificateur du type de joueur
     * @return (String) spécificateur du type
     */
    public String specificator() {
        return spec;
    }
}
