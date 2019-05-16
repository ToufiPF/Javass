package ch.epfl.javass.gametest;

import java.util.HashMap;
import java.util.Map;

import ch.epfl.javass.jass.JassGame;
import ch.epfl.javass.jass.PacedPlayer;
import ch.epfl.javass.jass.Player;
import ch.epfl.javass.jass.PlayerId;

public final class RandomJassGame {
    public static void main(String[] args) {
        Map<PlayerId, Player> players = new HashMap<>();
        Map<PlayerId, String> playerNames = new HashMap<>();

        for (PlayerId pId : PlayerId.ALL) {
            Player player = new RandomPlayer(2019);
            if (pId == PlayerId.PLAYER_1)
                player = new PrintingPlayer(new PacedPlayer(player, 0));
            players.put(pId, player);
            playerNames.put(pId, pId.name());
        }

        // Original seed : 2019
        JassGame g = new JassGame(2019, players, playerNames);
        while (!g.isGameOver()) {
            g.advanceToEndOfNextTrick();
            System.out.println("----");
        }
    }
}