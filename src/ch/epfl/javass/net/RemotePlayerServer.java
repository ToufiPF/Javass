package ch.epfl.javass.net;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
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
 * RemotePlayerServer Une classe qui prend en argument un joueur, et le fait
 * jouer dans sa méthode run, en fonction des commandes/requêtes envoyées par le
 * client. Elle implémente Runnable, afin de pouvoir la faire fonctionner dans
 * un Thread.
 *
 * @author Amaury Pierre (296498)
 * @author Aurélien Clergeot (302592)
 */
public final class RemotePlayerServer implements Runnable {

    public static final int DEFAULT_PORT = 5108;

    private final Player subPlayer;
    private final int effectivePort;

    /**
     * Construit un RemotePlayerServer, avec le joueur donné, et connecté au
     * port par default
     * 
     * @param p
     *            (Player) player jouant dans le serveur
     */
    public RemotePlayerServer(Player p) {
        this(p, DEFAULT_PORT);
    }

    /**
     * Construit un RemotePlayerServer, avec le joueur donné, et connecté au
     * port spécifié
     * 
     * @param p
     *            (Player) player jouant dans le serveur
     * @param port
     *            (int) port de l'hôte
     */
    public RemotePlayerServer(Player p, int port) {
        subPlayer = p;
        effectivePort = port;
    }

    /**
     * Donne le port auquel le serveur s'est branché
     * 
     * @return (int) port du serveur
     */
    public int getPort() {
        return effectivePort;
    }

    private void onCardToPlay(String args, BufferedWriter output)
            throws IOException {
        TurnState state = StringSerializer
                .deserializeTurnState(args.substring(0, args.indexOf(' ')));
        final long pkHand = StringSerializer
                .deserializeLong(args.substring(args.indexOf(' ') + 1));
        Card card = subPlayer.cardToPlay(state, CardSet.ofPacked(pkHand));

        output.write(StringSerializer.serializeInt(card.packed()));
        output.write('\n');
        output.flush();
    }

    private void onSetPlayer(String args) {
        int id = StringSerializer
                .deserializeInt(args.substring(0, args.indexOf(' ')));
        Map<PlayerId, String> map = StringSerializer
                .deserializeMapNames(args.substring(args.indexOf(' ') + 1));
        subPlayer.setPlayers(PlayerId.ALL.get(id), map);
    }

    private void onSetTrump(String args) {
        int color = StringSerializer.deserializeInt(args);
        subPlayer.setTrump(Card.Color.ALL.get(color));
    }

    private void onSetWinningTeam(String args) {
        final int winningTeam = StringSerializer.deserializeInt(args);
        subPlayer.setWinningTeam(TeamId.ALL.get(winningTeam));
    }

    private void onUpdateHand(String args) {
        final long pkHand = StringSerializer.deserializeLong(args);
        subPlayer.updateHand(CardSet.ofPacked(pkHand));
    }

    private void onUpdateScore(String args) {
        final long pkScore = StringSerializer.deserializeLong(args);
        subPlayer.updateScore(Score.ofPacked(pkScore));
    }

    private void onUpdateTrick(String args) {
        final int pkTrick = StringSerializer.deserializeInt(args);
        subPlayer.updateTrick(Trick.ofPacked(pkTrick));
    }

    @Override
    public void run() throws IllegalArgumentException {
        try (ServerSocket serv = new ServerSocket(effectivePort);
                Socket s = serv.accept();
                BufferedReader input = new BufferedReader(
                        new InputStreamReader(s.getInputStream()));
                BufferedWriter output = new BufferedWriter(
                        new OutputStreamWriter(s.getOutputStream()))) {

            while (true) {
                String msg = input.readLine().trim();
                JassCommand cmd = JassCommand
                        .valueOfByCommand(msg.substring(0, msg.indexOf(' ')));
                String args = msg.substring(msg.indexOf(' ') + 1);

                switch (cmd) {
                case CARD_TO_PLAY:
                    onCardToPlay(args, output);
                    break;
                case SET_PLAYERS:
                    onSetPlayer(args);
                    break;
                case SET_TRUMP:
                    onSetTrump(args);
                    break;
                case SET_WINNING_TEAM:
                    onSetWinningTeam(args);
                    break;
                case UPDATE_HAND:
                    onUpdateHand(args);
                    break;
                case UPDATE_SCORE:
                    onUpdateScore(args);
                    break;
                case UPDATE_TRICK:
                    onUpdateTrick(args);
                    break;

                default:
                    throw new IllegalArgumentException(
                            "Unsupported Command : " + cmd);
                }
            }
        } catch (SocketException e) {
            System.err.println("Connexion lost with client.");
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
