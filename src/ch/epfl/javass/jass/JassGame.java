package ch.epfl.javass.jass;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * JassGame, représente une partie de Jass
 *
 * @author Amaury Pierre (296498) 
 * @author Aurélien Clergeot (302592)
 */
public final class JassGame {

    private final Map<PlayerId, Player> mMapPlayers;
    private final Map<PlayerId, String> mMapPlayerNames;

    private final Random mShuffleRng;
    private final Random mTrumpRng;

    private TurnState mTurnState;
    private PlayerId mFirstPlayer;
    private CardSet[] mHands;
    
    private boolean mGameIsOver;

    /**
     * Construit un JassGame à partir de la seed et des
     * joueurs donnés en argument
     * @param rngSeed (long) la seed du générateur pseudo-aléatoire
     * @param players (Map<PlayerId, Player>) la map des joueurs
     * @param playerNames (Map<PlayerId, String>) la map des noms des joueurs
     */
    public JassGame(long rngSeed, Map<PlayerId, Player> players, Map<PlayerId, String> playerNames) {
        mMapPlayers = Collections.unmodifiableMap(new EnumMap<>(players));
        mMapPlayerNames = Collections.unmodifiableMap(new EnumMap<>(playerNames));

        Random rng = new Random(rngSeed);
        mShuffleRng = new Random(rng.nextLong());
        mTrumpRng = new Random(rng.nextLong());

        mHands = new CardSet[PlayerId.COUNT];
        for (int i = 0 ; i < mHands.length ; ++i)
            mHands[i] = CardSet.EMPTY;

        for (PlayerId id : PlayerId.ALL)
            mMapPlayers.get(id).setPlayers(id, mMapPlayerNames);
    }

    /**
     * Vérifie si la partie est terminée
     * @return (boolean) true ssi la partie est finie
     */
    public boolean isGameOver() {
        for (TeamId id : TeamId.ALL) {
            if (mTurnState.score().gamePoints(id) >= 1000) {
                setWinningTeamForAllPlayers(id);
                return true;
            }
        }
        return false;
    }

    /**
     * Avance l'état du jeu jusqu'à la fin du prochain pli
     * Ne fait rien si la partie est terminée
     * (ne ramasse PAS le pli courant)
     */
    public void advanceToEndOfNextTrick() {
        if (isGameOver())
            return;

        // On est au 1er pli du 1er tour de la partie
        if (mTurnState == null) {
            dealCards(getShuffledCards(), mHands);
            mFirstPlayer = PlayerId.ALL.get(getIndexOfHandWith(mHands, Card.of(Card.Color.DIAMOND, Card.Rank.SEVEN)));

            mTurnState = TurnState.initial(generateTrump(), Score.INITIAL, mFirstPlayer);
            setTrumpForAllPlayers(mTurnState.trick().trump());
        }
        // On commence le premier pli d'un prochain tour
        else if (mTurnState.isTerminal()) {
            dealCards(getShuffledCards(), mHands);
            mFirstPlayer = PlayerId.ALL.get((mFirstPlayer.ordinal() + 1) % PlayerId.COUNT);

            mTurnState = TurnState.initial(generateTrump(), mTurnState.score(), mFirstPlayer);
            setTrumpForAllPlayers(mTurnState.trick().trump());
        }
        // On est dans un pli au milieu d'un tour
        else {
            mTurnState = mTurnState.withTrickCollected();
        }
        updateScoreForAllPlayers(mTurnState.score());

        for (int i = 0 ; i < PlayerId.COUNT ; ++i) {
            PlayerId player_i = mTurnState.nextPlayer();
            Card card_i = mMapPlayers.get(player_i).cardToPlay(mTurnState, mHands[player_i.ordinal()]);

            mHands[player_i.ordinal()].remove(card_i);
            mTurnState = mTurnState.withNewCardPlayed(card_i);

            mMapPlayers.get(player_i).updateHand(mHands[player_i.ordinal()]);
            updateTrickForAllPlayers(mTurnState.trick());
        }
    }

    private List<Card> getShuffledCards() {
        List<Card> cards = new ArrayList<Card>();
        for (Card.Color c : Card.Color.ALL)
            for (Card.Rank r : Card.Rank.ALL)
                cards.add(Card.of(c, r));

        Collections.shuffle(cards, mShuffleRng);
        return cards;
    }
    private void dealCards(List<Card> shuffled, CardSet[] hands) {
        final int cardsPerPlayer = shuffled.size() / hands.length;
        for (int i = 0 ; i < hands.length ; ++i)
            hands[i] = CardSet.of(shuffled.subList(i * cardsPerPlayer, (i + 1) * cardsPerPlayer));
    }
    
    private PlayerId getPlayerWith7Diamond() {
        return PlayerId.ALL.get(getIndexOfHandWith(mHands, Card.of(Card.Color.DIAMOND, Card.Rank.SEVEN)));
    }
    
    private int getIndexOfHandWith(CardSet[] hands, Card card) {
        for (int i = 0 ; i < hands.length ; ++i)
            if (hands[i].contains(card))
                return i;
        return -1;
    }

    private Card.Color generateTrump() {
        return Card.Color.ALL.get(mTrumpRng.nextInt(Card.Color.COUNT));
    }

    private void updateTrickForAllPlayers(Trick newTrick) {
        for (Map.Entry<PlayerId, Player> e: mMapPlayers.entrySet())
            e.getValue().updateTrick(newTrick);
    }
    private void updateScoreForAllPlayers(Score newScore) {
        for (Map.Entry<PlayerId, Player> e: mMapPlayers.entrySet())
            e.getValue().updateScore(newScore);
    }
    private void setTrumpForAllPlayers(Card.Color trump) {
        for (Map.Entry<PlayerId, Player> e: mMapPlayers.entrySet())
            e.getValue().setTrump(trump);
    }
    private void setWinningTeamForAllPlayers(TeamId winningTeam) {
        for (Map.Entry<PlayerId, Player> e: mMapPlayers.entrySet())
            e.getValue().setWinningTeam(winningTeam);
    }
}
