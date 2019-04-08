package ch.epfl.javass.gui;

import ch.epfl.javass.jass.TeamId;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;

public final class ScoreBean {
    private IntegerProperty turnPointsTeam1 = new SimpleIntegerProperty();
    private IntegerProperty gamePointsTeam1 = new SimpleIntegerProperty();
    private IntegerProperty totalPointsTeam1 = new SimpleIntegerProperty();
    private IntegerProperty turnPointsTeam2 = new SimpleIntegerProperty();
    private IntegerProperty gamePointsTeam2 = new SimpleIntegerProperty();
    private IntegerProperty totalPointsTeam2 = new SimpleIntegerProperty();
    private ObjectProperty<TeamId> winningTeam = new SimpleObjectProperty<>();


    /**
     * Méthode publique retournant la propriété contenant
     *      les points du tour de l'équipe donnée
     * @param team (TeamId) l'équipe dont on veut connaitre la propriété contenant
     *      les points du tour
     * @return (ReadOnlyIntegerProperty) la propriété en lecture seule 
     *      contenant les points du tour de l'équipe team
     * @throws IllegalArgumentException lorsque team n'est pas une TeamId valide
     */
    public ReadOnlyIntegerProperty turnPointsProperty(TeamId team) throws IllegalArgumentException {
        switch(team) {
        case TEAM_1 : return turnPointsTeam1;
        case TEAM_2 : return turnPointsTeam2;
        default : throw new IllegalArgumentException("Team non reconnue");
        }
    }

    /**
     * Méthode publique modifiant les points du tour contenu dans la propriété de l'équipe donnée
     * @param team (TeamId) l'équipe dont on veut modifier les points du tour
     * @param newTurnPoints (int) les nouveaux points du tour de l'équipe team
     * @throws IllegalArgumentException lorsque team n'est pas une TeamId valide
     */
    public void setTurnPoints(TeamId team, int newTurnPoints) throws IllegalArgumentException {
        switch(team) {
        case TEAM_1 : turnPointsTeam1.set(newTurnPoints);
        case TEAM_2 : turnPointsTeam2.set(newTurnPoints);
        default : throw new IllegalArgumentException("Team non reconnue");
        }
    }
    
    /**
     * Méthode publique retournant la propriété contenant
     *      les points de la partie de l'équipe donnée
     * @param team (TeamId) l'équipe dont on veut connaitre la propriété contenant
     *      les points de la partie
     * @return (ReadOnlyIntegerProperty) la propriété en lecture seule 
     *      contenant les points de la partie de l'équipe team
     * @throws IllegalArgumentException lorsque team n'est pas une TeamId valide
     */
    public ReadOnlyIntegerProperty gamePointsProperty(TeamId team) throws IllegalArgumentException {
        switch(team) {
        case TEAM_1 : return gamePointsTeam1;
        case TEAM_2 : return gamePointsTeam2;
        default : throw new IllegalArgumentException("Team non reconnue");
        }
    }
    
    /**
     * Méthode publique modifiant les points de la partie contenu dans la propriété de l'équipe donnée
     * @param team (TeamId) l'équipe dont on veut modifier les points de la partie
     * @param newGamePoints (int) les nouveaux points de la partie de l'équipe team
     * @throws IllegalArgumentException lorsque team n'est pas une TeamId valide
     */
    public void setGamePoints(TeamId team, int newGamePoints) throws IllegalArgumentException {
        switch(team) {
        case TEAM_1 : gamePointsTeam1.set(newGamePoints);
        case TEAM_2 : gamePointsTeam2.set(newGamePoints);
        default : throw new IllegalArgumentException("Team non reconnue");
        }
    }
    
    /**
     * Méthode publique retournant la propriété contenant
     *      les points totaux de l'équipe donnée
     * @param team (TeamId) l'équipe dont on veut connaitre la propriété contenant
     *      les points totaux
     * @return (ReadOnlyIntegerProperty) la propriété en lecture seule 
     *      contenant les points totaux de l'équipe team
     * @throws IllegalArgumentException lorsque team n'est pas une TeamId valide
     */
    public ReadOnlyIntegerProperty totalPointsProperty(TeamId team) throws IllegalArgumentException {
        switch(team) {
        case TEAM_1 : return totalPointsTeam1;
        case TEAM_2 : return totalPointsTeam2;
        default : throw new IllegalArgumentException("Team non reconnue");
        }
    }
    
    /**
     * Méthode publique modifiant les points totaux contenu dans la propriété de l'équipe donnée
     * @param team (TeamId) l'équipe dont on veut modifier les points totaux
     * @param newTotalPoints (int) les nouveaux points totaux de l'équipe team
     * @throws IllegalArgumentException lorsque team n'est pas une TeamId valide
     */
    public void setTotalPoints(TeamId team, int newTotalPoints) throws IllegalArgumentException {
        switch(team) {
        case TEAM_1 : totalPointsTeam1.set(newTotalPoints);
        case TEAM_2 : totalPointsTeam2.set(newTotalPoints);
        default : throw new IllegalArgumentException("Team non reconnue");
        }
    }
    
    /**
     * Méthode publique retournant la propriété contenant l'équipe victorieuse
     * @return (ReadOnlyObjectProperty) la propriété contenant l'équipe victorieuse
     */
    public ReadOnlyObjectProperty<TeamId> winningTeamProperty() {
        return winningTeam;
    }
    
    /**
     * Méthode publique permettant de modifier la propriété contenant l'équipe victorieuse
     * @param winTeam (TeamId) l'équipe victorieuse
     */
    public void setWinningTeam(TeamId winTeam) {
        winningTeam.set(winTeam);
    }
}
