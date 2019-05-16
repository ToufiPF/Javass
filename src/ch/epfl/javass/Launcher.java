package ch.epfl.javass;

import java.util.ArrayList;

import com.sun.javafx.stage.StageHelper;

import ch.epfl.javass.jass.Jass;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.net.RemotePlayerServer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Launcher extends Application {

    public static void main(String[] args) {
        launch(args);
    }
    
    private Stage stage;
    private final Scene scene;
    private final VBox mainMenu;
    private final VBox createGameMenu;
    private final VBox joinGameMenu;

    public Launcher() {
        Platform.setImplicitExit(false);
        
        mainMenu = createMainMenu();
        mainMenu.setVisible(true);

        createGameMenu = createCreateGameMenu();
        createGameMenu.setVisible(false);

        joinGameMenu = createJoinGameMenu();
        joinGameMenu.setVisible(false);

        StackPane principal = new StackPane();
        principal.setMinSize(400, 600);
        principal.getChildren().add(mainMenu);
        principal.getChildren().add(createGameMenu);
        principal.getChildren().add(joinGameMenu);

        scene = new Scene(principal);
        stage = null;
    }

    @Override
    public void start(Stage arg0) throws Exception {
        stage = new Stage();
        stage.setTitle("Javass - Launcher");
        stage.setScene(scene);
        stage.setOnCloseRequest(e -> {
            if (StageHelper.getStages().size() == 1)
                System.exit(0);
        });
        stage.show();
    }

    private VBox createMainMenu() {
        VBox menu = new VBox();
        menu.setStyle("-fx-font: 16 Optima; -fx-background-color: lightgray;" + 
                " -fx-spacing: 15px; -fx-padding: 5px; -fx-alignment: center;");

        Label title = new Label("Javass");
        title.setStyle("-fx-padding: 50px; "
                + "-fx-font: 54 Cambria; -fx-underline: true; -fx-text-fill: forestgreen;");

        Button createGameBtn = new Button("Créer une partie");
        createGameBtn.setOnMouseClicked(e -> {
            mainMenu.setVisible(false);
            createGameMenu.setVisible(true);
        });

        Button joinGameBtn = new Button("Rejoindre une partie");
        joinGameBtn.setOnMouseClicked(e -> {
            mainMenu.setVisible(false);
            joinGameMenu.setVisible(true);
            RemotePlayerServer serv = RemoteMain.startGame();
            serv.connectionEstablishedProperty().addListener((ev, oldV, newV) -> {
                if (newV)
                    stage.close();
            });
        });

        Button quitBtn = new Button("Quitter");
        quitBtn.setOnMouseClicked(e -> System.exit(0));

        menu.getChildren().add(title);
        menu.getChildren().add(createGameBtn);
        menu.getChildren().add(joinGameBtn);
        menu.getChildren().add(quitBtn);

        return menu;
    }

    private VBox createCreateGameMenu() {
        VBox menu = new VBox();
        menu.setStyle("-fx-font: 16 Optima; -fx-background-color: lightgray;" + 
                " -fx-spacing: 15px; -fx-padding: 5px; -fx-alignment: center;");

        Label lbl = new Label("Créer une partie : ");
        menu.getChildren().add(lbl);

        ArrayList<ChoiceBox<String>> typeChoices = new ArrayList<>();
        TextField[] nameFields = new TextField[PlayerId.COUNT];
        ArrayList<Spinner<Integer>> IADifficultySpinners = new ArrayList<>();
        TextField[] ipFields = new TextField[PlayerId.COUNT];

        for (int i = 0 ; i < PlayerId.COUNT ; ++i) {
            HBox box = new HBox();

            typeChoices.add(new ChoiceBox<>(FXCollections.observableArrayList("Humain", "Simulé", "Distant")));
            typeChoices.get(i).setValue("Simulé");

            nameFields[i] = new TextField();
            nameFields[i].setText(Jass.DEFAULT_NAMES[i]);

            StackPane lastField = new StackPane();

            IADifficultySpinners.add(new Spinner<>(1, 10, 4));
            IADifficultySpinners.get(i).visibleProperty().bind(Bindings.equal("Simulé", typeChoices.get(i).valueProperty()));

            ipFields[i] = new TextField(Jass.DEFAULT_IP);
            ipFields[i].visibleProperty().bind(Bindings.equal("Distant", typeChoices.get(i).valueProperty()));

            lastField.getChildren().add(IADifficultySpinners.get(i));
            lastField.getChildren().add(ipFields[i]);

            box.getChildren().add(typeChoices.get(i));
            box.getChildren().add(nameFields[i]);
            box.getChildren().add(lastField);

            menu.getChildren().add(box);
        }

        HBox seedBox = new HBox();
        Label seedLbl = new Label("Entrez la graîne de la partie : (laisser vide pour aléatoire) : ");
        TextField seedField = new TextField();
        seedBox.getChildren().add(seedLbl);
        seedBox.getChildren().add(seedField);
        
        menu.getChildren().add(seedBox);

        Button launchGameBtn = new Button("Lancer la partie");
        launchGameBtn.setOnMouseClicked(e -> {
            ArrayList<String> args = new ArrayList<>();

            for (int i = 0 ; i < PlayerId.COUNT ; ++i) {
                String name = nameFields[i].getText().trim();
                if (name.isEmpty())
                    name = Jass.DEFAULT_NAMES[i];
                if (typeChoices.get(i).getValue().equals("Humain")) {
                    args.add("h:" + name);
                }
                else if (typeChoices.get(i).getValue().equals("Simulé")) {
                    args.add("s:" + name + ":" + IADifficultySpinners.get(i).getValue() * 10_000);
                }
                else {
                    args.add("r:" + name + ":" + ipFields[i].getText());
                }
            }
            if (!seedField.getText().isEmpty())
                args.add(seedField.getText());
            
            LocalMain.createGameFromArguments(args);
            stage.close();
        });
        menu.getChildren().add(launchGameBtn);

        return menu;
    }

    private VBox createJoinGameMenu() {
        VBox menu = new VBox();
        menu.setStyle("-fx-font: 16 Optima; -fx-background-color: lightgray;" + 
                " -fx-spacing: 15px; -fx-padding: 5px; -fx-alignment: center;");

        Label lbl = new Label("La partie commencera quand un client se connectera à votre serveur.");
        menu.getChildren().add(lbl);
        return menu;
    }

}
