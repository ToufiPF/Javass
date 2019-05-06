package ch.epfl.javass;

import java.util.List;

import javafx.application.Application;
import javafx.stage.Stage;

public class LocalMain extends Application {
    
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage arg0) throws Exception {
        List<String> args = getParameters().getRaw();
        if (!(args.size() == 4 || args.size() == 5)) {
            displayHelp();
            return;
        }
        
    }
    
    private void displayHelp() {
        StringBuilder b = new StringBuilder("Utilisation : java ch.epfl.javass.LocalMain <j1>...<j4> [<graine>]\n")
                .append("où :\n").append("<jn> spécifie le joueur n ainsi :\n")
                .append("  h:<nom>               -> un joueur humain, nommé <nom>\n")
                .append("  s:<nom>:<iterations>  -> un joueur simulé, nommé <nom> avec <iterations> itérations\n")
                .append("  r:<nom>:<adresse>     -> un joueur distant, nommé <nom> à l'adresse ip <adresse>\n")
                .append("Remarque : les champs <nom>, <graine>, et <adresse> peuvent être omis dans les \n")
                .append("arguments ci-dessus, ils sont alors remplacés par leur valeur par défaut (voir doc).\n")
                .append("[<graine>] (optionnel) la graine utilisée pour générer la partie");
        System.out.println(b.toString());
    }
}
