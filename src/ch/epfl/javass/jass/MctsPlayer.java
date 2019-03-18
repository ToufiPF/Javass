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

    /**
     * Node
     * Représente un noeud pour l'algorithme MCTS
     *  
     * @author Amaury Pierre (296498) 
     * @author Aurélien Clergeot (302592)
     */
    private class Node{
        private TurnState mState;
        private Node[] mChilds;
        private CardSet mNonExistingChilds;
        private int mTotalPoints;
        private int mNbTours;

        private Node(TurnState turnState, CardSet nonExistingChilds, int totalPoints, int nbTours) {
            mState = turnState;
            mChilds = new Node[nonExistingChilds.size()];
            mNonExistingChilds = nonExistingChilds;
            mTotalPoints = totalPoints;
            mNbTours = nbTours;
        }

        /**
         * Choisit l'index du "meilleur" enfant de ce Node,
         * càd celui ayant le plus grande valeur de V (cf. Etape 6 site internet)
         * C'est le fils auquel on devra ajouter un nouveau Node
         * 
         * @param c (int) constante pour l'importance du degré d'exploration des noeuds
         * (c=0 donnera l'index du Node avec la meilleure carte jouée)
         * @return (int) index dans le tableau d'enfant
         */
        private int bestChildIndex(int c) {
            double[] scoresChilds = new double [mChilds.length];
            for(int i = 0; i < mChilds.length; ++i) {
                if (mChilds[i] == null || mChilds[i].mNbTours <= 0)
                    scoresChilds[i] = Double.MAX_VALUE;
                else
                    scoresChilds[i] = (mChilds[i].mTotalPoints / mChilds[i].mNbTours) 
                    + c * Math.sqrt((2 * Math.log(mNbTours)) / mChilds[i].mNbTours);
            }

            int bestIndex = 0;
            double maxScore = 0;
            for(int i = 0; i < scoresChilds.length; ++i) {
                if(scoresChilds[i] > maxScore) {
                    maxScore = scoresChilds[i];
                    bestIndex = i;
                }
            }
            return bestIndex;
        }

        /**
         * Ajoute un enfant à ce Node,
         * en y jouant la carte à l'index donné dans le
         * CardSet nonExistingChilds
         * @param index (int) l'index de la carte
         */
        private void addChildToIndex(int index) {
            assert index >= 0;
            assert index < mChilds.length;
            
            if (mChilds[index] == null) {
                mChilds[index] = child;
            }
            else
                System.out.println("Node " + hashCode() + " - addChildToNode() - index child déjà occupé par " + mChilds[index]);
        }
        

        private CardSet getPossibleCards() {
            CardSet possibleCards = CardSet.ALL_CARDS;
            for (int i = 0 ; i < mState.trick().size() ; ++i)
                possibleCards.remove(mState.trick().card(i));
            return possibleCards.difference(mNonExistingChilds);
        }

        private Score computeScoreEndOfTrick(TurnState state) {
            CardSet possibleCards = getPossibleCards();
            while (!PackedTrick.isFull(state.packedTrick())) {
                Card card = possibleCards.get(mRng.nextInt(possibleCards.size()));
                state = state.withNewCardPlayed(card);
                possibleCards.remove(card);
            }
            return state.score();
        }
    }

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
        Node baseTree = new Node(state, hand, 0, mIterations);
        for (int i = 0 ; i < mIterations ; ++i) {
            
        }

        return null;
    }
    

    
}
