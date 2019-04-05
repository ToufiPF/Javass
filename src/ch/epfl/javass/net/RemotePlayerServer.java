package ch.epfl.javass.net;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.net.Socket;
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
 *  
 * @author Amaury Pierre (296498) 
 * @author Aurélien Clergeot (302592)
 */
public final class RemotePlayerServer {
    
    public static final int PORT = 5108;
    
    private Player subPlayer;
    
    public RemotePlayerServer(Player p) {
        subPlayer = p;
    }
    
    public void run() {
        try (ServerSocket serv = new ServerSocket(PORT) ; Socket s = serv.accept() ; 
                BufferedReader input = new BufferedReader(new InputStreamReader(s.getInputStream())) ;
                BufferedWriter output = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()))) {
            
            while (true) {
                String msg = input.readLine().trim();
                JassCommand cmd = JassCommand.valueOfByCommand(msg.substring(0, msg.indexOf(' ')));
                String args = msg.substring(msg.indexOf(' ') + 1);
                
                switch (cmd) {
                case CARD_TO_PLAY:
                {
                    TurnState state = StringSerializer.deserializeTurnState(args.substring(0, args.indexOf(' ')));
                    final long pkHand = StringSerializer.deserializeLong(args.substring(args.indexOf(' ') + 1));
                    Card card = subPlayer.cardToPlay(state, CardSet.ofPacked(pkHand));
                    
                    output.write(StringSerializer.serializeInt(card.packed()));
                    output.write('\n');
                    output.flush();
                    break;
                }
                case SET_PLAYERS:
                {
                    int id = StringSerializer.deserializeInt(args.substring(0, args.indexOf(' ')));
                    Map<PlayerId, String> map = StringSerializer.deserializeMapNames(args.substring(args.indexOf(' ') + 1));
                    subPlayer.setPlayers(PlayerId.ALL.get(id), map);
                    break;
                }
                case SET_TRUMP:
                {
                    int color = StringSerializer.deserializeInt(args);
                    subPlayer.setTrump(Card.Color.ALL.get(color));
                    break;
                }
                case SET_WINNING_TEAM:
                {
                    final int winningTeam = StringSerializer.deserializeInt(args);
                    subPlayer.setWinningTeam(TeamId.ALL.get(winningTeam));
                    break;
                }
                case UPDATE_HAND:
                {
                    final long pkHand = StringSerializer.deserializeLong(args);
                    subPlayer.updateHand(CardSet.ofPacked(pkHand));
                    break;
                }
                case UPDATE_SCORE:
                {
                    final long pkScore = StringSerializer.deserializeLong(args);
                    subPlayer.updateScore(Score.ofPacked(pkScore));
                    break;
                }
                case UPDATE_TRICK:
                {
                    final int pkTrick = StringSerializer.deserializeInt(args);
                    subPlayer.updateTrick(Trick.ofPacked(pkTrick));
                    break;
                }
                default:
                    throw new IllegalArgumentException("Unsupported Command : " + cmd);
                }
            }
            
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (IllegalArgumentException e) {
            System.err.println(e);
        }
    }
}
