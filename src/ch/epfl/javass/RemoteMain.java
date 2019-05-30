package ch.epfl.javass;

import ch.epfl.javass.gui.GraphicalPlayerAdapter;
import ch.epfl.javass.net.RemotePlayerServer;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * RemoteMain Une classe permettant de jouer à une partie distante
 *
 * @author Amaury Pierre (296498)
 * @author Aurélien Clergeot (302592)
 */
public final class RemoteMain extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Commence la partie avec le stage donné
     * 
     * @param stage
     *            (Stage) stage sur lequel afficher l'interface du serveur
     */
    public static void startGame(Stage stage) {
        // RemotePlayerServer implémente Runnable
        RemotePlayerServer server = new RemotePlayerServer(
                new GraphicalPlayerAdapter(stage));
        Thread remoteThread = new Thread(server);
        remoteThread.setDaemon(true);
        remoteThread.start();
        System.out.println("La partie commencera à la connexion du client...");
    }

    @Override
    public void start(Stage arg0) throws Exception {
        startGame(arg0);
    }
}