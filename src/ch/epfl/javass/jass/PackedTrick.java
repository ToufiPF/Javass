package ch.epfl.javass.jass;

import java.util.StringJoiner;

import ch.epfl.javass.bits.Bits32;

/**
 * PackedTrick Une classe non instanciable contenant des méthodes statiques pour
 * manipuler des plis empaquetés sous la forme d'un int
 *
 * @author Amaury Pierre (296498)
 * @author Aurélien Clergeot (302592)
 */
public final class PackedTrick {
    /**
     * Représente un pli empaqueté invalide (ce n'est pas le seul pli invalide
     * possible)
     */
    public static final int INVALID = 0xFFFF_FFFF; // -1, tous les bits à 1
    private static final int MAX_VALID_INDEX_TRICK = Jass.TRICKS_PER_TURN - 1;

    private static final int CARD_SIZE = 6;
    
    private static final int INDEX_START = 24;
    private static final int INDEX_SIZE = 4;
    
    private static final int PLAYER1_START = 28;
    private static final int PLAYER1_SIZE = 2;
    
    private static final int TRUMP_START = 30;
    private static final int TRUMP_SIZE = 2;
    /**
     * Donne la couleur de la première carte du pli
     * 
     * @param pkTrick
     *            (int) le pli, supposé non vide
     * @return (Card.Color) la couleur de la 1ere carte
     */
    public static Card.Color baseColor(int pkTrick) {
        assert isValid(pkTrick);
        assert !isEmpty(pkTrick);
        return PackedCard.color(card(pkTrick, 0));
    }

    private static int bestCard(int pkTrick) {
        return card(pkTrick, bestCardIndex(pkTrick));
    }

    /**
     * Donne l'index de la meilleur carte du pli
     * 
     * @param pkTrick
     *            (int) le pli
     * @return (int) l'index dans le pli de la meilleure carte
     */
    private static int bestCardIndex(int pkTrick) {
        assert !isEmpty(pkTrick);

        final int sizeTrick = size(pkTrick);
        final Card.Color trump = trump(pkTrick);

        int bestCard = card(pkTrick, 0);
        int bestCardID = 0;
        for (int i = 1; i < sizeTrick; ++i) {
            int card_i = card(pkTrick, i);
            if (PackedCard.isBetter(trump, card_i, bestCard)) {
                bestCard = card_i;
                bestCardID = i;
            }
        }
        return bestCardID;
    }

    /**
     * Donne la carte (en version empaquetée) à l'index donné
     * 
     * @param pkTrick
     *            (int) le pli
     * @param indexCard
     *            (int) l'index de la carte
     * @return (int) la carte à l'index donné
     */
    public static int card(int pkTrick, int indexCard) {
        assert isValid(pkTrick);
        assert indexCard >= 0;
        assert indexCard < 4;
        return Bits32.extract(pkTrick, indexCard * CARD_SIZE, CARD_SIZE);
    }

    /**
     * Donne le pli empaqueté vide (le premier du tour) avec l'atout et le 1er
     * joueur donnés
     * 
     * @param trump
     *            (Card.Color) couleur de l'atout
     * @param firstPlayer
     *            (PlayerId) 1er joueur
     * @return (int) le pli empaqueté vide
     */
    public static int firstEmpty(Card.Color trump, PlayerId firstPlayer) {
        return packEmptyTrick(0, firstPlayer, trump);
    }

    /**
     * Donne l'index du pli, càd le numero du pli dans le tour
     * 
     * @param pkTrick
     *            (int) le pli en question
     * @return (int) l'index du pli
     */
    public static int index(int pkTrick) {
        assert isValid(pkTrick);
        return Bits32.extract(pkTrick, INDEX_START, INDEX_SIZE);
    }

