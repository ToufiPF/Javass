package ch.epfl.javass.gui;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.CardSet;
import ch.epfl.javass.jass.Jass;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleSetProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;

public final class HandBean {
    private ObservableList<Card> hand = new SimpleListProperty<>();
    private ObservableSet<Card> playableCards = new SimpleSetProperty<>();
    
    public ObservableList<Card> hand() {
        return FXCollections.unmodifiableObservableList(hand);
    }
    
    public void setHand(CardSet newHand) throws IllegalArgumentException {
        if(newHand.size() > Jass.HAND_SIZE) {
            throw new IllegalArgumentException("Nouvelle main trop grande");
        }
        else if (newHand.size() == Jass.HAND_SIZE) {
            hand.clear();
            for(int i = 0; i < newHand.size(); ++ i)
                hand.add(newHand.get(i));
        }
        else if (newHand.size() < Jass.HAND_SIZE) {
            for(int i = 0; i < hand.size(); ++ i) {
                if(!newHand.contains(hand.get(i))) {
                    hand.set(i, null);
                }
            }
        }
    }
}
