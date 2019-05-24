package ch.epfl.javass;

import java.util.ArrayList;
import java.util.StringTokenizer;

import com.sun.javafx.stage.StageHelper;

import ch.epfl.javass.jass.Jass;
import ch.epfl.javass.jass.PlayerId;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
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

public final class Launcher extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    public static void requestTryAgain() {
        tryAgainProperty.set(true);
    }

    private static boolean isIpv4Valid(String text) {
        if (text.equals("localhost"))
            return true;

        StringTokenizer st = new StringTokenizer(text,".");
        try {
            for(int i = 0; i < 4; ++i){ 
                if(!st.hasMoreTokens())
                    return false;
                
                int num = Integer.parseInt(st.nextToken());
                if(num < 0 || num > 255)
                    return false;
            }
        }
        catch (NumberFormatException e) {
            return false;
        }
        return !st.hasMoreTokens();
    }
    
    public final static int ITERATIONS_BY_IA_LEVEL = 10_000;
    
    private final static BooleanProperty tryAgainProperty = new SimpleBooleanProperty(false);
    private Stage primaryStage;
    private final Scene scene;
    private final VBox mainMenu;
    private final VBox createGameMenu;
    private final VBox joinGameMenu;

    public Launcher() {

        mainMenu = createMainMenu();
        createGameMenu = createCreateGameMenu();
        joinGameMenu = createJoinGameMenu();
        displayMainMenu();

        StackPane principal = new StackPane();
        principal.setMinSize(400, 600);
        principal.getChildren().add(mainMenu);
        principal.getChildren().add(createGameMenu);
        principal.getChildren().add(joinGameMenu);

        scene = new Scene(principal);
        primaryStage = null;

        tryAgainProperty.addListener((e, oldV, newV) -> {
            if (newV) {
                for (Stage st : StageHelper.getStages())
                    if (!primaryStage.equals(st))
                        Platform.runLater(() -> st.close());

                displayMainMenu();

                tryAgainProperty.set(false);
                primaryStage.setTitle("Javass - Launcher");
                primaryStage.setScene(scene);
            }
        });
    }

    @Override
    public void start(Stage arg0) throws Exception {
        primaryStage = arg0;
        primaryStage.setTitle("Javass - Launcher");
        primaryStage.setScene(scene);
        primaryStage.show();
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
            displayCreateGameMenu();
        });

        Button joinGameBtn = new Button("Rejoindre une partie");
        joinGameBtn.setOnMouseClicked(e -> {
            displayJoinGameMenu();
            RemoteMain.startGame(primaryStage);
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
            final int index = i;
            HBox box = new HBox();

            typeChoices.add(new ChoiceBox<>(
                    FXCollections.observableArrayList(PlayerSpecificator.HUMAN.frenchName(), 
                            PlayerSpecificator.SIMULATED.frenchName(), PlayerSpecificator.REMOTE.frenchName())));
            typeChoices.get(index).getSelectionModel().select(1);

            nameFields[index] = new TextField();
            nameFields[index].setText(Jass.DEFAULT_NAMES[index]);

            StackPane lastField = new StackPane();

            IADifficultySpinners.add(new Spinner<>(1, 10, 4));
            IADifficultySpinners.get(index).visibleProperty().bind(
                    Bindings.equal(PlayerSpecificator.SIMULATED.frenchName(), typeChoices.get(index).valueProperty()));

            ipFields[index] = new TextField(Jass.DEFAULT_IP);
            ipFields[index].textProperty().addListener((observ, oldV, newV) -> {
                if (isIpv4Valid(newV))
                    ipFields[index].setStyle("-fx-text-fill: black;");
                else
                    ipFields[index].setStyle("-fx-text-fill: red;");
            });
            ipFields[index].visibleProperty().bind(
                    Bindings.equal(PlayerSpecificator.REMOTE.frenchName(), typeChoices.get(index).valueProperty()));

            lastField.getChildren().add(IADifficultySpinners.get(index));
            lastField.getChildren().add(ipFields[index]);

            box.getChildren().add(typeChoices.get(index));
            box.getChildren().add(nameFields[index]);
            box.getChildren().add(lastField);

            menu.getChildren().add(box);
        }

        HBox seedBox = new HBox();
        seedBox.setAlignment(Pos.CENTER);
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
                if (typeChoices.get(i).getValue().equals(PlayerSpecificator.HUMAN.frenchName())) {
                    args.add("h:" + name);
                }
                else if (typeChoices.get(i).getValue().equals(PlayerSpecificator.SIMULATED.frenchName())) {
                    args.add("s:" + name + ":" + IADifficultySpinners.get(i).getValue() * ITERATIONS_BY_IA_LEVEL);
                }
                else {
                    if (!isIpv4Valid(ipFields[i].getText()))
                        return;
                    args.add("r:" + name + ":" + ipFields[i].getText());
                }
            }
            if (!seedField.getText().isEmpty())
                args.add(seedField.getText());

            LocalMain.createGameFromArguments(args, primaryStage);
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

    private void displayMainMenu() {
        mainMenu.setVisible(true);
        createGameMenu.setVisible(false);
        joinGameMenu.setVisible(false);
    }
    private void displayCreateGameMenu() {
        mainMenu.setVisible(false);
        createGameMenu.setVisible(true);
        joinGameMenu.setVisible(false);
    }
    private void displayJoinGameMenu() {
        mainMenu.setVisible(false);
        createGameMenu.setVisible(false);
        joinGameMenu.setVisible(true);
    }
}
