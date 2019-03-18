package ch.epfl.javass.jass;

import ch.epfl.javass.Preconditions;

/**
 * MctsPlayer Une classe publique et finale représentant un joueur
 *  simulé au moyen de l'algorithme MCTS
 *  
 *@author Amaury Pierre (296498) 
 * @author Aurélien Clergeot (302592)
 */
public final class MctsPlayer implements Player {
    private class Node{
        private TurnState mTurnState;
        private Node[] mChilds;
        private CardSet mNonExistingWires;
        private int mTotalPoints;
        private int mNbTours;
        
        public Node(TurnState turnState, Node[] childs, CardSet nonExistingWires, int totalPoints, int nbTours) {
            mTurnState = turnState;
            mChilds = childs;
            mNonExistingWires = nonExistingWires;
            mTotalPoints = totalPoints;
            mNbTours = nbTours;
        }
        
        private int bestChild(int c) {
            double[] scoresChilds = new double [mChilds.length];
            int bestIndex = 0;
            for(int i = 0; i < mChilds.length; ++i) {
                if(mChilds[i].mNbTours > 0) {
                    scoresChilds[i] = Integer.MAX_VALUE;
                }
                else {
                    scoresChilds[i] = (mChilds[i].mTotalPoints/mChilds[i].mNbTours) 
                            + c * Math.sqrt((2 * Math.log(mNbTours)) / mChilds[i].mNbTours);
                }
            }
            
            for(int i = 0; i < scoresChilds.length; ++i) {
                double maxScore = 0;
                if(scoresChilds[i] > maxScore) {
                    maxScore = scoresChilds[i];
                    bestIndex = i;
                }
            }
            return bestIndex;
        }
    }
    
    private PlayerId mOwnId;
    private long mRngSeed;
    private int mIterations;
    
    public MctsPlayer(PlayerId ownId, long rngSeed, int iterations) {
        Preconditions.checkArgument(iterations >= 9);
        mOwnId = ownId;
        mRngSeed = rngSeed;
        mIterations = iterations;
    }

    
    @Override
    public Card cardToPlay(TurnState state, CardSet hand) {
        return null;
    }
}
