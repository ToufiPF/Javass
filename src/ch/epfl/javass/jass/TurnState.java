package ch.epfl.javass.jass;

import ch.epfl.javass.Preconditions;
import ch.epfl.javass.jass.Card.Color;

/**
 * TurnState Une classe publique, finale et immuable représentant l'état d'un
 * tour
 * *
 * @author Amaury Pierre (296498) 
 * @author Aurélien Clergeot (302592)
 */
public final class TurnState {

    private final long actualScore;
    private final long unplayedCards;
    private final int actualTrick;

    private TurnState(long actualScore, long unplayedCards, int actualTrick) {
        assert PackedScore.isValid(actualScore);
        assert PackedCardSet.isValid(unplayedCards);
        assert PackedTrick.isValid(actualTrick);

        this.actualScore = actualScore;
        this.unplayedCards = unplayedCards;
        this.actualTrick = actualTrick;
    }

    private void exceptionIfTrickFull() throws IllegalStateException {
        if (PackedTrick.isFull(actualTrick)) {
            throw new IllegalStateException();
        }
    }

    private void exceptionIfTrickNotFull() throws IllegalStateException {
        if (!PackedTrick.isFull(actualTrick)) {
            throw new IllegalStateException();
        }
    }

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
     */
    public static TurnState ofPackedComponents(long pkScore,
            long pkUnplayedCards, int pkTrick) {
        Preconditions.checkArgument(PackedScore.isValid(pkScore));
        Preconditions.checkArgument(PackedCardSet.isValid(pkUnplayedCards));
        Preconditions.checkArgument(PackedTrick.isValid(pkTrick));

        return new TurnState(pkScore, pkUnplayedCards, pkTrick);
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
     * Accesseur à la version empaquetée des cartes non jouées du tour
     * 
     * @return (long) la version empaquetée des cartes non jouées du tour
     */
    public long packedUnplayedCards() {
        return unplayedCards;
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
     * Accesseur à la version objet du score actuel du tour
     * 
     * @return (Score) la version objet du score actuel du tour
     */
    public Score score() {
        return Score.ofPacked(actualScore);
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
     * Accesseur à la version objet du pli actuel du tour
     * 
     * @return (Trick) la version objet du pli actuel du tour
     */
    public Trick trick() {
        return Trick.ofPacked(actualTrick);
    }

    /**
     * Retourne si l'état est terminal, càd si le dernier pli du tour a été joué
     * 
     * @return true (boolean) ssi l'état est terminal, càd si le pli actuel est
     *         le dernier et est plein
     */
    public boolean isTerminal() {
        return PackedTrick.isLast(actualTrick)
                && PackedTrick.isFull(actualTrick);
    }

    /**
     * Retourne le joueur devant jouer la prochaine carte
     * 
     * @return (PlayerId) le joueur devant jouer la prochaine carte
     */
    public PlayerId nextPlayer() {
        exceptionIfTrickFull();
        return PackedTrick.player(actualTrick, PackedTrick.size(actualTrick));
    }

    /**
     * Retourne l'état correspondant à celui auquel on l'applique après avoir
     * joué la carte donnée
     * 
     * @param card
     *            (Card) la carte à jouer dans l'état du tour
     * @return (TurnState) l'état du tour après avoir joué la carte card
     */
    public TurnState withNewCardPlayed(Card card) {
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
     * pli courant ait été ramassé
     * 
     * @return (TurnState) l'état correspondant à celui auquel on l'applique
     *         après que le pli courant ait été ramassé
     */
    public TurnState withTrickCollected() {
        exceptionIfTrickNotFull();

        // On met à jour le score actuel
        long newActualScore = PackedScore.nextTurn(actualScore);

        // On met à jour le pli actuel
        int newActualTrick = PackedTrick.nextEmpty(actualTrick);

        return new TurnState(newActualScore, unplayedCards, newActualTrick);
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
        if (updated.trick().isFull()) {
            updated = updated.withTrickCollected();
        }

        return updated;
    }
}
