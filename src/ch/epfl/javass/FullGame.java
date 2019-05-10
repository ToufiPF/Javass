package ch.epfl.javass;

import java.util.ArrayList;

import ch.epfl.javass.jass.PlayerId;
import javafx.application.Application;
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

public class FullGame extends Application {
    
    public static void main(String[] args) {
        launch(args);
    }
    
    private final Scene scene;
    private final VBox mainMenu;
    private final VBox createGameMenu;
    private final VBox joinGameMenu;
    
    public FullGame() {
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
    }
    
    @Override
    public void start(Stage arg0) throws Exception {
        Stage stage = new Stage();
        stage.setTitle("Javass - FullGame");
        stage.setScene(scene);
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
            //TODO : lancer le serveur
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
        
        ArrayList<ChoiceBox<String>> typeChoices = new ArrayList<>(PlayerId.COUNT);
        
        for (int i = 0 ; i < PlayerId.COUNT ; ++i) {
            HBox box = new HBox();

            ChoiceBox<String> typeChoice = new ChoiceBox<>(FXCollections.observableArrayList("Humain", "Simulé", "Distant"));
            typeChoice.setValue("Simulé");
            
            TextField nameField = new TextField();
            nameField.setText(LocalMain.DEFAULT_NAMES[i]);
            
            StackPane lastField = new StackPane();
            
            Spinner<Integer> difficultyIA = new Spinner<>(1, 10, 4);
            difficultyIA.visibleProperty().bind(Bindings.equal("Simulé", typeChoice.valueProperty()));
            
            TextField ipField = new TextField(LocalMain.DEFAULT_IP);
            ipField.visibleProperty().bind(Bindings.equal("Distant", typeChoice.valueProperty()));
            
            lastField.getChildren().add(difficultyIA);
            lastField.getChildren().add(ipField);
            
            box.getChildren().add(typeChoice);
            box.getChildren().add(nameField);
            box.getChildren().add(lastField);
            
            menu.getChildren().add(box);
        }

        HBox seedBox = new HBox();
        {
            Label seedLbl = new Label("Entrez la graîne de la partie : (laisser vide pour aléatoire) : ");
            TextField seedField = new TextField();
            seedBox.getChildren().add(seedLbl);
            seedBox.getChildren().add(seedField);
        }
        menu.getChildren().add(seedBox);
        
        Button launchGameBtn = new Button("Lancer la partie");
        launchGameBtn.setOnMouseClicked(e -> {
            String[] args = new String[PlayerId.COUNT];
            
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
