package ch.epfl.javass.gui;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.Card.Color;
import ch.epfl.javass.jass.Jass;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.jass.TeamId;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

/**
 * GraphicalPlayerView Une classe qui permet de gérer la partie graphique du
 * jeu, en ayant la vue du joueur donné en paramêtre
 *
 * @author Amaury Pierre (296498)
 * @author Aurélien Clergeot (302592)
 */
public final class GraphicalPlayerView {

    private static final ObservableMap<Card, Image> mapLargeCardsImages = computeMapLargeCardsImages();
    private static final ObservableMap<Card, Image> mapSmallCardsImages = computeMapSmallCardsImages();
    private static final ObservableMap<Card.Color, Image> mapImagesTrumps = computeMapImagesTrumps();

    private static final int WIDTH_SMALL_CARD_IMAGE = 160;
    private static final int HEIGHT_SMALL_CARD_IMAGE = 240;
    private static final int WIDTH_LARGE_CARD_IMAGE = 240;
    private static final int HEIGHT_LARGE_CARD_IMAGE = 360;
    private static final int SIZE_TRUMP_IMAGE = 202;

    private static final int GAUSSIAN_BLUR_HALO_BEST_CARD = 4;
    private static final int SPACING_PLAYER_CARD = 10;
    private static final int TRUMP_MARGIN = 25;

    private static String pathToCard(Card c, int width) {
        return "/card_" + c.color().ordinal() + "_" + c.rank().ordinal() + "_"
                + width + ".png";
    }

    /**
     * Donne le chemin vers l'image de l'atout de la couleur donnée
     *
     * @param trump
     *            (Card.Color)
     * @return (String) le chemin vers la couleur donnée
     */
    public static String pathToTrump(Card.Color trump) {
        return "/trump_" + trump.ordinal() + ".png";
    }

    private static ObservableMap<Card, Image> computeMapSmallCardsImages() {
        Map<Card, Image> map = new HashMap<>();
        for (Card.Color c : Card.Color.ALL) {
            for (Card.Rank r : Card.Rank.ALL) {
                Card card = Card.of(c, r);
                map.put(card, new Image(pathToCard(card, WIDTH_SMALL_CARD_IMAGE)));
            }
        }
        return FXCollections
                .unmodifiableObservableMap(FXCollections.observableMap(map));
    }

