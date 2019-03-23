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
        public static final int V_DEGRE_EXPLORATION = 40;
        
        /// Etat du tour pour ce node
        private final TurnState state;
        private final Node parent;

        /// CardSet pour les nodes enfants pas encore créés
        private CardSet nonExistingChildren;
        /// Enfants du node
        private Node[] children;

        private int totalPoints;
        private int nbTours;

        private Node(TurnState turnState, CardSet nonExistingChildren, Node parent) {
            this.state = turnState;
            this.parent = parent;

            this.nonExistingChildren = nonExistingChildren;
            this.children = new Node[nonExistingChildren.size()];

            this.totalPoints = 0;
            this.nbTours = (parent == null) ? 0 : 1;
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
            double[] scoresChilds = new double [children.length];
            for(int i = 0; i < children.length; ++i) {
                if (children[i] == null || children[i].nbTours <= 0)
                    scoresChilds[i] = Double.MAX_VALUE;
                else
                    scoresChilds[i] = (children[i].totalPoints / children[i].nbTours) 
                    + c * Math.sqrt((2 * Math.log(nbTours)) / children[i].nbTours);
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
        CardSet playable = state.trick().playableCards(hand);
        if (playable.size() == 1)
            return playable.get(0);

        Node root = new Node(state, playable, null);
        for (int i = 0 ; i < mIterations ; ++i) {
            createChildForNode(root, hand);
        }
        return playable.get(root.bestChildIndex(0));
    }
    
    private void createChildForNode(Node p, CardSet handOfMcts) {
        int index = p.bestChildIndex(Node.V_CONSTANTE);
        Preconditions.checkIndex(index, p.children.length);
        
        ++p.nbTours;
        if (p.children[index] == null) {
            Card card = p.nonExistingChildren.get(0);
            TurnState chState = p.state.withNewCardPlayedAndTrickCollected(card);
            if (chState.isTerminal()) {
                return;
            }
            p.nonExistingChildren = p.nonExistingChildren.remove(card);
            CardSet cardsetChild = chState.nextPlayer() == mOwnId ? 
                    chState.trick().playableCards(unplayedCardsInHand(chState, handOfMcts))
                            : chState.trick().playableCards(unplayedCardsForOther(chState, handOfMcts));
            
            p.children[index] = new Node(chState, cardsetChild, p);
            Score sc = computeEndOfTurnScore(chState, handOfMcts);
            
            List<Node> path = getPathToRootFrom(p.children[index]);
            for (Node n : path)
                addScoreToNode(n, sc);
        }
        else {
            createChildForNode(p.children[index], handOfMcts);
        }
    }
    
    private void addScoreToNode(Node n, Score sc) {
        if (n.parent == null)
            n.totalPoints += sc.turnPoints(mOwnId.team().other());
        else
            n.totalPoints += sc.turnPoints(n.parent.state.nextPlayer().team());
    }
    
    private Score computeEndOfTurnScore(TurnState state, CardSet handOfMctsplayer) {
        CardSet mctsCards = unplayedCardsInHand(state, handOfMctsplayer);
        CardSet otherCards = unplayedCardsForOther(state, handOfMctsplayer);

        while (!state.isTerminal()) {
            CardSet playable = state.nextPlayer() == mOwnId ? state.trick().playableCards(mctsCards)
                    : state.trick().playableCards(otherCards);
            
            final Card card = playable.get(mRng.nextInt(playable.size()));
            state = state.withNewCardPlayedAndTrickCollected(card);
            
            mctsCards = mctsCards.remove(card);
            otherCards = otherCards.remove(card);
        }
        return state.score();
    }

    private static List<Node> getPathToRootFrom(Node n) {
        List<Node> path = new LinkedList<Node>();
        path.add(n);
        
        while (n.parent != null) {
            n = n.parent;
            path.add(n);
        }
        return path;
    }
    
    private static CardSet unplayedCardsInHand(TurnState state, CardSet hand) {
        return state.unplayedCards().intersection(hand);
    }
    private static CardSet unplayedCardsForOther(TurnState state, CardSet handOfMcts) {
        return state.unplayedCards().difference(handOfMcts);
    }
}
