package ch.epfl.javass.jass;

import java.util.LinkedList;
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

        /// CardSet pour les nodes enfants pas encore créés
        private long pkNonExistingChildren;
        /// Enfants du node
        private Node[] children;

        private int totalPoints;
        private int nbTours;

        private Node(TurnState turnState, long pkNonExistingChildren) {
            this.state = turnState;

            this.pkNonExistingChildren = pkNonExistingChildren;
            this.children = new Node[PackedCardSet.size(pkNonExistingChildren)];

            this.totalPoints = 0; 
            this.nbTours = 0;
        }
        
        /**
         * Ajoute le score de la team donnée aux nombre total de points
         * @param pkSc (int) packed score
         * @param team (Teamid) team à ajouter
         */
        private void addScore(long pkSc, TeamId team) {
            totalPoints += PackedScore.turnPoints(pkSc, team);
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
        private int bestChildIndex(int c) {
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

        @Override
        public String toString() {
            StringBuilder build = new StringBuilder();
            build.append("Nb d'enfants:").append(children.length).append(", ");
            build.append("Points:").append(this.totalPoints).append(", ");
            build.append("NbTours:").append(this.nbTours).append(", ");
            build.append("PtsMoyens:").append((double) this.totalPoints / this.nbTours);

            return build.toString();
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

        Node root = new Node(state, playable.packed());
        for (int i = 0 ; i < mIterations ; ++i) {
            LinkedList<Node> path = createChild(root, hand.packed());
            long sc = computeEndOfTurnScore(path.getLast().state, hand.packed());
            Node parent = null;
            for (Node n : path) {
                n.addScore(sc, parent == null ? mOwnId.team().other() : parent.state.nextPlayer().team());
                parent = n;
            }
        }
        
        System.out.println(root);
        for (Node c : root.children)
            System.out.println(" - " + c);
        
        return playable.get(root.bestChildIndex(0));
    }
    /**
     * Crée un enfant à partir de la racine donnée, si possible 
     * (càd si son meilleur enfant peut encore en créer un)
     * Retourne le chemin de la racine à le Node ajouté
     * (ou le Node terminale si l'ajout n'a pas été possible)
     * 
     * @param root (Node) racine
     * @param handOfMcts (CardSet) main du joueur simulé
     * @return (List<Node>) chemin de la racine jusqu'à la Node ajoutée
     */
    private LinkedList<Node> createChild(Node root, long handOfMcts) {
        LinkedList<Node> pathToNewNode = new LinkedList<Node>();
        pathToNewNode.add(root);
        Node n = root;
        int id = root.bestChildIndex(Node.V_DEGRE_EXPLORATION);
        
        while (n.children[id] != null) { 
            Preconditions.checkIndex(id, n.children.length);

            ++n.nbTours;
            n = n.children[id];
            id = n.bestChildIndex(Node.V_DEGRE_EXPLORATION);
            
            pathToNewNode.add(n);
        }
        
        //Ici, n.children[id] == null
        int pkCard = PackedCardSet.get(n.pkNonExistingChildren, 0);
        TurnState chState = n.state.withNewCardPlayedAndTrickCollected(Card.ofPacked(pkCard));
        if (chState.isTerminal()) {
            return pathToNewNode;
        }
        n.pkNonExistingChildren = PackedCardSet.remove(n.pkNonExistingChildren, pkCard);
        long cardsetChild = chState.nextPlayer() == mOwnId ? 
                PackedTrick.playableCards(chState.packedTrick(), unplayedCardsInHand(chState, handOfMcts))
                : PackedTrick.playableCards(chState.packedTrick(), unplayedCardsForOther(chState, handOfMcts));

        n.children[id] = new Node(chState, cardsetChild);
        pathToNewNode.add(n.children[id]);

        return pathToNewNode;
    }


    private long computeEndOfTurnScore(TurnState state, long handOfMctsplayer) {
        long mctsCards = unplayedCardsInHand(state, handOfMctsplayer);
        long otherCards = unplayedCardsForOther(state, handOfMctsplayer);

        while (!state.isTerminal()) {
            long playable = state.nextPlayer() == mOwnId ? PackedTrick.playableCards(state.packedTrick(), mctsCards)
                    : PackedTrick.playableCards(state.packedTrick(), otherCards);

            final int pkCard = PackedCardSet.get(playable, mRng.nextInt(PackedCardSet.size(playable)));
            state = state.withNewCardPlayedAndTrickCollected(Card.ofPacked(pkCard));

            mctsCards = PackedCardSet.remove(mctsCards, pkCard);
            otherCards = PackedCardSet.remove(otherCards, pkCard);
        }
        return state.packedScore();
    }

    private static long unplayedCardsInHand(TurnState state, long hand) {
        return PackedCardSet.intersection(state.packedUnplayedCards(), hand);
    }
    private static long unplayedCardsForOther(TurnState state, long handOfMcts) {
        return PackedCardSet.difference(state.packedUnplayedCards(), handOfMcts);
    }
}
