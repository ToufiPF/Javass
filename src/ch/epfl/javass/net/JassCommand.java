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
    
    private JassCommand(String str) {
    }
}
