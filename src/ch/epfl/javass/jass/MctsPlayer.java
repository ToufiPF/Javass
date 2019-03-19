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
        /// Joueur lié au node
        private final PlayerId mLinkedPlayer;

        /// Racine du node, this si ce node est la racine
        private final Node mRoot;
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

        public Node(TurnState turnState, CardSet nonExistingChildren, Node parent) {
            mState = turnState;
            mLinkedPlayer = mState.nextPlayer();
            
            if (parent == null) {
                mRoot = this;
            }
            else {
                mRoot = parent.mRoot;
            }
            mParent = parent;
            
            mNonExistingChildren = nonExistingChildren;
            if (this.mLinkedPlayer == mRoot.mLinkedPlayer)
                mPlayableCards = mState.trick().playableCards(mNonExistingChildren);
            else
                mPlayableCards = mState.trick().playableCards(getPossibleCardsForOthers());
            
            mChildren = new Node[mPlayableCards.size()];
            mTotalPoints = computeTotalPoints();
            mNbTours = computeNbTours();
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
                /// /!\ TurnState peut etre invalide ???
                // si le joueur n'est pas le joueur original, card n'est pas sensé être dans nonExistingChildren donc osef
                Card card = mPlayableCards.get(index);
                mChildren[index] = new Node(mState.withNewCardPlayedAndTrickCollected(card), mNonExistingChildren.remove(card), this);
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
         * Calcule le nb de tours d'un Node
         * (càd celui de ses enfants + 1 si il n'est pas racine)
         * @return (int) le nombre de points de la team de linkedPlayer
         */
        private int computeNbTours() {
            int nbTours = this == mRoot ? 0 : 1;
            for (int i = 0 ; i < mChildren.length ; ++i)
                if (mChildren[i] != null)
                    nbTours += mChildren[i].mNbTours;
            return nbTours;
        }
        
        /**
         * Calcule le score final du tour en jouant de manière aléatoire :
         * le joueur correspondant au node joue des cartes au hasard dans nonExistingChildren
         * et les autres des cartes dans celles retournées par getPossibleCards()
         * @return (Score) le score à la fin du tour joué aléatoirement
         */
        private Score computeScoreEndOfTurn(SplittableRandom rng) {
            TurnState state = mState;

            CardSet othersCards = getPlayableCardsForOthers();
            CardSet ownCards = mNonExistingChildren;
            while (!state.isTerminal()) {
                if (state.nextPlayer() == mRoot.mLinkedPlayer) {
                    CardSet playable = state.trick().playableCards(ownCards);
                    Card card = playable.get(rng.nextInt(playable.size()));
                    state = state.withNewCardPlayedAndTrickCollected(card);
                    ownCards = ownCards.remove(card);
                }
                else {
                    Card card = othersCards.get(rng.nextInt(othersCards.size()));
                    state = state.withNewCardPlayedAndTrickCollected(card);
                    othersCards = othersCards.remove(card);
                }
            }
            return state.score();
        }

        /**
         * Donne les cartes jouables parmi celles étant 
         * possiblement possedées par les autres joueurs,
         * étant donné l'état du pli, les cartes déja jouées
         * @return (CardSet) les cartes que les autres joueurs pourraient possiblement jouer
         */
        private CardSet getPlayableCardsForOthers() {
            CardSet possibleCards = mState.unplayedCards().difference(mNonExistingChildren);
            return mState.trick().playableCards(possibleCards);
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
            return hand.get(0);
        
        Node baseTree = new Node(state, hand, null);        
        for (int i = 0 ; i < mIterations ; ++i)
            baseTree.iterate();

        return baseTree.mPlayableCards.get(baseTree.bestChildIndex(0));
    }    
}
