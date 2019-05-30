package ch.epfl.javass.gametest;

import java.util.Map;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.Card.Color;
import ch.epfl.javass.jass.CardSet;
import ch.epfl.javass.jass.Player;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.jass.Score;
import ch.epfl.javass.jass.TeamId;
import ch.epfl.javass.jass.Trick;
import ch.epfl.javass.jass.TurnState;

public final class PrintingPlayer implements Player {
    private final Player underlyingPlayer;
    private String mName = null;

    public PrintingPlayer(Player underlyingPlayer) {
        this.underlyingPlayer = underlyingPlayer;
    }

    @Override
    public Card cardToPlay(TurnState state, CardSet hand) {
        System.out.print("C'est à moi de jouer (" + mName + ")... Je joue : ");
        Card c = underlyingPlayer.cardToPlay(state, hand);
        System.out.println(c + " - (au choix dans : "
                + state.trick().playableCards(hand).toString() + ").");
        return c;
    }

    @Override
    public Color chooseTrump(CardSet hand) {
        Card.Color trump = underlyingPlayer.chooseTrump(hand);
        System.out.println("Je choisis l'atout : " + trump);
        return trump;
    }

    @Override
    public void setPlayers(PlayerId ownId, Map<PlayerId, String> mapNames) {
        mName = mapNames.get(ownId);
        System.out.println("Les joueurs sont : ");
        for (Map.Entry<PlayerId, String> e : mapNames.entrySet())
            System.out.println(" \u2022 " + (e.getKey().ordinal() + 1) + ": "
                    + e.getValue() + (e.getKey() == ownId ? " (moi)" : ""));
        underlyingPlayer.setPlayers(ownId, mapNames);
    }

    @Override
    public void setTrump(Color trump) {
        System.out.println("La nouvelle couleur atout est : " + trump);
        underlyingPlayer.setTrump(trump);
    }

    @Override
    public void setWinningTeam(TeamId winningTeam) {
        System.out.println("EQUIPE GAGNANTE : " + winningTeam);
        underlyingPlayer.setWinningTeam(winningTeam);
    }

    @Override
    public void updateHand(CardSet newHand) {
        System.out.println("Ma nouvelle main : " + newHand.toString());
        underlyingPlayer.updateHand(newHand);
    }

    @Override
    public void updateScore(Score newScore) {
        System.out.println("Scores : " + newScore.toString());
        underlyingPlayer.updateScore(newScore);
    }

    @Override
    public void updateTrick(Trick newTrick) {
        System.out.println("Pli " + newTrick.toString());
        underlyingPlayer.updateTrick(newTrick);
    }
}