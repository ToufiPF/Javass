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

        private Node(TurnState turnState, CardSet nonExistingChilds, int totalPoints) {
            mState = turnState;
            mChilds = new Node[nonExistingChilds.size()];
            mNonExistingChilds = nonExistingChilds;
            mTotalPoints = 0;
            mNbTours = 1;
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

            ++mNbTours;
            if (mChilds[index] == null) {
                mChilds[index] = new Node(mState, mNonExistingChilds.remove(mState.));
            }
            else {
                System.out.println("Node.addChildToNode() - index child déjà occupé par " + mChilds[index]);
            }
        }
        
        /**
         * Donne les cartes possiblement jouables,
         * étant donné l'état du pli, les cartes déja jouées 
         * et la main du joueur du Node actuel
         * @return (CardSet) les cartes que les autres joueurs pourraient possiblement jouer
         */
        private CardSet getPossibleCards() {
            CardSet possibleCards = CardSet.ALL_CARDS.difference(mNonExistingChilds).difference(mState.unplayedCards());
            return mState.trick().playableCards(possibleCards);
        }

        private Score computeScoreEndOfTurnWhenPlaying(Card c) {
            TurnState state = mState;
            if (state.trick().isFull())
                state.withTrickCollected();
            state = state.withNewCardPlayedAndTrickCollected(c);

            CardSet possibleCards = getPossibleCards();
            CardSet ownCards = mNonExistingChilds;
            while (!state.isTerminal()) {
                if (mState.nextPlayer() == mOwnId) {
                    CardSet playable = state.trick().playableCards(ownCards);
                    Card card = playable.get(mRng.nextInt(playable.size()));
                    state = state.withNewCardPlayedAndTrickCollected(card);
                    ownCards.remove(c);
                }
                else {
                    Card card = possibleCards.get(mRng.nextInt(possibleCards.size()));
                    state = state.withNewCardPlayedAndTrickCollected(card);
                    possibleCards.remove(card);
                }
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
