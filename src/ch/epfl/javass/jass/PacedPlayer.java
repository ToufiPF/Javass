package ch.epfl.javass.jass;

import java.util.Map;

import ch.epfl.javass.Preconditions;
import ch.epfl.javass.jass.Card.Color;

/**
 * PacedPlayer, une classe public et finale qui permet de s'assurer qu'un joueur
 * met un temps minimum avant de jouer.
 *
 * @author Amaury Pierre (296498)
 * @author Aurélien Clergeot (302592)
 */
public final class PacedPlayer implements Player {

    private final Player mUnderPlayer;
    private final long mMinTime;

    /**
     * Construit un PacedPlayer se comportant de la même manière que
     * underlyingPlayer, à l'exception que la méthode cardToPlay met toujours un
     * temps minimum avant de retourner sa carte
     *
     * @param underlyingPlayer
     *            (Player) le joueur sous-jacent
     * @param minTimeInSecs
     *            (double) le temps minimum pour la méthode cardToPlay, en
     *            secondes
     * @throws IllegalArgumentException
     *             si minTimeInSecs < 0
     */
    public PacedPlayer(Player underlyingPlayer, double minTimeInSecs)
            throws IllegalArgumentException {
        Preconditions.checkArgument(minTimeInSecs >= 0);

        mUnderPlayer = underlyingPlayer;
        mMinTime = (long) (minTimeInSecs * 1000);
    }

    @Override
    public Card cardToPlay(TurnState state, CardSet hand) {
        long startTime = System.currentTimeMillis();
        Card card = mUnderPlayer.cardToPlay(state, hand);
        long delta = System.currentTimeMillis() - startTime;

        if (delta < mMinTime) {
            try {
                Thread.sleep(mMinTime - delta);
            } catch (InterruptedException e) {
                /* ignore */
            }
        }
        return card;
    }

    @Override
    public Color chooseTrump(CardSet hand) {
        return mUnderPlayer.chooseTrump(hand);
    }

    @Override
    public void setPlayers(PlayerId ownId, Map<PlayerId, String> mapNames) {
        mUnderPlayer.setPlayers(ownId, mapNames);
    }

    @Override
    public void setTrump(Color trump) {
        mUnderPlayer.setTrump(trump);
    }

    @Override
    public void setWinningTeam(TeamId winningTeam) {
        mUnderPlayer.setWinningTeam(winningTeam);
    }

    @Override
    public void updateHand(CardSet newHand) {
        mUnderPlayer.updateHand(newHand);
    }

    @Override
    public void updateScore(Score newScore) {
        mUnderPlayer.updateScore(newScore);
    }

    @Override
    public void updateTrick(Trick newTrick) {
        mUnderPlayer.updateTrick(newTrick);
    }
}
