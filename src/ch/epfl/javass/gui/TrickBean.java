package ch.epfl.javass.gui;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.jass.Trick;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

/**
 * TrickBean Un bean JavaFX pour l'observation d'un object Trick
 * @author Amaury Pierre (296498) 
 * @author Aurélien Clergeot (302592)
 */
public final class TrickBean {
    private ObjectProperty<Card.Color> trumpProp = new SimpleObjectProperty<Card.Color>();
    private ObservableMap<PlayerId, Card> trickProp = FXCollections.<PlayerId, Card>observableHashMap();
    private ObjectProperty<PlayerId> winningPlayer = new SimpleObjectProperty<PlayerId>();
     
    /**
     * Getter pour la propriété trump (Card.Color) du Trick
     * @return (ObjectProperty<Card.Color>) propriété trump
     */
    public ObjectProperty<Card.Color> trumpProperty() {
        return trumpProp;
    }
    /**
     * Setter pour la propriété trump du Trick
     * @param trump la couleur des atouts
     */
    public void setTrump(Card.Color trump) {
        trumpProp.set(trump);
    }
    
    /**
     * Getter pour la propriété trick 
     * (map décrivant l'état du pli, avec le PlayerId et la carte qu'il a joué)
     * @return (ObservableMap<PlayerId, Card>) (UNMODIFIABLE) map décrivant le trick
     */
    public ObservableMap<PlayerId, Card> trickProperty() {
        return FXCollections.unmodifiableObservableMap(trickProp);
    }
    /**
     * Change l'état du Trick observé, càd les cartes en jeu
     * @param trick (Trick) nouveau pli
     */
    public void setTrick(Trick trick) {
        final int trickSize = trick.size();
        trickProp.clear();
        for (int i = 0 ; i < trickSize ; ++i)
            trickProp.put(trick.player(i), trick.card(i));
        
        if (trick.isEmpty())
            winningPlayer.set(null);
        else
            winningPlayer.set(trick.winningPlayer());
    }
    
    /**
     * Getter pour le joueur en train de gagner le pli
     * (celui avec la carte la plus forte).
     * Si le pli est vide, retourne un ObjectProperty contenant null
     * @return (ObjectProperty<PlayerId>) joueur gagnant actuellement le pli
     */
    public ObjectProperty<PlayerId> winningPlayerProperty() {
        return winningPlayer;
    }
}
