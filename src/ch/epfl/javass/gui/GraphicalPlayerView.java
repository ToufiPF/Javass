package ch.epfl.javass.gui;

import java.util.HashMap;
import java.util.Map;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.Card.Color;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.jass.TeamId;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public final class GraphicalPlayerView {
    private static String pathToCard(Card c, int width) {
        return "/card_" + c.color().ordinal() + "_" + c.rank().ordinal() + "_" + width + ".png";
    }
    /**
     * Donne le chemin vers l'image de la carte donnée, en version 240x360px
     * @param c (Card)
     * @return (String) le chemin vers la carte donnée
     */
    public static String pathToCard240px(Card c) {
        return pathToCard(c, 240);
    }
    /**
     * Donne le chemin vers l'image de la carte donnée, en version 160x240px
     * @param c (Card)
     * @return (String) le chemin vers la carte donnée
     */
    public static String pathToCard160px(Card c) {
        return pathToCard(c, 160);
    }
    /**
     * Donne le chemin vers l'image de la couleur donnée
     * @param trump (Card.Color)
     * @return (String) le chemin vers la couleur donnée
     */
    public static String pathToTrump(Card.Color trump) {
        return "/trump_" + trump.ordinal() + ".png";
    }

    private static ObservableMap<Card, Image> computeMapImagesCards() {
        Map<Card, Image> map = new HashMap<>();
        for (Card.Color c : Card.Color.ALL) {
            for (Card.Rank r : Card.Rank.ALL) {
                Card card = Card.of(c, r);
                map.put(card, new Image(pathToCard240px(card)));
            }
        }
        return FXCollections.unmodifiableObservableMap(FXCollections.observableMap(map));
    }

    private static ObservableMap<Color, Image> computeMapImagesTrumps() {
        Map<Card.Color, Image> map = new HashMap<>();
        for (Card.Color c : Card.Color.ALL)
            map.put(c, new Image(pathToTrump(c)));
        
        return FXCollections.unmodifiableObservableMap(FXCollections.observableMap(map));
    }
    public static final ObservableMap<Card, Image> mapImagesCards = computeMapImagesCards();
    public static final ObservableMap<Card.Color, Image> mapImagesTrumps = computeMapImagesTrumps();
    
    //Graphics :
    private final Scene scene;

    public GraphicalPlayerView(PlayerId ownId, Map<PlayerId, String> nameMap, ScoreBean sb, TrickBean tb) {
        
        GridPane scorePane = createScorePanes(nameMap, sb);
        GridPane trickPane = createTrickPane(ownId, tb);
        BorderPane winTeam1Pane = new BorderPane(), winTeam2Pane = new BorderPane();


        BorderPane gamePane = new BorderPane(trickPane);
        gamePane.setTop(scorePane);

        StackPane principalPane = new StackPane(gamePane);
        this.scene = new Scene(principalPane);
    }
    public Stage createStage() {
        Stage st = new Stage();
        st.setScene(scene);
        return st;
    }

    private static GridPane createScorePanes(Map<PlayerId, String> nameMap, ScoreBean sb) {
        GridPane scorePane = new GridPane();
        scorePane.setStyle("-fx-font: 16 Optima; -fx-background-color: lightgray; -fx-padding: 5px;\n -fx-alignment: center;");

        for (int i = 0 ; i < TeamId.COUNT ; ++i) {
            Text namesTxt = new Text(nameMap.get(PlayerId.ALL.get(i)) + " et " + nameMap.get(PlayerId.ALL.get(i + 2)) + " : ");
            scorePane.add(namesTxt, 0, i);
            
            Text turnScoreTxt = new Text();
            turnScoreTxt.textProperty().bind(Bindings.convert(sb.turnPointsProperty(TeamId.ALL.get(i))));
            scorePane.add(turnScoreTxt, 1, i);
            
            Text lastTrickScore = new Text();
            StringProperty lastTrickProp = new SimpleStringProperty();
            sb.turnPointsProperty(TeamId.ALL.get(i)).addListener((e, oldVal, newVal) -> {
                lastTrickProp.set("(+" + (newVal.intValue() - oldVal.intValue()) + ")");
            });
            lastTrickScore.textProperty().bind(lastTrickProp);
            scorePane.add(lastTrickScore, 2, i);
            
            Text totalTxt = new Text(" / Total : ");
            scorePane.add(totalTxt, 3, i);
            
            Text gameScoreTxt = new Text();
            gameScoreTxt.textProperty().bind(Bindings.convert(sb.gamePointsProperty(TeamId.ALL.get(i))));
            scorePane.add(gameScoreTxt, 4, i);
        }

        return scorePane;
    }
    
    
    private static GridPane createTrickPane(PlayerId ownId, TrickBean tb) {
        GridPane trickPane = new GridPane();
        trickPane.setStyle("-fx-background-color: whitesmoke; -fx-padding: 5px; -fx-border-width: 3px 0px; " + 
                "-fx-border-style: solid; -fx-border-color: gray; -fx-alignment: center;");
        
        //Création des cartes
        ImageView[] imagesCards = new ImageView[PlayerId.COUNT];
        for (int i = 0 ; i < PlayerId.COUNT ; ++i) {
            imagesCards[i] = new ImageView();
            imagesCards[i].setFitWidth(120);
            imagesCards[i].setFitHeight(180);
            imagesCards[i].imageProperty().bind(Bindings.valueAt(mapImagesCards, Bindings.valueAt(tb.trick(), PlayerId.ALL.get(i))));
        }
        
        ImageView trumpImage = new ImageView();
        trumpImage.setFitWidth(101);
        trumpImage.setFitHeight(101);
        trumpImage.imageProperty().bind(Bindings.valueAt(mapImagesTrumps, tb.trump()));

        //Placement des cartes:
        final int ownIndex = ownId.ordinal();
        trickPane.add(imagesCards[ownIndex], 1, 2);
        trickPane.add(imagesCards[(ownIndex + 1) % PlayerId.COUNT], 2, 0, 1, 3);
        trickPane.add(imagesCards[(ownIndex + 2) % PlayerId.COUNT], 1, 0);
        trickPane.add(imagesCards[(ownIndex + 3) % PlayerId.COUNT], 0, 0, 1, 3);
        
        trickPane.add(trumpImage, 1, 1);
        
        return trickPane;
    }
} 
