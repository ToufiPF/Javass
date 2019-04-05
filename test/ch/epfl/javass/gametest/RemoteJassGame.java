package ch.epfl.javass.gametest;

import java.util.HashMap;
import java.util.Map;

import ch.epfl.javass.jass.JassGame;
import ch.epfl.javass.jass.MctsPlayer;
import ch.epfl.javass.jass.Player;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.net.RemotePlayerClient;
import ch.epfl.javass.net.RemotePlayerServer;

public class RemoteJassGame {

    public static void main(String[] args) throws Exception {
        Map<PlayerId, Player> players = new HashMap<>();
        Map<PlayerId, String> playerNames = new HashMap<>();

        //Original seed : 2019L
        final long RNG_SEED = 2019;
        
        RemotePlayerServer serv = new RemotePlayerServer(new MctsPlayer(PlayerId.PLAYER_1, RNG_SEED, 100_000), 51148);
        Thread serverThread = new Thread(serv);
        serverThread.start();

        for (PlayerId pId: PlayerId.ALL) {
            Player player;
            if (pId == PlayerId.PLAYER_1)
                player = new PrintingPlayer(new RemotePlayerClient("localhost", serv.getPort()));
            else 
                player = new RandomPlayer(RNG_SEED);

            players.put(pId, player);
            playerNames.put(pId, pId.name());        
        }
        
        JassGame game = new JassGame(RNG_SEED, players, playerNames);
        while (!game.isGameOver()) {
            game.advanceToEndOfNextTrick();
            System.out.println("---");
        }
    }
}
