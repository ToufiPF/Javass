package ch.epfl.javass;

import java.io.IOError;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import ch.epfl.javass.gui.GraphicalPlayerAdapter;
import ch.epfl.javass.jass.JassGame;
import ch.epfl.javass.jass.MctsPlayer;
import ch.epfl.javass.jass.PacedPlayer;
import ch.epfl.javass.jass.Player;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.net.RemotePlayerClient;
import javafx.application.Application;
import javafx.stage.Stage;

public class LocalMain extends Application {

    private static enum PlayerType {
        HUMAN("h", 1, 2), SIMULATED("s", 1, 3), REMOTE("r", 1, 3);

        public static final List<PlayerType> ALL = Arrays.asList(values());

        private final String spec;
        private final int minFields;
        private final int maxFields;

        private PlayerType(String specificator, int minFields, int maxFields) {
            this.spec = specificator;
            this.minFields = minFields;
            this.maxFields = maxFields;
        }

        public String specificator() {
            return spec;
        }
        public int minNbFields() {
            return minFields;
        }
        public int maxNbFields() {
            return maxFields;
        }
    }

    public static final double WAIT_TIME_MCTS_PLAYER = .5; // default:2.0

    public static final long WAIT_TIME_TRICK_END = 1000;

    public static final String[] DEFAULT_NAMES = {"Aline", "Bastien", "Colette", "David" };

    public static final int DEFAULT_ITERATIONS = 10_000;

    public static final String DEFAULT_IP = "localhost";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage arg0) throws Exception {
        List<String> args = getParameters().getRaw();
        
        Random SEED_GENERATOR = null;
        if (args.size() == PlayerId.COUNT) {
            //Graine aléatoire
            SEED_GENERATOR = new Random();
        }
        else if (args.size() == PlayerId.COUNT + 1) {
            //Graine donnée
            try {
                long seed = Long.parseLong(args.get(PlayerId.COUNT));
                SEED_GENERATOR = new Random(seed);
            }
            catch (NumberFormatException e) {
                System.err.println("Erreur : graine erronée : " + args.get(PlayerId.COUNT));
                System.err.println(e.toString());
                System.exit(1);
            }
        }
        else {
            System.err.println("Erreur : nombre d'arguments invalide");
            System.err.println(getHelpMessage());
            System.exit(1);
        }

        Map<PlayerId, String> mapNames = new HashMap<>();
        Map<PlayerId, Player> mapPlayers = new HashMap<>();
        final long gameSeed = SEED_GENERATOR.nextLong();
        
        for (int i = 0 ; i < PlayerId.COUNT ; ++i) {
            String[] fields = args.get(i).split(":");

            PlayerType type = null;
            for (PlayerType t : PlayerType.ALL)
                if (fields[0].equals(t.specificator()))
                    type = t;

            if (type == null) {
                System.err.println("Erreur : spécificateur de joueur inconnu : '" + fields[0] + "'.");
                System.exit(1);
            }
            checkNbFieldsForPlayerType(fields, type);
            
            String name = fields.length <= 1 || fields[1].isEmpty() ? DEFAULT_NAMES[i] : fields[1];
            Player player = null;
            long playerSeed = SEED_GENERATOR.nextLong();
            
            switch (type) {
            case SIMULATED:
                int iterations = DEFAULT_ITERATIONS;
                if (fields.length > 2 && !fields[2].isEmpty()) {
                    try {
                        iterations = Integer.parseInt(fields[2]);
                    }
                    catch (NumberFormatException e) {
                        System.err.println("Erreur : nb d'iterations erroné : " + fields[2]);
                        System.err.println(e.toString());
                        System.exit(1);
                    }
                }
                player = new PacedPlayer(new MctsPlayer(PlayerId.ALL.get(i), playerSeed, iterations), WAIT_TIME_MCTS_PLAYER);
                break;
                
            case REMOTE:
                String hostName = fields.length <= 2 || fields[2].isEmpty() ? DEFAULT_IP : fields[2];
                try {
                    player = new RemotePlayerClient(hostName);
                }
                catch (IOException e) {
                    System.err.println("Erreur : connexion au serveur " + hostName + " impossible.");
                    System.err.println(e);
                    System.exit(1);
                }
                catch (IOError e) {
                    System.err.println("Erreur : connexion au serveur " + hostName + " impossible.");
                    System.err.println(e);
                    System.exit(1);
                }
                break;
                
            default: // assumed HUMAN
                player = new GraphicalPlayerAdapter();
                break;
            }

            mapNames.put(PlayerId.ALL.get(i), name);
            mapPlayers.put(PlayerId.ALL.get(i), player);
        }
        
        Thread gameThread = new Thread(() -> {
            JassGame g = new JassGame(gameSeed, mapPlayers, mapNames);
            while (!g.isGameOver()) {
                g.advanceToEndOfNextTrick();
                try { 
                    Thread.sleep(WAIT_TIME_TRICK_END); 
                } catch (InterruptedException e) {
                    // ignore
                }
            }
        });
        gameThread.setDaemon(true);
        gameThread.start();
    }

    private static String getHelpMessage() {
        StringBuilder b = new StringBuilder()
                .append("Utilisation : java ch.epfl.javass.LocalMain <j1>...<j4> [<graine>]\n")
                .append("où :\n").append("<jn> spécifie le joueur n ainsi :\n")
                .append("  h:<nom>               -> un joueur humain, nommé <nom>\n")
                .append("  s:<nom>:<iterations>  -> un joueur simulé, nommé <nom> avec <iterations> itérations\n")
                .append("  r:<nom>:<adresse>     -> un joueur distant, nommé <nom> à l'adresse ip <adresse>\n")
                .append("Remarque : les champs <nom>, <graine>, et <adresse> peuvent être omis dans les \n")
                .append("arguments ci-dessus, ils sont alors remplacés par leur valeur par défaut (voir doc).\n")
                .append("[<graine>] (optionnel) la graine utilisée pour générer la partie");
        return b.toString();
    }

    private static void checkNbFieldsForPlayerType(String[] fields, PlayerType type) {
        if (fields.length < type.minNbFields() || fields.length > type.maxNbFields()) {
            System.err.println("Erreur : nombre de champs invalide pour le spécificateur de joueur '" 
                    + type.specificator() + "' (" + type.toString() + ").");
            System.err.println("Nombre de champs attendu : entre " + type.minNbFields() + " et " + type.maxNbFields() + 
                    ", (reçu : " + fields.length + ").");

            System.exit(1);
        }
    }
    
}