    /**
     * Vérifie si le pli donné est vide, càd qu'aucune carte n'est encore été
     * jouée
     * 
     * @param pkTrick
     *            (int) le pli à vérifier, supposé valide
     * @return (boolean) true ssi le pli est vide
     */
    public static boolean isEmpty(int pkTrick) {
        assert isValid(pkTrick);
        // La 1ere carte est invalide -> le pli est vide
        return !PackedCard.isValid(card(pkTrick, 0));
    }

    /**
     * Vérifie si le pli donné est plein, càd que chaque joueur a joué une carte
     * 
     * @param pkTrick
     *            (int) le pli à vérifier, supposé valide
     * @return (boolean) true ssi le pli est plein
     */
    public static boolean isFull(int pkTrick) {
        assert isValid(pkTrick);
        // La 4e carte est valide -> le pli est plein
        return PackedCard.isValid(card(pkTrick, 3));
    }

    /**
     * Vérifie si le pli donné est le dernier du tour (si son index vaut 8)
     * 
     * @param pkTrick
     *            (int) le pli à vérifier
     * @return (boolean) true ssi pkTrick est le dernier pli
     */
    public static boolean isLast(int pkTrick) {
        assert isValid(pkTrick);
        return index(pkTrick) == MAX_VALID_INDEX_TRICK;
    }

    /**
     * Vérifie si le pli empaqueté donné est valide, càd que : son index soit
     * compris entre 0 et 8 inclus, si une carte invalide est trouvée, toutes
     * les cartes suivantes sont également invalides
     * 
     * @param pkTrick
     *            (int) le pli empaqueté à valider
     * @return (boolean) true ssi le pli est valide
     */
    public static boolean isValid(int pkTrick) {
        int index = Bits32.extract(pkTrick, INDEX_START, INDEX_SIZE);
        if (index < 0 || index > MAX_VALID_INDEX_TRICK)
            return false;

        // Pour chaque carte, de 0 à 3 :
        // si la carte est invalide, les prochaines doivent
        // l'être aussi, sinon le pli est invalide
        for (int i = 0; i < PlayerId.COUNT; ++i) {
            if (!PackedCard.isValid(Bits32.extract(pkTrick, i * CARD_SIZE, CARD_SIZE))) {
                for (int j = i + 1; j < PlayerId.COUNT; ++j) {
                    if (PackedCard.isValid(Bits32.extract(pkTrick, j * CARD_SIZE, CARD_SIZE)))
                        return false;
                }
                return true;
            }
        }
        // Toutes les cartes sont valides
        // Le pli l'est aussi
        return true;
    }

    /**
     * Donne le pli empaqueté vide suivant celui donné, càd avec l'index
     * suivant, le joueur gagnant dans pkTrick commençant le prochain, et la
     * même couleur d'atout Si le pli donné est le dernier, retourne INVALID
     * 
     * @param pkTrick
     *            (int) l'ancien pli, supposé plein
     * @return (int) le pli suivant, vide (ou INVALID si pkTrick est le dernier
     *         pli)
     */
    public static int nextEmpty(int pkTrick) {
        assert isValid(pkTrick);

        if (isLast(pkTrick))
            return INVALID;

        return packEmptyTrick(index(pkTrick) + 1, winningPlayer(pkTrick),
                trump(pkTrick));
    }

    /**
     * Pack un pli vide selon les arguments donnés
     * 
     * @param indexTrick
     *            (int) l'index du pli
     * @param player
     *            (PlayerId) le joueur commençant le pli
     * @param trump
     *            (Card.Color) la couleur des atouts
     * @return (int) un pli vide avec les arguments donnés
     */
    private static int packEmptyTrick(int indexTrick, PlayerId player,
            Card.Color trump) {
        return packTrick(PackedCard.INVALID, PackedCard.INVALID,
                PackedCard.INVALID, PackedCard.INVALID, indexTrick, player,
                trump);
    }

