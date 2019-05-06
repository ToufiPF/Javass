package ch.epfl.javass;

import ch.epfl.javass.gui.GraphicalPlayerAdapter;
import ch.epfl.javass.net.RemotePlayerServer;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * RemoteMain Une classe permettant de jouer à une partie distante
 * @author Amaury Pierre (296498)
 * @author Aurélien Clergeot (302592)
 */
public final class RemoteMain extends Application {   
    public static void main (String[] args) {
       Application.launch(args); 
    }
    
    @Override
    public void start(Stage arg0) throws Exception {
        Thread remoteThread = new Thread(() -> {
            RemotePlayerServer server = new RemotePlayerServer(new GraphicalPlayerAdapter());
            server.run();
        });
        remoteThread.setDaemon(true);
        remoteThread.start();
        System.out.println("La partie commencera à la connexion du client...");
    }
}