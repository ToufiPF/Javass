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
        private Node[] mChildren;
        private CardSet mNonExistingChildren;
        private int mTotalPoints;
        private int mNbTours;

        private Node(TurnState turnState, CardSet nonExistingChildren) {
            mState = turnState;
            mChildren = new Node[nonExistingChildren.size()];
            mNonExistingChildren = nonExistingChildren;
            mTotalPoints = computeScoreEndOfTurn().totalPoints();
            mNbTours = 1;
        }
        
        private Node createChildWithCard(Card c) {
            return new Node(mState.withNewCardPlayedAndTrickCollected(c), mNonExistingChildren.remove(c));
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
            double[] scoresChilds = new double [mChildren.length];
            for(int i = 0; i < mChildren.length; ++i) {
                if (mChildren[i] == null || mChildren[i].mNbTours <= 0)
                    scoresChilds[i] = Double.MAX_VALUE;
                else
                    scoresChilds[i] = (mChildren[i].mTotalPoints / mChildren[i].mNbTours) 
                    + c * Math.sqrt((2 * Math.log(mNbTours)) / mChildren[i].mNbTours);
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
            assert index < mChildren.length;

            ++mNbTours;
            if (mChildren[index] == null) {
                //mChildren[index] = new Node(mState, mNonExistingChildren.remove(mState.));
            }
            else {
                System.out.println("Node.addChildToNode() - index child déjà occupé par " + mChildren[index]);
            }
        }
        
        /**
         * Donne les cartes possiblement jouables par les autres joueurs,
         * étant donné l'état du pli, les cartes déja jouées 
         * et la main du joueur du Node actuel
         * @return (CardSet) les cartes que les autres joueurs pourraient possiblement jouer
         */
        private CardSet getPossibleCardsForOthers() {
            CardSet possibleCards = mState.unplayedCards().difference(mNonExistingChildren);
            return mState.trick().playableCards(possibleCards);
        }
        
        /**
         * Calcule le score final du tour en jouant de manière aléatoire :
         * le joueur correspondant au node joue des cartes au hasard dans nonExistingChildren
         * et les autres des cartes dans celles retournées par getPossibleCards()
         * @return (Score) le score à la fin du tour joué aléatoirement
         */
        private Score computeScoreEndOfTurn() {
            TurnState state = mState;

            CardSet othersCards = getPossibleCardsForOthers();
            CardSet ownCards = mNonExistingChildren;
            while (!state.isTerminal()) {
                if (state.nextPlayer() == mOwnId) {
                    CardSet playable = state.trick().playableCards(ownCards);
                    Card card = playable.get(mRng.nextInt(playable.size()));
                    state = state.withNewCardPlayedAndTrickCollected(card);
                    ownCards = ownCards.remove(card);
                }
                else {
                    Card card = othersCards.get(mRng.nextInt(othersCards.size()));
                    state = state.withNewCardPlayedAndTrickCollected(card);
                    othersCards = othersCards.remove(card);
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
        Node baseTree = new Node(state, hand, 0);
        for (int i = 0 ; i < mIterations ; ++i) {
            
        }

        return null;
    }
    

    
}
