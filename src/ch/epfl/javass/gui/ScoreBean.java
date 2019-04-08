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


    public ReadOnlyIntegerProperty turnPointsProperty(TeamId team) throws IllegalArgumentException {
        switch(team) {
        case TEAM_1 : return turnPointsTeam1;
        case TEAM_2 : return turnPointsTeam2;
        default : throw new IllegalArgumentException("Team non reconnue");
        }
    }

    public void setTurnPoints(TeamId team, int newTurnPoints) throws IllegalArgumentException {
        switch(team) {
        case TEAM_1 : turnPointsTeam1.set(newTurnPoints);
        case TEAM_2 : turnPointsTeam2.set(newTurnPoints);
        default : throw new IllegalArgumentException("Team non reconnue");
        }
    }
    
    public ReadOnlyIntegerProperty gamePointsProperty(TeamId team) throws IllegalArgumentException {
        switch(team) {
        case TEAM_1 : return gamePointsTeam1;
        case TEAM_2 : return gamePointsTeam2;
        default : throw new IllegalArgumentException("Team non reconnue");
        }
    }
    
    public void setTotalPoints(TeamId team, int newTotalPoints) throws IllegalArgumentException {
        switch(team) {
        case TEAM_1 : totalPointsTeam1.set(newTotalPoints);
        case TEAM_2 : totalPointsTeam2.set(newTotalPoints);
        default : throw new IllegalArgumentException("Team non reconnue");
        }
    }
    
    public ReadOnlyIntegerProperty totalPointsProperty(TeamId team) throws IllegalArgumentException {
        switch(team) {
        case TEAM_1 : return totalPointsTeam1;
        case TEAM_2 : return totalPointsTeam2;
        default : throw new IllegalArgumentException("Team non reconnue");
        }
    }
    
    public void setGamePoints(TeamId team, int newGamePoints) throws IllegalArgumentException {
        switch(team) {
        case TEAM_1 : gamePointsTeam1.set(newGamePoints);
        case TEAM_2 : gamePointsTeam2.set(newGamePoints);
        default : throw new IllegalArgumentException("Team non reconnue");
        }
    }
    
    public ReadOnlyObjectProperty<TeamId> winningTeamProperty() {
        return winningTeam;
    }
    
    public void setWinningTeam(TeamId winTeam) {
        winningTeam.set(winTeam);
    }
}
