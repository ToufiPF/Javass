package ch.epfl.javass.net;

/**
 * JassCommand
 * Enum représentant les commandes utilisées pour 
 * communiquer en réseau entre un client et un serveur
 * 
 * @author Amaury Pierre (296498) 
 * @author Aurélien Clergeot (302592)
 */
public enum JassCommand {
    SET_PLAYERS("PLRS"), SET_TRUMP("TRMP"), SET_WINNING_TEAM("WINR"), 
    UPDATE_HAND("HAND"), UPDATE_TRICK("TRCK"), UPDATE_SCORE("SCOR"),
    CARD_TO_PLAY("CARD");
    
    /** Nombre de commandes **/
    public static final int COUNT = JassCommand.values().length;

    /**
     * Retourne l'enum correspondant à la commande donnée
     * @param cmd (String) une commande du type "PLRS", "TRMP"...
     * @return (JassCommand) l'enum correspondant
     * @throws IllegalArgumentException si le String en argument ne 
     * correspondant à aucun enum
     */
    public static JassCommand valueOfByCommand(String cmd) throws IllegalArgumentException {
        for (JassCommand e : values())
            if (e.cmd.equals(cmd))
                return e;
        
        throw new IllegalArgumentException("Unknown command : " + cmd);
    }

    
    private final String cmd;
    
    private JassCommand(String str) {
        this.cmd = str;
    }
    
    public String command() {
        return cmd;
    }
}