    /**
     * Pack un pli avec les arguments donnés
     * 
     * @param card0
     *            (int) la carte 0
     * @param card1
     *            (int) la carte 1
     * @param card2
     *            (int) la carte 2
     * @param card3
     *            (int) la carte 3
     * @param indexTrick
     *            (int) l'index du pli
     * @param player
     *            (PlayerId) le joueur commençant le pli
     * @param trump
     *            (Card.Color) la couleur des atouts
     * @return (int) un pli avec les arguments donnés
     */
    private static int packTrick(int card0, int card1, int card2, int card3,
            int indexTrick, PlayerId player, Card.Color trump) {
        return Bits32.pack(card0, CARD_SIZE, card1, CARD_SIZE, card2, CARD_SIZE, card3, CARD_SIZE, indexTrick,
                INDEX_SIZE, player.ordinal(), PLAYER1_SIZE, trump.ordinal(), TRUMP_SIZE);
    }

    /**
     * Donne l'ensemble de cartes jouables dans ce pli parmi les cartes données
     * 
     * @param pkTrick
     *            (int) le pli, supposé non plein
     * @param pkHand
     *            (long) la version empaquetée de l'ensemble de carte
     * @return (long) l'ensemble de cartes jouables dans pkHand
     */
    public static long playableCards(int pkTrick, long pkHand) {
        assert isValid(pkTrick);
        assert !isFull(pkTrick);
        assert PackedCardSet.isValid(pkHand);

        // Si le pli est vide, on peut tout jouer au choix
        if (isEmpty(pkTrick))
            return pkHand;

        // On a fait le choix de ne pas traiter séparément
        // le cas où couleurBase == couleurTrump
        // ce n'est pas un problème, car les atouts qu'on aurait ignoré
        // dans playableTrumps sont alors dans playableCards
        final Card.Color bc = baseColor(pkTrick);
        final Card.Color tc = trump(pkTrick);

        final long SINGLETON_BOUR = PackedCardSet
                .singleton(PackedCard.pack(tc, Card.Rank.JACK));

        // On peut jouer toutes les cartes de la couleur de base
        long playableCards = PackedCardSet.subsetOfColor(pkHand, bc);
        // Si le joueur n'en a pas (ou si il ne peut jouer que le Bour), il peut
        // jouer toutes les cartes non-atout qu'il veut
        if (PackedCardSet.isEmpty(playableCards)
                || playableCards == SINGLETON_BOUR) {
            for (Card.Color c : Card.Color.ALL)
                if (!c.equals(tc))
                    playableCards |= PackedCardSet.subsetOfColor(pkHand, c);
        }

        // Et des atouts :
        long playableTrumps = PackedCardSet.EMPTY;
        final int bestCard = bestCard(pkTrick);
        // Si il y a déjà un atout sur dans le pli, on peut jouer seulement un
        // meilleur
        if (PackedCard.color(bestCard) == tc)
            playableTrumps = PackedCardSet.intersection(
                    PackedCardSet.subsetOfColor(pkHand, tc),
                    PackedCardSet.trumpAbove(bestCard));
        // Sinon, on peut jouer tous les atouts
        else
            playableTrumps = PackedCardSet.subsetOfColor(pkHand, tc);

        final long totalPlayable = PackedCardSet.union(playableCards,
                playableTrumps);
        if (PackedCardSet.isEmpty(totalPlayable))
            return pkHand;

        return totalPlayable;
    }

    /**
     * Donne le joueur à l'index dans le pli donné, càd en comptant depuis le
     * premier joueur
     * 
     * @param pkTrick
     *            (int) le pli en question
     * @param index
     *            (int) l'index du joueur dans le pli
     * @return (PlayerId) le joueur à l'index donné
     */
    public static PlayerId player(int pkTrick, int index) {
        assert isValid(pkTrick);
        assert index >= 0;
        assert index < PlayerId.COUNT;
        int firstPlayer = Bits32.extract(pkTrick, PLAYER1_START, PLAYER1_SIZE);
        return PlayerId.ALL.get((firstPlayer + index) % PlayerId.COUNT);
    }

