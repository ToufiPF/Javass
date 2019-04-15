package ch.epfl.javass.gui;

import ch.epfl.javass.jass.Card;

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
}
