package ch.epfl.javass.jass;

import ch.epfl.javass.Preconditions;
import ch.epfl.javass.jass.Card.Color;

/**
 * TurnState Une classe publique, finale et immuable représentant l'état d'un
 * tour
 *
 * @author Amaury Pierre (296498)
 * @author Aurélien Clergeot (302592)
 */
public final class TurnState {

    /**
     * Méthode publique et statique retournant l'état initial du tour d'atout,
     * score et premier joueur donnés
     *
     * @param trump
     *            (Color) la couleur de l'atout
     * @param score
     *            (Score) la score initial du tour
     * @param firstPlayer
     *            (PlayerId) le joueur initial
     * @return (TurnState) l'état initial du tour d'atout, score et premier
     *         joueur donnés
     */
    public static TurnState initial(Color trump, Score score,
            PlayerId firstPlayer) {
        return new TurnState(score.packed(), PackedCardSet.ALL_CARDS,
                Trick.firstEmpty(trump, firstPlayer).packed());
    }

    /**
     * Méthode publique et statique retournant l'état dont les composantes sont
     * celles données
     *
     * @param pkScore
     *            (long) le score empaqueté du tour
     * @param pkUnplayedCards
     *            (long) l'ensemble empaqueté des cartes non jouées du tour
     * @param pkTrick
     *            (int) le pli empaqueté actuel
     * @return (TurnState) l'état du tour dont les composantes sont celles
     *         données
     * @throws IllegalArgumentException
     *             si l'un des arguments packed est invalide
     */
    public static TurnState ofPackedComponents(long pkScore,
            long pkUnplayedCards, int pkTrick) throws IllegalArgumentException {
        Preconditions.checkArgument(PackedScore.isValid(pkScore));
        Preconditions.checkArgument(PackedCardSet.isValid(pkUnplayedCards));
        Preconditions.checkArgument(PackedTrick.isValid(pkTrick));

        return new TurnState(pkScore, pkUnplayedCards, pkTrick);
    }

    private final long actualScore;

    private final long unplayedCards;

    private final int actualTrick;

    private TurnState(long actualScore, long unplayedCards, int actualTrick) {
        this.actualScore = actualScore;
        this.unplayedCards = unplayedCards;
        this.actualTrick = actualTrick;
    }

    /**
     * Retourne si l'état est terminal, càd si le dernier pli du tour a été joué
     *
     * @return true (boolean) ssi l'état est terminal, càd si le pli actuel est
     *         invalide
     */
    public boolean isTerminal() {
        return actualTrick == PackedTrick.INVALID;
    }

    /**
     * Retourne le joueur devant jouer la prochaine carte
     *
     * @return (PlayerId) le joueur devant jouer la prochaine carte
     * @throws IllegalStateException
     *             si le pli est plein
     */
    public PlayerId nextPlayer() throws IllegalStateException {
        exceptionIfTrickFull();
        return PackedTrick.player(actualTrick, PackedTrick.size(actualTrick));
    }

    /**
     * Accesseur à la version empaquetée du score actuel du tour
     *
     * @return (long) la version empaquetée du score su tour
     */
    public long packedScore() {
        return actualScore;
    }

    /**
     * Accesseur à la version empaquetée du pli actuel du tour
     *
     * @return (int) la version empaquetée du pli actuel du tour
     */
    public int packedTrick() {
        return actualTrick;
    }

    /**
     * Accesseur à la version empaquetée des cartes non jouées du tour
     *
     * @return (long) la version empaquetée des cartes non jouées du tour
     */
    public long packedUnplayedCards() {
        return unplayedCards;
    }

    /**
     * Accesseur à la version objet du score actuel du tour
     *
     * @return (Score) la version objet du score actuel du tour
     */
    public Score score() {
        return Score.ofPacked(actualScore);
    }

    /**
     * Accesseur à la version objet du pli actuel du tour
     *
     * @return (Trick) la version objet du pli actuel du tour
     */
    public Trick trick() {
        return Trick.ofPacked(actualTrick);
    }

    /**
     * Accesseur à la version objet des cartes non jouées du tour
     *
     * @return (CardSet) la version objet des cartes non jouées du tour
     */
    public CardSet unplayedCards() {
        return CardSet.ofPacked(unplayedCards);
    }

    /**
     * Retourne l'état correspondant à celui auquel on l'applique après avoir
     * joué la carte donnée
     *
     * @param card
     *            (Card) la carte à jouer dans l'état du tour
     * @return (TurnState) l'état du tour après avoir joué la carte card
     * @throws IllegalStateException
     *             si le pli est plein
     */
    public TurnState withNewCardPlayed(Card card) throws IllegalStateException {
        assert (PackedCardSet.contains(unplayedCards, card.packed()));
        exceptionIfTrickFull();

        // On ajoute la carte card au pli actuel
        int newActualTrick = PackedTrick.withAddedCard(actualTrick,
                card.packed());

        // On retire la carte card des cartes non-jouées
        long newUnplayedCards = PackedCardSet.remove(unplayedCards,
                card.packed());
        return new TurnState(actualScore, newUnplayedCards, newActualTrick);
    }

    /**
     * Retourne l'état correspondant à celui auquel on l'applique après que le
     * prochain joueur ait joué la carte donnée, et que le pli courant ait été
     * ramassé s'il est alors plein
     *
     * @param card
     *            (Card) la carte à ajouter au tour
     * @return (TurnState) l'état du tour après avoir ajouté la carte donnée et
     *         ramassé le pli s'il est alors plein
     */
    public TurnState withNewCardPlayedAndTrickCollected(Card card) {
        // On ajoute la carte card
        TurnState updated = this.withNewCardPlayed(card);

        // On vérifie si le pli est plein et si oui, on ramasse le pli courant
        if (updated.trick().isFull())
            updated = updated.withTrickCollected();

        return updated;
    }

    /**
     * Retourne l'état correspondant à celui auquel on l'applique après que le
     * pli courant ait été ramassé
     *
     * @return (TurnState) l'état correspondant à celui auquel on l'applique
     *         après que le pli courant ait été ramassé
     * @throws IllegalStateException
     *             si le pli n'est pas plein
     */
    public TurnState withTrickCollected() throws IllegalStateException {
        exceptionIfTrickNotFull();

        // On met à jour le score actuel
        long newScore = PackedScore.withAdditionalTrick(actualScore,
                PackedTrick.winningPlayer(actualTrick).team(),
                PackedTrick.points(actualTrick));

        // On met à jour le pli actuel
        int newTrick = PackedTrick.nextEmpty(actualTrick);

        return new TurnState(newScore, unplayedCards, newTrick);
    }

    private void exceptionIfTrickFull() throws IllegalStateException {
        if (PackedTrick.isFull(actualTrick))
            throw new IllegalStateException();
    }

    private void exceptionIfTrickNotFull() throws IllegalStateException {
        if (!PackedTrick.isFull(actualTrick))
            throw new IllegalStateException();
    }
}
