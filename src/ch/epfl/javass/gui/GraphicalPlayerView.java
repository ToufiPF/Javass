package ch.epfl.javass.gui;

import java.util.HashMap;
import java.util.Map;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.Card.Color;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.jass.TeamId;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public final class GraphicalPlayerView {
    private static String pathToCard(Card c, int width) {
        return "/card_" + c.color().ordinal() + "_" + c.rank().ordinal() + "_"
                + width + ".png";
    }

    /**
     * Donne le chemin vers l'image de la carte donnée, en version 240x360px
     * 
     * @param c
     *            (Card)
     * @return (String) le chemin vers la carte donnée
     */
    public static String pathToCard240px(Card c) {
        return pathToCard(c, 240);
    }

    /**
     * Donne le chemin vers l'image de la carte donnée, en version 160x240px
     * 
     * @param c
     *            (Card)
     * @return (String) le chemin vers la carte donnée
     */
    public static String pathToCard160px(Card c) {
        return pathToCard(c, 160);
    }

    /**
     * Donne le chemin vers l'image de la couleur donnée
     * 
     * @param trump
     *            (Card.Color)
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
        return FXCollections
                .unmodifiableObservableMap(FXCollections.observableMap(map));
    }

    private static ObservableMap<Color, Image> computeMapImagesTrumps() {
        Map<Card.Color, Image> map = new HashMap<>();
        for (Card.Color c : Card.Color.ALL)
            map.put(c, new Image(pathToTrump(c)));

        return FXCollections
                .unmodifiableObservableMap(FXCollections.observableMap(map));
    }

    public static final ObservableMap<Card, Image> mapImagesCards = computeMapImagesCards();
    public static final ObservableMap<Card.Color, Image> mapImagesTrumps = computeMapImagesTrumps();

    // Graphics :
    private final Scene scene;

    public GraphicalPlayerView(PlayerId ownId, Map<PlayerId, String> nameMap,
            ScoreBean sb, TrickBean tb) {

        GridPane scorePane = createScorePanes(nameMap, sb);
        GridPane trickPane = createTrickPane(ownId, nameMap, tb);
        BorderPane winTeam1Pane = createWinningTeamPane(TeamId.TEAM_1, nameMap, sb);
        BorderPane winTeam2Pane = createWinningTeamPane(TeamId.TEAM_2, nameMap, sb);

        BorderPane gamePane = new BorderPane(trickPane);
        gamePane.setTop(scorePane);

        StackPane principalPane = new StackPane(gamePane);
        principalPane.getChildren().add(winTeam1Pane);
        principalPane.getChildren().add(winTeam2Pane);
        this.scene = new Scene(principalPane);
    }

    public Stage createStage() {
        Stage st = new Stage();
        st.setScene(scene);
        return st;
    }

    private static GridPane createScorePanes(Map<PlayerId, String> nameMap,
            ScoreBean sb) {
        GridPane scorePane = new GridPane();
        scorePane.setStyle(
                "-fx-font: 16 Optima; -fx-background-color: lightgray; -fx-padding: 5px; -fx-alignment: center;");

        for (int i = 0; i < TeamId.COUNT; ++i) {
            Text namesTxt = new Text(nameMap.get(PlayerId.ALL.get(i)) + " et "
                    + nameMap.get(PlayerId.ALL.get(i + 2)) + " : ");
            scorePane.add(namesTxt, 0, i);

            Text turnScoreTxt = new Text();
            turnScoreTxt.textProperty().bind(
                    Bindings.convert(sb.turnPointsProperty(TeamId.ALL.get(i))));
            scorePane.add(turnScoreTxt, 1, i);

            Text lastTrickScore = new Text();
            StringProperty lastTrickProp = new SimpleStringProperty();
            sb.turnPointsProperty(TeamId.ALL.get(i))
                    .addListener((e, oldVal, newVal) -> {
                        lastTrickProp.set(
                                "(+" + (newVal.intValue() - oldVal.intValue())
                                        + ")");
                    });
            lastTrickScore.textProperty().bind(lastTrickProp);
            scorePane.add(lastTrickScore, 2, i);

            Text totalTxt = new Text(" / Total : ");
            scorePane.add(totalTxt, 3, i);

            Text gameScoreTxt = new Text();
            gameScoreTxt.textProperty().bind(
                    Bindings.convert(sb.gamePointsProperty(TeamId.ALL.get(i))));
            scorePane.add(gameScoreTxt, 4, i);
        }

        return scorePane;
    }

    private static GridPane createTrickPane(PlayerId ownId,
            Map<PlayerId, String> nameMap, TrickBean tb) {
        GridPane trickPane = new GridPane();
        trickPane.setStyle(
                "-fx-background-color: whitesmoke; -fx-padding: 5px; -fx-border-width: 3px 0px; -fx-border-style: solid; -fx-border-color: gray; -fx-alignment: center;");

        final int ownIndex = ownId.ordinal();

        ImageView trumpImage = new ImageView();
        trumpImage.setFitWidth(101);
        trumpImage.setFitHeight(101);
        trumpImage.imageProperty()
                .bind(Bindings.valueAt(mapImagesTrumps, tb.trumpProperty()));

        // Création des cartes
        StackPane[] imagesPanes = new StackPane[PlayerId.COUNT];
        for (int i = 0; i < PlayerId.COUNT; ++i) {
            imagesPanes[i] = new StackPane();

            Rectangle halo = new Rectangle(120, 180);
            halo.setStyle(
                    "-fx-arc-width: 20; -fx-arc-height: 20; -fx-fill: transparent; -fx-stroke: lightpink; -fx-stroke-width: 5; -fx-opacity: 0.5;");
            halo.setEffect(new GaussianBlur(4));
            halo.visibleProperty().bind(
                    tb.winningPlayerProperty().isEqualTo(PlayerId.ALL.get(i)));

            ImageView img = new ImageView();
            img.setFitWidth(120);
            img.setFitHeight(180);
            img.imageProperty().bind(Bindings.valueAt(mapImagesCards,
                    Bindings.valueAt(tb.trickProperty(), PlayerId.ALL.get(i))));

            imagesPanes[i].getChildren().add(halo);
            imagesPanes[i].getChildren().add(img);
        }

        VBox cardBoxes[] = new VBox[PlayerId.COUNT];
        for (int i = 0; i < PlayerId.COUNT; ++i) {
            Text txt = new Text(nameMap.get(PlayerId.ALL.get(i)));
            txt.setStyle("-fx-font: 14 Optima;");
            cardBoxes[i] = new VBox(10);

            if (i == ownIndex) {
                cardBoxes[i].getChildren().add(imagesPanes[i]);
                cardBoxes[i].getChildren().add(txt);
            } else {
                cardBoxes[i].getChildren().add(txt);
                cardBoxes[i].getChildren().add(imagesPanes[i]);
            }
        }

        trickPane.add(trumpImage, 1, 1);
        // Placement des boxes:
        trickPane.add(cardBoxes[ownIndex], 1, 2);
        trickPane.add(cardBoxes[(ownIndex + 1) % PlayerId.COUNT], 2, 0, 1, 3);
        trickPane.add(cardBoxes[(ownIndex + 2) % PlayerId.COUNT], 1, 0);
        trickPane.add(cardBoxes[(ownIndex + 3) % PlayerId.COUNT], 0, 0, 1, 3);

        return trickPane;
    }
    
    
    private static BorderPane createWinningTeamPane(TeamId id, Map<PlayerId, String> nameMap, ScoreBean sb) {
        BorderPane winPane = new BorderPane();
        winPane.visibleProperty().bind(sb.winningTeamProperty().isEqualTo(id));
        winPane.setStyle("-fx-font: 16 Optima; -fx-background-color: white;");
        
        Text txt = new Text();
        PlayerId pA = id.equals(TeamId.TEAM_1) ? PlayerId.PLAYER_1 : PlayerId.PLAYER_2;
        PlayerId pB = id.equals(TeamId.TEAM_1) ? PlayerId.PLAYER_3 : PlayerId.PLAYER_4;
        txt.textProperty().bind(
                Bindings.format(nameMap.get(pA) + " et " + nameMap.get(pB) + 
                        " ont gagné avec %d points contre %d.", 
                        sb.gamePointsProperty(id), sb.gamePointsProperty(id.other())));
        
        return winPane;
    }
}
