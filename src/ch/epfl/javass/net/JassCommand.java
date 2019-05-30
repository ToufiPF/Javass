package ch.epfl.javass.net;

/**
 * JassCommand Enum représentant les commandes utilisées pour communiquer en
 * réseau entre un client et un serveur
 *
 * @author Amaury Pierre (296498)
 * @author Aurélien Clergeot (302592)
 */
public enum JassCommand {
    SET_PLAYERS("PLRS"), CHOOSE_TRUMP("CHOS"), SET_TRUMP(
            "TRMP"), SET_WINNING_TEAM("WINR"), UPDATE_HAND(
                    "HAND"), UPDATE_TRICK("TRCK"), UPDATE_SCORE(
                            "SCOR"), CARD_TO_PLAY("CARD"), CLOSE("CLOS");

    /** Nombre de commandes **/
    public static final int COUNT = values().length;

    /**
     * Retourne l'enum correspondant à la commande donnée
     *
     * @param cmd
     *            (String) une commande du type "PLRS", "TRMP"...
     * @return (JassCommand) l'enum correspondant
     * @throws IllegalArgumentException
     *             si le String en argument ne correspondant à aucun enum
     */
    public static JassCommand valueOfByCommand(String cmd)
            throws IllegalArgumentException {
        for (JassCommand e : values())
            if (e.cmd.equals(cmd))
                return e;

        throw new IllegalArgumentException("Unknown command : " + cmd);
    }

    private final String cmd;

    private JassCommand(String str) {
        this.cmd = str;
    }

    /**
     * Donne la commande correspondant à la valeur de l'enum (String de 4 char
     * du type "PLRS", "TRMP"...)
     *
     * @return (String) commande de l'enum
     */
    public String command() {
        return cmd;
    }
}
