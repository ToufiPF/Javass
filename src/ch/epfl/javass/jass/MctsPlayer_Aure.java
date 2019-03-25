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
public final class MctsPlayer_Aure implements Player {

    /**
     * Node
     * Représente un noeud pour l'algorithme MCTS
     *  
     * @author Amaury Pierre (296498) 
     * @author Aurélien Clergeot (302592)
     */
    private static class Node{
        /** Constante interne à Node pour déterminer l'importance du degré d'exploration des nodes */ 
        public static final int V_DEGRE_EXPLORATION = 40;
        
        /// Etat du tour pour ce node
        private final TurnState mState;
        /// Joueur lié au node (=mState.nextPlayer())
        private final PlayerId mNextPlayer;
        
        /// Joueur MCTS à la base
        private final MctsPlayer_Aure mRootMctsPlayer;

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
        
        /// Points totaux : somme des points des enfants pour la team de mRootPlayer (+ ceux du node actuel si simulé)
        private int mTotalPoints;
        /// Nombre de tours totaux : somme des tours des enfants (+1 si un tour a été simulé pour ce node)
        private int mNbTours;
        
        private Node(TurnState turnState, CardSet nonExistingChildren, Node parent, MctsPlayer_Aure rootPlayer) {
            mState = turnState;
            mNextPlayer = mState.nextPlayer();
            
            mRootMctsPlayer = rootPlayer;
            mParent = parent;
            
            mNonExistingChildren = nonExistingChildren;
            // Si le joueur du Node est celui simulé
            // les cartes jouables sont extraites de sa main
            if (this.mNextPlayer == mRootMctsPlayer.mOwnId)
                mPlayableCards = mState.trick().playableCards(mNonExistingChildren);
            // sinon, les cartes jouables sont déduites des cartes non jouées
            else
                mPlayableCards = mState.trick().playableCards(getRemainingCardsForOthers());
            
            // Un enfant par carte jouable
            mChildren = new Node[mPlayableCards.size()];
            
            // On simule le tour si le node n'est pas racine
            mScoreEndOfTurn = (parent == null) ? Score.INITIAL : computeScoreEndOfTurn();
            mNbTours = (parent == null) ? 0 : 1;
            
            // On recalcule le nombre de points total, pour tous les nodes parents
            mTotalPoints = 0;
            for (Node n : getPathToRootFrom(this))
                n.addScoreToTotalPoints(mScoreEndOfTurn);
        }
        private Node(TurnState turnState, CardSet nonExistingChildren, Node parent) {
            this(turnState, nonExistingChildren, parent, parent.mRootMctsPlayer);
        }
        
        public static Node rootNode(TurnState state, CardSet nonExistingChildren, MctsPlayer_Aure rootPlayer) {
            return new Node(state, nonExistingChildren, null, rootPlayer);
        }

        /**
         * @return (List<Node>) le chemin de nodes 
         * partant du node donné (inclus), allant jusqu'à la racine (incluse)
         */
        private static List<Node> getPathToRootFrom(Node n) {
            List<Node> list = new LinkedList<Node>();
            Node node = n;
            list.add(node);
            while (node.mParent != null) {
                node = node.mParent;
                list.add(node);
            }
            return list;
        }
        
        /**
         * Advance the Mcts algorithm of one step
         */
        public void iterate() {
            assignChildToIndex(bestChildIndex(V_DEGRE_EXPLORATION));
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
                    //TODO: quoi faire ici ?
                    return;
                }
                mChildren[index] = new Node(childState, mNonExistingChildren.remove(card), this);
            }
            else {
                mChildren[index].assignChildToIndex(mChildren[index].bestChildIndex(V_DEGRE_EXPLORATION));
            }
        }
        
        /**
         * Calcule le nb de points total d'un Node
         * (càd son score à la fin du tour + celui de ses enfants)
         * @return (int) le nombre de points de la team de linkedPlayer
         */
        /*private void recomputeTotalPoints() {
            mTotalPoints = mScoreEndOfTurn.turnPoints(mRootMctsPlayer.mOwnId.team());
            for (Node child : mChildren) {
                if (child != null)
                    mTotalPoints += child.mTotalPoints;
            }
        }*/
        
        private void addScoreToTotalPoints(Score score) {
            if (mParent != null)
                mTotalPoints += score.turnPoints(mParent.mNextPlayer.team());
            else
                mTotalPoints += score.turnPoints(mNextPlayer.team());
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
                
                if (PackedTrick.isFull(state.packedTrick()))
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
        
        
        @Override
        public String toString() {
            StringBuilder build = new StringBuilder();
            build.append("Pronfondeur=").append(getPathToRootFrom(this).size() - 1).append(", ");
            build.append("Nb d'enfants:").append(mChildren.length).append(", ");
            build.append("Points:").append(this.mTotalPoints).append(", ");
            build.append("NbTours:").append(this.mNbTours).append(", ");
            build.append("PtsMoyens:").append((double) this.mTotalPoints / this.mNbTours);
            
            return build.toString();
        }
    }

    private final PlayerId mOwnId;
    private final int mIterations;
    private final SplittableRandom mRng;

    public MctsPlayer_Aure(PlayerId ownId, long rngSeed, int iterations) {
        Preconditions.checkArgument(iterations >= 9);
        mOwnId = ownId;
        mRng = new SplittableRandom(rngSeed);
        mIterations = iterations;
    }

    @Override
    public Card cardToPlay(TurnState state, CardSet hand) {
        // Si il n'y a qu'une carte jouable : pas besoin de réfléchir
        if (state.trick().playableCards(hand).size() == 1)
            return state.trick().playableCards(hand).get(0);

        Node baseTree = Node.rootNode(state, hand, this);
        
        for (int i = 0 ; i < mIterations ; ++i)
            baseTree.iterate();
        
        
        return baseTree.mPlayableCards.get(baseTree.bestChildIndex(0));
    }    
}