    /**
     * Donne le nombre de points du pli, en comptant les 5 points bonus si le
     * pli est le dernier
     * 
     * @param pkTrick
     *            (int) le pli en question
     * @return (int) la valeur en points du pli
     */
    public static int points(int pkTrick) {
        assert isValid(pkTrick);

        final Card.Color trump = trump(pkTrick);

        int nbPoints = isLast(pkTrick) ? Jass.LAST_TRICK_ADDITIONAL_POINTS : 0;
        for (int i = 0; i < size(pkTrick); ++i)
            nbPoints += PackedCard.points(trump, card(pkTrick, i));
        return nbPoints;
    }

    /**
     * Donne la taille du pli donné, càd le nombre de cartes jouées
     * 
     * @param pkTrick
     *            (int) le pli à mesurer
     * @return (int) la taille du pli
     */
    public static int size(int pkTrick) {
        assert isValid(pkTrick);
        // On retourne l'index de la 1ere carte invalide trouvée
        for (int i = 0; i < PlayerId.COUNT; ++i)
            if (!PackedCard.isValid(card(pkTrick, i)))
                return i;
        // On n'a pas trouvé de carte invalide, le pli est plein
        return PlayerId.COUNT;
    }

    /**
     * Represente le pli donné dans un String de la forme (index_pli/8),
     * {cartes_jouées}, trump:, points_pli
     * 
     * @param pkTrick
     *            (int) le pli à représenter
     * @return (String) une représentation de pkTrick
     */
    public static String toString(int pkTrick) {
        assert isValid(pkTrick);

        StringJoiner j = new StringJoiner(",", "{", "}");
        for (int i = 0; i < size(pkTrick); ++i)
            j.add(PackedCard.toString(PackedTrick.card(pkTrick, i)));

        return "(" + index(pkTrick) + "/" + MAX_VALID_INDEX_TRICK
                + "), 1st Player: " + player(pkTrick, 0).toString() + ", "
                + j.toString() + ", trump:" + trump(pkTrick) + ", "
                + points(pkTrick) + "pts";
    }

    /**
     * Donne la couleur d'atout du pli (et donc du tour entier)
     * 
     * @param pkTrick
     *            (int) le pli en question
     * @return (Card.Color) la couleur des atouts
     */
    public static Card.Color trump(int pkTrick) {
        assert isValid(pkTrick);
        return Card.Color.ALL.get(Bits32.extract(pkTrick, TRUMP_START, TRUMP_SIZE));
    }

    /**
     * Donne le joueur qui remporte actuellement le pli
     * 
     * @param pkTrick
     *            (int) le pli, supposé non vide
     * @return (PlayerId) le joueur gagnant le pli
     */
    public static PlayerId winningPlayer(int pkTrick) {
        assert isValid(pkTrick);

        return player(pkTrick, bestCardIndex(pkTrick));
    }

    /**
     * Retourne un pli identique à celui donné, mais auquel on a ajouté la carte
     * donnée
     * 
     * @param pkTrick
     *            (int) le pli original, supposé non plein
     * @param pkCard
     *            (int) la carte à ajouter
     * @return (int) le nouveau pli avec pkCard en plus
     */
    public static int withAddedCard(int pkTrick, int pkCard) {
        assert isValid(pkTrick);
        assert !isFull(pkTrick);
        assert PackedCard.isValid(pkCard);

        final int sizeTrick = size(pkTrick);
        int[] cards = new int[PlayerId.COUNT];
        // les cartes déjà jouées sont copiées
        for (int i = 0; i < sizeTrick; ++i)
            cards[i] = card(pkTrick, i);
        // on ajoute la carte donnée
        cards[sizeTrick] = pkCard;
        // on remplit le reste de cartes invalides (non jouées)
        for (int i = sizeTrick + 1; i < PlayerId.COUNT; ++i)
            cards[i] = PackedCard.INVALID;

        return packTrick(cards[0], cards[1], cards[2], cards[3], index(pkTrick),
                player(pkTrick, 0), trump(pkTrick));
    }

    private PackedTrick() {
    }
}
