package ch.epfl.javass.jass;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.SplittableRandom;

import ch.epfl.javass.Preconditions;

/**
 * MctsPlayer Une classe publique et finale représentant un joueur simulé au
 * moyen de l'algorithme MCTS
 *
 * @author Amaury Pierre (296498)
 * @author Aurélien Clergeot (302592)
 */
public final class MctsPlayer implements Player {
    /**
     * Node Représente un noeud pour l'algorithme MCTS
     *
     * @author Amaury Pierre (296498)
     * @author Aurélien Clergeot (302592)
     */
    private static class Node {
        /**
         * Constante interne à Node pour déterminer l'importance du degré
         * d'exploration des nodes
         */
        public static final int V_DEGRE_EXPLORATION = 40;

        /// Etat du tour pour ce node
        private final TurnState state;

        /// CardSet pour les nodes enfants pas encore créés
        private long pkNonExistingChildren;
        /// Enfants du node
        private final Node[] children;

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
         * Ajoute le score de la team donnée aux nombre total de points et
         * incrémente le nombre de tours simulés du Node
         *
         * @param pkSc
         *            (int) packed score
         * @param team
         *            (Teamid) team à ajouter
         */
        private void addSimulatedTurnScore(long pkSc, TeamId team) {
            totalPoints += PackedScore.turnPoints(pkSc, team);
            ++nbTours;
        }

        /**
         * Choisit l'index du "meilleur" enfant de ce Node, càd celui ayant le
         * plus grande valeur de V (cf. Etape 6 site internet) C'est l'index du
         * tableau auquel on devra assigner un nouveau Node
         *
         * @param c
         *            (int) constante pour l'importance du degré d'exploration
         *            des noeuds (c=0 donnera l'index du Node avec la meilleure
         *            carte jouée)
         * @return (int) index dans le tableau d'enfant
         */
        private int bestChildIndex(int c) {
            double[] scoresChilds = new double[children.length];
            // Dans le cas où nbTours == 0, c'est qu'aucun enfant n'a encore été
            // créé
            // donc le fait que logNbTours = Double.NegativeInfinity n'est pas
            // gênant
            final double logNbTours = Math.log(nbTours);

            int bestIndex = 0;
            double maxScore = 0;

            for (int i = 0; i < children.length; ++i) {
                // Si l'enfant n'a pas été créé : on le retourne directement
                if (children[i] == null)
                    return i;
                else
                    scoresChilds[i] = (double) children[i].totalPoints
                            / children[i].nbTours
                            + c * Math
                                    .sqrt(2 * logNbTours / children[i].nbTours);

                if (scoresChilds[i] > maxScore) {
                    maxScore = scoresChilds[i];
                    bestIndex = i;
                }
            }
            return bestIndex;
        }

        /**
         * Crée un enfant à partir du Node, si possible (càd si son meilleur
         * enfant peut encore en créer un lui même). Retourne le chemin de ce
         * Node jusqu'au Node ajouté (ou le Node terminal si l'ajout n'a pas été
         * possible)
         *
         * @param handOfMcts
         *            (long) main du joueur simulé
         * @param idMcts
         *            (PlayerId) id du joueur simulé
         * @return (LinkedList<Node>) chemin de la racine jusqu'à la Node
         *         ajoutée
         */
        private LinkedList<Node> createBestChild(long handOfMcts,
                PlayerId idMcts) {
            LinkedList<Node> pathToNewNode = new LinkedList<Node>();

            Node n = this;
            int id = this.bestChildIndex(Node.V_DEGRE_EXPLORATION);
            pathToNewNode.add(this);

            while (n.children[id] != null) {
                n = n.children[id];
                id = n.bestChildIndex(Node.V_DEGRE_EXPLORATION);
                pathToNewNode.add(n);
            }
            // A partir d'ici, n.children[id] == null

            int pkCard = PackedCardSet.get(n.pkNonExistingChildren, 0);
            TurnState chState = n.state
                    .withNewCardPlayedAndTrickCollected(Card.ofPacked(pkCard));

            // Si le tour est terminé, on ne peut pas créer l'enfant
            if (chState.isTerminal())
                return pathToNewNode;

            n.pkNonExistingChildren = PackedCardSet
                    .remove(n.pkNonExistingChildren, pkCard);
            long cardsetChild = chState.nextPlayer() == idMcts
                    ? PackedTrick.playableCards(chState.packedTrick(),
                            unplayedCardsInHand(chState, handOfMcts))
                    : PackedTrick.playableCards(chState.packedTrick(),
                            unplayedCardsForOther(chState, handOfMcts));

            n.children[id] = new Node(chState, cardsetChild);
            pathToNewNode.add(n.children[id]);

            return pathToNewNode;
        }
    }

    private static long unplayedCardsForOther(TurnState state, long hand) {
        return PackedCardSet.difference(state.packedUnplayedCards(), hand);
    }

    private static long unplayedCardsInHand(TurnState state, long hand) {
        return PackedCardSet.intersection(state.packedUnplayedCards(), hand);
    }

    private final PlayerId mOwnId;

    private final int mIterations;

    private final SplittableRandom mRng;

    public MctsPlayer(PlayerId ownId, long rngSeed, int iterations) {
        Preconditions.checkArgument(iterations >= Jass.HAND_SIZE);
        mOwnId = ownId;
        mRng = new SplittableRandom(rngSeed);
        mIterations = iterations;
    }

    @Override
    public Card cardToPlay(TurnState state, CardSet hand) {
        CardSet playable = state.trick().playableCards(hand);
        // Si on a qu'une carte jouable, pas besoin de réfléchir
        if (playable.size() == 1)
            return playable.get(0);

        Node root = new Node(state, playable.packed());
        for (int i = 0; i < mIterations; ++i) {
            // On crée un enfant à chaque itération
            LinkedList<Node> path = root.createBestChild(hand.packed(), mOwnId);

            // On calcule le score pour un tour aléatoire à partir du TurnState
            // de l'enfant créé
            long sc = computeEndOfTurnScore(path.getLast().state,
                    hand.packed());

            // On ajoute un score à la racine (celui de la team adverse faute de
            // mieux)
            Iterator<Node> it = path.iterator();
            Node parent = it.next();
            parent.addSimulatedTurnScore(sc, mOwnId.team().other());

            // On ajoute ensuite les scores aux enfants
            while (it.hasNext()) {
                Node child = it.next();
                child.addSimulatedTurnScore(sc,
                        parent.state.nextPlayer().team());
                parent = child;
            }
        }

        return playable.get(root.bestChildIndex(0));
    }

    private long computeEndOfTurnScore(TurnState state, long handOfMctsplayer) {
        long mctsCards = unplayedCardsInHand(state, handOfMctsplayer);
        long otherCards = unplayedCardsForOther(state, handOfMctsplayer);

        while (!state.isTerminal()) {
            long playable = state.nextPlayer() == mOwnId
                    ? PackedTrick.playableCards(state.packedTrick(), mctsCards)
                    : PackedTrick.playableCards(state.packedTrick(),
                            otherCards);

            final int pkCard = PackedCardSet.get(playable,
                    mRng.nextInt(PackedCardSet.size(playable)));
            state = state
                    .withNewCardPlayedAndTrickCollected(Card.ofPacked(pkCard));

            mctsCards = PackedCardSet.remove(mctsCards, pkCard);
            otherCards = PackedCardSet.remove(otherCards, pkCard);
        }
        return state.packedScore();
    }
}
