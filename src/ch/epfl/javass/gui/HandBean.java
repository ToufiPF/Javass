package ch.epfl.javass.gui;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.CardSet;
import ch.epfl.javass.jass.Jass;
import javafx.beans.property.SimpleSetProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;

public final class HandBean {
    private Card[] hand = new Card[Jass.HAND_SIZE];
    private ObservableSet<Card> playableCards = new SimpleSetProperty<>();
    
    public ObservableList<Card> hand() {
        return FXCollections.unmodifiableObservableList(FXCollections.observableArrayList(hand));
    }
    public void setHand(CardSet newHand) throws IllegalArgumentException {
        final int handSize = newHand.size();
        if(handSize > Jass.HAND_SIZE) {
            throw new IllegalArgumentException("Nouvelle main trop grande.");
        }
        else if (handSize == Jass.HAND_SIZE) {
            for(int i = 0; i < handSize ; ++ i)
                hand[i] = newHand.get(i);
        }
        else if (handSize < Jass.HAND_SIZE) {
            for(int i = 0; i < Jass.HAND_SIZE; ++ i)
                if(!newHand.contains(hand[i]))
                    hand[i] = null;
        }
    }
    
    public ObservableSet<Card> playableCards() {
        return FXCollections.unmodifiableObservableSet(playableCards);
    }
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
