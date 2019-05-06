package ch.epfl.javass;

import java.util.List;

import javafx.application.Application;
import javafx.stage.Stage;

public class LocalMain extends Application {
    
    public static final double WAIT_TIME_MCTS_PLAYER = 2.;
    
    public static final double WAIT_TIME_TRICK_END = 1.;
    
    public static final String[] DEFAULT_NAMES = {"Aline", "Bastien", "Colette", "David" };
    
    public static final int DEFAULT_ITERATIONS = 10_000;
    
    public static final String DEFAULT_IP = "localhost";
    
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage arg0) throws Exception {
        List<String> args = getParameters().getRaw();
        if (!(args.size() == 4 || args.size() == 5)) {
            System.err.println("Erreur : nombre d'arguments invalide");
            System.err.println(getHelpMessage());
            return;
        }
        
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
}
