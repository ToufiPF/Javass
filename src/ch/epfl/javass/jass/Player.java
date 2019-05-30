package ch.epfl.javass.jass;

import java.util.Map;

import ch.epfl.javass.jass.Card.Color;

/**
 * Player, l'interface chargée de représenter un joueur Doit être implémentée
 * par les differents types de joueurs que l'on créera plus tard (humain,
 * simulé...)
 *
 * @author Amaury Pierre (296498)
 * @author Aurélien Clergeot (302592)
 */
public interface Player {
    /**
     * Donne la carte que le joueur souhaite jouer
     *
     * @param state
     *            (TurnState) l'état du tour actuel
     * @param hand
     *            (CardSet) la main du joueur
     * @return (Card) la carte jouée par le joueur
     */
    public Card cardToPlay(TurnState state, CardSet hand);

    /**
     * Donne la couleur de l'atout choisie par le joueur
     * 
     * @param hand (CardSet) la main du joueur, afin qu'il puisse choisir
     *   l'atout en fonction de ses cartes
     * @return (Color) la couleur d'atout choisie par le joueur
     */
    public Color chooseTrump(CardSet hand);
   
    /**
     * Informe le joueur de son Id, et de ce nom ainsi que celui de ses
     * adversaires. A appeler une fois, en début de partie
     *
     * @param ownId
     *            (PlayerId) l'id du joueur
     * @param mapNames
     *            (Map<PlayerId, String>) la map de noms des joueurs
     */
    public default void setPlayers(PlayerId ownId,
            Map<PlayerId, String> mapNames) {
        // System.out.println("Player.setPlayer(PlayerId, Map<PlayerId,String>)
        // default");
    }

    /**
     * Informe le joueur du changement de couleur des atouts. A appeler quand
     * l'atout est changé (à chaque début de tour)
     *
     * @param trump
     *            (Card.Color) la nouvelle couleur atout
     */
    public default void setTrump(Card.Color trump) {
        // System.out.println("Player.setTrump(Card.Color) default");
    }

    /**
     * Informe le joueur de l'équipe gagnante A appeler une seule fois, quand
     * une équipe dépasse le seuil des 1000 pts
     *
     * @param winningTeam
     *            (TeamId) l'équipe gagnante
     */
    public default void setWinningTeam(TeamId winningTeam) {
        // System.out.println("Player.setWinningTeam(TeamId) default");
    }

    /**
     * Informe le joueur de sa nouvelle main. A appeler dès que la main du
     * joueur change (en début de tour, ou après avoir joué une carte)
     *
     * @param newHand
     */
    public default void updateHand(CardSet newHand) {
        // System.out.println("Player.updateHand(CardSet) default");
    }

    /**
     * Informe le joueur du score de la partie. A appeler chaque fois que le
     * score change (quand le pli est ramassé)
     *
     * @param newScore
     *            (Score) le nouveau Score
     */
    public default void updateScore(Score newScore) {
        // System.out.println("Player.updateScore(Score) default");
    }

    /**
     * Informe le joueur de l'état du pli. A appeler chaque fois que le pli
     * change (après qu'une carte soit jouée, ou après un nouveau pli)
     *
     * @param newTrick
     *            (Trick) le nouveau pli
     */
    public default void updateTrick(Trick newTrick) {
        // System.out.println("Player.updateTrick(Trick) default");
    }
}
