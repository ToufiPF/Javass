package ch.epfl.javass.jass;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * JassGame, représente une partie de Jass
 *
 * @author Amaury Pierre (296498) 
 * @author Aurélien Clergeot (302592)
 */
public final class JassGame {
    
    private final Map<PlayerId, Player> mMapPlayers;
    private final Map<PlayerId, String> mMapPlayerNames;
    
    private final Random mShuffleRng;
    private final Random mTrumpRng;
    
    private TurnState mTurnState;
    
    /**
     * Construit un JassGame à partir de la seed et des
     * joueurs donnés en argument
     * @param rngSeed (long) la seed du générateur pseudo-aléatoire
     * @param players (Map<PlayerId, Player>) la map des joueurs
     * @param playerNames (Map<PlayerId, String>) la map des noms des joueurs
     */
    public JassGame(long rngSeed, Map<PlayerId, Player> players, Map<PlayerId, String> playerNames) {
        mMapPlayers = Collections.unmodifiableMap(new EnumMap<>(players));
        mMapPlayerNames = Collections.unmodifiableMap(new EnumMap<>(playerNames));
        
        Random rng = new Random(rngSeed);
        mShuffleRng = new Random(rng.nextLong());
        mTrumpRng = new Random(rng.nextLong());
    }
    
    /**
     * Vérifie si la partie est terminée
     * @return (boolean) true ssi la partie est finie
     */
    public boolean isGameOver() {
        return false;
    }
    
    /**
     * Avance l'état du jeu jusqu'à la fin du prochain pli
     * Ne fait rien si la partie est terminée
     * (ne ramasse PAS le pli courant)
     */
    public void advanceToEndOfNextTrick() {
        if (isGameOver())
            return;
    }
    
    private List<Card> getShuffledCards() {
        List<Card> cards = new ArrayList<Card>();
        for (Card.Color c : Card.Color.ALL)
            for (Card.Rank r : Card.Rank.ALL)
                cards.add(Card.of(c, r));
        
        Collections.shuffle(cards, mShuffleRng);
        return cards;
    }
}
