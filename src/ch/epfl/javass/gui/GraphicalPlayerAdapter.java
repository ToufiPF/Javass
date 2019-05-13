package ch.epfl.javass.gui;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.Card.Color;
import ch.epfl.javass.jass.CardSet;
import ch.epfl.javass.jass.Player;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.jass.Score;
import ch.epfl.javass.jass.TeamId;
import ch.epfl.javass.jass.Trick;
import ch.epfl.javass.jass.TurnState;
import javafx.application.Platform;

/**
 * GraphicalPlayerAdapter Une classe permettant d'adapter 
 *      l'interface graphique pour en faire un joueur
 * @author Amaury Pierre (296498)
 * @author Aurélien Clergeot (302592)
 */
public final class GraphicalPlayerAdapter implements Player {
    private final HandBean hb = new HandBean();
    private final ScoreBean sb = new ScoreBean();
    private final TrickBean tb = new TrickBean();
    private final ArrayBlockingQueue<Card> cardQueue = new ArrayBlockingQueue<>(1);
    private final ArrayBlockingQueue<Color> trumpQueue = new ArrayBlockingQueue<>(1);
    private GraphicalPlayerView graphicalInterface;

    @Override
    public Card cardToPlay(TurnState state, CardSet hand) {
        Card c;
        
        // Les observables doivent être modifiés dans le thread JavaFX 
        // pour éviter les problèmes de compétition entre les thread
        Platform.runLater(() -> hb.setPlayableCards(state.trick().playableCards(hand)));
        
        // Plus pratique que cardQueue.take() : pas de try/catch à gérer
        do { c = cardQueue.poll(); } while (c == null);
        
        Platform.runLater(() -> hb.setPlayableCards(CardSet.EMPTY));
        return c;
    }

    @Override
    public Color chooseTrump(CardSet hand) {
        Color trump;
        Platform.runLater(() -> graphicalInterface.setMustChooseToTrue());
        do { trump = trumpQueue.poll(); } while (trump == null); 
        return trump;
    }
    
    @Override
    public void setPlayers(PlayerId ownId,
            Map<PlayerId, String> mapNames) {
        graphicalInterface = new GraphicalPlayerView(ownId, mapNames, sb, tb, hb, cardQueue, trumpQueue);
        Platform.runLater(() -> { graphicalInterface.createStage().show(); });
    }

    @Override
    public void setTrump(Card.Color trump) {
        Platform.runLater(() -> { tb.setTrump(trump); });
    }

    @Override
    public void setWinningTeam(TeamId winningTeam) {
        Platform.runLater(() -> { sb.setWinningTeam(winningTeam); });
    }

    @Override
    public void updateHand(CardSet newHand) {
        Platform.runLater(() -> { hb.setHand(newHand); });
    }

    @Override
    public void updateScore(Score newScore) {
        for(TeamId t : TeamId.ALL) {
            Platform.runLater(() -> {
                sb.setGamePoints(t, newScore.gamePoints(t)); 
                sb.setTotalPoints(t, newScore.totalPoints(t)); 
                sb.setTurnPoints(t, newScore.turnPoints(t)); });
        }
    }

    @Override
    public void updateTrick(Trick newTrick) {
        Platform.runLater(() -> {tb.setTrick(newTrick); });
    }
}
