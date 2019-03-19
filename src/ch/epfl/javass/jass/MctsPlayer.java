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
        
        public static final int V_CONSTANTE = 40;
        
        /// Etat du tour pour ce node
        private final TurnState mState;
        /// Joueur lié au node
        private final PlayerId mLinkedPlayer;

        /// Racine du node
        private final Node mRoot;
        /// Parent du node
        private final Node mParent;
        
        /// CardSet pour les nodes enfants pas encore créés
        private final CardSet mNonExistingChildren;
        /// CardSet pour les cartes jouables par le joueur lié à ce node
        private final CardSet mPlayableCards;
        
        /// Enfants du node
        private Node[] mChildren;
        
        /// Points totaux : somme des points des enfants (+ ceux du node actuel si simulé)
        private int mTotalPoints;
        /// Nombre de tours totaux : somme des tours des enfants (+1 si un tour a été simulé pour ce node)
        private int mNbTours;

        public Node(TurnState turnState, CardSet nonExistingChildren, Node parent) {
            mState = turnState;
            mLinkedPlayer = mState.nextPlayer();

            mRoot = computeRoot();
            mParent = parent;
            
            mNonExistingChildren = nonExistingChildren;
            if (this.mLinkedPlayer == mRoot.mLinkedPlayer)
                mPlayableCards = mState.trick().playableCards(mNonExistingChildren);
            else
                mPlayableCards = mState.trick().playableCards(getPossibleCardsForOthers());
            
            mChildren = new Node[mPlayableCards.size()];
            // pas bon :
            //mTotalPoints = computeScoreEndOfTurn().totalPoints(mLinkedPlayer.team());
            mNbTours = 1;
        }
        
        /**
         * @return (Node) la racine de l'arbre de nodes 
         */
        private Node computeRoot() {
            Node node = this;
            while (node.mParent != null)
                node = node.mParent;
            return node;  
        }
        
        /**
         * @return (List<Node>) le chemin de ce node jusqu'à la racine
         */
        public List<Node> getPathToRoot() {
            List<Node> list = new LinkedList<Node>();
            Node node = this;
            while (node.mParent != null) {
                list.add(node);
                node = node.mParent;
            }
            return list;
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
                mChildren[index] = createChildNodeWithCard(mPlayableCards.get(index));
            }
            else {
                mChildren[index].assignChildToIndex(mChildren[index].bestChildIndex(V_CONSTANTE));
            }
        }
        
        /**
         * Crée un enfant avec la carte donnée
         * @param card
         * @return
         */
        private Node createChildNodeWithCard(Card card) {
            /// /!\ TurnState peut etre invalide ???
            // si le joueur n'est pas le joueur original, card n'est pas sensé être dans nonExistingChildren donc osef
            return new Node(mState.withNewCardPlayedAndTrickCollected(card), mNonExistingChildren.remove(card), this);
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
                if (state.nextPlayer() == getRoot().mLinkedPlayer) {
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
