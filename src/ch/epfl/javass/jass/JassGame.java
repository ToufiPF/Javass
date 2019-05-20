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
    private final CardSet[] mHands;

    private boolean mGameIsOver;

    /**
     * Construit un JassGame à partir de la seed et des joueurs donnés en
     * argument
     *
     * @param rngSeed
     *            (long) la seed du générateur pseudo-aléatoire
     * @param players
     *            (Map<PlayerId, Player>) la map des joueurs
     * @param playerNames
     *            (Map<PlayerId, String>) la map des noms des joueurs
     */
    public JassGame(long rngSeed, Map<PlayerId, Player> players,
            Map<PlayerId, String> playerNames) {
        mMapPlayers = Collections.unmodifiableMap(new EnumMap<>(players));
        mMapPlayerNames = Collections
                .unmodifiableMap(new EnumMap<>(playerNames));

        Random rng = new Random(rngSeed);
        mShuffleRng = new Random(rng.nextLong());
        mTrumpRng = new Random(rng.nextLong());

        mHands = new CardSet[PlayerId.COUNT];
        for (int i = 0; i < mHands.length; ++i)
            mHands[i] = CardSet.EMPTY;

        for (PlayerId id : PlayerId.ALL)
            mMapPlayers.get(id).setPlayers(id, mMapPlayerNames);
    }

    /**
     * Avance l'état du jeu jusqu'à la fin du prochain pli Ne fait rien si la
     * partie est terminée (ne ramasse PAS le pli courant)
     */
    public void advanceToEndOfNextTrick() {
        if (isGameOver()) {
            return;
        }

        // On est au 1er pli du 1er tour de la partie
        if (mTurnState == null) {
            dealCardsToPlayers();
            updateHandForAll(mHands);
            mFirstPlayer = getPlayerWith7Diamond();
            
            mTurnState = TurnState.initial(mMapPlayers.get(mFirstPlayer)
                    .chooseTrump(mHands[mFirstPlayer.ordinal()]), Score.INITIAL, mFirstPlayer);
            setTrumpForAll(mTurnState.trick().trump());
        }
        // Sinon, on peut le ramasser
        else {
            mTurnState = mTurnState.withTrickCollected();
        }
        updateScoreForAll(mTurnState.score());

        // On vérifie si une team a gagné
        if (mTurnState.score()
                .totalPoints(getTeamWithMostPoints()) >= Jass.WINNING_POINTS) {
            setWinningTeamForAll(getTeamWithMostPoints());
            mGameIsOver = true;
            return;
        }

        // Si le tour est terminé, on en recrée un
        if (mTurnState.isTerminal()) {
            dealCardsToPlayers();
            updateHandForAll(mHands);
            mFirstPlayer = PlayerId.ALL
                    .get((mFirstPlayer.ordinal() + 1) % PlayerId.COUNT);
            mTurnState = TurnState.initial(mMapPlayers.get(mFirstPlayer).chooseTrump(mHands[mFirstPlayer.ordinal()]),
                    mTurnState.score().nextTurn(), mFirstPlayer);
            updateScoreForAll(mTurnState.score());
            setTrumpForAll(mTurnState.trick().trump());
        }

        // On fait jouer les joueurs jusqu'à la fin du pli
        updateTrickForAll(mTurnState.trick());
        while (!mTurnState.trick().isFull()) {
            PlayerId player = mTurnState.nextPlayer();
            Card card = mMapPlayers.get(player).cardToPlay(mTurnState,
                    mHands[player.ordinal()]);

            mHands[player.ordinal()] = mHands[player.ordinal()].remove(card);
            mTurnState = mTurnState.withNewCardPlayed(card);

            mMapPlayers.get(player).updateHand(mHands[player.ordinal()]);
            updateTrickForAll(mTurnState.trick());
        }
    }

    /**
     * Vérifie si la partie est terminée
     *
     * @return (boolean) true ssi la partie est finie
     */
    public boolean isGameOver() {
        return mGameIsOver;
    }

    private void dealCardsToPlayers() {
        List<Card> shuffled = getShuffledCards();
        
        for (int i = 0; i < mHands.length; ++i)
            mHands[i] = CardSet.of(shuffled.subList(i * Jass.HAND_SIZE,
                    (i + 1) * Jass.HAND_SIZE));
    }

    /**
     * Génère aléatoirement une couleur pour les cartes d'atout
     *
     * @return (Card.Color) la nouvelle couleur pour les atouts
     */
    @SuppressWarnings("unused")
    private Card.Color generateTrump() {
        return Card.Color.ALL.get(mTrumpRng.nextInt(Card.Color.COUNT));
    }

    /**
     * Donne l'index de la main qui contient la carte donnée. Retourne -1 si
     * aucune main ne la possède
     *
     * @param card
     *            (Card) la carte à chercher
     * @return (int) l'index de la main
     */
    private int getIndexOfHandWith(Card card) {
        for (int i = 0; i < mHands.length; ++i)
            if (mHands[i].contains(card))
                return i;
        return -1;
    }

    /**
     * Retourne le joueur qui possède le 7 de carreaux
     *
     * @return (PlayerId) le joueur avec le 7 de carreaux
     * @throws IllegalStateException
     *             si aucun joueur ne le possède
     */
    private PlayerId getPlayerWith7Diamond() {
        final int index = getIndexOfHandWith(
                Card.of(Card.Color.DIAMOND, Card.Rank.SEVEN));
        if (index == -1)
            throw new IllegalStateException(
                    "Aucun joueur ne possède le 7 de carreaux.\n"
                            + "L'appel à la méthode getPlayerWith7Diamond est interdit.");
        return PlayerId.ALL.get(index);
    }

    private List<Card> getShuffledCards() {
        List<Card> cards = new ArrayList<Card>();
        for (Card.Color c : Card.Color.ALL)
            for (Card.Rank r : Card.Rank.ALL)
                cards.add(Card.of(c, r));

        Collections.shuffle(cards, mShuffleRng);
        return cards;
    }

    /**
     * Donne la team avec le plus de points au total (Dans le cas où le nb de
     * points est égal, retourne TEAM_1)
     *
     * @return (TeamId) la team avec le plus de points
     */
    private TeamId getTeamWithMostPoints() {
        if (mTurnState.score().totalPoints(TeamId.TEAM_1) >= mTurnState.score()
                .totalPoints(TeamId.TEAM_2))
            return TeamId.TEAM_1;
        return TeamId.TEAM_2;
    }

    private void setTrumpForAll(Card.Color trump) {
        for (Map.Entry<PlayerId, Player> e : mMapPlayers.entrySet())
            e.getValue().setTrump(trump);
    }

    private void setWinningTeamForAll(TeamId winningTeam) {
        for (Map.Entry<PlayerId, Player> e : mMapPlayers.entrySet())
            e.getValue().setWinningTeam(winningTeam);
    }

    private void updateHandForAll(CardSet[] hands) {
        for (Map.Entry<PlayerId, Player> e : mMapPlayers.entrySet())
            e.getValue().updateHand(hands[e.getKey().ordinal()]);
    }

    private void updateScoreForAll(Score newScore) {
        for (Map.Entry<PlayerId, Player> e : mMapPlayers.entrySet())
            e.getValue().updateScore(newScore);
    }

    private void updateTrickForAll(Trick newTrick) {
        for (Map.Entry<PlayerId, Player> e : mMapPlayers.entrySet())
            e.getValue().updateTrick(newTrick);
    }
}
