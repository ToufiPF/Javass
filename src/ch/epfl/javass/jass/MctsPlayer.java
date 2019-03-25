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
