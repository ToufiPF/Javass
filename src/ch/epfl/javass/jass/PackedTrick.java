package ch.epfl.javass.jass;

import java.util.StringJoiner;

import ch.epfl.javass.bits.Bits32;

/**
 * PackedTrick
 * Une classe non instanciable contenant des
 * méthodes statiques pour manipuler des plis empaquetés
 * sous la forme d'un int
 *
 * @author Amaury Pierre (296498) 
 * @author Aurélien Clergeot (302592)
 */
public final class PackedTrick {
    private PackedTrick() {
    }
    /**
     * Représente un pli empaqueté invalide
     * (ce n'est pas le seul pli invalide possible)
     */
    public static final int INVALID = 0xFFFF_FFFF; // -1, tous les bits à 1
    
    /**
     * Vérifie si le pli empaqueté donné est valide,
     * càd que : son index soit compris entre 0 et 8 inclus,
     * si une carte invalide est trouvée, toutes les cartes suivantes
     * sont également invalides
     * @param pkTrick (int) le pli empaqueté à valider
     * @return (boolean) true ssi le pli est valide
     */
    public static boolean isValid(int pkTrick) {
        int index = Bits32.extract(pkTrick, 24, 4);
        if (index < 0 || index >= 9)
            return false;
        
        // Pour chaque carte, de 0 à 3 :
        // si la carte est invalide, les prochaines doivent
        // l'être aussi, sinon le pli est invalide
        for (int i = 0 ; i < PlayerId.COUNT ; ++i) {
            if (!PackedCard.isValid(Bits32.extract(pkTrick, i * 6, 6))) {
                for (int j = i + 1 ; j < PlayerId.COUNT ; ++j) {
                    if (PackedCard.isValid(Bits32.extract(pkTrick, j * 6, 6)))
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
     * Donne le pli empaqueté vide (le premier du tour)
     * avec l'atout et le 1er joueur donnés
     * @param trump (Card.Color) couleur de l'atout
     * @param firstPlayer (PlayerId) 1er joueur
     * @return (int) le pli empaqueté vide
     */
    public static int firstEmpty(Card.Color trump, PlayerId firstPlayer) {
        return packEmptyTrick(0, firstPlayer, trump);
    }
    
    /**
     * Donne le pli empaqueté vide suivant celui donné,
     * càd avec l'index suivant, le joueur gagnant dans pkTrick
     * commençant le prochain, et la même couleur d'atout
     * Si le pli donné est le dernier, retourne INVALID
     * @param pkTrick (int) l'ancien pli, supposé plein
     * @return (int) le pli suivant, vide (ou INVALID si pkTrick est
     * le dernier pli)
     */
    public static int nextEmpty(int pkTrick) {
        if (isLast(pkTrick))
            return INVALID;
        
        return packEmptyTrick(index(pkTrick) + 1, winningPlayer(pkTrick), trump(pkTrick));
    }
    
    /**
     * Vérifie si le pli donné est le dernier du tour
     * (si son index vaut 8)
     * @param pkTrick (int) le pli à vérifier
     * @return (boolean) true ssi pkTrick est le dernier pli
     */
    public static boolean isLast(int pkTrick) {
        assert isValid(pkTrick);
        return index(pkTrick) == 8;
    }
    /**
     * Vérifie si le pli donné est vide,
     * càd qu'aucune carte n'est encore été jouée
     * @param pkTrick (int) le pli à vérifier, supposé valide
     * @return (boolean) true ssi le pli est vide
     */
    public static boolean isEmpty(int pkTrick ) {
        assert isValid(pkTrick);
        // La 1ere carte est invalide -> le pli est vide
        return !PackedCard.isValid(card(pkTrick, 0));
    }
    /**
     * Vérifie si le pli donné est plein,
     * càd que chaque joueur a joué une carte
     * @param pkTrick (int) le pli à vérifier, supposé valide
     * @return (boolean) true ssi le pli est plein
     */
    public static boolean isFull(int pkTrick) {
        assert isValid(pkTrick);
        // La 4e carte est valide -> le pli est plein
        return PackedCard.isValid(card(pkTrick, 3));
    }
    /**
     * Donne la taille du pli donné,
     * càd le nombre de cartes jouées
     * @param pkTrick (int) le pli à mesurer
     * @return (int) la taille du pli
     */
    public static int size(int pkTrick) {
        //On retourne l'index de la 1ere carte invalide trouvée
        for (int i = 0 ; i < PlayerId.COUNT ; ++i)
            if (!PackedCard.isValid(card(pkTrick, i)))
                return i;
        // On n'a pas trouvé de carte invalide, le pli est plein
        return PlayerId.COUNT;
    }
    
    /**
     * Donne la couleur d'atout du pli
     * (et donc du tour entier)
     * @param pkTrick (int) le pli en question
     * @return (Card.Color) la couleur des atouts
     */
    public static Card.Color trump(int pkTrick) {
        assert isValid(pkTrick);
        return Card.Color.ALL.get(Bits32.extract(pkTrick, 30, 2));
    }
    /**
     * Donne le joueur à l'index dans le pli donné,
     * càd en comptant depuis le premier joueur
     * @param pkTrick (int) le pli en question
     * @param index (int) l'index du joueur dans le pli
     * @return (PlayerId) le joueur à l'index donné
     */
    public static PlayerId player(int pkTrick, int index) {
        assert isValid(pkTrick);
        assert index >= 0;
        assert index < PlayerId.COUNT;
        int firstPlayer = Bits32.extract(pkTrick, 28, 2);
        return PlayerId.ALL.get((firstPlayer + index) % PlayerId.COUNT);
    }
    /**
     * Donne l'index du pli,
     * càd le numero du pli dans le tour
     * @param pkTrick (int) le pli en question
     * @return (int) l'index du pli
     */
    public static int index(int pkTrick) {
        assert isValid(pkTrick);
        return Bits32.extract(pkTrick, 24, 4);
    }
    /**
     * Donne la carte (en version empaquetée) à l'index donné
     * @param pkTrick (int) le pli
     * @param indexCard (int) l'index de la carte
     * @return (int) la carte à l'index donné
     */
    public static int card(int pkTrick, int indexCard) {
        assert isValid(pkTrick);
        assert indexCard >= 0;
        assert indexCard < 4;
        return Bits32.extract(pkTrick, indexCard * 6, 6);
    }
    /**
     * Retourne un pli identique à celui donné, mais
     * auquel on a ajouté la carte donnée
     * @param pkTrick (int) le pli original, supposé non plein
     * @param pkCard (int) la carte à ajouter
     * @return (int) le nouveau pli avec pkCard en plus
     */
    public static int withAddedCard(int pkTrick, int pkCard) {
        assert isValid(pkTrick);
        assert !isFull(pkTrick);
        assert PackedCard.isValid(pkCard);
        
        final int sizeTrick = size(pkTrick);
        int[] cards = new int[PlayerId.COUNT];
        // les cartes déjà jouées sont copiées
        for (int i = 0 ; i < sizeTrick ; ++i)
            cards[i] = card(pkTrick, i);
        // on ajoute la carte donnée
        cards[sizeTrick] = pkCard;
        // on remplit le reste de cartes invalides (non jouées)
        for (int i = sizeTrick + 1 ; i < PlayerId.COUNT ; ++i)
            cards[i] = PackedCard.INVALID;
        
        return packTrick(cards[0], cards[1], cards[2], cards[3], index(pkTrick), player(pkTrick, 0), trump(pkTrick));
    }
    /**
     * Donne la couleur de la première carte du pli
     * @param pkTrick (int) le pli, supposé non vide
     * @return (Card.Color) la couleur de la 1ere carte
     */
    public static Card.Color baseColor(int pkTrick) {
        assert isValid(pkTrick);
        assert !isEmpty(pkTrick);
        return PackedCard.color(card(pkTrick, 0));
    }
    /**
     * Donne l'ensemble de cartes jouables dans ce pli
     * parmi les cartes données
     * @param pkTrick (int) le pli, supposé non plein
     * @param pkHand (long) la version empaquetée de l'ensemble de carte
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
        
        // On peut jouer toutes les cartes de la couleur de base
        long playableCards = PackedCardSet.subsetOfColor(pkHand, bc);
        // Si le joueur n'en a pas (ou si il n'y a que le Bour), il peut jouer toutes les cartes non-atout qu'il veut
        if (PackedCardSet.isEmpty(playableCards) || playableCards == PackedCardSet.singleton(PackedCard.pack(tc, Card.Rank.JACK))) {
            for (Card.Color c : Card.Color.ALL)
                if (!c.equals(tc))
                    playableCards |= PackedCardSet.subsetOfColor(pkHand, c);
        }
        
        // Et des atouts :
        long playableTrumps = PackedCardSet.EMPTY;
        // Si il y a déjà un atout sur dans le pli, on peut jouer seulement un meilleur
        final int bestTrump = bestTrump(pkTrick);
        if (bestTrump != PackedCard.INVALID)
            playableTrumps = PackedCardSet.intersection(PackedCardSet.subsetOfColor(pkHand, tc), PackedCardSet.trumpAbove(bestTrump));
        // Sinon, on peut jouer tous les atouts
        else
            playableTrumps = PackedCardSet.subsetOfColor(pkHand, tc);
        
        final long totalPlayable = PackedCardSet.union(playableCards, playableTrumps);
        if (PackedCardSet.isEmpty(totalPlayable))
            return pkHand;
        
        return totalPlayable;
    }
    /**
     * Donne le nombre de points du pli,
     * en comptant les 5 points bonus si le pli est le dernier
     * @param pkTrick (int) le pli en question
     * @return (int) la valeur en points du pli
     */
    public static int points(int pkTrick) {
        assert isValid(pkTrick);
        
        final Card.Color trump = trump(pkTrick);
        
        int nbPoints = isLast(pkTrick) ? 5 : 0;
        for (int i = 0 ; i < size(pkTrick) ; ++i)
            nbPoints += PackedCard.points(trump, card(pkTrick, i));
        return nbPoints;
    }
    /**
     * Donne le joueur qui remporte actuellement le pli
     * @param pkTrick (int) le pli, supposé non vide
     * @return (PlayerId) le joueur gagnant le pli
     */
    public static PlayerId winningPlayer(int pkTrick) {
        assert isValid(pkTrick);
        
        return player(pkTrick, bestCardIndex(pkTrick));
    }
    /**
     * Represente le pli donné dans un String de la forme
     * {cartes_jouées}/index_pli/points_pli
     * @param pkTrick (int) le pli à représenter
     * @return (String) une représentation de pkTrick
     */
    public static String toString(int pkTrick) {
        assert isValid(pkTrick);

        StringJoiner j = new StringJoiner(",", "{", "}");
        for (int i = 0; i < size(pkTrick); ++i)
            j.add(PackedCard.toString(PackedTrick.card(pkTrick, i)));
        
        return j.toString() + "/" + index(pkTrick) + "/" + points(pkTrick);
    }
    
    private static int bestCardIndex(int pkTrick) {
        assert !isEmpty(pkTrick);
        
        final int sizeTrick = size(pkTrick);
        final Card.Color trump = trump(pkTrick);
        
        int bestCard = card(pkTrick, 0);
        int bestCardID = 0;
        for (int i = 1 ; i < sizeTrick ; ++i) {
            int card_i = card(pkTrick, i);
            if (PackedCard.isBetter(trump, card_i, bestCard)) {
                bestCard = card_i;
                bestCardID = i;
            }
        }
        return bestCardID;
    }
    @SuppressWarnings("unused")
    private static int bestCard(int pkTrick) {
        return card(pkTrick, bestCardIndex(pkTrick));
    }
    
    /**
     * Donne le meilleur atout dans le pli,
     * Retourne PackedCard.INVALID si il n'y a pas d'atout
     * @param pkTrick (int) le pli
     * @return (int) la version paquetée du meilleur atout
     */
    private static int bestTrump(int pkTrick) {
        assert !isEmpty(pkTrick);
        
        final int sizeTrick = size(pkTrick);
        final Card.Color trump = trump(pkTrick);
        
        int bestTrumpCard = PackedCard.INVALID;
        for (int i = 0 ; i < sizeTrick ; ++i) {
            int card_i = card(pkTrick, i);
            if (PackedCard.color(card_i) == trump) {
                if (bestTrumpCard == PackedCard.INVALID || PackedCard.isBetter(trump, card_i, bestTrumpCard))
                    bestTrumpCard = card_i;
            }
        }
        return bestTrumpCard;
    }
    
    private static int packTrick(int card0, int card1, int card2, int card3, int indexTrick, PlayerId player, Card.Color trump) {
        return Bits32.pack(card0, 6, card1, 6, card2, 6, card3, 6,
                indexTrick, 4, player.ordinal(), 2, trump.ordinal(), 2);
    }
    private static int packEmptyTrick(int indexTrick, PlayerId player, Card.Color trump) {
        return packTrick(PackedCard.INVALID, PackedCard.INVALID, PackedCard.INVALID, PackedCard.INVALID,
                indexTrick, player, trump);
    }
}

