package ch.epfl.javass.gui;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.CardSet;
import ch.epfl.javass.jass.Jass;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;

/**
 * Un bean JavaFX permettant l'observation de la main et des cartes jouables du joueur
 * @author Amaury Pierre (296498) 
 * @author Aurélien Clergeot (302592)
 */
public final class HandBean {
    private ObservableList<Card> hand;
    private ObservableSet<Card> playableCards;
    
    /**
     * Constructeur créant une main de taille Jass.HAND_SIZE vide
     */
    public HandBean() {
        hand = FXCollections.observableArrayList();
        for (int i = 0 ; i < Jass.HAND_SIZE ; ++i)
            hand.add(null);
        playableCards = FXCollections.observableSet();
    }
    
    /**
     * Getter pour la propriété de la main
     * @return (ObservableList<Card>) la propriété de la main 
     */
    public ObservableList<Card> hand() {
        return FXCollections.unmodifiableObservableList(hand);
    }
    
    /**
     * Change les valeurs contenues dans la propriété de la main
     * @param newHand (CardSet) la nouvelle main
     * @throws IllegalArgumentException lorsque newHand est plus grand que Jass.HAND_SIZE
     */
    public void setHand(CardSet newHand) throws IllegalArgumentException {
        final int handSize = newHand.size();
        if(handSize > Jass.HAND_SIZE) {
            throw new IllegalArgumentException("Nouvelle main trop grande.");
        }
        else if (handSize == Jass.HAND_SIZE) {
            for(int i = 0; i < handSize ; ++ i)
                hand.set(i, newHand.get(i));
        }
        else if (handSize < Jass.HAND_SIZE) {
            for(int i = 0; i < Jass.HAND_SIZE; ++ i)
                if(hand.get(i) != null && !newHand.contains(hand.get(i)))
                    hand.set(i, null);
        }
    }
    
    /**
     * Getter pour la propriété des cartes jouables de la main
     * @return (ObservableSet<Card>) la propriété des cartes jouables
     */
    public ObservableSet<Card> playableCards() {
        return FXCollections.unmodifiableObservableSet(playableCards);
    }
    
    /**
     * Méthode permettant de changer les cartes jouables contenues dans la propriété
     * @param newPlayableCards (CardSet) les nouvelles cartes jouables
     */
    public void setPlayableCards(CardSet newPlayableCards) {
        final int playableSize = newPlayableCards.size();
        if(playableSize > Jass.HAND_SIZE) {
            throw new IllegalArgumentException("Trop de cartes jouables.");
        }
        playableCards.clear();
        for (int i = 0 ; i < playableSize ; ++i)
            playableCards.add(newPlayableCards.get(i));
    }
}
