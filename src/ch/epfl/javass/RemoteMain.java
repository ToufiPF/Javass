package ch.epfl.javass;

import com.sun.javafx.stage.StageHelper;

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

    @Override
    public void start(Stage arg0) throws Exception {
        startGame(arg0);
    }
    
    public static RemotePlayerServer startGame(Stage stage) {
        //RemotePlayerServer implémente Runnable
        RemotePlayerServer server = new RemotePlayerServer(new GraphicalPlayerAdapter(stage));
        stage.setOnCloseRequest(e -> {
            if (StageHelper.getStages().size() == 1)
                System.exit(0);
        });
        Thread remoteThread = new Thread(server);
        remoteThread.setDaemon(true);
        remoteThread.start();
        System.out.println("La partie commencera à la connexion du client...");
        return server;
    }
}