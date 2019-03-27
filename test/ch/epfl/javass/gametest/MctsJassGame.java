package ch.epfl.javass.gametest;

import java.util.HashMap;
import java.util.Map;

import ch.epfl.javass.jass.JassGame;
import ch.epfl.javass.jass.MctsPlayer;
import ch.epfl.javass.jass.PacedPlayer;
import ch.epfl.javass.jass.PackedTrick;
import ch.epfl.javass.jass.Player;
import ch.epfl.javass.jass.PlayerId;

public final class MctsJassGame {
    
    public static void main(String[] args) {
        Map<PlayerId, Player> players = new HashMap<>();
        Map<PlayerId, String> playerNames = new HashMap<>();

        //Original seed : 2019L
        final long RNG_SEED = 2019;
        final int ITERATIONS = 100;
        final double WAIT_TIME = 0;

        for (PlayerId pId: PlayerId.ALL) {
            Player player;
            if (pId == PlayerId.PLAYER_1)
                player = new PrintingPlayer(new PacedPlayer(new MctsPlayer(pId, RNG_SEED, ITERATIONS), WAIT_TIME));
            else if (pId == PlayerId.PLAYER_3)
                player = new PacedPlayer(new MctsPlayer(pId, RNG_SEED, ITERATIONS), WAIT_TIME);
            else 
                player = new RandomPlayer(RNG_SEED);

            players.put(pId, player);
            playerNames.put(pId, pId.name());
        }

        //final int NB_GAMES = 1;
        float tempsMoyen = 0.f;
        int nbVictoiresEquipe2 = 0;
        double nbParties = 0.0;
        for (int i = 1000 ; i < 3000 ; ++i) {
            ++nbParties;
            final long startTime = System.currentTimeMillis();
            JassGame g = new JassGame(RNG_SEED, players, playerNames);
            while (!g.isGameOver()) {
                g.advanceToEndOfNextTrick();
                System.out.println("----");
            }
           //TODO : Compter le nombre de victoires de l'équipe 2
            final long endTime = System.currentTimeMillis();
            tempsMoyen += endTime - startTime;
        }
        tempsMoyen /= nbParties;
        System.out.println("Temps écoulé (Algo terminal) : " + tempsMoyen / 1000.f + "s.");
        System.out.println(nbParties);
    }
}