    private static ObservableMap<Card, Image> computeMapLargeCardsImages() {
        Map<Card, Image> map = new HashMap<>();
        for (Card.Color c : Card.Color.ALL) {
            for (Card.Rank r : Card.Rank.ALL) {
                Card card = Card.of(c, r);
                map.put(card, new Image(pathToCard(card, WIDTH_LARGE_CARD_IMAGE)));
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

    private static HBox createHandPane(HandBean hb,
            ArrayBlockingQueue<Card> cardQueue) {
        HBox handPane = new HBox();
        handPane.setStyle("-fx-background-color: lightgray; "
                + "-fx-spacing: 5px; -fx-padding: 5px;");

        for (int i = 0; i < Jass.HAND_SIZE; ++i) {
            final int iConst = i;
            ImageView img = new ImageView();
            img.setFitWidth(WIDTH_SMALL_CARD_IMAGE / 2);
            img.setFitHeight(HEIGHT_SMALL_CARD_IMAGE / 2);
            img.imageProperty().bind(Bindings.valueAt(mapSmallCardsImages,
                    Bindings.valueAt(hb.handProperty(), i)));

            BooleanBinding isPlayable = Bindings.createBooleanBinding(
                    () -> hb.playableCardsProperty().contains(hb.handProperty().get(iConst)), hb.playableCardsProperty(), hb.handProperty());
            img.opacityProperty().bind(Bindings.when(isPlayable).then(1.0).otherwise(0.2));
            img.disableProperty().bind(Bindings.not(isPlayable));
            img.setOnMouseClicked(e -> {
                cardQueue.add(hb.handProperty().get(iConst));
            });

            handPane.getChildren().add(img);
        }
        return handPane;
    }

    private static GridPane createScorePane(Map<PlayerId, String> nameMap,
            ScoreBean sb) {
        GridPane scorePane = new GridPane();
        scorePane.setStyle(
                "-fx-font: 16 Optima; -fx-background-color: lightgray; "
                        + "-fx-padding: 5px; -fx-alignment: center;");

        for (int i = 0; i < TeamId.COUNT; ++i) {
            Text namesTxt = new Text(nameMap.get(PlayerId.ALL.get(i)) + " et "
                    + nameMap.get(PlayerId.ALL.get(i + 2)) + " : ");
            scorePane.add(namesTxt, 0, i);

            Text turnScoreTxt = new Text();
            turnScoreTxt.textProperty().bind(
                    Bindings.convert(sb.turnPointsProperty(TeamId.ALL.get(i))));
            scorePane.add(turnScoreTxt, 1, i);

            Text lastTrickScore = new Text();
            sb.turnPointsProperty(TeamId.ALL.get(i))
            .addListener((e, oldVal, newVal) -> {
                int deltaScore = newVal.intValue() - oldVal.intValue();
                // Dans le cas où on change de tour
                if (deltaScore < 0)
                    deltaScore = 0;
                lastTrickScore.setText("(+" + deltaScore + ")");
            });
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
        trickPane
        .setStyle("-fx-background-color: whitesmoke; -fx-padding: 5px; "
                + "-fx-border-width: 3px 0px; -fx-border-style: solid; "
                + "-fx-border-color: gray; -fx-alignment: center;");

        final int ownIndex = ownId.ordinal();

        ImageView trumpImage = new ImageView();
        trumpImage.setFitWidth(SIZE_TRUMP_IMAGE / 2);
        trumpImage.setFitHeight(SIZE_TRUMP_IMAGE / 2);
        trumpImage.imageProperty()
        .bind(Bindings.valueAt(mapImagesTrumps, tb.trumpProperty()));

        // Création des cartes
        StackPane[] imagesPanes = new StackPane[PlayerId.COUNT];
        for (int i = 0; i < PlayerId.COUNT; ++i) {
            imagesPanes[i] = new StackPane();

            Rectangle halo = new Rectangle(WIDTH_LARGE_CARD_IMAGE / 2,
                    HEIGHT_LARGE_CARD_IMAGE / 2);
            halo.setStyle("-fx-arc-width: 20; -fx-arc-height: 20;"
                    + " -fx-fill: transparent; -fx-stroke: lightpink;"
                    + " -fx-stroke-width: 5; -fx-opacity: 0.5;");

            halo.setEffect(new GaussianBlur(GAUSSIAN_BLUR_HALO_BEST_CARD));
            halo.visibleProperty().bind(
                    tb.winningPlayerProperty().isEqualTo(PlayerId.ALL.get(i)));

            ImageView img = new ImageView();
            img.setFitWidth(WIDTH_LARGE_CARD_IMAGE / 2);
            img.setFitHeight(HEIGHT_LARGE_CARD_IMAGE / 2);
            img.imageProperty().bind(Bindings.valueAt(mapLargeCardsImages,
                    Bindings.valueAt(tb.trickProperty(), PlayerId.ALL.get(i))));

            imagesPanes[i].getChildren().add(halo);
            imagesPanes[i].getChildren().add(img);
        }

        VBox cardBoxes[] = new VBox[PlayerId.COUNT];
        for (int i = 0; i < PlayerId.COUNT; ++i) {
            Text txt = new Text(nameMap.get(PlayerId.ALL.get(i)));
            txt.setStyle("-fx-font: 14 Optima;");

            cardBoxes[i] = new VBox(SPACING_PLAYER_CARD);
            cardBoxes[i].setAlignment(Pos.CENTER);

            if (i == ownIndex) {
                cardBoxes[i].getChildren().add(imagesPanes[i]);
                cardBoxes[i].getChildren().add(txt);
            } else {
                cardBoxes[i].getChildren().add(txt);
                cardBoxes[i].getChildren().add(imagesPanes[i]);
            }
        }

        trickPane.add(trumpImage, 1, 1);
        GridPane.setHalignment(trumpImage, HPos.CENTER);
        GridPane.setValignment(trumpImage, VPos.CENTER);
        GridPane.setMargin(trumpImage, new Insets(TRUMP_MARGIN));

        // Placement des boxes:
        trickPane.add(cardBoxes[ownIndex], 1, 2);
        trickPane.add(cardBoxes[(ownIndex + 1) % PlayerId.COUNT], 2, 0, 1, 3);
        trickPane.add(cardBoxes[(ownIndex + 2) % PlayerId.COUNT], 1, 0);
        trickPane.add(cardBoxes[(ownIndex + 3) % PlayerId.COUNT], 0, 0, 1, 3);

        return trickPane;
    }

    private static BorderPane createWinningTeamPane(TeamId id,
            Map<PlayerId, String> nameMap, ScoreBean sb) {

        BorderPane winPane = new BorderPane();
        winPane.visibleProperty().bind(sb.winningTeamProperty().isEqualTo(id));
        winPane.setStyle("-fx-font: 16 Optima; -fx-background-color: white;");

        PlayerId pA, pB;
        if (id.equals(TeamId.TEAM_1)) {
            pA = PlayerId.PLAYER_1;
            pB = PlayerId.PLAYER_3;
        } else {
            pA = PlayerId.PLAYER_2;
            pB = PlayerId.PLAYER_4;
        }

        Text txt = new Text();
        txt.setTextAlignment(TextAlignment.CENTER);
        txt.textProperty().bind(Bindings.format(
                nameMap.get(pA) + " et " + nameMap.get(pB)
                + " ont gagné\navec %d points contre %d.",
                sb.totalPointsProperty(id), sb.totalPointsProperty(id.other())));

        winPane.setCenter(txt);
        return winPane;
    }

    // Graphics :
    private final Scene scene;
    private final String ownName;

    /**
     * Construit un nouveau GraphicalPlayerView
     *
     * @param ownId
     *            (PlayerId) Id du joueur à qui appartient la vue
     * @param nameMap
     *            (Map<PlayerId, String>) map des noms des joueurs
     * @param sb
     *            (ScoreBean) le bean des scores
     * @param tb
     *            (TrickBean) le bean des plis
     */
    public GraphicalPlayerView(PlayerId ownId, Map<PlayerId, String> nameMap,
            ScoreBean sb, TrickBean tb, HandBean hb,
            ArrayBlockingQueue<Card> cardQueue) {

        GridPane score = createScorePane(nameMap, sb);
        GridPane trick = createTrickPane(ownId, nameMap, tb);
        HBox hand = createHandPane(hb, cardQueue);
        BorderPane winT1 = createWinningTeamPane(TeamId.TEAM_1, nameMap, sb);
        BorderPane winT2 = createWinningTeamPane(TeamId.TEAM_2, nameMap, sb);

        BorderPane gamePane = new BorderPane(trick);
        gamePane.setTop(score);
        gamePane.setBottom(hand);

        StackPane principalPane = new StackPane(gamePane);
        principalPane.getChildren().add(winT1);
        principalPane.getChildren().add(winT2);
        this.scene = new Scene(principalPane);
        this.ownName = nameMap.get(ownId);
    }

    /**
     * Crée un nouveau Stage et le retourne après lui avoir appliqué la Scene du
     * GraphicalPlayerView
     *
     * @return (Stage) stage auquel la scène de ce GraphicalPlayerView a été
     *         donnée
     * @throws IllegalStateException
     *             si la méthode est appelée dans un Thread différent de celui
     *             de l'Application JavaFX
     */
    public Stage createStage() throws IllegalStateException {
        Stage st = new Stage();
        st.setScene(scene);
        st.setTitle("Javass - " + ownName);
        return st;
    }
}
