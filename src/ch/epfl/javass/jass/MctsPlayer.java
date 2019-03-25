package ch.epfl.javass.jass;

import java.util.SplittableRandom;

import ch.epfl.javass.Preconditions;

/**
 * MctsPlayer Une classe publique et finale représentant un joueur
 *  simulé au moyen de l'algorithme MCTS
 *  
 * @author Amaury Pierre (296498) 
 * @author Aurélien Clergeot (302592)
 */
public final class MctsPlayer implements Player {
    //TODO choose best class and put it here
    private final PlayerId mOwnId;
    private final int mIterations;
    private final SplittableRandom mRng;

    /**
     * Construit un joueur utilisant l'algorithme MCTS pour décider quelle carte jouer
     * @param ownId (PlayerId) l'identité du joueur simulé
     * @param rngSeed (long) la graine aléatoire permettant de simuler des parties aléatoires
     * @param iterations (int) le nombre d'itérations à effectuer dans l'algorithme MCTS
     */
    public MctsPlayer(PlayerId ownId, long rngSeed, int iterations) {
        Preconditions.checkArgument(iterations >= 9);
        mOwnId = ownId;
        mRng = new SplittableRandom(rngSeed);
        mIterations = iterations;
    }
    @Override
    public Card cardToPlay(TurnState state, CardSet hand) {
        return null;
    }
}
