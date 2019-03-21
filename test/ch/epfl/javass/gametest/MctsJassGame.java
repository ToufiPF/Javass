package ch.epfl.javass.gametest;

import java.util.HashMap;
import java.util.Map;

import ch.epfl.javass.jass.JassGame;
import ch.epfl.javass.jass.MctsPlayer;
import ch.epfl.javass.jass.PacedPlayer;
import ch.epfl.javass.jass.Player;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.jass.TeamId;

public final class MctsJassGame {
    public static void main(String[] args) {
        Map<PlayerId, Player> players = new HashMap<>();
        Map<PlayerId, String> playerNames = new HashMap<>();

        for (PlayerId pId: PlayerId.ALL) {
            Player player;
            if (pId.team() == TeamId.TEAM_1) {
                player = new PrintingPlayer(new PacedPlayer(new MctsPlayer(pId, 2019, 5000), 0.f));
                //player = new PacedPlayer(new MctsPlayer(pId, 2019, 1000), 0.f);
            }
            else {
                player = new RandomPlayer(2019);
            }
            players.put(pId, player);
            playerNames.put(pId, pId.name());
        }

        //Original seed : 2019
        JassGame g = new JassGame(2019, players, playerNames);
        while (!g.isGameOver()) {
            g.advanceToEndOfNextTrick();
            System.out.println("----");
        }
    }
}