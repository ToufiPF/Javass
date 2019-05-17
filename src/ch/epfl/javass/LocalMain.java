package ch.epfl.javass;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.sun.javafx.stage.StageHelper;

import ch.epfl.javass.gui.GraphicalPlayerAdapter;
import ch.epfl.javass.jass.Jass;
import ch.epfl.javass.jass.JassGame;
import ch.epfl.javass.jass.MctsPlayer;
import ch.epfl.javass.jass.PacedPlayer;
import ch.epfl.javass.jass.Player;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.net.RemotePlayerClient;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * LocalMain Une classe permettant de lancer une partie locale
 * 
 * @author Amaury Pierre (296498)
 * @author Aurélien Clergeot (302592)
 */
public final class LocalMain extends Application {

    /**
     * Crée une partie et le Thread la faisant tourner à partir de la liste
     * d'arguments donnée
     * 
     * @param args
     *            (List<String>) la liste d'arguments pour lancer la partie
     */
    public static void createGameFromArguments(List<String> args, Stage firstStage) {
        // On commence par générer (ou récupérer, si elle a été fournie) la
        // graine
        Random SEED_GENERATOR = null;
        if (args.size() == PlayerId.COUNT) {
            SEED_GENERATOR = new Random();
        } else if (args.size() == PlayerId.COUNT + 1) {
            try {
                long seed = Long.parseLong(args.get(PlayerId.COUNT));
                SEED_GENERATOR = new Random(seed);
            } catch (NumberFormatException e) {
                displayErrorAndExit(1,
                        "Erreur : la graine fournie n'est pas valide : "
                                + args.get(PlayerId.COUNT),
                        e.toString());
            }
        } else {
            displayErrorAndExit(1, "Erreur : nombre d'arguments invalide.",
                    getHelpMessage());
        }

        Map<PlayerId, String> mapNames = new HashMap<>();
        Map<PlayerId, Player> mapPlayers = new HashMap<>();
        final long gameSeed = SEED_GENERATOR.nextLong();
        
        boolean firstStageAlreadyUsed = false;
        
        // On initialise chaque joueur :
        for (int i = 0; i < PlayerId.COUNT; ++i) {
            String[] fields = args.get(i).split(":");

            // On détermine le type de joueur spécifié (premier "champ")
            PlayerSpecificator type = null;
            for (PlayerSpecificator t : PlayerSpecificator.ALL)
                if (fields[0].equals(t.specificator()))
                    type = t;

            if (type == null)
                displayErrorAndExit(1,
                        "Erreur : spécificateur de joueur inconnu : '"
                                + fields[0] + "'.");

            if (fields.length < type.minNbFields()
                    || fields.length > type.maxNbFields())
                displayErrorAndExit(1,
                        "Erreur : nombre de champs entrés (" + fields.length
                                + ") invalide pour le spécificateur "
                                + fields[0] + ".");

            // On récupère le nom fourni (deuxieme "champ")
            String name = fields.length <= 1 || fields[1].isEmpty()
                    ? Jass.DEFAULT_NAMES[i]
                    : fields[1];

            // On analyse le troisième champ, qui dépend du type de joueur :
            Player player = null;
            long playerSeed = SEED_GENERATOR.nextLong();
            switch (type) {
            case SIMULATED:
                int iterations = Jass.DEFAULT_ITERATIONS;
                if (fields.length > 2 && !fields[2].isEmpty()) {
                    try {
                        iterations = Integer.parseInt(fields[2]);
                    } catch (NumberFormatException e) {
                        displayErrorAndExit(1,
                                "Erreur : nb d'iterations erroné : "
                                        + fields[2],
                                e.toString());
                    }
                }
                player = new PacedPlayer(new MctsPlayer(PlayerId.ALL.get(i),
                        playerSeed, iterations), Jass.WAIT_TIME_MCTS_PLAYER);
                break;

            case REMOTE:
                String hostName = fields.length <= 2 || fields[2].isEmpty()
                        ? Jass.DEFAULT_IP
                        : fields[2];

                try {
                    player = new RemotePlayerClient(hostName);
                } catch (IOException e) {
                    displayErrorAndExit(1, "Erreur : connexion au serveur "
                            + hostName + " impossible.", e.toString());
                }
                break;

            default: // assumed HUMAN
                Stage gui;
                if (!firstStageAlreadyUsed) {
                    gui = firstStage;
                    firstStageAlreadyUsed = true;
                }
                else {
                    gui = new Stage();
                }
                gui.setOnCloseRequest(e -> {
                    if (StageHelper.getStages().size() == 1)
                        System.exit(0);
                });
                player = new GraphicalPlayerAdapter(gui);
                break;
            }

            mapNames.put(PlayerId.ALL.get(i), name);
            mapPlayers.put(PlayerId.ALL.get(i), player);
        }

        createGameThread(gameSeed, mapPlayers, mapNames);
    }

    public static void main(String[] args) {
        launch(args);
    }

    private static void createGameThread(long gameSeed,
            Map<PlayerId, Player> mapPlayers, Map<PlayerId, String> mapNames) {
        Thread gameThread = new Thread(() -> {
            JassGame g = new JassGame(gameSeed, mapPlayers, mapNames);
            while (!g.isGameOver()) {
                g.advanceToEndOfNextTrick();
                try {
                    Thread.sleep(Jass.WAIT_TIME_TRICK_END);
                } catch (InterruptedException e) {
                    // ignore
                }
            }
        });
        gameThread.setDaemon(true);
        gameThread.start();
    }

    /**
     * Affiche les erreurs fournies puis quitte le programme avec le statut
     * exitStatus
     * 
     * @param exitStatus
     *            (int) le statut de l'erreur
     * @param errs
     *            (String...) les erreurs à afficher, un String par ligne
     */
    private static void displayErrorAndExit(int exitStatus, String... errs) {
        for (String s : errs)
            System.err.println(s);
        System.exit(exitStatus);
    }

    private static String getHelpMessage() {
        String str = "Utilisation : java ch.epfl.javass.LocalMain <j1>...<j4> [<graine>]\n"
                + "où :\n" + "<jn> spécifie le joueur n ainsi :\n"
                + "  h:<nom>               -> un joueur humain, nommé <nom>\n"
                + "  s:<nom>:<iterations>  -> un joueur simulé, nommé <nom> avec <iterations> itérations\n"
                + "  r:<nom>:<adresse>     -> un joueur distant, nommé <nom> à l'adresse ip <adresse>\n"
                + "Remarque : les champs <nom>, <graine>, et <adresse> peuvent être omis dans les \n"
                + "arguments ci-dessus, ils sont alors remplacés par leur valeur par défaut (voir doc).\n"
                + "[<graine>] (optionnel) la graine utilisée pour générer la partie.";
        return str;
    }

    @Override
    public void start(Stage arg0) {
        createGameFromArguments(getParameters().getRaw(), arg0);
    }
}
