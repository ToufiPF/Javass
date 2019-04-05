package ch.epfl.javass.net;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UncheckedIOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.CardSet;
import ch.epfl.javass.jass.Player;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.jass.Score;
import ch.epfl.javass.jass.TeamId;
import ch.epfl.javass.jass.Trick;
import ch.epfl.javass.jass.TurnState;

/**
 * RemotePlayerClient 
 * Classe publique et finale représentant le cliant d'un joueur
 * @author Amaury Pierre (296498) 
 * @author Aurélien Clergeot (302592)
 */
public final class RemotePlayerClient implements Player, AutoCloseable{
    BufferedWriter w;
    BufferedReader r;
    Socket s;

    /**
     * Constructeur se connectant au serveur du joueur distant grâce au nom de l'hôte
     * @param hostName (String) le nom de l'hôte
     * @throws IOException si les flots ne sont pas fermés
     */
    public RemotePlayerClient(String hostName) throws IOException {
        this(hostName, RemotePlayerServer.PORT);
    }
    
    public RemotePlayerClient(String hostName, int port) throws IOException {
        s = new Socket(hostName, port);
        r = new BufferedReader(
                new InputStreamReader(s.getInputStream(),
                        StandardCharsets.US_ASCII));
        w = new BufferedWriter(
                new OutputStreamWriter(s.getOutputStream(),
                        StandardCharsets.US_ASCII));
    }
    
    private void sendString(String toSend) {
        try{
            w.write(toSend);
            w.write('\n');
            w.flush();
        }
        catch(IOException e) {
            throw new UncheckedIOException(e);
        }
    }
    
    @Override
    public void setPlayers(PlayerId ownId, Map<PlayerId, String> mapNames) {
        String toSend = StringSerializer.join(" ", JassCommand.SET_PLAYERS.command(), StringSerializer.serializeInt(ownId.ordinal()),
                StringSerializer.serializeMapNames(mapNames));
        sendString(toSend);
    }

    @Override
    public void updateHand(CardSet newHand) {
        String toSend = StringSerializer.join(" ", JassCommand.UPDATE_HAND.command(),
                StringSerializer.serializeLong(newHand.packed()));
        sendString(toSend);
    }

    @Override
    public void setTrump(Card.Color trump) {
        String toSend = StringSerializer.join(" ", JassCommand.SET_TRUMP.command(),
                StringSerializer.serializeInt(trump.ordinal()));
        sendString(toSend);

    }

    @Override
    public void updateTrick(Trick newTrick) {
        String toSend = StringSerializer.join(" ", JassCommand.UPDATE_TRICK.command(),
                StringSerializer.serializeInt(newTrick.packed()));
        sendString(toSend);
    }

    @Override
    public void updateScore(Score newScore) {
        String toSend = StringSerializer.join(" ", JassCommand.UPDATE_SCORE.command(),
                StringSerializer.serializeLong(newScore.packed())); 
        sendString(toSend);
    }

    @Override
    public void setWinningTeam(TeamId winningTeam) {
        String toSend = StringSerializer.join(" ", JassCommand.SET_WINNING_TEAM.command(),
                StringSerializer.serializeInt(winningTeam.ordinal()));
        sendString(toSend);
    }
    
    @Override
    public Card cardToPlay(TurnState state, CardSet hand) {
        String toSend = StringSerializer.join(" ", JassCommand.CARD_TO_PLAY.command(),
                StringSerializer.serializeTurnState(state), StringSerializer.serializeLong(hand.packed()));
        sendString(toSend);
        
        try {
        String cardString = r.readLine().trim();
        Card card = Card.ofPacked(StringSerializer.deserializeInt(cardString));
        return card;
        }
        catch(IOException e) {
            throw new UncheckedIOException(e);
        }
        
    }

    @Override
    public void close() throws Exception {
        w.close();
        r.close();
        s.close();
    }

}
