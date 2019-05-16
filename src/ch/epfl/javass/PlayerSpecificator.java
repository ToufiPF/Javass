package ch.epfl.javass;

import java.util.Arrays;
import java.util.List;

public enum PlayerSpecificator {
    HUMAN("h", 1, 2, "Humain"), SIMULATED("s", 1, 3, "Simulé"), 
    REMOTE("r", 1, 3, "Distant");

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

    public String frenchName() {
        return name;
    }

    public int maxNbFields() {
        return maxFields;
    }

    public int minNbFields() {
        return minFields;
    }

    public String specificator() {
        return spec;
    }
}
