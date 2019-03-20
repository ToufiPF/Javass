package ch.epfl.javass.jass;

import java.util.LinkedList;
import java.util.List;
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
    private static class Node{
        /** Constante interne à Node pour déterminer l'importance du degré d'exploration des nodes */ 
        public static final int V_CONSTANTE = 40;
        
        /// Etat du tour pour ce node
        private final TurnState mState;
        /// Joueur lié au node (=mState.nextPlayer())
        private final PlayerId mLinkedPlayer;
        
        /// 
        private final MctsPlayer mRootMctsPlayer;

        /// Parent du node, null si ce node est la racine
        private final Node mParent;
        
        /// CardSet pour les nodes enfants pas encore créés
        private final CardSet mNonExistingChildren;
        /// CardSet pour les cartes jouables par le joueur lié à ce node
        private final CardSet mPlayableCards;
        
        /// Enfants du node
        private Node[] mChildren;

        /// Score à la fin du tour simulé
        private Score mScoreEndOfTurn;
        /// Points totaux : somme des points des enfants pour la team de linkedPlayer (+ ceux du node actuel si simulé)
        private int mTotalPoints;
        /// Nombre de tours totaux : somme des tours des enfants (+1 si un tour a été simulé pour ce node)
        private int mNbTours;
        
        private Node(TurnState turnState, CardSet nonExistingChildren, Node parent, MctsPlayer rootPlayer) {
            mState = turnState;
            mLinkedPlayer = mState.nextPlayer();
            
            mRootMctsPlayer = rootPlayer;
            mParent = parent;
            
            mNonExistingChildren = nonExistingChildren;
            if (this.mLinkedPlayer == mRootMctsPlayer.mOwnId)
                mPlayableCards = mState.trick().playableCards(mNonExistingChildren);
            else
                mPlayableCards = mState.trick().playableCards(getRemainingCardsForOthers());
            
            mChildren = new Node[mPlayableCards.size()];
            
            mScoreEndOfTurn = (parent == null) ? Score.INITIAL : computeScoreEndOfTurn();
            
            mTotalPoints = computeTotalPoints();
            mNbTours = (parent == null) ? 0 : 1;
        }
        private Node(TurnState turnState, CardSet nonExistingChildren, Node parent) {
            this(turnState, nonExistingChildren, parent, parent.mRootMctsPlayer);
        }
        
        public static Node rootNode(TurnState state, CardSet nonExistingChildren, MctsPlayer rootPlayer) {
            return new Node(state, nonExistingChildren, null, rootPlayer);
        }
        
        
        /**
         * Advance the Mcts algorithm of one step
         */
        public void iterate() {
            assignChildToIndex(bestChildIndex(V_CONSTANTE));
        }
        
        /**
         * Choisit l'index du "meilleur" enfant de ce Node,
         * càd celui ayant le plus grande valeur de V (cf. Etape 6 site internet)
         * C'est l'index du tableau auquel on devra assigner un nouveau Node
         * 
         * @param c (int) constante pour l'importance du degré d'exploration des noeuds
         * (c=0 donnera l'index du Node avec la meilleure carte jouée)
         * @return (int) index dans le tableau d'enfant
         */
        public int bestChildIndex(int c) {
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
         * Assigne un Node à l'index donné avec la carte à l'index donné (dans playableCards),
         * ou, si l'index est déjà pris,
         * assigne le meilleur enfant du Node à cet index à celui-ci
         * @param index (int) l'index de la carte
         */
        private void assignChildToIndex(int index) {
            assert index >= 0;
            assert index < mChildren.length;

            ++mNbTours;
            if (mChildren[index] == null) {
                Card card = mPlayableCards.get(index);
                TurnState childState = mState.withNewCardPlayedAndTrickCollected(card);
                if (childState.isTerminal()) {
                    childState = TurnState.initial(Card.Color.ALL.get(mRootMctsPlayer.mRng.nextInt(Card.Color.COUNT)), 
                            mState.score().nextTurn(), mState.nextPlayer());
                }
                mChildren[index] = new Node(childState, mNonExistingChildren.remove(card), this);
            }
            else {
                mChildren[index].assignChildToIndex(mChildren[index].bestChildIndex(V_CONSTANTE));
            }
            mTotalPoints = computeTotalPoints();
        }
        

        /**
         * @return (List<Node>) le chemin de ce node jusqu'à la racine
         */
        private List<Node> getPathToRoot() {
            List<Node> list = new LinkedList<Node>();
            Node node = this;
            while (node.mParent != null) {
                list.add(node);
                node = node.mParent;
            }
            return list;
        }
        
        /**
         * Calcule le nb de points total d'un Node
         * (càd son score à la fin du tour + celui de ses enfants)
         * @return (int) le nombre de points de la team de linkedPlayer
         */
        private int computeTotalPoints() {
            int points = mScoreEndOfTurn.turnPoints(mLinkedPlayer.team());
            for (int i = 0 ; i < mChildren.length ; ++i)
                if (mChildren[i] != null)
                    points += mChildren[i].mScoreEndOfTurn.turnPoints(mLinkedPlayer.team());
            return points;
        }
        
        /**
         * Calcule le score final du tour en jouant de manière aléatoire :
         * le joueur correspondant au node joue des cartes au hasard dans nonExistingChildren
         * et les autres des cartes dans celles retournées par getPossibleCards()
         * @return (Score) le score à la fin du tour joué aléatoirement
         */
        private Score computeScoreEndOfTurn() {
            TurnState state = mState;

            CardSet othersCards = getRemainingCardsForOthers();
            CardSet ownCards = mNonExistingChildren;
            while (!state.isTerminal()) {
                if (state.nextPlayer() == mRootMctsPlayer.mOwnId) {
                    CardSet playable = state.trick().playableCards(ownCards);
                    Card card = playable.get(mRootMctsPlayer.mRng.nextInt(playable.size()));
                    state = state.withNewCardPlayed(card);
                    ownCards = ownCards.remove(card);
                }
                else {
                    CardSet playable = state.trick().playableCards(othersCards);
                    Card card = playable.get(mRootMctsPlayer.mRng.nextInt(playable.size()));
                    state = state.withNewCardPlayed(card);
                    othersCards = othersCards.remove(card);
                }
                
                //System.out.println(PackedTrick.toString(state.packedTrick()));
                //System.out.println("Cartes restantes : " + ownCards + ", " + othersCards);
                if (state.trick().isFull())
                    state = state.withTrickCollected();
            }
            return state.score();
        }

        /**
         * Donne les cartes possiblement possedées par les autres joueurs,
         * étant donné l'état du pli, les cartes déja jouées et celles dans la main du joueur simulé
         * @return (CardSet) les cartes que les autres joueurs pourraient possiblement posseder
         */
        private CardSet getRemainingCardsForOthers() {
            return mState.unplayedCards().difference(mNonExistingChildren);
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
        if (state.trick().playableCards(hand).size() == 1)
            return state.trick().playableCards(hand).get(0);
        
        Node baseTree = Node.rootNode(state, hand, this);
        for (int i = 0 ; i < mIterations ; ++i)
            baseTree.iterate();

        return baseTree.mPlayableCards.get(baseTree.bestChildIndex(0));
    }    
}
