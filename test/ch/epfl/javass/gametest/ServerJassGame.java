package ch.epfl.javass.gametest;

import ch.epfl.javass.jass.MctsPlayer;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.net.RemotePlayerServer;

public class ServerJassGame {
    public static void main(String[] args) {
        RemotePlayerServer serv = new RemotePlayerServer(new MctsPlayer(PlayerId.PLAYER_1, 2019L, 100_000));
        serv.run();
    }
}
