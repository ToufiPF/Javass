package ch.epfl.javass.net;

public enum JassCommand {
    SET_PLAYERS("PLRS"), SET_TRUMP("TRMP"), SET_WINNING_TEAM("WINR"), 
    UPDATE_HAND("HAND"), UPDATE_TRICK("TRCK"), UPDATE_SCORE("SCOR"),
    CARD_TO_PLAY("CARD");
    
    public static final int COUNT = JassCommand.values().length;
    
    //private final String mStr;
    
    private JassCommand(String str) {
        //mStr = str;
    }
}
